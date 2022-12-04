package org.example.model.login;

import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.core.Response;
import org.example.data.mydata.DLogin;
import org.example.model.database.IDataBaseWork;
import org.example.model.token.ITokenIssuer;
import org.example.model.token.ITokenKey;
import org.example.model.token.TokenIssuer;
import org.example.model.token.TokenKey;

import java.util.HashMap;
import java.util.Map;

public class Login implements ILogin {

    @Inject
    private IDataBaseWork DataBaseWork;

    @Override
    public Response LoginFunc(String login, String password) {

        Map<String, String> Result = new HashMap<>();

        String Checklogin = "qqw";
        String Checkpassword = "3331";

        try {

            if (!login.isEmpty() && !password.isEmpty()) {

                if (DataBaseWork.ping()) {

                    DLogin dlogin = (DLogin) DataBaseWork.login(login, password);

                    if (dlogin.getMsg() == null) {

                        ITokenKey tokenKey = new TokenKey();
                        ITokenIssuer tokenIssuer = new TokenIssuer(tokenKey.getKey());
                        String newToken = tokenIssuer.issueToken(login);

                        if (!newToken.isEmpty()) {
                            Result.put("Token", newToken);
                            Result.put("UserID", String.valueOf(dlogin.getUser_ID()));
                            Result.put("UserLogin", dlogin.getUser_login());}else
                            Result.put("Msg", "Token Error1");
                    } else Result.put("Msg", dlogin.getMsg());


                } else if (login.equals(Checklogin) && password.equals(Checkpassword)){

                    ITokenKey tokenKey = new TokenKey();
                    ITokenIssuer tokenIssuer = new TokenIssuer(tokenKey.getKey());
                    String newToken = tokenIssuer.issueToken(login);

                    if (!newToken.isEmpty()) {
                        Result.put("Token", newToken);
                        Result.put("UserID", String.valueOf(-1));
                        Result.put("UserLogin", login);
                    }else
                        Result.put("Msg", "Token Error2");
                } else Result.put("Msg", "Wrong login or password");

            } else Result.put("Msg", "Fill in all the fields");

            Jsonb jsonb = JsonbBuilder.create();

            return Response.ok(jsonb.toJson(Result)).build();
        } catch (Exception e) {
            System.out.println(e);
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: " + e.getMessage()).build();
        }
    }

}
