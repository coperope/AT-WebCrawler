package controller;

import java.io.IOException;
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
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import agent.AID;
import agent.AgentManager;
import agent.AgentType;
import message.ACLMessage;
import message.MessageManager;
import message.MessageManagerBean;
import message.Performative;
import node.AgentCenter;
import serverCommunications.Communications;
import serverCommunications.CommunicationsRest;
import util.ObjectFactory;

@Stateless
@Path("/client")
@LocalBean
@Remote(ClientControllerRemote.class)
public class ClientController implements ClientControllerRemote {

	@EJB
	AgentManager agm;

	@EJB
	MessageManagerBean msm;

	@EJB
	private Communications communications;

	@GET
	@Path("/test")
	@Produces(MediaType.TEXT_PLAIN)
	public String test() {
		return "Test";
	}

	@GET
	@Path("/agents/classes")
	@Produces(MediaType.APPLICATION_JSON)
	public List<AgentType> getAvailableAgentClasses() {
		return agm.getAvailableAgentClasses();
	}

	@GET
	@Path("/agents/running")
	@Produces(MediaType.APPLICATION_JSON)
	public List<AID> getRunningAgents() {
		return agm.getRunningAgents();
	}

	@PUT
	@Path("/agents/running/{type}/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public AID startAgentOfType(@PathParam("name") String name, @PathParam("type") String type) throws IOException {
		List<AgentType> agentTypes = agm.getAvailableAgentClasses();
		for (AgentType agentType : agentTypes) {
			if (agentType.getName().equals(type)) {
				return agm.startServerAgent(agentType, name);
			}
		}
		return new AID();
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
		System.out.println("Odje saljem poruku");
		// MessageManagerBean messsageMenager;
		for (AID aid : message.receivers) {
			AgentCenter host = (aid.getHost().getAddress().equals(communications.getAgentCenter().getAddress())) ? null
					: aid.getHost();
			if (host == null) {
				MessageManager manager = ObjectFactory.getMessageManager(host);
				manager.post(message);
			} else {
				ResteasyClient client = new ResteasyClientBuilder().build();
				ResteasyWebTarget rtarget = client
						.target("http://" + host.getAddress() + "/AT-WebCrawlerWAR/rest/server/messages");
				rtarget.request(MediaType.APPLICATION_JSON)
						.post(Entity.entity(message, MediaType.APPLICATION_JSON));
			}

		}
	}

	@GET
	@Path("/messages")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getPerformatives() {
		List<String> list = msm.getPerformatives();
		return list;
	}

}
