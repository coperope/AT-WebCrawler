package serverCommunications;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import agent.AID;
import agent.Agent;
import agent.AgentManager;
import agent.AgentType;
import node.AgentCenter;
import test.Ping;
import util.JSON;

@Singleton
@Startup
@LocalBean
@AccessTimeout(value = 60, unit = TimeUnit.SECONDS)
public class Communications {
	
	private AgentCenter master = new AgentCenter("master","");
	private AgentCenter agentCenter = new AgentCenter("localHost2","79e00c5c6112.ngrok.io");
	private List<AgentCenter> connections = new ArrayList<AgentCenter>();
	
	@EJB
	private AgentManager agm;
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
			
			List<Object> runningAgents = rest.getRunningAgents();
			List<AgentType> types = agm.getAvailableAgentClasses();
			List<Agent> realRunningAgents = new ArrayList<Agent>();
			try {
				for (AgentType agentType : types) {
					for (Object agent : runningAgents) {
						try {
							System.out.println(agent.toString());
							ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
							String json = ow.writeValueAsString(agent);
							Agent a = (Agent) JSON.mapper.readValue(json, Class.forName("test." + agentType.getName()));
							realRunningAgents.add(a);
						}catch (Exception e) {
							System.out.println("E JBG STARI");
							System.out.println(Ping.class.getName());
							e.printStackTrace();
							continue;
						}
						/*
						 * if(Class.forName("test." + agentType.getName()).isInstance(agent)) {
						 * 
						 * realRunningAgents.add((Agent)Class.forName("test." +
						 * agentType.getName()).cast(agent)); }
						 */
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			HashMap<AID, Agent> tempAgents = new HashMap<AID, Agent>();
	    	for (Agent agent : realRunningAgents) {
				tempAgents.put(agent.getAid(), agent);
			}
	    	agm.setAgents(tempAgents);
			
			System.out.println("Connections: ");
			for (AgentCenter agentCenter : connections) {
				System.out.println(agentCenter.getAlias());
			}
			System.out.println("ZAVRSIO");
		}
	}
   

	  @Schedule(hour = "*", minute = "*",second = "*/45", info = "every ten minutes")
	  public void heartBeat() { 
		  System.out.println("Timer");
		  System.out.println("Checking health for: ");
		  ResteasyClient client = new ResteasyClientBuilder().build();
		  for (AgentCenter center : this.connections) {
			  ResteasyWebTarget rtarget = client.target("http://" + center.getAddress() + "/AT-WebCrawlerWAR/rest/server"); 
			  CommunicationsRest rest = rtarget.proxy(CommunicationsRest.class);
			  
			  try {
				 rest.getNode();
			  }catch (Exception e) {
				  try {
					  rest.getNode();
				  }catch (Exception e2) {
					  System.out.println("Node is null");
					  removeNodeTellEveryone(center);
				  }
			  }
			  System.out.println(center.getAlias());
			  System.out.println(center.getAddress());
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
			ResteasyWebTarget rtarget = client.target("http://" + connection.getAddress() + "/AT-WebCrawlerWAR/rest/server");
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
