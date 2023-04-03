package org.example.model.messageBroker;

import jakarta.ws.rs.core.Response;

public interface IMessBrok {
    Response send(String json);

    Response declare(String json);

    Response consume();
}
