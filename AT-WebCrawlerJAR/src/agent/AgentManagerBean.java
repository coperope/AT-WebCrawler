package agent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.naming.NamingException;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import node.AgentCenter;
import serverCommunications.Communications;
import serverCommunications.CommunicationsRest;
import serverCommunications.ConnectionsBean;
import util.AgentTypesJndiFinder;
import util.ObjectFactory;
import util.WSMessageCreator;

@Singleton
@Remote(AgentManager.class)
@LocalBean
public class AgentManagerBean implements AgentManager {

	private HashMap<AID, Agent> agents;
	
	@EJB
	private AgentTypesJndiFinder agentTypesFinder;
	@EJB
	private WSMessageCreator wsMessageCreator;
	
	@EJB
	private Communications communications;
	
	@Inject
	ConnectionsBean communicate;
	
	@PostConstruct
	public void postConstruct() {
		agents = new HashMap<AID, Agent>();
	}
	
	@Override
	public List<AgentType> getAvailableAgentClasses() {
		System.out.println("-------------------------    jboss.node.name    -------------------------");
		System.out.println(System.getProperty("jboss.node.name"));
		System.out.println("-------------------------------------------------------------------------");
		try {
			return agentTypesFinder.parse();
		} catch (NamingException ex) {
			throw new IllegalStateException(ex);
		}
	}

	@Override
	public List<AID> getRunningAgents() {
		Set<AID> set = agents.keySet();
		if (set.size() > 0) {
			AID aid = set.iterator().next();
			try {
				try {
					ObjectFactory.lookup(getAgentLookup(aid.getType(), true), Agent.class, null);
				} catch (Exception ex) {
					ObjectFactory.lookup(getAgentLookup(aid.getType(), false), Agent.class, null);
				}
			} catch (Exception ex) {
				set.remove(aid);
				agents.remove(aid);
			}
		}
		return new ArrayList<AID>(set);
	}

	@Override
	public AID startServerAgent(AgentType type, String name) throws IOException {
		
		AgentCenter host = communications.getAgentCenter();
		
		AID aid = new AID(name, type, host);
		Agent agent = null;

		try {
			String path = getAgentLookup(aid.getType(), true);
			agent = ObjectFactory.lookup(path, Agent.class, null);
		} catch (IllegalStateException ex) {
			String path = getAgentLookup(aid.getType(), true);
			agent = ObjectFactory.lookup(path, Agent.class, null);
		}

		agent.init(aid);
		agents.put(aid, agent);
		
		List<Agent> runningAgents = new ArrayList<Agent>();
		for (Agent agent2 : agents.values()) {
			runningAgents.add(agent2);
		}
		try {
			wsMessageCreator.sendActiveAgents(getRunningAgents());
			communicate.sendRunningAgentsToEveryone(runningAgents);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return aid;
	}

	@Override
	public void stopAgent(AID aid) {
		Agent agent = agents.get(aid);
		if (agent != null) {
			agent.stop();
			agents.remove(aid);
			try {
				wsMessageCreator.sendActiveAgents(getRunningAgents());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private String getAgentLookup(AgentType agType, boolean stateful) {
		if (agType.getModule().contains("/")) {
			// in ear file
			if (stateful)
				return String.format("ejb:%s//%s!%s?stateful", agType.getModule(),
						agType.getName(), Agent.class.getName());
			else
				return String.format("ejb:%s//%s!%s", agType.getModule(), agType.getName(),
						Agent.class.getName());
		} else {
			// in jar file
			if (stateful)
				return String.format("ejb:/%s//%s!%s?stateful", agType.getModule(),
						agType.getName(), Agent.class.getName());
			else
				return String.format("ejb:/%s//%s!%s", agType.getModule(), agType.getName(),
						Agent.class.getName());
		}
	}

	public Agent getAgent(AID aid) {
		return agents.get(aid);
	}

	public HashMap<AID, Agent> getAgents() {
		return agents;
	}

	public void setAgents(HashMap<AID, Agent> agents) {
		this.agents = agents;
	}
	
}
