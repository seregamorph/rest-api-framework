package com.seregamorph.restapi.test.base.support;

import static java.nio.charset.StandardCharsets.UTF_8;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.val;
import org.springframework.util.Base64Utils;

@RequiredArgsConstructor
@Getter
@ToString(exclude = "password")
public class BasicUserCredentials {

    private final String username;
    private final String password;

    public String basicAuthorizationToken() {
        val authority = getUsername() + ":" + getPassword();
        return Base64Utils.encodeToString(authority.getBytes(UTF_8));
    }

}
