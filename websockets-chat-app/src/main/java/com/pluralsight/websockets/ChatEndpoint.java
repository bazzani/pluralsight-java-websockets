package com.pluralsight.websockets;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/chat")
public class ChatEndpoint {

	static List<ChatEndpoint> clients = new CopyOnWriteArrayList<ChatEndpoint>();
	private Session session;

	@OnOpen
	public void onOpen(Session session, EndpointConfig _) {
		this.session = session;
		clients.add(this);
	}

	@OnClose
	public void onClose(Session session, CloseReason reason) {
		System.out.println(
				String.format("socket closed [%s], reason: %s", reason.getCloseCode(), reason.getReasonPhrase()));
		clients.remove(this);
	}

	@OnMessage
	public void onMessage(String message) {
		broadcast(message);
	}

	private void broadcast(String message) {
		for (ChatEndpoint client : clients) {
			try {
				client.session.getBasicRemote().sendText(message);
			} catch (IOException e) {
				clients.remove(this);
				try {
					client.session.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
