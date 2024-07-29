package com.seregamorph.restapi.partial;

import static com.seregamorph.restapi.test.utils.MoreMatchers.where;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;

import com.seregamorph.restapi.test.common.AbstractUnitTest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import lombok.val;
import org.junit.Test;

public class PartialPayloadUtilsTest extends AbstractUnitTest {

    private static final String REDUNDANT_PROPERTY_NAME = "redundantPropertyName";
    private static final String REDUNDANT_PROPERTY_VALUE = "redundantPropertyValue";

    @Test
    public void shouldValidateSuccessWhenPartialPayloadIsValid() {
        PartialPayloadUtils.validate(validSamplePayload());
    }

    @Test
    public void shouldValidateFailureWhenPartialPayloadHasRedundantFields() {
        expectedException.expect(RedundantFieldsException.class);
        expectedException.expect(where(RedundantFieldsException::getTargetClass, equalTo(SamplePartialPayload.class)));
        expectedException.expect(where(RedundantFieldsException::getRedundantFieldNames,
                contains(REDUNDANT_PROPERTY_NAME)));

        PartialPayloadUtils.validate(invalidSamplePayloadWithRedundantFields());
    }

    @Test
    public void shouldValidateFailureWhenNestedPartialPayloadHasRedundantFields() {
        expectedException.expect(RedundantFieldsException.class);
        expectedException.expect(where(RedundantFieldsException::getTargetClass, equalTo(SimplePartialPayload.class)));
        expectedException.expect(where(RedundantFieldsException::getRedundantFieldNames,
                contains(REDUNDANT_PROPERTY_NAME)));

        PartialPayloadUtils.validate(invalidSamplePayloadWithNestedRedundantFields());
    }

    @Test
    public void shouldValidateFailureWhenPartialPayloadInNestedCollectionHasRedundantFields() {
        expectedException.expect(RedundantFieldsException.class);
        expectedException.expect(where(RedundantFieldsException::getTargetClass, equalTo(SimplePartialPayload.class)));
        expectedException.expect(where(RedundantFieldsException::getRedundantFieldNames,
                contains(REDUNDANT_PROPERTY_NAME)));

        PartialPayloadUtils.validate(invalidSamplePayloadWithRedundantFieldsInNestedCollection());
    }

    @Test
    public void shouldValidateFailureWhenPartialPayloadInNestedArrayHasRedundantFields() {
        expectedException.expect(RedundantFieldsException.class);
        expectedException.expect(where(RedundantFieldsException::getTargetClass, equalTo(SimplePartialPayload.class)));
        expectedException.expect(where(RedundantFieldsException::getRedundantFieldNames,
                contains(REDUNDANT_PROPERTY_NAME)));

        PartialPayloadUtils.validate(invalidSamplePayloadWithRedundantFieldsInNestedArray());
    }

    @Test
    public void shouldValidateFailureWhenPartialPayloadDoesNotHaveRequiredFields() {
        expectedException.expect(RequiredFieldsException.class);
        expectedException.expect(where(RequiredFieldsException::getTargetClass, equalTo(SamplePartialPayload.class)));
        expectedException.expect(where(RequiredFieldsException::getRequiredFieldNames, contains(
                SamplePartialPayload.Fields.NORMAL_FIELD1,
                SamplePartialPayload.Fields.PARTIAL_PAYLOAD_FIELD1,
                SamplePartialPayload.Fields.PARTIAL_PAYLOAD_ARRAY1)));

        PartialPayloadUtils.validate(invalidSamplePayloadWithMissingRequiredFields());
    }

    @Test
    public void shouldValidateFailureWhenNestedPartialPayloadDoesNotHaveRequiredFields() {
        expectedException.expect(RequiredFieldsException.class);
        expectedException.expect(where(RequiredFieldsException::getTargetClass, equalTo(SimplePartialPayload.class)));
        expectedException.expect(where(RequiredFieldsException::getRequiredFieldNames,
                contains(SimplePartialPayload.Fields.NAME, SimplePartialPayload.Fields.TITLE)));

        PartialPayloadUtils.validate(invalidSamplePayloadWithNestedMissingRequiredFields());
    }

