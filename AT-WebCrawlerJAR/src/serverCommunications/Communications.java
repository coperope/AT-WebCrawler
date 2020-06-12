package serverCommunications;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import node.AgentCenter;


@Singleton
@Startup
@LocalBean
public class Communications {
	
	private AgentCenter master = new AgentCenter("master","");
	private AgentCenter agentCenter = new AgentCenter("localHost1","");
	private List<AgentCenter> connections = new ArrayList<AgentCenter>();
    /**
     * Default constructor. 
     */
    @PostConstruct
	private void init() {
    	System.out.println("Here");
    	if (master != null && !master.getAddress().equals("")) {
    		
			ResteasyClient client = new ResteasyClientBuilder().build();
			ResteasyWebTarget rtarget = client.target("http://" + master + "/AT-WebCrawlerWAR/rest/server");
			CommunicationsRest rest = rtarget.proxy(CommunicationsRest.class);
			this.connections = rest.newConnection(this.getAgentCenter());
			this.connections.remove(this.getAgentCenter());
			this.connections.add(this.master);
			
			System.out.println("ZAVRSIO");
		}
    }
   

    
    @Schedule(hour = "*", minute = "*",second = "*/45", info = "every ten minutes")
    public void heartBeat() {
    	System.out.println("Timer");
    	ResteasyClient client = new ResteasyClientBuilder()
                .build();
    	for (AgentCenter center : this.connections) {
    		ResteasyWebTarget rtarget = client.target("http://" + center.getAddress() + "/AT-WebCrawlerWAR/rest/server");
    		CommunicationsRest rest = rtarget.proxy(CommunicationsRest.class);
    		AgentCenter node = rest.getNode();
    		if(node == null) {
    			AgentCenter node1 = rest.getNode();
    			if (node1 == null) {
    				removeNodeTellEveryone(center);
    			}
    		}
    		
		}
    }
	public void removeNodeTellEveryone(AgentCenter connection) {
		ResteasyClient client = new ResteasyClientBuilder()
                .build();
    	for (AgentCenter center : this.connections) {
    		if(center.getAddress().equals(connection.getAddress())) {
    			continue;
    		}
    		ResteasyWebTarget rtarget = client.target("http://" + center.getAddress() + "/AT-WebCrawlerWAR/rest/server");
    		CommunicationsRest rest = rtarget.proxy(CommunicationsRest.class);
    		rest.deleteNode(connection.getAlias());
    	}
    	this.connections.remove(connection);
	}
	@PreDestroy
	private void destroy() {
		ResteasyClient client = new ResteasyClientBuilder().build();
		
		for (AgentCenter connection : this.connections) {
			ResteasyWebTarget rtarget = client.target("http://" + connection.getAddress() + "/AT-WebCrawlerWAR/connection");
			CommunicationsRest rest = rtarget.proxy(CommunicationsRest.class);
			rest.deleteNode(agentCenter.getAlias());
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
