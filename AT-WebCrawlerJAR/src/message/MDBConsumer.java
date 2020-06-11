package message;

import java.io.IOException;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import agent.AID;
import agent.Agent;
import agent.AgentManagerBean;


@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/queue/mojQueue")
})
public class MDBConsumer implements MessageListener {

	@EJB
	private AgentManagerBean agm;

	@Override
	public void onMessage(Message msg) {
		try {
			processMessage(msg);
			
		} catch (JMSException | IOException ex) {
			System.out.println("Cannot process an incoming message.");
		}
	}

	private void processMessage(Message msg) throws JMSException, IOException {
		ACLMessage acl = (ACLMessage) ((ObjectMessage) msg).getObject();
		AID aid = getAid(msg, acl);
		deliverMessage(acl, aid);
		
	}
	
	private AID getAid(Message msg, ACLMessage acl) throws JMSException {
		int i = msg.getIntProperty("AIDIndex");
		return acl.receivers.get(i);
	}

	
	private void deliverMessage(ACLMessage msg, AID aid) throws IOException {
		Agent agent = agm.getAgent(aid);
		if (agent != null) {
			System.out.println("Odje agent obradjuje poruku");
			agent.handleMessage(msg);
		} else {
			System.out.println("No such agent: {}");
		}
	}
}
