package org.example.controller.WebSocket;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import org.example.controller.WebSocket.Message.InMessage;
import org.example.controller.WebSocket.Message.MessageDecoder;
import org.example.controller.WebSocket.Message.MessageEncoder;
import org.example.controller.WebSocket.Message.OutMessage;
import org.example.data.mydata.DUserConnect;
import org.example.model.connections.IUserConnections;
import org.example.model.connections.UserConnections;
import org.example.model.utils.FileUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@ServerEndpoint(
    value = "/WSConnect/{login}",
    decoders = MessageDecoder.class,
    encoders = MessageEncoder.class
)
public class WebSocket {

    private static final Set<Session> sessions = new HashSet<>();
    private final IUserConnections userConnections = new UserConnections();

    @OnOpen
    public void onOpen(Session session, @PathParam("login") String login) {
        try {
            sessions.add(session);

            userConnections.setUserData(session.getId(), login);

            OutMessage outMessage = new OutMessage();
            outMessage.setType("MSG");
            outMessage.setMessage("Успешное подключение \nID: " + session.getId());

            session.getBasicRemote().sendObject(outMessage);
        } catch (Exception e){
            System.out.println("Ошибка при открытии WebSocket: " + e.getMessage());
        }
    }

    @OnClose
    public void onClose(Session session) {
        try {
            sessions.remove(session);

            userConnections.delUserData(session.getId());

            System.out.println("User " + session.getId() + " out");
        }catch (Exception e){
            System.out.println("Ошибка при закрытии WebSocket: " + e.getMessage());

        }
    }

    public void sendResultMessage(OutMessage outMessage, String login, DUserConnect.Analysis analysis) {
        try {
            Jsonb jsonb = JsonbBuilder.create();

            FileUtils fileUtils = new FileUtils();

            ArrayList<DUserConnect> userConnects = userConnections.getUserConnect();

            Optional<DUserConnect> findUser = userConnects.stream()
                    .filter(dUserConnect -> dUserConnect.getUserLogin().equals(login)).findFirst();

            if (findUser.isPresent()){

                findUser.get().getAnalysis().forEach(value -> {
                    if (value.getUuid().equals(analysis.getUuid()))
                        value.setStatus(true);
                });

                Optional<Session> findSession = sessions.stream().filter(session -> session.getId().equals(findUser.get().getConnectID())).findFirst();

                if(findSession.isPresent()) {
                    findSession.get().getBasicRemote().sendObject(outMessage);
                }

            } else {



//                findUser.get().getAnalysis().forEach(value -> {
//                    if (value.getUuid().equals(analysis.getUuid()))
//                        value.setStatus(true);
//                });
//
//                Optional<Session> findSession = sessions.stream().filter(session -> session.getId().equals(findUser.get().getConnectID())).findFirst();
//
//                if(findSession.isPresent()) {
//                    findSession.get().getBasicRemote().sendObject(outMessage);
//                }

                DUserConnect userConnect = new DUserConnect();
                userConnect.setUserLogin(login);
                userConnect.getAnalysis().add(analysis);
                userConnects.add(userConnect);
            }

            userConnections.saveUserConnect(jsonb.toJson(userConnects));

//            Runnable task = new Runnable() {
//                public void run() {
//
//                    try {
//                        Thread.sleep(5000);
//
//                        for (Session sess : sessions) {
//                            try {
//                                sess.getBasicRemote().sendObject(outMessage);
//                            } catch (Exception e) {
//                                System.out.println("Ошибка сообщений WebSocket: " + e.getMessage());
//                            }
//                        }
//
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            };
//
//            Thread thread = new Thread(task);
//            thread.start();

        } catch (Exception e) {
            System.out.println("Ошибка при отправке сообщения WebSocket: " + e.getMessage());
        }
    }


}
