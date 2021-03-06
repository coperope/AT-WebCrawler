package serverCommunications;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.AccessTimeout;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import agent.AID;
import agent.Agent;
import agent.AgentManager;
import agent.AgentType;
import node.AgentCenter;
import util.JSON;
import util.WSMessageCreator;

@Singleton
@Startup
@LocalBean
@AccessTimeout(value = 60, unit = TimeUnit.SECONDS)
public class Communications {

	private AgentCenter master = new AgentCenter("master", "");
	private AgentCenter agentCenter = new AgentCenter("localHost1", "6414552c6fcc.ngrok.io");
	private List<AgentCenter> connections = new ArrayList<AgentCenter>();

	@EJB
	private AgentManager agm;
	
	@EJB
	private WSMessageCreator wsMessageCreator;

	/**
	 * Default constructor.
	 */
	@PostConstruct
	private void init() {
		System.out.println("Here");
		if (master != null && !master.getAddress().equals("")) {
			ResteasyClient client = new ResteasyClientBuilder().build();
			ResteasyWebTarget rtarget = client
					.target("http://" + master.getAddress() + "/AT-WebCrawlerWAR/rest/server");
			CommunicationsRest rest = rtarget.proxy(CommunicationsRest.class);
			this.connections = rest.newConnection(this.getAgentCenter());
			this.connections.add(this.master);

			Set<AID> runningAgents = rest.getRunningAgents();

			for (AID aid : runningAgents) {
				agm.putAgent(aid, null);
				System.out.println(aid);
			}

			System.out.println("Connections: ");
			for (AgentCenter agentCenter : connections) {
				System.out.println(agentCenter.getAlias());
			}
			System.out.println("ZAVRSIO");
		}
	}

	@Schedule(hour = "*", minute = "*/1", info = "every ten minutes")
	public void heartBeat() {
		System.out.println("Timer");
		System.out.println("Checking health for: ");
		ResteasyClient client = new ResteasyClientBuilder().build();
		for (AgentCenter center : this.connections) {
			ResteasyWebTarget rtarget = client
					.target("http://" + center.getAddress() + "/AT-WebCrawlerWAR/rest/server");
			CommunicationsRest rest = rtarget.proxy(CommunicationsRest.class);

			try {
				rest.getNode();
			} catch (Exception e) {
				try {
					rest.getNode();
				} catch (Exception e2) {
					System.out.println("Node is null");
					removeNodeTellEveryone(center);
				}
			}
			System.out.println(center.getAlias());
			System.out.println(center.getAddress());
		}
	}

	public void removeNodeTellEveryone(AgentCenter connection) {
		ResteasyClient client = new ResteasyClientBuilder().build();
		HashMap<AID, Agent> newRunningAgents = new HashMap<AID, Agent>();
    	for (AID agent : agm.getAgents().keySet()) {
    		if (!agent.getHost().getAddress().equals(connection.getAddress())) {
    			newRunningAgents.put(agent, agm.getAgents().get(agent));
			}
		}
		for (AgentCenter center : this.connections) {
			if (center.getAddress().equals(connection.getAddress())) {
				continue;
			}
			ResteasyWebTarget rtarget = client
					.target("http://" + center.getAddress() + "/AT-WebCrawlerWAR/rest/server");
			CommunicationsRest rest = rtarget.proxy(CommunicationsRest.class);
			rest.sendRunningAgents(newRunningAgents.keySet());
			rest.deleteNode(connection.getAlias());
		}
		agm.setAgents(newRunningAgents);
		try {
			wsMessageCreator.sendActiveAgents(agm.getRunningAgents());
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.connections.remove(connection);
	}

	@PreDestroy
	private void destroy() {
		System.out.println("USAO U destroy");
		ResteasyClient client = new ResteasyClientBuilder().build();

		for (AgentCenter connection : this.connections) {
			ResteasyWebTarget rtarget = client.target("http://" + connection.getAddress() + "/AT-WebCrawlerWAR/rest/server");
			CommunicationsRest rest = rtarget.proxy(CommunicationsRest.class);
			rest.deleteNode(agentCenter.getAlias());
			System.out.println("NODE SEND ACTION TO DELETE");
		}

		System.out.println("Node is destroyed");
	}

	public AgentCenter getMaster() {
		return master;
	}

	public void setMaster(AgentCenter master) {
		this.master = master;
	}

	public AgentCenter getAgentCenter() {
		return agentCenter;
	}

	public void setAgentCenter(AgentCenter agentCenter) {
		this.agentCenter = agentCenter;
	}

	public List<AgentCenter> getConnections() {
		return connections;
	}

	public void setConnections(List<AgentCenter> connections) {
		this.connections = connections;
	}

}
