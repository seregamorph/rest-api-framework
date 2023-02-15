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

public class PartialResourceUtilsTest extends AbstractUnitTest {

    @Test
    public void shouldValidateSuccessWhenPartialResourceIsValid() {
        PartialResourceUtils.validate(validSampleResource(), SamplePartial.class);
    }

    @Test
    public void shouldValidateFailureWhenPartialResourceHasRedundantFields() {
        expectedException.expect(RedundantFieldsException.class);
        expectedException.expect(where(RedundantFieldsException::getTargetClass, equalTo(SamplePartialResource.class)));
        expectedException.expect(where(RedundantFieldsException::getRedundantFieldNames, contains(
                SamplePartialResource.Fields.PARTIAL_RESOURCE_COLLECTION2,
                SamplePartialResource.Fields.NORMAL_FIELD2,
                SamplePartialResource.Fields.PARTIAL_RESOURCE_FIELD2,
                SamplePartialResource.Fields.PARTIAL_RESOURCE_ARRAY2)));

        PartialResourceUtils.validate(invalidSampleResourceWithRedundantFields(), SamplePartial.class);
    }

    @Test
    public void shouldValidateFailureWhenNestedPartialResourceHasRedundantFields() {
        expectedException.expect(RedundantFieldsException.class);
        expectedException.expect(where(RedundantFieldsException::getTargetClass, equalTo(SimplePartialResource.class)));
        expectedException.expect(where(RedundantFieldsException::getRedundantFieldNames, contains(
                SimplePartialResource.Fields.DESCRIPTION,
                SimplePartialResource.Fields.VERSION)));

        PartialResourceUtils.validate(invalidSampleResourceWithNestedRedundantFields(), SamplePartial.class);
    }

    @Test
    public void shouldValidateFailureWhenPartialResourceInNestedCollectionHasRedundantFields() {
        expectedException.expect(RedundantFieldsException.class);
        expectedException.expect(where(RedundantFieldsException::getTargetClass, equalTo(SimplePartialResource.class)));
        expectedException.expect(where(RedundantFieldsException::getRedundantFieldNames,
                contains(SimplePartialResource.Fields.DESCRIPTION, SimplePartialResource.Fields.VERSION)));

        PartialResourceUtils
                .validate(invalidSampleResourceWithRedundantFieldsInNestedCollection(), SamplePartial.class);
    }

    @Test
    public void shouldValidateFailureWhenPartialResourceInNestedArrayHasRedundantFields() {
        expectedException.expect(RedundantFieldsException.class);
        expectedException.expect(where(RedundantFieldsException::getTargetClass, equalTo(SimplePartialResource.class)));
        expectedException.expect(where(RedundantFieldsException::getRedundantFieldNames,
                contains(SimplePartialResource.Fields.DESCRIPTION, SimplePartialResource.Fields.VERSION)));

        PartialResourceUtils
                .validate(invalidSampleResourceWithRedundantFieldsInNestedArray(), SamplePartial.class);
    }

    @Test
    public void shouldValidateFailureWhenPartialResourceDoesNotHaveRequiredFields() {
        expectedException.expect(RequiredFieldsException.class);
        expectedException.expect(where(RequiredFieldsException::getTargetClass, equalTo(SamplePartialResource.class)));
        expectedException.expect(where(RequiredFieldsException::getRequiredFieldNames, contains(
                SamplePartialResource.Fields.PARTIAL_RESOURCE_ARRAY1,
                SamplePartialResource.Fields.PARTIAL_RESOURCE_FIELD1,
                SamplePartialResource.Fields.NORMAL_FIELD1)));

        PartialResourceUtils.validate(invalidSampleResourceWithMissingRequiredFields(), SamplePartial.class);
    }

    @Test
    public void shouldValidateFailureWhenNestedPartialResourceDoesNotHaveRequiredFields() {
        expectedException.expect(RequiredFieldsException.class);
        expectedException.expect(where(RequiredFieldsException::getTargetClass, equalTo(SimplePartialResource.class)));
        expectedException.expect(where(RequiredFieldsException::getRequiredFieldNames,
                contains(SimplePartialResource.Fields.NAME, SimplePartialResource.Fields.TITLE)));

        PartialResourceUtils.validate(invalidSampleResourceWithNestedMissingRequiredFields(), SamplePartial.class);
    }

