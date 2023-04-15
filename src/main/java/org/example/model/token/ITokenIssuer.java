package org.example.model.token;

public interface ITokenIssuer {
    String issueToken(String username, String admin, String userID);
}
