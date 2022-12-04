package org.example.model.login;

import jakarta.ws.rs.core.Response;

public interface ILogin {
    Response LoginFunc(String login, String password);
}
