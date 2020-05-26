package node;

public class AgentCenter {

	public String alias;
	public String address;
	
	public AgentCenter(String alias, String address) {
		this.alias = alias;
		this.address = address;
	}
	
	public String getAlias() {
		return alias;
	}
	
	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
}
