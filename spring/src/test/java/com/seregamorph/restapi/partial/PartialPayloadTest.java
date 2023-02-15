package com.seregamorph.restapi.partial;

import static com.seregamorph.restapi.partial.PartialPayloadFactory.partial;
import static org.hamcrest.Matchers.equalTo;

import com.seregamorph.restapi.base.IdResource;
import com.seregamorph.restapi.test.common.AbstractUnitTest;
import java.util.Collections;
import java.util.Map;
import lombok.val;
import org.junit.Test;

public class PartialPayloadTest extends AbstractUnitTest {

    private static final String FIELD_NAME = "name";

    @Test
    public void shouldSetProperties() {
        Map<String, Object> properties = Collections.singletonMap(FIELD_NAME, Long.MAX_VALUE);

        PartialResource payload = new SamplePartialResource();
        payload.setPartialProperties(properties);

        collector.checkThat(payload.getPartialProperties(), equalTo(properties));
    }

    @Test
    public void partialShouldHavePartialProperty() {
        val resource = partial(SamplePartialResource.class)
                .setId(1L);

        collector.checkThat(resource.isPartialPropertiesInitialized(), equalTo(true));
        collector.checkThat(resource.hasPartialProperty(SamplePartialResource.FIELD_ID), equalTo(true));
    }

    @Test
    public void newPartialShouldNotHavePartialPropertiesInitialized() {
        val resource = new SamplePartialResource()
                .setId(1L);

        collector.checkThat(resource.isPartialPropertiesInitialized(), equalTo(false));
    }

    @Test
    public void getPartialPropertiesShouldThrowExceptionWhenNewPartialPayload() {
        PartialResource resource = new SimplePartialResource()
                .setId(1L);
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Partial properties not defined for SimplePartialResource. Please check if "
                + "your ObjectMapper is configured correctly (either with " + JacksonBootConfig.class.getSimpleName()
                + " or " + PartialPayloadMapperUtils.class.getSimpleName() + ".configure), or create "
                + "PartialPayload instance via " + PartialPayloadFactory.class.getSimpleName() + ".partial(Class), "
                + "not with default constructor.");

        resource.getPartialProperties();
    }

    @Test
    public void hasPartialPropertyShouldThrowExceptionWhenNewPartialPayload() {
        val resource = new SimplePartialResource()
                .setId(1L);
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Partial properties not defined for SimplePartialResource. Please check if "
                + "your ObjectMapper is configured correctly (either with " + JacksonBootConfig.class.getSimpleName()
                + " or " + PartialPayloadMapperUtils.class.getSimpleName() + ".configure), or create "
                + "PartialPayload instance via " + PartialPayloadFactory.class.getSimpleName() + ".partial(Class), "
                + "not with default constructor.");

        resource.hasPartialProperty(IdResource.FIELD_ID);
    }

}
