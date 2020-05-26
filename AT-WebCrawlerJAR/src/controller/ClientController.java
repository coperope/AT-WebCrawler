package controller;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import agent.AgentManager;
import agent.AgentType;
import test.TestAgent;

@Stateless
@Path("/client")
@LocalBean
public class ClientController {
	
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
		//Testing purposes
		AgentType cls = new AgentType("AT-WebCrawlerEAR/AT-WebCrawlerJAR", "TestAgent");
		//agm.startServerAgent(cls, "test");
		//
		return agm.getAvailableAgentClasses();
	}
}
