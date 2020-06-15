package agent;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public interface AgentManager {

	public List<AgentType> getAvailableAgentClasses();

	public List<AID> getRunningAgents();

	public AID startServerAgent(AgentType agClass, String runtimeName) throws IOException;

	public void stopAgent(AID aid);
	
	public HashMap<AID, Agent> getAgents();

	public void setAgents(HashMap<AID, Agent> agents);
	
	public void putAgent(AID aid, Agent agent);
	
	public Agent getAgent(AID aid);
	
	public void removeAgent(AID aid);
}
