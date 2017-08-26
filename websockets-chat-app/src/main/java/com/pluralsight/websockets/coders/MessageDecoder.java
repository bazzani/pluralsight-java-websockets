package com.pluralsight.websockets.coders;

import java.io.IOException;
import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pluralsight.websockets.message.ChatMessage;
import com.pluralsight.websockets.message.GetUsersMessage;
import com.pluralsight.websockets.message.JoinMessage;
import com.pluralsight.websockets.message.Message;

public class MessageDecoder implements Decoder.Text<Message> {

	static final int JOIN_MESSAGE = 1;
	static final int CHAT_MESSAGE = 2;
	static final int GETUSERS_MESSAGE = 3;

	@Override
	public Message decode(String msg) throws DecodeException {
		Message message = null;

		if (willDecode(msg)) {
			try {
				JsonObject obj = Json.createReader(new StringReader(msg)).readObject();
				ObjectMapper mapper = new ObjectMapper();

				int type = obj.getInt("type");

				switch (type) {
				case JOIN_MESSAGE:
					message = mapper.readValue(msg, JoinMessage.class);
					break;
				case CHAT_MESSAGE:
					message = mapper.readValue(msg, ChatMessage.class);
					break;
				case GETUSERS_MESSAGE:
					message = mapper.readValue(msg, GetUsersMessage.class);
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return message;
	}

	@Override
	public boolean willDecode(String msg) {
		try {
			Json.createReader(new StringReader(msg));
			return true;
		} catch (JsonException e) {
			return false;
		}
	}

	@Override
	public void init(EndpointConfig config) {
		// TODO Auto-generated method stub
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}
}
