package agent;

import java.io.IOException;

import message.ACLMessage;
import message.MessageManager;
import node.AgentCenter;
import util.ObjectFactory;

public abstract class BaseAgent implements Agent{

	private static final long serialVersionUID = 1L;

	protected AID id;
	
	private MessageManager msm;
	
	@Override
	public void init(AID id) throws IOException {
		this.id = id;
	}

	@Override
	public void handleMessage(ACLMessage msg) throws IOException {
	}

	@Override
	public AID getAid() {
		return this.id;
	}

	@Override
	public void stop() {
	}
	
	protected MessageManager msm() {
		if (msm == null)
			msm = ObjectFactory.getMessageManager(null);
		return msm;
	}

	public MessageManager getMsm() {
		return msm;
	}

}
