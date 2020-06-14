package agent;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import node.AgentCenter;

public class AID implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private AgentType type;
	private AgentCenter host;
	private String str;
	
	public AID() {
		this.str = "";
	}
	
//	public AID(String name, AgentType type, AgentCenter host) {
//		super();
//		this.name = name;
//		this.type = type;
//		this.host = host;
//		this.str = name + "@" + host.alias;
//	}
	
	@JsonCreator
	public AID(
			@JsonProperty("name") String name,
			@JsonProperty("type") AgentType type,
			@JsonProperty("host") AgentCenter host
			) {
		try {
			this.name = name;
			this.type = type;
			this.host = host != null ? host : null;
			this.str = name + "@" + host.alias;
		} catch (Exception ex) {
			throw new IllegalArgumentException(ex);
		}
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
	
	
	
	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	@Override
	public int hashCode() {
		return str.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		this.str = this.name + "@" + this.host.alias;
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AID other = (AID) obj;
		other.str = other.name + "@" + other.host.alias;
		return str.equals(other.str);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "AID[name="+ this.name +" ,type="+this.type+" ,host="+ this.host + " ,str="+str+"]";
	}
	
	
}
