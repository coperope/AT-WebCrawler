package message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;

import agent.AID;
import util.JSON;

public class ACLMessage implements Serializable{

	private static final long serialVersionUID = 1L;

	public Performative performative;
	public AID sender;
	public List<AID> receivers;

	public AID replyTo;

	public String content;
	public Serializable contentObj;
	
	public Map<String, Serializable> userArgs;
	public String language;
	public String encoding;
	public String ontology;

	public String protocol;
	public String conversationId;
	public String replyWith;
	public String inReplyTo;
	public long replyBy;
	public int opcode;
	
	public ACLMessage() {
		this(Performative.NOT_UNDERSTOOD);
	}

	public ACLMessage(Performative performative) {
		this.performative = performative;
		receivers = new ArrayList<>();
		userArgs = new HashMap<>();
	}
	
	public ACLMessage(String jsonString) throws Exception {
		System.out.println("ACLMessage JSON: " + jsonString);
		
		ACLMessage m = JSON.mapper.readValue(jsonString, ACLMessage.class);
		System.out.println("ACLMessage m: " + m);
		
		this.content = m.content;
		this.conversationId = m.conversationId;
		this.encoding = m.encoding;
		this.inReplyTo = m.inReplyTo;
		this.language = m.language;
		this.ontology = m.ontology;
		this.performative = m.performative;
		this.protocol = m.protocol;
		this.receivers = m.receivers;
		this.replyBy = m.replyBy;
		this.replyTo = m.replyTo;
		this.sender = m.sender;
		this.userArgs = m.userArgs;
	}
	
	public boolean canReplyTo() {
		return sender != null || replyTo != null;
	}
	
	public ACLMessage makeReply(Performative performative) {
		if (!canReplyTo())
			throw new IllegalArgumentException("There's no-one to receive the reply.");
		ACLMessage reply = new ACLMessage(performative);
		// receiver
		reply.receivers.add(replyTo != null ? replyTo : sender);
		// description of content
		reply.language = language;
		reply.ontology = ontology;
		reply.encoding = encoding;
		// control of conversation
		reply.protocol = protocol;
		reply.conversationId = conversationId;
		reply.inReplyTo = replyWith;
		return reply;
	}
	
	@Override
	public String toString() {
		try {
			return JSON.mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}
}
