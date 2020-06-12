package serverCommunications;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import agent.AID;
import agent.Agent;
import agent.AgentManager;
import agent.AgentType;
import node.AgentCenter;
import util.WSMessageCreator;


/**
 * Session Bean implementation class ConnectionsBean
 */
@Stateless
@LocalBean
@Path("/server")
@Remote(CommunicationsRest.class)
public class ConnectionsBean implements CommunicationsRest, CommunicationsRestLocal {

	@EJB
	Communications communications;
	
	@EJB
	WSMessageCreator ws;
	
	@EJB
	AgentManager agm;
	/*
	 * @EJB WSEndPoint ws;
	 */
	
    /**
     * Default constructor. 
     */
    public ConnectionsBean() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public List<AgentCenter> newConnection(AgentCenter connection){
    	System.out.println("usao");
    	ResteasyClient client = new ResteasyClientBuilder()
                .build();
    	for (AgentCenter connections : communications.getConnections()) {
    		ResteasyWebTarget rtarget = client.target("http://" + connections.getAddress() + "/AT-WebCrawlerWAR/rest/server");
    		CommunicationsRest rest = rtarget.proxy(CommunicationsRest.class);
    		rest.oneNode(connection);
		}
    	ResteasyWebTarget rtarget = client.target("http://" + connection + "/AT-WebCrawlerWAR/rest/server");
    	//rest = rtarget.proxy(comunicationsRest.class);
		//boolean test = rest.allUsers(this.usrmsg.getUsersLoggedin());
		//System.out.println(connection);
    	List<AgentCenter> retVal = new ArrayList<AgentCenter>();
    	for (AgentCenter agentCenter : communications.getConnections()) {
			retVal.add(agentCenter);
		}
    	communications.getConnections().add(connection);
    	return retVal;
    }
    
    @Override
    public boolean oneNode(AgentCenter connection){
    	for (AgentCenter connections : communications.getConnections()) {
			if(connections.getAddress().equals(connection.getAddress())) {
				return true;
			}
		}
    	communications.getConnections().add(connection);
    	return true;
    }
    
    @Override
    public boolean allNodes(List<AgentCenter> connection){
    	communications.setConnections(connection);
    	return true;
    }
    
    @Override
	public boolean deleteNode(@PathParam("alias") String alias) {
    	for (AgentCenter agentCenter : this.communications.getConnections()) {
			if (agentCenter.getAlias().equals(alias)) {
				this.communications.getConnections().remove(agentCenter);
				return true;
			}
		}
    	return false;
	}
	
    @Override
    public boolean getNode() {
    	return true;
    }
    
    @Override
    public List<AgentType> allAgentTypes(){
    	return agm.getAvailableAgentClasses();
    }
    
    
    @Override
    public List<AgentType> sendAgentTypes(List<AgentType> agentTypes){
    	return new ArrayList<AgentType>();
    }
    
    
	/*
	 * @Override public void tellEveryone(HashMap<String,User> usersLoggedIn) {
	 * ResteasyClient client = new ResteasyClientBuilder() .build(); for (String
	 * string : comunications.getConnection()) { ResteasyWebTarget rtarget =
	 * client.target("http://" + string + "/WAR2020/rest/server"); comunicationsRest
	 * rest = rtarget.proxy(comunicationsRest.class);
	 * rest.allUsers(usrmsg.getUsersLoggedin()); } }
	 */
    
    @Override
    public AgentCenter getHost() {
    	return communications.getAgentCenter();
    }
    
    @Override
    public boolean sendRunningAgents(HashMap<AID, Agent> agents){
    	agm.setAgents(agents);
    	try {
			ws.sendActiveAgents(agm.getRunningAgents());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return true;
    }
    
    @Override
    public void sendRunningAgentsToEveryone(HashMap<AID,Agent> runningAgents) {
    	ResteasyClient client = new ResteasyClientBuilder()
                .build();
    	for (AgentCenter center : communications.getConnections()) {
    		ResteasyWebTarget rtarget = client.target("http://" + center + "/WAR2020/rest/server");
    		CommunicationsRest rest = rtarget.proxy(CommunicationsRest.class);
    		rest.sendRunningAgents(runningAgents);
		}
    }
    
    @Override
    public HashMap<AID, Agent> getRunningAgents(){
    	return agm.getAgents();
    }
}