    @Test
    public void shouldValidateFailureWhenPartialResourceInNestedCollectionDoesNotHaveRequiredFields() {
        expectedException.expect(RequiredFieldsException.class);
        expectedException.expect(where(RequiredFieldsException::getTargetClass, equalTo(SimplePartialResource.class)));
        expectedException.expect(where(RequiredFieldsException::getRequiredFieldNames,
                contains(SimplePartialResource.Fields.NAME, SimplePartialResource.Fields.TITLE)));

        PartialResourceUtils
                .validate(invalidSampleResourceWithMissingRequiredFieldsInNestedCollection(), SamplePartial.class);
    }

    @Test
    public void shouldValidateFailureWhenPartialResourceInNestedArrayDoesNotHaveRequiredFields() {
        expectedException.expect(RequiredFieldsException.class);
        expectedException.expect(where(RequiredFieldsException::getTargetClass, equalTo(SimplePartialResource.class)));
        expectedException.expect(where(RequiredFieldsException::getRequiredFieldNames,
                contains(SimplePartialResource.Fields.NAME, SimplePartialResource.Fields.TITLE)));

        PartialResourceUtils
                .validate(invalidSampleResourceWithMissingRequiredFieldsInNestedArray(), SamplePartial.class);
    }

    @Test
    public void shouldCopyForPartialResource() {
        SamplePartialResource first = invalidSampleResourceWithRedundantFields();
        SamplePartialResource second = validSampleResource().setUntouchedField(Long.MAX_VALUE);
        second.getPartialResourceCollection1().add(first.getPartialResourceCollection1().get(0));

        first.copyTo(second);

        // Remember we copy every thing, but:
        // - untouchedField is not in first resource, so it's not supposed to be touched
        // - we replace collection elements, but not the collection itself
        collector.checkThat(((PartialResource) second).getPartialProperties(),
                equalTo(((PartialResource) first).getPartialProperties()));
        collector.checkThat(second.getId(), equalTo(first.getId()));
        collector.checkThat(second.getNormalField1(), equalTo(first.getNormalField1()));
        collector.checkThat(second.getNormalField2(), equalTo(first.getNormalField2()));
        collector.checkThat(second.getPartialResourceField1(), equalTo(first.getPartialResourceField1()));
        collector.checkThat(second.getPartialResourceField2(), equalTo(first.getPartialResourceField2()));
        collector.checkThat(second.getPartialResourceCollection1(), equalTo(first.getPartialResourceCollection1()));
        collector.checkThat(second.getPartialResourceCollection1(),
                not(sameInstance(first.getPartialResourceCollection1())));
        collector
                .checkThat(second.getPartialResourceCollection2(), sameInstance(first.getPartialResourceCollection2()));
        collector.checkThat(second.getUntouchedField(), equalTo(Long.MAX_VALUE));
    }

    @Test
    public void proxyClassShouldBeCached() {
        val sample1 = SamplePartialResource.create();
        val sample2 = SamplePartialResource.create();

        collector.checkThat(sample1.getClass(), sameInstance(sample2.getClass()));
    }

    private static SamplePartialResource validSampleResource() {
        return SamplePartialResource.create()
                .setNormalField1(UUID.randomUUID().toString())
                .setPartialResourceField1(validSimpleResource())
                .setPartialResourceCollection1(validSimpleResourceList())
                .setPartialResourceArray1(validSimpleResources())
                .setId(ThreadLocalRandom.current().nextLong())
                .extractPayload();
    }

    private static SimplePartialResource validSimpleResource() {
        return SimplePartialResource.create()
                .setName(UUID.randomUUID().toString())
                .setTitle(UUID.randomUUID().toString())
                .setId(ThreadLocalRandom.current().nextLong())
                .extractPayload();
    }

    private static List<SimplePartialResource> validSimpleResourceList() {
        return new ArrayList<>(Collections.singleton(validSimpleResource()));
    }

    private static Set<SimplePartialResource> validSimpleResourceSet() {
        return new HashSet<>(Collections.singleton(validSimpleResource()));
    }

    private static SimplePartialResource[] validSimpleResources() {
        return new SimplePartialResource[] {validSimpleResource()};
    }

    private static SimplePartialResource invalidSimpleResourceWithRedundantFields() {
        return SimplePartialResource.create()
                .setName(UUID.randomUUID().toString())
                .setTitle(UUID.randomUUID().toString())
                .setDescription(UUID.randomUUID().toString())
                .setVersion(ThreadLocalRandom.current().nextInt())
                .setId(ThreadLocalRandom.current().nextLong())
                .extractPayload();
    }

    private static List<SimplePartialResource> invalidSimpleResourceCollectionWithRedundantFields() {
        return new ArrayList<>(Collections.singleton(invalidSimpleResourceWithRedundantFields()));
    }

