package org.example.controller.WebSocket;

import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.example.controller.WebSocket.Message.Message;
import org.example.controller.WebSocket.Message.MessageDecoder;
import org.example.controller.WebSocket.Message.MessageEncoder;

@ServerEndpoint(
    value = "/subs",
    decoders = MessageDecoder.class,
    encoders = MessageEncoder.class
)
public class WebSocket {

    @OnOpen
    public void onOpen(Session session) {
        Message message = new Message();
        message.setText("Успешное подключение");
        try {
            session.getBasicRemote().sendObject(message);
        } catch (Exception e){
            System.out.println("WebSocket error: " + e.getMessage());
        }
    }

}
