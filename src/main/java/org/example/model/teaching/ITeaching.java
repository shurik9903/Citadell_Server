package org.example.model.teaching;

import jakarta.ws.rs.core.Response;

public interface ITeaching {

    Response startTeaching(String json, String userLogin, String userID);

}