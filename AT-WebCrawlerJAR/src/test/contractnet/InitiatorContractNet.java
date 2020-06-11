package test.contractnet;

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
import util.ObjectFactory;
import util.WSMessageCreator;

@Stateful
@Remote(Agent.class)
public class InitiatorContractNet extends BaseAgent {

	private static final long serialVersionUID = 1L;

	@EJB
	WSMessageCreator wsMessageCreator;

	@Override
	public void init(AID id) throws IOException {
		this.id = id;
		wsMessageCreator.log("InitiatorContractNet agent created");
	}

	@Override
	public void handleMessage(ACLMessage msg) throws IOException {
		wsMessageCreator.log("InitiatorContractNet agent handle message");
		switch (msg.performative) {
		case REQUEST:

			wsMessageCreator.log("Initiator received request to start the job");

			AgentCenter host = new AgentCenter("localhost", "test");
			AID reciver = new AID(msg.content,
					new AgentType(ObjectFactory.PROJECT_MODULE, ReciverContractNet.class.getSimpleName()), host);
			ACLMessage qmsg = new ACLMessage(Performative.CALL_FOR_PROPOSAL);
			qmsg.receivers.add(reciver);
			qmsg.ontology = "Yellow House";
			qmsg.content = "Sell Kidney, qty 2.";
			qmsg.sender = id;
			msm().post(qmsg);
			break;
		case PROPOSE:
			if (Math.random() > 0.5) {
				ACLMessage reply = msg.makeReply(Performative.ACCEPT_PROPOSAL);
				reply.ontology = "Yellow House";
				reply.content = "Proposal accepted do your thing, but gently.";
				reply.sender = id;
				msm().post(reply);
			} else {
				ACLMessage reply = msg.makeReply(Performative.REJECT_PROPOSAL);
				reply.content = "I reject your fucking proposal. You cheap bastard.";
				msm().post(reply);
			}
			break;
		case REFUSE:
			wsMessageCreator.log("REFUSE");
			break;
		case INFORM:
			wsMessageCreator.log("INFORM");
			break;
		case FAILURE:
			wsMessageCreator.log("FAILURE");
			break;
		default:
			break;
		}

	}
}
