package agent;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import node.AgentCenter;
import util.AgentTypesJndiFinder;

@Singleton
@Remote(AgentManager.class)
@LocalBean
public class AgentManagerBean implements AgentManager {

	private HashMap<AID, Agent> agents;
	
	@EJB
	private AgentTypesJndiFinder agentTypesFinder;
	
	private Context context;
	
	@PostConstruct
	public void postConstruct() {
		agents = new HashMap<AID, Agent>();
		Hashtable<String, Object> jndiProps = new Hashtable<>();
		jndiProps.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
		//jndiProps.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory"); 
		//jndiProps.put(Context.PROVIDER_URL,"http-remoting://localhost:8080");
		try {
			context = new InitialContext(jndiProps);
		} catch (NamingException e) {
			e.printStackTrace();
		}
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AID startServerAgent(AgentType type, String name) {
		AgentCenter host = new AgentCenter("localhost", "test");

		AID aid = new AID(name, type, host);
		Agent agent = null;
		try {
			try {
				String path = getAgentLookup(aid.getType(), true);
				agent = (Agent) context.lookup(path);
			} catch (IllegalStateException ex) {
				String path = getAgentLookup(aid.getType(), true);
				agent = (Agent) context.lookup(path);
			}
		} catch (NamingException e) {
			e.printStackTrace();
		}
		agents.put(aid, agent);
		agent.init(aid);
		
		return aid;
	}

	@Override
	public void stopAgent(AID aid) {
		// TODO Auto-generated method stub
		
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

}
