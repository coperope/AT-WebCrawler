package agent;

import java.io.Serializable;

import node.AgentCenter;

public class AID implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private AgentType type;
	private AgentCenter host;
	
	public AID(String name, AgentType type, AgentCenter host) {
		super();
		this.name = name;
		this.type = type;
		this.host = host;
	}

	public String getName() {
		return name;
	}

	public AgentType getType() {
		return type;
	}

	public AgentCenter getHost() {
		return host;
	}
}
