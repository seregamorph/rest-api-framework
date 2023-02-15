package com.seregamorph.restapi.test.base;

import static org.junit.Assert.assertEquals;

import lombok.val;
import org.junit.Test;

public class TestRestTemplateRequestBuilderDelegateTest {

    @Test
    public void shouldFormatUriWithParameters() {
        val delegate = new TestRestTemplateRequestBuilderDelegate(null, null, null, null, null,
                "/api/v1/languages?query=value");
        delegate.param("search", "name<=C+Plus", "age>30");

        assertEquals("/api/v1/languages?query=value&search=name%3C%3DC%2BPlus&search=age%3E30",
                delegate.formatUri().toString());
    }

}
