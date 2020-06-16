package wc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.fasterxml.jackson.core.type.TypeReference;

import agent.AID;
import agent.Agent;
import agent.AgentManagerBean;
import agent.AgentType;
import agent.BaseAgent;
import message.ACLMessage;
import message.Performative;
import node.AgentCenter;
import serverCommunications.Communications;
import util.JSON;
import util.WSMessageCreator;

@Stateful
@Remote(Agent.class)
public class Master extends BaseAgent {
	private static final long serialVersionUID = 1L;
	
	@EJB
	WSMessageCreator wsMessageCreator;
	@EJB
	Communications communications;
	@EJB
	AgentManagerBean agentManager;
	
	// Properties to return
	ArrayList<Property> properties;
	// Path to collect properties from -> url to scrape *edit when new site is added*
	HashMap<String, String> siteAndRegionMap;
	// Path to collect properties from -> collected or not *created on request performative*
	HashMap<String, Boolean> regionAndCollectedMap;
	// Path to collect properties from -> host checked for data *assuming indexes in list wont change runtime except when host is deleted*
	HashMap<String, Integer> browserAgentsHostIndex;
	// Needed for information when to stop the master agent
	int createdCollectors;
	int createdBrowsers;
	// For clearer code purposes
	private AgentType collectorType;
	private AgentType browserType;
		
	// What to do with properties
	private String ontology;
	
	@Override
	public void init(AID id) throws IOException {
		this.id = id;
		this.properties = new ArrayList<Property>();
		this.siteAndRegionMap = initializeSiteAndRegionHashmap();
		this.createdCollectors = 0;
		this.createdBrowsers = 0;

		this.browserAgentsHostIndex = new HashMap<String, Integer>();
		List<AgentType> agentTypes = agentManager.getAvailableAgentClasses();
		for (AgentType type : agentTypes) {
			if(type.getName().equals("Collector")) {
				this.collectorType = type;
			} else if (type.getName().equals("Browser")) {
				this.browserType = type;
			}
		}

		wsMessageCreator.log("Master agent created");
	}
	
	@Override
	public void handleMessage(ACLMessage msg) throws IOException {

		switch (msg.performative) {
		/*
		 *  Requesting some data from master agent
		 *  Starting point of work
		 */
		case REQUEST:
			this.regionAndCollectedMap = initializeRegionAndCollectedMap(msg.content);
			this.ontology = msg.ontology;
			for (String region: regionAndCollectedMap.keySet()) {
				startBrowserLocally(region);
			}

			break;
		/*
		 *  Browser found the data
		 *  If everything is collected send it to user
		 */
		case CONFIRM:
			wsMessageCreator.log("Master: Browser found the data");
			String propertiesJsonString =  msg.content;
			String path = msg.ontology;
			--createdBrowsers;
			
			if (!regionAndCollectedMap.get(path)) {
				properties.addAll(JSON.mapper.readValue(propertiesJsonString, new TypeReference<List<Property>>(){}));
				regionAndCollectedMap.put(path, true);
			}
			
			if (!regionAndCollectedMap.containsValue(false)) {
				// Aggregate, sort... whatever... Send to user
				System.out.println("MASTER: " + properties);
				
				// Stop master agent if no collectors are started, if they are, agent will be stopped in CASE: INFORM
				if (createdCollectors == 0 && createdBrowsers == 0) {
					agentManager.stopAgent(this.id);
				}
			}
			break;
		/*
		 *  No data on the given path and on the given host
		 *  Check other hosts but run scraper on the given host, will need the data later
		 */
		case DISCONFIRM: 
			wsMessageCreator.log("Master: Browser didnt find the data");

			String host = msg.content;
			String pathToScrape = msg.ontology;
			--createdBrowsers;
			// Run scraper
//			if (host.equals(communications.getAgentCenter().getAddress())) {
//				runCollectorLocally(pathToScrape);
//			} else {
//				runCollectorRemotelly(pathToScrape, host);
//			}
			
			// Check other hosts
			if (browserAgentsHostIndex.get(pathToScrape) == null) {
				if (communications.getConnections().size() > 0) {
					browserAgentsHostIndex.put(pathToScrape, 0);
					startBrowserRemotely(pathToScrape, null);
				} else {
					runCollectorLocally(pathToScrape);
				}
			} else {
				int nextHostIndex = 0;
				if (browserAgentsHostIndex.get(pathToScrape) + 1 < communications.getConnections().size()) {
					nextHostIndex = browserAgentsHostIndex.get(pathToScrape) + 1;
				}
				if (nextHostIndex == 0) { 
					// All hosts checked for data, none has it, run scraper localy for the given path
					browserAgentsHostIndex.put(pathToScrape, browserAgentsHostIndex.get(pathToScrape));
					runCollectorLocally(pathToScrape);
				} else {
					browserAgentsHostIndex.put(pathToScrape, nextHostIndex);
					startBrowserRemotely(pathToScrape, null);
				}
			}
			break;
		/*
		 *  Collector finished
		 *  Run browser for the given path and on the given host
		 */
		case INFORM:
			wsMessageCreator.log("Master: Collector finished scraping");

			host = msg.content;
			String pathToGetData = msg.ontology;
			--createdCollectors;
			
			if (!regionAndCollectedMap.get(pathToGetData)) {
				// Run browser
				if (host.equals(communications.getAgentCenter().getAddress())) {
					startBrowserLocally(pathToGetData);
				} else {
					startBrowserRemotely(pathToGetData, host);
				}
			}
			
			if (createdCollectors == 0 && !regionAndCollectedMap.containsValue(false)) {
				// Stop master agent when all created collectors are done to avoid errors with no running master aid
				agentManager.stopAgent(this.id);
			}
			break;
		default:
			wsMessageCreator.log("Master: invalid performative");
			break;
		}
	}
	
