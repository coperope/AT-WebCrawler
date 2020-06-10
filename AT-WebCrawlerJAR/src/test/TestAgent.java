package test;

import java.io.IOException;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;

import agent.AID;
import agent.Agent;
import agent.BaseAgent;
import message.ACLMessage;
import util.WSMessageCreator;

@Stateful
@Remote(Agent.class)
public class TestAgent extends BaseAgent{

	private static final long serialVersionUID = 1L;
	@EJB
	private WSMessageCreator wsMessageCreator;
	
	@Override
	public void handleMessage(ACLMessage msg) {
		System.out.println("Test");
		try {
			wsMessageCreator.sendLog("Test");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init(AID id) {
		// TODO Auto-generated method stub
		
	}
}
