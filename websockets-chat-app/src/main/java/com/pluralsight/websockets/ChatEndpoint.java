package com.pluralsight.websockets;

import com.pluralsight.websockets.coders.MessageDecoder;
import com.pluralsight.websockets.coders.MessageEncoder;
import com.pluralsight.websockets.message.*;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@ServerEndpoint(value = "/chat", decoders = MessageDecoder.class, encoders = MessageEncoder.class)
public class ChatEndpoint {

    static List<ChatEndpoint> clients = new CopyOnWriteArrayList<>();
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

    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    @OnMessage
    public void onMessage(ByteBuffer byteBuffer, boolean complete) {
        try {
            buffer.write(byteBuffer.array());
            if(complete) {
                FileOutputStream fileOutputStream = null;

                try {
                    fileOutputStream = new FileOutputStream(Paths.get("temp\\image.jpg").toFile());
                    fileOutputStream.write(buffer.toByteArray());
                } finally {
                    if(fileOutputStream != null){
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    }
                }

                for(ChatEndpoint client:clients){
                    final ByteBuffer sendData = ByteBuffer.allocate(buffer.toByteArray().length);
                    sendData.put(buffer.toByteArray());
                    sendData.rewind();
                    client.session.getAsyncRemote().sendBinary(sendData, new SendHandler() {
                        @Override
                        public void onResult(SendResult sendResult) {
                            System.out.println(sendResult.isOK());
                        }
                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        } catch (IOException | EncodeException e) {
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
