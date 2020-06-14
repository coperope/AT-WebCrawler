package wc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import agent.AID;
import agent.Agent;
import agent.AgentManagerBean;
import agent.AgentType;
import agent.BaseAgent;
import message.ACLMessage;
import message.Performative;
import node.AgentCenter;
import serverCommunications.Communications;
import test.ReciverContractNet;
import util.JSON;
import util.ObjectFactory;
import util.WSMessageCreator;

@Stateless
@Remote(Agent.class)
public class Master extends BaseAgent {
	private static final long serialVersionUID = 1L;
	
	@EJB
	WSMessageCreator wsMessageCreator;
	@EJB
	Communications communications;
	@EJB
	AgentManagerBean agentManager;
	
	@Override
	public void init(AID id) throws IOException {
		this.id = id;
		wsMessageCreator.log("Master agent created");
	}
	
	@Override
	public void handleMessage(ACLMessage msg) throws IOException {
		switch (msg.performative) {

		case REQUEST:
			AgentCenter host = communications.getAgentCenter();
			List<AgentType> agentTypes = agentManager.getAvailableAgentClasses();
			AID aid = null;
			for (AgentType agentType : agentTypes) {
				if(agentType.getName().equals("Browser")) {
					aid = agentManager.startServerAgent(agentType, "Browser");
				}
			}
			if (aid != null) {
				ACLMessage qmsg = new ACLMessage(Performative.REQUEST);
				qmsg.receivers.add(aid);
				qmsg.content = "properties";
				qmsg.sender = id;
				msm().post(qmsg);
			}
			break;
		case AGREE:
			System.out.println("MASTER: " + msg.content);
			break;
		default:
			wsMessageCreator.log("Master: invalid performative");
			break;
		}

	}
	
	private ArrayList<String> greenAcresRegions() {
		ArrayList<String> regions = new ArrayList<String>();
		regions.add("https://www.green-acres.fr/property-for-sale/paris");
		regions.add("https://www.green-acres.fr/property-for-sale/hauts-de-seine");
		regions.add("https://www.green-acres.fr/property-for-sale/val-de-marne");
		regions.add("https://www.green-acres.fr/property-for-sale/seine-saint-denis");
		regions.add("https://www.green-acres.fr/property-for-sale/seine-et-marne");
		regions.add("https://www.green-acres.fr/property-for-sale/val-d-oise");
		regions.add("https://www.green-acres.fr/property-for-sale/yvelines");
		regions.add("https://www.green-acres.fr/property-for-sale/essonne");
		return regions;
	}
}