    @Test
    public void shouldValidateFailureWhenPartialPayloadInNestedCollectionDoesNotHaveRequiredFields() {
        expectedException.expect(RequiredFieldsException.class);
        expectedException.expect(where(RequiredFieldsException::getTargetClass, equalTo(SimplePartialPayload.class)));
        expectedException.expect(where(RequiredFieldsException::getRequiredFieldNames,
                contains(SimplePartialPayload.Fields.NAME, SimplePartialPayload.Fields.TITLE)));

        PartialPayloadUtils.validate(invalidSamplePayloadWithMissingRequiredFieldsInNestedCollection());
    }

    @Test
    public void shouldValidateFailureWhenPartialPayloadInNestedArrayDoesNotHaveRequiredFields() {
        expectedException.expect(RequiredFieldsException.class);
        expectedException.expect(where(RequiredFieldsException::getTargetClass, equalTo(SimplePartialPayload.class)));
        expectedException.expect(where(RequiredFieldsException::getRequiredFieldNames,
                contains(SimplePartialPayload.Fields.NAME, SimplePartialPayload.Fields.TITLE)));

        PartialPayloadUtils.validate(invalidSamplePayloadWithMissingRequiredFieldsInNestedArray());
    }

    @Test
    public void shouldCopyForPartialPayload() {
        SamplePartialPayload first = invalidSamplePayloadWithRedundantFields();
        SamplePartialPayload second = validSamplePayload().setUntouchedField(Long.MAX_VALUE).extractPayload();
        second.getPartialPayloadCollection1().add(first.getPartialPayloadCollection1().get(0));

        first.copyTo(second);

        // Remember we copy every thing, but:
        // - untouchedField is not in first payload, so it's not supposed to be touched
        // - we replace collection elements, but not the collection itself
        collector.checkThat(second.getPartialProperties(),
                equalTo(first.getPartialProperties()));
        collector.checkThat(second.getNormalField1(), equalTo(first.getNormalField1()));
        collector.checkThat(second.getNormalField2(), equalTo(first.getNormalField2()));
        collector.checkThat(second.getPartialPayloadField1(), equalTo(first.getPartialPayloadField1()));
        collector.checkThat(second.getPartialPayloadField2(), equalTo(first.getPartialPayloadField2()));
        collector.checkThat(second.getPartialPayloadCollection1(), equalTo(first.getPartialPayloadCollection1()));
        collector.checkThat(second.getPartialPayloadCollection1(),
                not(sameInstance(first.getPartialPayloadCollection1())));
        collector
                .checkThat(second.getPartialPayloadCollection2(), sameInstance(first.getPartialPayloadCollection2()));
        collector.checkThat(second.getUntouchedField(), equalTo(Long.MAX_VALUE));
    }

    @Test
    public void proxyClassShouldBeCached() {
        val sample1 = SamplePartialPayload.create();
        val sample2 = SamplePartialPayload.create();

        collector.checkThat(sample1.getClass(), sameInstance(sample2.getClass()));
    }

    private static SamplePartialPayload validSamplePayload() {
        return SamplePartialPayload.create()
                .setNormalField1(UUID.randomUUID().toString())
                .setPartialPayloadField1(validSimplePayload())
                .setPartialPayloadCollection1(validSimplePayloadList())
                .setPartialPayloadArray1(validSimplePayloads());
    }

    private static SimplePartialPayload validSimplePayload() {
        return SimplePartialPayload.create()
                .setName(UUID.randomUUID().toString())
                .setTitle(UUID.randomUUID().toString());
    }

    private static List<SimplePartialPayload> validSimplePayloadList() {
        return new ArrayList<>(Collections.singleton(validSimplePayload()));
    }

    private static Set<SimplePartialPayload> validSimplePayloadSet() {
        return new HashSet<>(Collections.singleton(validSimplePayload()));
    }

    private static SimplePartialPayload[] validSimplePayloads() {
        return new SimplePartialPayload[] {validSimplePayload()};
    }

    private static SimplePartialPayload invalidSimplePayloadWithRedundantFields() {
        SimplePartialPayload simplePartialPayload = SimplePartialPayload.create()
                .setName(UUID.randomUUID().toString())
                .setTitle(UUID.randomUUID().toString())
                .setDescription(UUID.randomUUID().toString())
                .setVersion(ThreadLocalRandom.current().nextInt());
        simplePartialPayload.setPartialProperty(REDUNDANT_PROPERTY_NAME, REDUNDANT_PROPERTY_VALUE);
        return simplePartialPayload;
    }

    private static List<SimplePartialPayload> invalidSimplePayloadCollectionWithRedundantFields() {
        return new ArrayList<>(Collections.singleton(invalidSimplePayloadWithRedundantFields()));
    }

