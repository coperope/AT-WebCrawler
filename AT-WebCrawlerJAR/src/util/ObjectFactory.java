package util;

import javax.ejb.SessionContext;
import javax.naming.NamingException;

import agent.AgentManager;
import agent.AgentManagerBean;
import controller.ClientController;
import controller.ClientControllerRemote;
import node.AgentCenter;

public class ObjectFactory {
	static String PROJECT_MODULE = "AT-WebCrawlerJAR";
	static String PROJECT_EAR = "AT-WebCrawlerEAR";
	static String PROJECT_WAR = "AT-WebCrawlerWAR";
	

	public static final String AgentManagerLookup = "ejb:" + PROJECT_EAR + "/" + PROJECT_MODULE + "//"
			+ AgentManagerBean.class.getSimpleName() + "!" + AgentManager.class.getName();
	public static final String ClientRestControllerLookup = "ejb:" + PROJECT_EAR + "/" + PROJECT_MODULE + "//"
			+ ClientController.class.getSimpleName() + "!" + ClientControllerRemote.class.getName();
			//+ "?stateful";

	public static AgentManager getAgentManager(AgentCenter remote) {
		return lookup(AgentManagerLookup, AgentManager.class, remote);
	}

	public static ClientControllerRemote getClientRestController(AgentCenter remote) {
		return lookup(ClientRestControllerLookup, ClientControllerRemote.class, remote);
	}

	public static SessionContext getSessionContext() {
		return lookup("java:comp/EJBContext", SessionContext.class, null);
	}

	@SuppressWarnings("unchecked")
	public static <T> T lookup(String name, Class<T> c, AgentCenter remote) {
		try {
			return (T) ContextFactory.get(remote).lookup(name);
		} catch (NamingException ex) {
			throw new IllegalStateException("Failed to lookup " + name, ex);
		}
	}
}
