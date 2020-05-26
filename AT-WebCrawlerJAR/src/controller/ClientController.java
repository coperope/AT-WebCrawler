package controller;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import agent.AID;
import agent.AgentManager;
import agent.AgentType;
import message.Performative;

@Stateless
@Path("/client")
@LocalBean
@Remote(ClientControllerRemote.class)
public class ClientController implements ClientControllerRemote{
	
	@EJB
	AgentManager agm;
	
	@GET
	@Path("/test")
	@Produces(MediaType.TEXT_PLAIN)
	public String test() {
		return "Test";
	}
	
	@GET
	@Path("/agents/classes")
	@Produces(MediaType.APPLICATION_JSON)
	public List<AgentType> getAvailableAgentClasses(){
		return agm.getAvailableAgentClasses();
	}
	
	@GET
	@Path("/agents/running")
	@Produces(MediaType.APPLICATION_JSON)
	public List<AID> getRunningAgents(){
		return agm.getRunningAgents();
	}
	
	@DELETE
	@Path("/agents/running")
	@Consumes(MediaType.APPLICATION_JSON)
	public void stopAgent(AID aid) {
		agm.stopAgent(aid);
	}
	
	@GET
	@Path("/messages")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getPerformatives(){
		final Performative[] arr = Performative.values();
		List<String> list = new ArrayList<>(arr.length);
		for (Performative p : arr)
			list.add(p.toString());
		return list;
	}
	
}
