package com.pluralsight.websockets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.websocket.CloseReason;
import javax.websocket.EncodeException;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.pluralsight.websockets.coders.MessageDecoder;
import com.pluralsight.websockets.coders.MessageEncoder;
import com.pluralsight.websockets.message.ChatMessage;
import com.pluralsight.websockets.message.ChatMessagesMessage;
import com.pluralsight.websockets.message.GetUsersMessage;
import com.pluralsight.websockets.message.JoinMessage;
import com.pluralsight.websockets.message.Message;
import com.pluralsight.websockets.message.UserListMessage;

@ServerEndpoint(value = "/chat", decoders = MessageDecoder.class, encoders = MessageEncoder.class)
public class ChatEndpoint {

	static List<ChatEndpoint> clients = new CopyOnWriteArrayList<ChatEndpoint>();
	static List<User> users = new ArrayList<>();
	static List<ChatMessage> messages = new ArrayList<>();

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
	public void onMessage(Message message) {
		if (message instanceof JoinMessage) {
			processMessage((JoinMessage) message);
		} else if (message instanceof ChatMessage) {
			processMessage((ChatMessage) message);
		} else if (message instanceof GetUsersMessage) {
			processMessage((GetUsersMessage) message);
		} else {
			System.err.println(String.format("Unknown message type [%s]", message.getClass().getName()));
		}
	}

	private void processMessage(JoinMessage message) {
		User user = new User();
		user.setName(message.getName());
		users.add(user);
		broadcast(message);
	}

	private void processMessage(ChatMessage message) {
		messages.add(message);
		broadcast(message);
	}

	private void processMessage(GetUsersMessage message) {
		try {
			session.getBasicRemote().sendObject(new UserListMessage(ChatEndpoint.users));
			session.getBasicRemote().sendObject(new ChatMessagesMessage(ChatEndpoint.messages));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (EncodeException e) {
			e.printStackTrace();
		}
	}

	private void broadcast(Message message) {
		for (ChatEndpoint client : clients) {
			try {
				client.session.getBasicRemote().sendObject(message);
			} catch (IOException e) {
				clients.remove(this);
				try {
					client.session.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} catch (EncodeException e) {
				e.printStackTrace();
			}
		}
	}
}
