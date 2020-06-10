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
import javax.naming.NamingException;

import node.AgentCenter;
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
		
	@PostConstruct
	public void postConstruct() {
		agents = new HashMap<AID, Agent>();
	}
	
	@Override
	public List<AgentType> getAvailableAgentClasses() {
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
	public AID startServerAgent(AgentType type, String name) {
		AgentCenter host = new AgentCenter("localhost", "test");

		AID aid = new AID(name, type, host);
		Agent agent = null;

		try {
			String path = getAgentLookup(aid.getType(), true);
			agent = ObjectFactory.lookup(path, Agent.class, null);
		} catch (IllegalStateException ex) {
			String path = getAgentLookup(aid.getType(), true);
			agent = ObjectFactory.lookup(path, Agent.class, null);
		}

		agents.put(aid, agent);
		agent.init(aid);
		
		try {
			WSMessageCreator.sendActiveAgents(getRunningAgents());
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
}
