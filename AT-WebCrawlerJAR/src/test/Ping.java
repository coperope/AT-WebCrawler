package test;

import java.io.IOException;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;

import agent.AID;
import agent.Agent;
import agent.AgentManagerBean;
import agent.AgentType;
import agent.BaseAgent;
import message.ACLMessage;
import message.Performative;
import node.AgentCenter;
import serverCommunications.Communications;
import util.ObjectFactory;
import util.WSMessageCreator;

@Stateful
@Remote(Agent.class)
public class Ping extends BaseAgent {

	private static final long serialVersionUID = 1L;

	@EJB
	WSMessageCreator wsMessageCreator;

	@EJB
	Communications communications;
	
	@EJB
	AgentManagerBean agm;
	
	@Override
	public void init(AID id) throws IOException {
		this.id = id;
		wsMessageCreator.log("Ping agent created");
	}

	@Override
	public void handleMessage(ACLMessage msg) throws IOException {
		wsMessageCreator.log("Ping agent handle message");
		if (msg.performative == Performative.REQUEST) {
			AgentCenter host = communications.getAgentCenter();
			AID pongAid = null;
			for (AID aid : agm.getAgents().keySet()) {
				if (aid.getName().equals(msg.content)) {
					pongAid = aid;
					break;
				}
			}
			if(pongAid != null) {
				ACLMessage msgToPong = new ACLMessage(Performative.REQUEST);
				msgToPong.replyTo = id;
				msgToPong.sender = id;
				msgToPong.receivers.add(pongAid);
				msm().post(msgToPong);
			} else {
				System.out.println("Could not find pong agent.");
			}
			

			
		} else if (msg.performative == Performative.INFORM) {
			ACLMessage msgFromPong = msg;
			wsMessageCreator.log("Ping-Pong counter: " + msgFromPong.content);
		}
	}
}
