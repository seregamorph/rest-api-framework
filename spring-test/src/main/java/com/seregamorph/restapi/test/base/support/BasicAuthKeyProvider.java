package com.seregamorph.restapi.test.base.support;

import com.seregamorph.restapi.test.base.Rest;
import com.seregamorph.restapi.test.config.spi.AuthKeyProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;

@RequiredArgsConstructor
public class BasicAuthKeyProvider implements AuthKeyProvider {

    private final BasicUserCredentials userCredentials;

    @Override
    public String getAuthHeader() {
        return HttpHeaders.AUTHORIZATION;
    }

    @Override
    public String getAuthKey(Rest rest, String... authorities) throws Exception {
        return "Basic " + userCredentials.basicAuthorizationToken();
    }

}
