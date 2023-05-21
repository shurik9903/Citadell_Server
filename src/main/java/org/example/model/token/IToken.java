package org.example.model.token;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

public interface IToken {
    String create(String login, String admin, String userID);

    Map<String, String> getData(String token);

    boolean check(String login, String token, boolean admin) throws NoSuchAlgorithmException;
}
