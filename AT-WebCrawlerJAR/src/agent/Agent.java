package agent;

import java.io.Serializable;

import message.ACLMessage;

public interface Agent extends Serializable{

	void init(AID id);
	
	void handleMessage(ACLMessage msg);

	AID getAid();
	
	void stop();
}
