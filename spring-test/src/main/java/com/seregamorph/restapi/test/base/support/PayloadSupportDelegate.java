package com.seregamorph.restapi.test.base.support;

import static com.seregamorph.restapi.test.base.InvalidPayloadStatus.BAD_REQUEST;
import static com.seregamorph.restapi.test.base.ResultMatchers.collect;

import com.fasterxml.jackson.databind.JsonNode;
import com.seregamorph.restapi.base.BasePayload;
import com.seregamorph.restapi.partial.PartialPayload;
import com.seregamorph.restapi.test.base.InvalidPayloadStatus;
import com.seregamorph.restapi.test.base.setup.BaseSetup;
import com.seregamorph.restapi.test.base.setup.common.VerifiablePayload;
import com.seregamorph.restapi.test.base.setup.common.payload.GenericPayload;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.springframework.test.web.servlet.ResultMatcher;

public interface PayloadSupportDelegate<P extends BaseSetup<P, ?>> {

    PayloadSupport<P> getPayloadSupport();

    default P setRequestPayload(GenericPayload requestPayload) {
        return getPayloadSupport().setRequestPayload(requestPayload);
    }

    default P setRequestPayload(JsonNode requestPayload) {
        return getPayloadSupport().setRequestPayload(requestPayload);
    }

    default P setRequestPayload(PartialPayload requestPayload) {
        return getPayloadSupport().setRequestPayload(requestPayload);
    }

    default P setRequestPayload(Collection<? extends PartialPayload> requestPayload) {
        return getPayloadSupport().setRequestPayload(requestPayload);
    }

    default boolean hasGenericPayload() {
        return getPayloadSupport().hasGenericPayload();
    }

    default GenericPayload getGenericPayload() {
        return getPayloadSupport().getGenericPayload();
    }

    default Object getDefaultPayload() {
        return getPayloadSupport().getDefaultPayload();
    }

    default boolean hasDefaultPayload() {
        return getPayloadSupport().hasDefaultPayload();
    }

    default Object getMinimalPayload() {
        return getPayloadSupport().getMinimalPayload();
    }

    default boolean hasMinimalPayload() {
        return getPayloadSupport().hasMinimalPayload();
    }

    default boolean hasPayloadsWithoutRequiredFields() {
        return getPayloadSupport().hasPayloadsWithoutRequiredFields();
    }

    default boolean hasPayloadsWithOptionalFields() {
        return getPayloadSupport().hasPayloadsWithOptionalFields();
    }

    default boolean hasPayloadsWithRedundantFields() {
        return getPayloadSupport().hasPayloadsWithRedundantFields();
    }

    default List<VerifiablePayload> getValidPayloads() {
        return getPayloadSupport().getValidPayloads();
    }

    default P putValidPayload(String name, Object validPayload, ResultMatcher... resultMatchers) {
        return putValidPayload(name, validPayload, Arrays.asList(resultMatchers));
    }

    default P putValidPayload(String name, Object validPayload, Collection<ResultMatcher> resultMatchers) {
        return getPayloadSupport().putValidPayload(name, validPayload, collect(resultMatchers));
    }

    default P putValidPayload(String name, Object validPayload, BasePayload jsonMatchingPayload) {
        return getPayloadSupport().putValidPayload(name, validPayload, jsonMatchingPayload);
    }

    default List<VerifiablePayload> getInvalidPayloads() {
        return getPayloadSupport().getInvalidPayloads();
    }

    default P putInvalidPayload(String name, Object invalidPayload, ResultMatcher... resultMatchers) {
        return putInvalidPayload(name, invalidPayload, Arrays.asList(resultMatchers));
    }

    default P putInvalidPayload(String name, Object invalidPayload, Collection<ResultMatcher> resultMatchers) {
        return putInvalidPayload(name, invalidPayload, BAD_REQUEST, resultMatchers);
    }

    default P putInvalidPayload(String name, Object invalidPayload, InvalidPayloadStatus invalidPayloadStatus,
                                ResultMatcher... resultMatchers) {
        return putInvalidPayload(name, invalidPayload, invalidPayloadStatus, Arrays.asList(resultMatchers));
    }

    default P putInvalidPayload(String name, Object invalidPayload, InvalidPayloadStatus invalidPayloadStatus,
                                Collection<ResultMatcher> resultMatchers) {
        return getPayloadSupport()
                .putInvalidPayload(name, invalidPayload, invalidPayloadStatus, collect(resultMatchers));
    }

}
