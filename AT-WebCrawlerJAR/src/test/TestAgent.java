package test;

import javax.ejb.Remote;
import javax.ejb.Stateful;

import agent.AID;
import agent.Agent;
import agent.BaseAgent;
import message.ACLMessage;

@Stateful
@Remote(Agent.class)
public class TestAgent extends BaseAgent{

	private static final long serialVersionUID = 1L;

	@Override
	public void handleMessage(ACLMessage msg) {
		System.out.println("Test");
	}

	@Override
	public void init(AID id) {
		// TODO Auto-generated method stub
		
	}
}
