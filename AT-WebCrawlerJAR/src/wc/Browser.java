package wc;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;

import com.fasterxml.jackson.core.type.TypeReference;

import agent.AID;
import agent.Agent;
import agent.AgentManagerBean;
import agent.BaseAgent;
import message.ACLMessage;
import message.Performative;
import util.JSON;
import util.WSMessageCreator;

@Stateful
@Remote(Agent.class)
public class Browser extends BaseAgent {
	private static final long serialVersionUID = 1L;

	@EJB
	WSMessageCreator wsMessageCreator;
	@EJB
	AgentManagerBean agentManager;

	@Override
	public void init(AID id) throws IOException {
		this.id = id;
		wsMessageCreator.log("Browser agent created");
	}

	@Override
	public void handleMessage(ACLMessage msg) throws IOException {
		switch (msg.performative) {

		case REQUEST:
			String path = msg.content;
			ArrayList<Property> properties = new ArrayList<Property>();
			try {
				properties = loadPropertiesFromFolder(path);
				String propertiesJsonString = JSON.mapper.writeValueAsString(properties);

				if (properties.size() != 0) {
					ACLMessage reply = msg.makeReply(Performative.CONFIRM);
					reply.content = propertiesJsonString;
					reply.sender = id;
					reply.ontology = path;
					agentManager.stopAgent(this.id);
					msm().post(reply);
				} else {
					ACLMessage reply = msg.makeReply(Performative.DISCONFIRM);
					reply.content = this.getAid().getHost().getAddress();
					reply.sender = id;
					reply.ontology = path;
					agentManager.stopAgent(this.id);
					msm().post(reply);
				}
			} catch (IOException e) {
				ACLMessage reply = msg.makeReply(Performative.DISCONFIRM);
				reply.content = this.getAid().getHost().getAddress();
				reply.sender = id;
				reply.ontology = path;
				agentManager.stopAgent(this.id);
				msm().post(reply);
			}
			
			break;
		default:
			wsMessageCreator.log("Browser: invalid performative");
			agentManager.stopAgent(this.id);
			break;
		}

	}

	private ArrayList<Property> loadPropertiesFromFolder(String path) throws IOException {
		if (path.contains(".json")) {
			String content = readFile(path, StandardCharsets.UTF_8);
			return (ArrayList<Property>) JSON.mapper.readValue(content, new TypeReference<List<Property>>() {
			});
		} else {
			ArrayList<Property> properties = new ArrayList<Property>();

			File folder = new File(path);
			File[] listOfFiles = folder.listFiles();

			for (int i = 0; i < listOfFiles.length; i++) {
				properties.addAll(loadPropertiesFromFolder(path + "/" + listOfFiles[i].getName()));
			}
			return properties;
		}
	}

	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
}