    private static SimplePartialResource[] invalidSimpleResourcesWithRedundantFields() {
        return new SimplePartialResource[] {invalidSimpleResourceWithRedundantFields()};
    }

    private static SimplePartialResource invalidSimpleResourceWithoutRequiredFields() {
        return SimplePartialResource.create()
                .setId(ThreadLocalRandom.current().nextLong())
                .extractPayload();
    }

    private static List<SimplePartialResource> invalidSimpleResourceCollectionWithoutRequiredFields() {
        return new ArrayList<>(Collections.singleton(invalidSimpleResourceWithoutRequiredFields()));
    }

    private static SimplePartialResource[] invalidSimpleResourcesWithoutRequiredFields() {
        return new SimplePartialResource[] {invalidSimpleResourceWithoutRequiredFields()};
    }

    private static SamplePartialResource invalidSampleResourceWithRedundantFields() {
        return SamplePartialResource.create()
                .setNormalField1(UUID.randomUUID().toString())
                .setNormalField2(ThreadLocalRandom.current().nextInt())
                .setPartialResourceField1(validSimpleResource())
                .setPartialResourceField2(validSimpleResource())
                .setPartialResourceCollection1(validSimpleResourceList())
                .setPartialResourceCollection2(validSimpleResourceSet())
                .setPartialResourceArray1(validSimpleResources())
                .setPartialResourceArray2(validSimpleResources())
                .setId(ThreadLocalRandom.current().nextLong())
                .extractPayload();
    }

    private static SamplePartialResource invalidSampleResourceWithNestedRedundantFields() {
        return SamplePartialResource.create()
                .setNormalField1(UUID.randomUUID().toString())
                .setPartialResourceField1(invalidSimpleResourceWithRedundantFields())
                .setPartialResourceCollection1(validSimpleResourceList())
                .setPartialResourceArray1(validSimpleResources())
                .setId(ThreadLocalRandom.current().nextLong())
                .extractPayload();
    }

    private static SamplePartialResource invalidSampleResourceWithRedundantFieldsInNestedCollection() {
        return SamplePartialResource.create()
                .setNormalField1(UUID.randomUUID().toString())
                .setPartialResourceField1(validSimpleResource())
                .setPartialResourceCollection1(invalidSimpleResourceCollectionWithRedundantFields())
                .setPartialResourceArray1(validSimpleResources())
                .setId(ThreadLocalRandom.current().nextLong())
                .extractPayload();
    }

    private static SamplePartialResource invalidSampleResourceWithRedundantFieldsInNestedArray() {
        return SamplePartialResource.create()
                .setNormalField1(UUID.randomUUID().toString())
                .setPartialResourceField1(validSimpleResource())
                .setPartialResourceCollection1(validSimpleResourceList())
                .setPartialResourceArray1(invalidSimpleResourcesWithRedundantFields())
                .setId(ThreadLocalRandom.current().nextLong())
                .extractPayload();
    }

    private static SamplePartialResource invalidSampleResourceWithMissingRequiredFields() {
        return SamplePartialResource.create()
                .setPartialResourceCollection1(validSimpleResourceList())
                .setId(ThreadLocalRandom.current().nextLong())
                .extractPayload();
    }

    private static SamplePartialResource invalidSampleResourceWithNestedMissingRequiredFields() {
        return SamplePartialResource.create()
                .setNormalField1(UUID.randomUUID().toString())
                .setPartialResourceField1(invalidSimpleResourceWithoutRequiredFields())
                .setPartialResourceCollection1(validSimpleResourceList())
                .setPartialResourceArray1(validSimpleResources())
                .setId(ThreadLocalRandom.current().nextLong())
                .extractPayload();
    }

    private static SamplePartialResource invalidSampleResourceWithMissingRequiredFieldsInNestedCollection() {
        return SamplePartialResource.create()
                .setNormalField1(UUID.randomUUID().toString())
                .setPartialResourceField1(validSimpleResource())
                .setPartialResourceCollection1(invalidSimpleResourceCollectionWithoutRequiredFields())
                .setPartialResourceArray1(validSimpleResources())
                .setId(ThreadLocalRandom.current().nextLong())
                .extractPayload();
    }

    private static SamplePartialResource invalidSampleResourceWithMissingRequiredFieldsInNestedArray() {
        return SamplePartialResource.create()
                .setNormalField1(UUID.randomUUID().toString())
                .setPartialResourceField1(validSimpleResource())
                .setPartialResourceCollection1(validSimpleResourceList())
                .setPartialResourceArray1(invalidSimpleResourcesWithoutRequiredFields())
                .setId(ThreadLocalRandom.current().nextLong())
                .extractPayload();
    }
}
