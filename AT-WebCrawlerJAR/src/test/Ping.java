package test;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
import util.ObjectFactory;
import util.WSMessageCreator;

@Stateful
@Remote(Agent.class)
public class Ping extends BaseAgent {

	private static final long serialVersionUID = 1L;

	@EJB
	WSMessageCreator wsMessageCreator;

	@Override
	public void init(AID id) throws IOException {
		wsMessageCreator.log("Ping agent created");
	}

	@Override
	public void handleMessage(ACLMessage msg) throws IOException {
		wsMessageCreator.log("Ping agent handle message");
		if (msg.performative == Performative.REQUEST) {
			AID pongAid = new AID(msg.content, new AgentType(ObjectFactory.PROJECT_MODULE, Pong.class.getSimpleName()),
					null);
			ACLMessage msgToPong = new ACLMessage(Performative.REQUEST);
			msgToPong.sender = id;
			msgToPong.receivers.add(pongAid);

			msm().post(msgToPong);
		} else if (msg.performative == Performative.INFORM) {
			ACLMessage msgFromPong = msg;
			wsMessageCreator.log("Ping-Pong: " + msgFromPong.content);
		}
	}
}
