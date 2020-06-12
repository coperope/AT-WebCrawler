package test;

import java.io.IOException;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;

import agent.AID;
import agent.Agent;
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
			AID pongAid = new AID(msg.content, new AgentType(ObjectFactory.PROJECT_MODULE, Pong.class.getSimpleName()),
					host);
			ACLMessage msgToPong = new ACLMessage(Performative.REQUEST);
			msgToPong.replyTo = id;
			msgToPong.sender = id;
			msgToPong.receivers.add(pongAid);

			msm().post(msgToPong);
		} else if (msg.performative == Performative.INFORM) {
			ACLMessage msgFromPong = msg;
			wsMessageCreator.log("Ping-Pong counter: " + msgFromPong.content);
		}
	}
}