	@Override
	public void stop() {
		this.properties = null;
		this.siteAndRegionMap = null;
		this.regionAndCollectedMap = null;
		this.createdCollectors = 0;
		this.browserAgentsHostIndex = null;
	}
	
	private void runCollectorLocally(String pathToScrape) throws IOException {
		AID aid = agentManager.startServerAgent(this.collectorType, "Collector-" + pathToScrape);
		ACLMessage qmsg = new ACLMessage(Performative.REQUEST);
		qmsg.receivers.add(aid);
		qmsg.content = siteAndRegionMap.get(pathToScrape) + " " + pathToScrape; // Url to scrape and file to save it
		qmsg.sender = id;
		msm().post(qmsg);
		createdCollectors++;
	}
	
	private void runCollectorRemotelly(String pathToScrape, String hostAddress) {
		// Create agent on remote server
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget rtarget = client
				.target("http://" + hostAddress + "/AT-WebCrawlerWAR/rest/client/agents/running/Collector/Collector-" + pathToScrape.replace("/", "-"));
		Response response = rtarget.request(MediaType.APPLICATION_JSON).put(Entity.entity(null, MediaType.APPLICATION_JSON));
		AID aid = response.readEntity(AID.class);
		// Create message
		ACLMessage qmsg = new ACLMessage(Performative.REQUEST);
		qmsg.receivers.add(aid);
		qmsg.content = siteAndRegionMap.get(pathToScrape) + " " + pathToScrape; // Url to scrape and file to save it
		qmsg.sender = id;
		// Send message to the agent on a remote server
		msm().post(qmsg);
		createdCollectors++;
	}
	
	private void startBrowserLocally(String pathToBrowse) throws IOException {
		AID aid = agentManager.startServerAgent(this.browserType, "Browser-" + pathToBrowse);
		createdBrowsers++;
		ACLMessage qmsg = new ACLMessage(Performative.REQUEST);
		qmsg.receivers.add(aid);
		qmsg.content = pathToBrowse; // Content is path to search for data
		qmsg.sender = id;
		msm().post(qmsg);
	}
	
	// hostAddress is set when scraper finishes
	private void startBrowserRemotely(String pathToBrowse, String hostAddress) {
		if (hostAddress == null) {
			int hostIndex = browserAgentsHostIndex.get(pathToBrowse);
			AgentCenter host = communications.getConnections().get(hostIndex);
			hostAddress = host.getAddress();
		}
		
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget rtarget = client
				.target("http://" + hostAddress + "/AT-WebCrawlerWAR/rest/client/agents/running/Browser/Browser-" + pathToBrowse.replace("/", "-"));
		Response response = rtarget.request(MediaType.APPLICATION_JSON).put(Entity.entity(null, MediaType.APPLICATION_JSON));
		AID aid = response.readEntity(AID.class);
		createdBrowsers++;
		ACLMessage qmsg = new ACLMessage(Performative.REQUEST);
		qmsg.receivers.add(aid);
		qmsg.content = pathToBrowse; // Content is path to search for data
		qmsg.sender = id;
		msm().post(qmsg);
		
	}
	
	private HashMap<String, String> initializeSiteAndRegionHashmap(){
		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put("properties/city-nekretnine.json","http://www.city-nekretnine.rs/pretraga.php?l=sr&filter=&property_category=");
		map.put("properties/info-nekretnine.json","http://www.info-nekretnine.rs/pretraga.php?l=sr&filter=&property_category=");
		map.put("properties/021-nekretnine.json","http://www.021-nekretnine.rs/pretraga.php?l=sr&filter=&property_category=");

//		map.put("properties/green-acres/paris.json", "https://www.green-acres.fr/property-for-sale/paris");
//		map.put("properties/green-acres/hauts-de-seine.json", "https://www.green-acres.fr/property-for-sale/hauts-de-seine");
//		map.put("properties/green-acres/val-de-marne.json", "https://www.green-acres.fr/property-for-sale/val-de-marne");
//		map.put("properties/green-acres/seine-saint-denis.json", "https://www.green-acres.fr/property-for-sale/seine-saint-denis");
//		map.put("properties/green-acres/seine-et-marne.json", "https://www.green-acres.fr/property-for-sale/seine-et-marne");
//		map.put("properties/green-acres/val-d-oise.json", "https://www.green-acres.fr/property-for-sale/val-d-oise");
//		map.put("properties/green-acres/yvelines.json", "https://www.green-acres.fr/property-for-sale/yvelines");
//		map.put("properties/green-acres/essonne.json", "https://www.green-acres.fr/property-for-sale/essonne");

		return map;
	}
	
	private HashMap<String, Boolean> initializeRegionAndCollectedMap(String desiredRegions){
		HashMap<String, Boolean> map = new HashMap<String, Boolean>();
		
		String[] regions = desiredRegions.split(" ");
		for (String region: regions) {
			map.put(region, false);
		}

		return map;
	}

}
