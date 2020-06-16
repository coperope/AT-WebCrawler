package controller;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

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
import serverCommunications.Communications;
import wc.DataRequestDTO;

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
		msm.post(message);
	}

	@GET
	@Path("/messages")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getPerformatives() {
		List<String> list = msm.getPerformatives();
		return list;
	}
	
	@POST
	@Path("/properties")
	@Consumes(MediaType.APPLICATION_JSON)
	public void sendMessage(DataRequestDTO request) {
		List<AgentType> agentTypes = agm.getAvailableAgentClasses();
		AID master = null;
		for (AgentType agentType : agentTypes) {
			if (agentType.getName().equals("Master")) {
				try {
					master = agm.startServerAgent(agentType, "Master-"+ UUID.randomUUID());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (master == null) {
			System.out.println("Internal server error");
			return;
		}
		String content = "";
		for (int i = 0; i < request.getContent().size(); i++) {
			content += request.getContent().get(i);
			if (i != request.getContent().size() - 1) {
				content += " ";
			}
		}
		ACLMessage qmsg = new ACLMessage(Performative.REQUEST);
		qmsg.receivers.add(master);
		qmsg.content = content;
		qmsg.ontology = request.getOntology();
		msm.post(qmsg);
	}

}
