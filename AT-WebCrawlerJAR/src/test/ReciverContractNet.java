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
public class ReciverContractNet extends BaseAgent {

	private static final long serialVersionUID = 1L;

	@EJB
	WSMessageCreator wsMessageCreator;

	@Override
	public void init(AID id) throws IOException {
		this.id = id;
		wsMessageCreator.log("ReciverContractNet agent created");
	}

	@Override
	public void handleMessage(ACLMessage msg) throws IOException {
		wsMessageCreator.log("ReciverContractNet agent handle message");
		wsMessageCreator.log(msg.performative + ": " + msg.content);

		switch (msg.performative) {
		case CALL_FOR_PROPOSAL:
			if (Math.random() > 0.4) {
				ACLMessage reply = msg.makeReply(Performative.PROPOSE);
				reply.ontology = "Yellow House";
				reply.content = "OK, price 15000$.";
				reply.sender = id;
				msm().post(reply);
			} else {
				ACLMessage reply = msg.makeReply(Performative.REFUSE);
				reply.content = "I don't have any more villagers.";
				msm().post(reply);
			}
			break;
		case REJECT_PROPOSAL:
			wsMessageCreator.log("REJECT_PROPOSAL");
			break;
		case ACCEPT_PROPOSAL:
			try {
				wsMessageCreator.log("Massacring two villagers... Please wait.");
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				ACLMessage reply = msg.makeReply(Performative.FAILURE);
				reply.content = "Something went wrong. Villager is an alcoholic. Kidney no good.";
				msm().post(reply);
			}

			if (Math.random() > 0.3) {
				ACLMessage reply = msg.makeReply(Performative.INFORM);
				reply.ontology = "Yellow House";
				reply.content = "Kidneys sold.";
				reply.sender = id;
				msm().post(reply);
			} else {
				ACLMessage reply = msg.makeReply(Performative.FAILURE);
				reply.content = "I don't have that, you will die.";
				msm().post(reply);
			}
			break;
		default:
			break;
		}

	}
}
