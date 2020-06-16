package wc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import wc.Master.Statistic;

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
	// Path to collect properties from -> url to scrape *edit when new site is
	// added*
	HashMap<String, String> siteAndRegionMap;
	// Path to collect properties from -> collected or not *created on request
	// performative*
	HashMap<String, Boolean> regionAndCollectedMap;
	// Path to collect properties from -> host checked for data *assuming indexes in
	// list wont change runtime except when host is deleted*
	HashMap<String, Integer> browserAgentsHostIndex;
	// Needed for information when to stop the master agent
	int createdCollectors;
	int createdBrowsers;
	// For clearer code purposes
	private AgentType collectorType;
	private AgentType browserType;

	// What to do with properties
	private String ontology;

	private Statistic statistic;

	@Override
	public void init(AID id) throws IOException {
		this.id = id;
		this.properties = new ArrayList<Property>();
		this.siteAndRegionMap = initializeSiteAndRegionHashmap();
		this.createdCollectors = 0;
		this.createdBrowsers = 0;

		this.statistic = new Statistic();

		this.browserAgentsHostIndex = new HashMap<String, Integer>();
		List<AgentType> agentTypes = agentManager.getAvailableAgentClasses();
		for (AgentType type : agentTypes) {
			if (type.getName().equals("Collector")) {
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
		 * Requesting some data from master agent Starting point of work
		 */
		case REQUEST:
			this.regionAndCollectedMap = initializeRegionAndCollectedMap(msg.content);
			this.ontology = msg.ontology;
			for (String region : regionAndCollectedMap.keySet()) {
				startBrowserLocally(region);
			}

			break;
		/*
		 * Browser found the data If everything is collected send it to user
		 */
		case CONFIRM:
			wsMessageCreator.log("Master: Browser found the data");
			String propertiesJsonString = msg.content;
			String path = msg.ontology;
			--createdBrowsers;

			if (!regionAndCollectedMap.get(path)) {
				properties.addAll(JSON.mapper.readValue(propertiesJsonString, new TypeReference<List<Property>>() {
				}));
				regionAndCollectedMap.put(path, true);
			}

			if (!regionAndCollectedMap.containsValue(false)) {
				// Aggregate, sort... whatever... Send to user
				System.out.println("MASTER: " + properties);
				String[] parameters = this.ontology.split(":"); // TOP-100:SORT-VIEWS-DESC

				if (parameters.length != 2) {
					// WS LOG ERROR
					return;
				}

				String action = parameters[0];
				String sort = parameters[1];

				System.out.println("************** ONTOLOGY *************");
				System.out.println(this.ontology);

				System.out.println("ACTION: " + action);
				System.out.println("SORT: " + sort);

				switch (action) {
				case "TOP-100":
					if (sort.isEmpty()) {
						break;
					}

					String[] sortParams = sort.split("-");
					String sortField = sortParams[1];
					String sortType = sortParams[2];

					List<Property> sortedProperties;

					if (this.properties.size() >= 100) {
						sortedProperties = sortProperties(sortField, sortType, this.properties).subList(0, 100);
					} else {
						sortedProperties = sortProperties(sortField, sortType, this.properties);
					}

					wsMessageCreator.sendPropertiesTop100(sortedProperties);
					break;

				case "TOP-100-LOCATION":
					LinkedHashMap<String, Integer> locationStatistic = new LinkedHashMap<String, Integer>();

					for (Property property : this.properties) {
						statistic.addLocationData(property);
					}

					locationStatistic = this.statistic.data.get("LOCATION").entrySet().stream()
							.sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).collect(Collectors
									.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
					
					System.out.println("*************** LOCATION STATISTIC ***************");
					
					List<Property> sortedPropertiesLocation;
					sortedPropertiesLocation = sortProperties("VIEWS", "DESC", this.properties);
					
					List<Property> topPropertiesOnLocation = new ArrayList<Property>();
					
					for (String key : locationStatistic.keySet()) {
						for (Property property : sortedPropertiesLocation) {
							if (property.getLocation().equals(key) && locationStatistic.get(key) > 0) {
								topPropertiesOnLocation.add(property);
								locationStatistic.put(key, locationStatistic.get(key)-1);
							}
						}
					}
					
					wsMessageCreator.sendPropertiesTopLocations(topPropertiesOnLocation);
					break;

				case "STATISTICS":

					List<Property> sortedPropertiesForStatistic = new ArrayList<Property>();

					if (this.properties.size() >= 100) {
						sortedPropertiesForStatistic = sortProperties("VIEWS", "DESC", this.properties).subList(0, 100);
					} else {
						sortedPropertiesForStatistic = sortProperties("VIEWS", "DESC", this.properties);
					}

					for (Property property : sortedPropertiesForStatistic) {
						statistic.addData(property);
					}

					System.out.println("*************** STATISTIC ***************");
					
					wsMessageCreator.sendStatistic(initStatistic(statistic));
					break;

				default:
					break;
				}

				// Stop master agent if no collectors are started, if they are, agent will be
				// stopped in CASE: INFORM
				if (createdCollectors == 0 && createdBrowsers == 0) {
					agentManager.stopAgent(this.id);
				}
			}
			break;
		/*
		 * No data on the given path and on the given host Check other hosts but run
		 * scraper on the given host, will need the data later
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
					// All hosts checked for data, none has it, run scraper localy for the given
					// path
					browserAgentsHostIndex.put(pathToScrape, browserAgentsHostIndex.get(pathToScrape));
					runCollectorLocally(pathToScrape);
				} else {
					browserAgentsHostIndex.put(pathToScrape, nextHostIndex);
					startBrowserRemotely(pathToScrape, null);
				}
			}
			break;
		/*
		 * Collector finished Run browser for the given path and on the given host
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
				// Stop master agent when all created collectors are done to avoid errors with
				// no running master aid
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
		this.statistic = null;
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
		ResteasyWebTarget rtarget = client.target("http://" + hostAddress
				+ "/AT-WebCrawlerWAR/rest/client/agents/running/Collector/Collector-" + pathToScrape.replace("/", "-"));
		Response response = rtarget.request(MediaType.APPLICATION_JSON)
				.put(Entity.entity(null, MediaType.APPLICATION_JSON));
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
		ResteasyWebTarget rtarget = client.target("http://" + hostAddress
				+ "/AT-WebCrawlerWAR/rest/client/agents/running/Browser/Browser-" + pathToBrowse.replace("/", "-"));
		Response response = rtarget.request(MediaType.APPLICATION_JSON)
				.put(Entity.entity(null, MediaType.APPLICATION_JSON));
		AID aid = response.readEntity(AID.class);
		createdBrowsers++;
		ACLMessage qmsg = new ACLMessage(Performative.REQUEST);
		qmsg.receivers.add(aid);
		qmsg.content = pathToBrowse; // Content is path to search for data
		qmsg.sender = id;
		msm().post(qmsg);

	}

	private HashMap<String, String> initializeSiteAndRegionHashmap() {
		HashMap<String, String> map = new HashMap<String, String>();

		map.put("properties/city-nekretnine.json",
				"http://www.city-nekretnine.rs/pretraga.php?l=sr&filter=&property_category=");
		map.put("properties/info-nekretnine.json",
				"http://www.info-nekretnine.rs/pretraga.php?l=sr&filter=&property_category=");
		map.put("properties/021-nekretnine.json",
				"http://www.021-nekretnine.rs/pretraga.php?l=sr&filter=&property_category=");

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

	private HashMap<String, Boolean> initializeRegionAndCollectedMap(String desiredRegions) {
		HashMap<String, Boolean> map = new HashMap<String, Boolean>();

		String[] regions = desiredRegions.split(" ");
		for (String region : regions) {
			map.put(region, false);
		}

		return map;
	}

	private List<Property> sortProperties(String sortField, String sortType, ArrayList<Property> properties) {
		if (!sortType.equals("ASC") && !sortType.equals("DESC")) {
			return new ArrayList();
		}

		switch (sortField) {
		case "VIEWS":
			if (sortType.equals("ASC")) {
				properties.sort(Comparator.comparing(Property::getViews));
			} else {
				properties.sort(Comparator.comparing(Property::getViews).reversed());
			}
			break;

		case "SIZE":
			if (sortType.equals("ASC")) {
				properties.sort(Comparator.comparing(Property::getSize));
			} else {
				properties.sort(Comparator.comparing(Property::getSize).reversed());
			}
			break;

		case "LAND":
			if (sortType.equals("ASC")) {
				properties.sort(Comparator.comparing(Property::getLand));
			} else {
				properties.sort(Comparator.comparing(Property::getLand).reversed());
			}
			break;

		case "PRICE":
			if (sortType.equals("ASC")) {
				properties.sort(Comparator.comparing(Property::getPrice));
			} else {
				properties.sort(Comparator.comparing(Property::getPrice).reversed());
			}
			break;

		default:
			return new ArrayList();
		}

		return properties;
	}
	
	public StatisticDTO initStatistic(Statistic statistic) {
		StatisticDTO statisticDTO = new StatisticDTO();
		statisticDTO.setType(statistic.data.get("TYPE"));
		statisticDTO.setArea(statistic.data.get("AREA"));
		statisticDTO.setLocation(statistic.data.get("LOCATION"));
		statisticDTO.setSize(statistic.data.get("SIZE"));
		statisticDTO.setLand(statistic.data.get("LAND"));
		statisticDTO.setPrice(statistic.data.get("PRICE"));
		statisticDTO.setState(statistic.data.get("STATE"));
		
		return statisticDTO;
	}

	public class Statistic {
		private HashMap<String, HashMap<String, Integer>> data;

		public Statistic() {
			super();
			this.data = new HashMap<String, HashMap<String, Integer>>();
			this.data.put("TYPE", new HashMap<String, Integer>());
			this.data.put("AREA", new HashMap<String, Integer>());
			this.data.put("LOCATION", new HashMap<String, Integer>());
			this.data.put("SIZE", new HashMap<String, Integer>());
			this.data.put("LAND", new HashMap<String, Integer>());
			this.data.put("PRICE", new HashMap<String, Integer>());
			this.data.put("STATE", new HashMap<String, Integer>());
		}

		public void addLocationData(Property property) {
			if (property.getLocation() != null) {
				if (this.data.get("LOCATION").containsKey(property.getLocation())) {
					Integer value = this.data.get("LOCATION").get(property.getLocation());
					this.data.get("LOCATION").put(property.getLocation(), ++value);
				} else {
					this.data.get("LOCATION").put(property.getLocation(), 1);
				}
			}
		}

		public void addData(Property property) {

			if (property.getType() != null) {
				if (this.data.get("TYPE").containsKey(property.getType())) {
					Integer value = this.data.get("TYPE").get(property.getType());
					this.data.get("TYPE").put(property.getType(), ++value);
				} else {
					this.data.get("TYPE").put(property.getType(), 1);
				}
			}

			if (property.getArea() != null) {
				if (this.data.get("AREA").containsKey(property.getArea())) {
					Integer value = this.data.get("AREA").get(property.getArea());
					this.data.get("AREA").put(property.getArea(), ++value);
				} else {
					this.data.get("AREA").put(property.getArea(), 1);
				}
			}

			if (property.getLocation() != null) {
				if (this.data.get("LOCATION").containsKey(property.getLocation())) {
					Integer value = this.data.get("LOCATION").get(property.getLocation());
					this.data.get("LOCATION").put(property.getLocation(), ++value);
				} else {
					this.data.get("LOCATION").put(property.getLocation(), 1);
				}
			}

			if (property.getSize() != null) {
				Integer aproxValue = 50 * (1 + (int) (property.getSize() / 50));
				if (this.data.get("SIZE").containsKey((aproxValue - 50) + " - " + aproxValue)) {
					Integer value = this.data.get("SIZE").get((aproxValue - 50) + " - " + aproxValue);
					this.data.get("SIZE").put((aproxValue - 50) + " - " + aproxValue, ++value);
				} else {
					this.data.get("SIZE").put((aproxValue - 50) + " - " + aproxValue, 1);
				}
			}

			if (property.getLand() != null) {
				Integer aproxValue = 100 * (1 + (int) (property.getLand() / 100));
				if (this.data.get("LAND").containsKey((aproxValue - 100) + " - " + aproxValue)) {
					Integer value = this.data.get("LAND").get((aproxValue - 100) + " - " + aproxValue);
					this.data.get("LAND").put((aproxValue - 100) + " - " + aproxValue, ++value);
				} else {
					this.data.get("LAND").put((aproxValue - 100) + " - " + aproxValue, 1);
				}
			}

			if (property.getPrice() != null) {
				Integer aproxValue = 10000 * (1 + (int) (property.getPrice() / 10000));
				if (this.data.get("PRICE").containsKey((aproxValue - 10000) + " - " + aproxValue)) {
					Integer value = this.data.get("PRICE").get((aproxValue - 10000) + " - " + aproxValue);
					this.data.get("PRICE").put((aproxValue - 10000) + " - " + aproxValue, ++value);
				} else {
					this.data.get("PRICE").put((aproxValue - 10000) + " - " + aproxValue, 1);
				}
			}

			if (property.getState() != null) {
				if (this.data.get("STATE").containsKey(property.getState())) {
					Integer value = this.data.get("STATE").get(property.getState());
					this.data.get("STATE").put(property.getState(), ++value);
				} else {
					this.data.get("STATE").put(property.getState(), 1);
				}
			}
		}

		@Override
		public String toString() {
			return "Statistic [data=" + data + "]";
		}
	}
}