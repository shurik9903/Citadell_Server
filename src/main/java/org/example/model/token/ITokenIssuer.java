package org.example.model.token;

public interface ITokenIssuer {
    String JWS_Create_Token(String username);

    String issueToken(String username);
}
