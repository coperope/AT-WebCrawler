package util;

import java.io.IOException;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.fasterxml.jackson.core.JsonProcessingException;

import agent.AID;
import agent.AgentType;
import wc.Property;
import wc.StatisticDTO;
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

	public void sendLog(String log) throws IOException {
		WSMessage wsMessage = new WSMessage(WSType.Log);
		wsMessage.setLog(log);
		String json = JSON.mapper.writeValueAsString(wsMessage);
		if(ws == null) {
			System.out.println("WS JE NULL");
		}else {
			ws.sendMessage(json);
		}
	}

	public void sendPropertiesTop100(List<Property> sortedProperties) throws IOException {
		WSMessage wsMessage = new WSMessage(WSType.Top100);
		wsMessage.setTop100(sortedProperties);
		String json = JSON.mapper.writeValueAsString(wsMessage);
		if(ws == null) {
			System.out.println("WS JE NULL");
		}else {
			ws.sendMessage(json);
		}
	}

	public void sendPropertiesTopLocations(List<Property> topPropertiesOnLocation) throws IOException {
		WSMessage wsMessage = new WSMessage(WSType.TopLocations);
		wsMessage.setTopLocations(topPropertiesOnLocation);
		String json = JSON.mapper.writeValueAsString(wsMessage);
		if(ws == null) {
			System.out.println("WS JE NULL");
		}else {
			ws.sendMessage(json);
		}
	}

	public void sendStatistic(StatisticDTO initStatistic) throws IOException {
		WSMessage wsMessage = new WSMessage(WSType.Statistic);
		wsMessage.setStatistic(initStatistic);
		String json = JSON.mapper.writeValueAsString(wsMessage);
		if(ws == null) {
			System.out.println("WS JE NULL");
		}else {
			ws.sendMessage(json);
		}
	}
}
