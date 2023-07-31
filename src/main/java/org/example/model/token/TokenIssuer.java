package org.example.model.token;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.Key;
import java.util.Date;

public class TokenIssuer implements ITokenIssuer{

    private Key key;

    public TokenIssuer(Key Key) {
        this.key = Key;
    }

    @Override
    public String issueToken(String userName, String admin, String userID) {

        return Jwts.builder()
                .setSubject(
                    "{" +
                        "\"login\":\""+ userName +"\"," +
                        "\"admin\":\""+ admin +"\"," +
                        "\"userID\":\""+ userID +"\"" +
                    "}")
                .claim("scope", "user")
                .signWith(key, SignatureAlgorithm.HS256)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + (1000 * 60 * 60 * 24)))
                .compact();
    }
}
