package agent;

import java.util.List;

public interface AgentManager {

	public List<AgentType> getAvailableAgentClasses();

	public List<AID> getRunningAgents();

	public AID startServerAgent(AgentType agClass, String runtimeName);

	public void stopAgent(AID aid);

}
