package ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

import util.JSON;

@Singleton
@LocalBean
@ServerEndpoint(value = "/ws")
public class WSEndpoint {
	private static List<Session> sessions = new ArrayList<Session>();

	@OnOpen
	public void onOpen(Session session) throws IOException {
		System.out.println("Kreirana sesija");
		if (!sessions.contains(session)) {
			sessions.add(session);
		}
	}

	@OnMessage
	public void sendMessage(String jsonString) throws IOException {
		System.out.println("Poslata poruka ws: " + jsonString);
		for (Session s : sessions) {
			s.getBasicRemote().sendText(jsonString);
		}
	}

	@OnClose
	public void close(Session session) throws IOException {
		sessions.remove(session);
		session.close();
	}

	@OnError
	public void error(Session session, Throwable t) throws IOException {
		sessions.remove(session);
		session.close();
		t.printStackTrace();
	}
}
