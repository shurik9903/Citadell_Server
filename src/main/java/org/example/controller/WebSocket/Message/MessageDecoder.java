package org.example.controller.WebSocket.Message;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.websocket.DecodeException;
import jakarta.websocket.Decoder;

public class MessageDecoder implements Decoder.Text<Message> {

    private static final Jsonb jsonb = JsonbBuilder.create();

    @Override
    public Message decode(String json) throws DecodeException {
        return jsonb.fromJson(json, Message.class);
    }

    @Override
    public boolean willDecode(String json) {
        return (jsonb != null);
    }

}
