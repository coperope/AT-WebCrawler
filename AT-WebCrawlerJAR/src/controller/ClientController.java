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
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import agent.AID;
import agent.AgentManager;
import agent.AgentType;
import message.ACLMessage;
import message.MessageManagerBean;
import message.Performative;

@Stateless
@Path("/client")
@LocalBean
@Remote(ClientControllerRemote.class)
public class ClientController implements ClientControllerRemote{
	
	@EJB
	AgentManager agm;
	
	@EJB
	MessageManagerBean msm;

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
	
	@PUT
	@Path("/agents/running/{type}/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public AID startAgentOfType(@PathParam("name") String name, @PathParam("type") String type ){
		AgentType at = new AgentType("AT-WebCrawlerEAR/AT-WebCrawlerJAR", type);
		return agm.startServerAgent(at, name);
	}

	@DELETE
	@Path("/agents/running")
	@Consumes(MediaType.APPLICATION_JSON)
	public void stopAgent(AID aid) {
		agm.stopAgent(aid);
	}
	
	@POST
	@Path("/messages")
	@Consumes(MediaType.APPLICATION_JSON)
	public void sendMessage(ACLMessage message) {


	}

	@GET
	@Path("/messages")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getPerformatives(){
		List<String> list = msm.getPerformatives();
		return list;
	}
	
}
