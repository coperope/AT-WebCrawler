package util;

import java.io.IOException;
import java.util.List;

import javax.ejb.EJB;

import agent.AID;
import agent.Agent;
import agent.AgentType;
import ws.WSEndpoint;
import ws.WSMessage;
import ws.WSType;

public class WSMessageCreator {

	@EJB
	private static WSEndpoint ws;

	public static void log(String message) throws IOException {

		System.out.println(message);

		WSMessage wsMessage = new WSMessage(WSType.Log);
		wsMessage.setLog(message);
		String json = JSON.mapper.writeValueAsString(wsMessage);
		ws.sendMessage(json);
	}

	public static void sendAgentsTypes(List<AgentType> agentsTypes) throws IOException {

		WSMessage wsMessage = new WSMessage(WSType.AgentsTypes);
		wsMessage.setAgentsTypes(agentsTypes);
		String json = JSON.mapper.writeValueAsString(wsMessage);
		ws.sendMessage(json);
	}

	public static void sendActiveAgents(List<AID> activeAgents) throws IOException {

		WSMessage wsMessage = new WSMessage(WSType.ActiveAgents);
		wsMessage.setActiveAgents(activeAgents);
		String json = JSON.mapper.writeValueAsString(wsMessage);
		ws.sendMessage(json);
	}
}
