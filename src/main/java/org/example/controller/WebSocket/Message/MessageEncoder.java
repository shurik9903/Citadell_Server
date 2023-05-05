package org.example.controller.WebSocket.Message;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.websocket.EncodeException;
import jakarta.websocket.Encoder;

public class MessageEncoder implements Encoder.Text<OutMessage> {

    private static final Jsonb jsonb = JsonbBuilder.create();

    @Override
    public String encode(OutMessage outMessage) throws EncodeException {
        return jsonb.toJson(outMessage);
    }

}
