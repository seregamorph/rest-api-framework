package com.seregamorph.restapi.test.config.spi;

import com.seregamorph.restapi.test.base.Rest;

public interface AuthKeyProvider {

    String getAuthHeader();

    String getAuthKey(Rest rest, String... authorities) throws Exception;

}
