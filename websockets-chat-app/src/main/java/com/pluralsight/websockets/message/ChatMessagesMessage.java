package com.pluralsight.websockets.message;

import java.util.List;

public class ChatMessagesMessage extends Message {
	private List<ChatMessage> messages;

	public ChatMessagesMessage(List<ChatMessage> messages) {
		super();
		this.messages = messages;
	}

	public List<ChatMessage> getMessages() {
		return messages;
	}

	public void setMessages(List<ChatMessage> messages) {
		this.setType(6);
		this.messages = messages;
	}
}
