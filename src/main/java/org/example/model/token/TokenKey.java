package org.example.model.token;


import org.example.model.properties.ServerProperties;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class TokenKey implements ITokenKey {
    private String key =  ServerProperties.getProperty("key");

    @Override
    public Key getKey() {
        return new SecretKeySpec(key.getBytes(),"HmacSHA256");
    }
}

