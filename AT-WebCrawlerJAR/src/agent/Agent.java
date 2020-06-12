package agent;

import java.io.IOException;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import message.ACLMessage;
import test.pingpong.Ping;


@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(value = BaseAgent.class, name = "BaseAgent"),
	@JsonSubTypes.Type(value = Ping.class, name = "Ping")
})
public interface Agent extends Serializable{

	void init(AID id) throws IOException;
	
	void handleMessage(ACLMessage msg) throws IOException;

	AID getAid();
	
	void stop();
}
