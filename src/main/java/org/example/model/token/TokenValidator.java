package org.example.model.token;

import io.jsonwebtoken.*;


public class TokenValidator implements ITokenValidator{

    private String key;

    public TokenValidator(String lKey) {
        key = lKey;
    }

    @Override
    public String validate(String token) throws Exception {

        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            return claims.getBody().getSubject();
        } catch (ExpiredJwtException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            throw new Exception("Invalid JWT");
        }
    }

}
