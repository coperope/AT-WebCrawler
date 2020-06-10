package util;

import java.io.IOException;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import agent.AID;
import agent.AgentType;
import ws.WSEndpoint;
import ws.WSMessage;
import ws.WSType;

@Stateless
public class WSMessageCreator {

	@EJB
	private WSEndpoint ws;

	public void log(String message) throws IOException {

		System.out.println(message);

		WSMessage wsMessage = new WSMessage(WSType.Log);
		wsMessage.setLog(message);
		String json = JSON.mapper.writeValueAsString(wsMessage);
		ws.sendMessage(json);
	}

	public void sendAgentsTypes(List<AgentType> agentsTypes) throws IOException {

		WSMessage wsMessage = new WSMessage(WSType.AgentsTypes);
		wsMessage.setAgentsTypes(agentsTypes);
		String json = JSON.mapper.writeValueAsString(wsMessage);
		ws.sendMessage(json);
	}

	public void sendActiveAgents(List<AID> activeAgents) throws IOException {

		WSMessage wsMessage = new WSMessage(WSType.ActiveAgents);
		wsMessage.setActiveAgents(activeAgents);
		String json = JSON.mapper.writeValueAsString(wsMessage);
		if(ws == null) {
			System.out.println("WS JE NULL");
		}else {
			ws.sendMessage(json);
		}
		
	}
}
