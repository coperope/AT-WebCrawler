package serverCommunications;

import java.util.List;
import java.util.Set;

import agent.AID;
import agent.Agent;
import node.AgentCenter;


public interface CommunicationsRestLocal {
	//public void tellEveryone(HashMap<String,User> usersLoggedIn);

	public AgentCenter getHost();
	
	public void sendRunningAgentsToEveryone(Set<AID> runningAgents);
	
	public void sendNewAgentToEveryone(AID aid);
	
	public void sendRemovedAgentToEveryone(AID aid);
}
