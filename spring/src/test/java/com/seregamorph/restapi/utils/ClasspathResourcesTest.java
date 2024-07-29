package com.seregamorph.restapi.utils;

import static org.hamcrest.Matchers.equalTo;

import com.seregamorph.restapi.test.common.AbstractUnitTest;
import org.junit.Test;

public class ClasspathResourcesTest extends AbstractUnitTest {

    @Test
    public void readStringShouldReadCorrectValue() {
        String str = ClasspathResources.readString("sample-resource.properties");
        collector.checkThat(str, equalTo("sample-key=sample-value\n"));
    }

    @Test
    public void readStringShouldThrowErrorIfResourceNotFound() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Missing resource [foobar-resource.properties]");
        ClasspathResources.readString("foobar-resource.properties");
    }
}
