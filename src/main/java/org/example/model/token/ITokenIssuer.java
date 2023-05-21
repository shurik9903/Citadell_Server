package org.example.model.token;

public interface ITokenIssuer {
    String issueToken(String userName, String admin, String userID);
}
