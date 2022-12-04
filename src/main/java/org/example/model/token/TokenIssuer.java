package org.example.model.token;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

public class TokenIssuer implements ITokenIssuer{

    private String key;


    public TokenIssuer(String lKey) {
        key = lKey;
    }

    @Override
    public String JWS_Create_Token(String username){

        String header = "{\"alg\":\"HS256\"}";
        String claims = "{\"sub\":\""+username+"\"}";
        String encodedHeader;
        String encodedClaims;
        encodedHeader = Base64.getUrlEncoder().encodeToString(header.getBytes(StandardCharsets.UTF_8));
        encodedClaims = Base64.getUrlEncoder().encodeToString(claims.getBytes(StandardCharsets.UTF_8));
        String concatenated = encodedHeader + '.' + encodedClaims;
        String jws = Encoders.BASE64.encode(new SecretKeySpec((concatenated+key).getBytes(StandardCharsets.UTF_8),
                "HmacSHA256").getEncoded());
        return concatenated + '.' + Base64.getUrlEncoder().encodeToString(jws.getBytes(StandardCharsets.UTF_8));

    }

    @Override
    public String issueToken(String username) {

        String jws = JWS_Create_Token(username);

        return Jwts.builder()
                .setSubject(username)
                .claim("scope", "user")
                .signWith(SignatureAlgorithm.HS256, key)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + (1000 * 60 * 10)))
                .compact();
    }
}
