package agent;

import message.ACLMessage;

public abstract class BaseAgent implements Agent{

	private static final long serialVersionUID = 1L;

	protected AID id;
	
	@Override
	public void init(AID id) {
		this.id = id;
	}

	@Override
	public void handleMessage(ACLMessage msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AID getAid() {
		return this.id;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

}