    private static SimplePartialPayload[] invalidSimplePayloadsWithRedundantFields() {
        return new SimplePartialPayload[] {invalidSimplePayloadWithRedundantFields()};
    }

    private static SimplePartialPayload invalidSimplePayloadWithoutRequiredFields() {
        return SimplePartialPayload.create();
    }

    private static List<SimplePartialPayload> invalidSimplePayloadCollectionWithoutRequiredFields() {
        return new ArrayList<>(Collections.singleton(invalidSimplePayloadWithoutRequiredFields()));
    }

    private static SimplePartialPayload[] invalidSimplePayloadsWithoutRequiredFields() {
        return new SimplePartialPayload[] {invalidSimplePayloadWithoutRequiredFields()};
    }

    private static SamplePartialPayload invalidSamplePayloadWithRedundantFields() {
        SamplePartialPayload samplePartialPayload = SamplePartialPayload.create()
                .setNormalField1(UUID.randomUUID().toString())
                .setNormalField2(ThreadLocalRandom.current().nextInt())
                .setPartialPayloadField1(validSimplePayload())
                .setPartialPayloadField2(validSimplePayload())
                .setPartialPayloadCollection1(validSimplePayloadList())
                .setPartialPayloadCollection2(validSimplePayloadSet())
                .setPartialPayloadArray1(validSimplePayloads())
                .setPartialPayloadArray2(validSimplePayloads());
        samplePartialPayload.setPartialProperty(REDUNDANT_PROPERTY_NAME, REDUNDANT_PROPERTY_VALUE);
        return samplePartialPayload;
    }

    private static SamplePartialPayload invalidSamplePayloadWithNestedRedundantFields() {
        return SamplePartialPayload.create()
                .setNormalField1(UUID.randomUUID().toString())
                .setPartialPayloadField1(invalidSimplePayloadWithRedundantFields())
                .setPartialPayloadCollection1(validSimplePayloadList())
                .setPartialPayloadArray1(validSimplePayloads());
    }

    private static SamplePartialPayload invalidSamplePayloadWithRedundantFieldsInNestedCollection() {
        return SamplePartialPayload.create()
                .setNormalField1(UUID.randomUUID().toString())
                .setPartialPayloadField1(validSimplePayload())
                .setPartialPayloadCollection1(invalidSimplePayloadCollectionWithRedundantFields())
                .setPartialPayloadArray1(validSimplePayloads());
    }

    private static SamplePartialPayload invalidSamplePayloadWithRedundantFieldsInNestedArray() {
        return SamplePartialPayload.create()
                .setNormalField1(UUID.randomUUID().toString())
                .setPartialPayloadField1(validSimplePayload())
                .setPartialPayloadCollection1(validSimplePayloadList())
                .setPartialPayloadArray1(invalidSimplePayloadsWithRedundantFields());
    }

    private static SamplePartialPayload invalidSamplePayloadWithMissingRequiredFields() {
        return SamplePartialPayload.create()
                .setPartialPayloadCollection1(validSimplePayloadList());
    }

    private static SamplePartialPayload invalidSamplePayloadWithNestedMissingRequiredFields() {
        return SamplePartialPayload.create()
                .setNormalField1(UUID.randomUUID().toString())
                .setPartialPayloadField1(invalidSimplePayloadWithoutRequiredFields())
                .setPartialPayloadCollection1(validSimplePayloadList())
                .setPartialPayloadArray1(validSimplePayloads());
    }

    private static SamplePartialPayload invalidSamplePayloadWithMissingRequiredFieldsInNestedCollection() {
        return SamplePartialPayload.create()
                .setNormalField1(UUID.randomUUID().toString())
                .setPartialPayloadField1(validSimplePayload())
                .setPartialPayloadCollection1(invalidSimplePayloadCollectionWithoutRequiredFields())
                .setPartialPayloadArray1(validSimplePayloads());
    }

    private static SamplePartialPayload invalidSamplePayloadWithMissingRequiredFieldsInNestedArray() {
        return SamplePartialPayload.create()
                .setNormalField1(UUID.randomUUID().toString())
                .setPartialPayloadField1(validSimplePayload())
                .setPartialPayloadCollection1(validSimplePayloadList())
                .setPartialPayloadArray1(invalidSimplePayloadsWithoutRequiredFields());
    }
}
