package org.example.model.token;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

public interface IToken {
    String create(String login, String teacher, String userID);

    Map<String, String> getData(String token);

    boolean check(String login, String token, boolean teacher) throws NoSuchAlgorithmException;
}
