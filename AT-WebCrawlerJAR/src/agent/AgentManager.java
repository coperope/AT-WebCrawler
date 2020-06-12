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
}
