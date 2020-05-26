package agent;

import java.util.HashMap;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Stateless;

@Stateless
@Remote(AgentManager.class)
@LocalBean
public class AgentManagerBean implements AgentManager {

	private HashMap<AID, Agent> agents;
	
	@Override
	public List<AgentType> getAvailableAgentClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AID> getRunningAgents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AID startServerAgent(AgentType agClass, String runtimeName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void stopAgent(AID aid) {
		// TODO Auto-generated method stub
		
	}

}
