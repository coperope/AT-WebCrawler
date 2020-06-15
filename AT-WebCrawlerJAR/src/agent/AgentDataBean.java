package agent;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.ejb.AccessTimeout;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

@Singleton
@AccessTimeout(value = 60, unit = TimeUnit.SECONDS)
public class AgentDataBean {
	private HashMap<AID, Agent> agents;
	
	@PostConstruct
	public void postConstruct() {
		agents = new HashMap<AID, Agent>();
	}
	
	public Agent getAgent(AID aid) {
		Agent a = null;
		a = agents.get(aid);
		return a;
	}

	@Lock(LockType.READ)
	@AccessTimeout(value=50000)
	public HashMap<AID, Agent> getAgents() {
		return agents;
	}
	@Lock(LockType.WRITE)
	@AccessTimeout(value=50000)
	public void setAgents(HashMap<AID, Agent> agents) {
		this.agents = agents;
	}

	public void putAgent(AID aid, Agent agent) {
		agents.put(aid, agent);
	}

	public void removeAgent(AID aid) {
		this.agents.remove(aid);
	}
}
