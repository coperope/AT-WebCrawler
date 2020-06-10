package ws;

import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import agent.AID;
import agent.Agent;
import agent.AgentType;


public class WSMessage {

	@Enumerated(EnumType.STRING)
	private WSType type;

	private String log;

	private List<AID> activeAgents;

	private List<AgentType> agentsTypes;

	public WSMessage() {
		super();
	}
	
	public WSMessage(WSType type) {
		super();
		this.type = type;
	}

	public WSMessage(WSType type, String log, List<AID> activeAgents, List<AgentType> agentsTypes) {
		super();
		this.type = type;
		this.log = log;
		this.activeAgents = activeAgents;
		this.agentsTypes = agentsTypes;
	}

	public WSType getType() {
		return type;
	}

	public void setType(WSType type) {
		this.type = type;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public List<AID> getActiveAgents() {
		return activeAgents;
	}

	public void setActiveAgents(List<AID> activeAgents) {
		this.activeAgents = activeAgents;
	}

	public List<AgentType> getAgentsTypes() {
		return agentsTypes;
	}

	public void setAgentsTypes(List<AgentType> agentsTypes) {
		this.agentsTypes = agentsTypes;
	}

	
}
