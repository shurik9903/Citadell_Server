package org.example.model.login;

import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.core.Response;
import org.example.data.entity.ELogin;
import org.example.model.database.IDataBaseWork;
import org.example.model.token.*;

import java.util.HashMap;
import java.util.Map;

public class Login implements ILogin {

    @Inject
    private IToken token;

    @Inject
    private IDataBaseWork DataBaseWork;

    @Override
    public Response LoginFunc(String jsonData) {

        Jsonb jsonb = JsonbBuilder.create();

        Map<String, String> dLogin = new HashMap<>();
        dLogin = jsonb.fromJson(jsonData, dLogin.getClass());

        String login = dLogin.getOrDefault("login", null);
        String password = dLogin.getOrDefault("password", null);


        Map<String, String> Result = new HashMap<>();

        try {

            if (login.isEmpty() || password.isEmpty()) {
                Result.put("Msg", "Fill in all the fields");
                return Response.ok(jsonb.toJson(Result)).build();
            }

            if (!DataBaseWork.ping()) {
                Result.put("Msg", "Wrong login or password");
                return Response.ok(jsonb.toJson(Result)).build();
            }

            ELogin eLogin = DataBaseWork.login(login, password);

            if (eLogin.getMsg() != null) {
                Result.put("Msg", eLogin.getMsg());
                return Response.ok(jsonb.toJson(Result)).build();
            }

            String newToken = token.create(login, String.valueOf(eLogin.isPermission()), eLogin.getUser_ID().toString());

            Result.put("token", newToken);
            Result.put("login", login);

            return Response.ok(jsonb.toJson(Result)).build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: " + e.getMessage()).build();
        }
    }

}
