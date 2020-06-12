package test;

import java.io.IOException;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;

import agent.AID;
import agent.Agent;
import agent.BaseAgent;
import message.ACLMessage;
import message.Performative;
import util.WSMessageCreator;

@Stateful
@Remote(Agent.class)
public class Pong extends BaseAgent {

	private static final long serialVersionUID = 1L;

	private int counter;

	@EJB
	WSMessageCreator wsMessageCreator;

	@Override
	public void init(AID id) throws IOException{
		this.id = id;
		this.counter = 0;
		wsMessageCreator.log("Pong agent created");
	}

	@Override
	public void handleMessage(ACLMessage msg) throws IOException {
		wsMessageCreator.log("Pong agent handle message");
		if (msg.performative == Performative.REQUEST) {
			ACLMessage reply = msg.makeReply(Performative.INFORM);
			reply.sender = id;
			reply.content = String.valueOf(++counter);
			msm().post(reply);
		}
	}
}
