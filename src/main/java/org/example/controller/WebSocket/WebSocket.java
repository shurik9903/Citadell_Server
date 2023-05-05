package org.example.controller.WebSocket;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.example.controller.WebSocket.Message.InMessage;
import org.example.controller.WebSocket.Message.MessageDecoder;
import org.example.controller.WebSocket.Message.MessageEncoder;
import org.example.controller.WebSocket.Message.OutMessage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@ServerEndpoint(
    value = "/WSConnect",
    decoders = MessageDecoder.class,
    encoders = MessageEncoder.class
)
public class WebSocket {

    private static Set<Session> sessions = new HashSet<>();

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);

        OutMessage outMessage = new OutMessage();
        outMessage.setType("MSG");
        outMessage.setMessage("Успешное подключение");
        try {
            session.getBasicRemote().sendObject(outMessage);
        } catch (Exception e){
            System.out.println("WebSocket Open error: " + e.getMessage());
        }
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        System.out.println("User " + session.getId() + " out");
    }

    @OnMessage
    public void onMessage(Session session, InMessage inMessage) {
        
        OutMessage outMessage = new OutMessage();

        outMessage.setLogin(inMessage.getLogin());
        outMessage.setType("MSG");
        outMessage.setMessage("Test MSG");

        for(Session sess : sessions){
            try {
                sess.getBasicRemote().sendObject(outMessage);
            } catch (Exception e) {
                System.out.println("WebSocket Message error: " + e.getMessage());
            }
        }
    }

    public void sendMessage(OutMessage outMessage){

        Runnable task = new Runnable() {
            public void run() {

                try {
                    Thread.sleep(5000);

                    for (Session sess : sessions) {
                        try {
                            sess.getBasicRemote().sendObject(outMessage);
                        } catch (Exception e) {
                            System.out.println("WebSocket Message error: " + e.getMessage());
                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("Hello, World!");
            }
        };

        Thread thread = new Thread(task);
        thread.start();
    }
}
