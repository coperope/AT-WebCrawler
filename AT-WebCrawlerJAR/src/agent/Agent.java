package agent;

import java.io.IOException;
import java.io.Serializable;

import javax.ejb.Remote;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import message.ACLMessage;
import serverCommunications.CommunicationsRest;
import test.Ping;



public interface Agent extends Serializable{

	void init(AID id) throws IOException;
	
	void handleMessage(ACLMessage msg) throws IOException;

	AID getAid();
	
	void stop();
}
