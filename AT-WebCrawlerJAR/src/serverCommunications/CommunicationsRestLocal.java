package serverCommunications;

import java.util.List;

import agent.Agent;
import node.AgentCenter;


public interface CommunicationsRestLocal {
	//public void tellEveryone(HashMap<String,User> usersLoggedIn);

	public AgentCenter getHost();
	
	public void sendRunningAgentsToEveryone(List<Agent> runningAgents);
}
