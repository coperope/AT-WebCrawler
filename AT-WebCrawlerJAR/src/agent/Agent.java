package agent;

import java.io.IOException;
import java.io.Serializable;

import message.ACLMessage;

public interface Agent extends Serializable{

	void init(AID id) throws IOException;
	
	void handleMessage(ACLMessage msg) throws IOException;

	AID getAid();
	
	void stop();
}
