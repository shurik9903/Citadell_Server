package org.example.model.login;

import jakarta.ws.rs.core.Response;

public interface ILogin {
    Response loginFunc(String jsonData);
}
