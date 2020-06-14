/**
 * Licensed to the Apache Software Foundation (ASF) under one 
 * or more contributor license agreements. See the NOTICE file 
 * distributed with this work for additional information regarding 
 * copyright ownership. The ASF licenses this file to you under 
 * the Apache License, Version 2.0 (the "License"); you may not 
 * use this file except in compliance with the License. You may 
 * obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. 
 * 
 * See the License for the specific language governing permissions 
 * and limitations under the License.
 */

package message;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import agent.AID;
import node.AgentCenter;
import serverCommunications.Communications;

@Stateless
@Remote(MessageManager.class)
@LocalBean
public class MessageManagerBean implements MessageManager {

	@EJB
	private JMSFactory factory;

	@EJB
	Communications communications;

	private Session session;
	private MessageProducer defaultProducer;

	@Resource
	TimerService timerService;

	@PostConstruct
	public void postConstruct() {
		session = factory.getSession();
		defaultProducer = factory.getProducer(session);
	}

	@PreDestroy
	public void preDestroy() {
		try {
			session.close();
		} catch (JMSException e) {
		}
	}

	@Override
	public List<String> getPerformatives() {
		final Performative[] arr = Performative.values();
		List<String> list = new ArrayList<>(arr.length);
		for (Performative p : arr)
			list.add(p.toString());
		return list;
	}

	@Override
	public void post(ACLMessage msg) {
		post(msg, 0L);
	}

	@Override
	public void post(ACLMessage msg, long delayMillisec) {
		// TODO : Check if the agent/subscriber exists
		// http://hornetq.sourceforge.net/docs/hornetq-2.0.0.BETA5/user-manual/en/html/management.html#d0e5742
		for (int i = 0; i < msg.receivers.size(); i++) {
			AgentCenter host = (msg.receivers.get(i).getHost().getAddress()
					.equals(communications.getAgentCenter().getAddress())) ? null : msg.receivers.get(i).getHost();
			if (msg.receivers.get(i) == null) {
				throw new IllegalArgumentException("AID cannot be null.");
			} else {
				if (host == null) {
					postToReceiver(msg, i, delayMillisec);
				} else {
					ResteasyClient client = new ResteasyClientBuilder().build();
					ResteasyWebTarget rtarget = client
							.target("http://" + host.getAddress() + "/AT-WebCrawlerWAR/rest/client/messages");
					rtarget.request(MediaType.APPLICATION_JSON).post(Entity.entity(msg, MediaType.APPLICATION_JSON));
				}
			}
		}
	}

	@Override
	public String ping() {
		return "Pong";
	}

	private void postToReceiver(ACLMessage msg, int index, long delayMillisec) {
		AID aid = msg.receivers.get(index);
		try {
			ObjectMessage jmsMsg = session.createObjectMessage(msg);
			setupJmsMsg(jmsMsg, aid, index, delayMillisec);
			if (delayMillisec == 0) {
				defaultProducer.send(jmsMsg);
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	private void setupJmsMsg(ObjectMessage jmsMsg, AID aid, int index, long delayMillisec) throws JMSException {
		// TODO See message grouping in a cluster
		// http://docs.jboss.org/hornetq/2.2.5.Final/user-manual/en/html/message-grouping.html
		jmsMsg.setStringProperty("JMSXGroupID", aid.getStr());
		jmsMsg.setIntProperty("AIDIndex", index);
		jmsMsg.setStringProperty("_HQ_DUPL_ID", UUID.randomUUID().toString());
	}
}
