package com.seregamorph.restapi.test.base.support;

import static com.seregamorph.restapi.test.base.setup.common.payload.GenericPayloadEnhancer.enhance;

import com.fasterxml.jackson.databind.JsonNode;
import com.seregamorph.restapi.base.BasePayload;
import com.seregamorph.restapi.partial.PartialPayload;
import com.seregamorph.restapi.test.base.InvalidPayloadStatus;
import com.seregamorph.restapi.test.base.setup.BaseSetup;
import com.seregamorph.restapi.test.base.setup.common.VerifiablePayload;
import com.seregamorph.restapi.test.base.setup.common.payload.GenericPayload;
import com.seregamorph.restapi.test.base.setup.common.payload.GenericPayloads;
import com.seregamorph.restapi.test.base.setup.common.payload.PayloadUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.test.web.servlet.ResultMatcher;

@SuppressWarnings({"unused", "WeakerAccess"})
@RequiredArgsConstructor
public class PayloadSupport<P extends BaseSetup<P, ?>> {

    private final P parent;

    @Getter
    private GenericPayload genericPayload;

    @Getter
    private Object defaultPayload;

    @Getter
    private Object minimalPayload;

    @Getter
    private final List<VerifiablePayload> validPayloads = new ArrayList<>();

    @Getter
    private final List<VerifiablePayload> invalidPayloads = new ArrayList<>();

    public P setRequestPayload(@Nonnull GenericPayload requestPayload) {
        this.genericPayload = enhance(requestPayload);
        this.defaultPayload = this.genericPayload.getDefaultPayload();
        this.minimalPayload = this.genericPayload.getMinimalPayload();
        return this.parent;
    }

    public P setRequestPayload(@Nonnull PartialPayload requestPayload) {
        if (GenericPayloads.isGenericPayloadProxy(requestPayload)) {
            return setRequestPayload(GenericPayloads.genericPayloadOf(requestPayload));
        } else {
            this.genericPayload = null;
            this.defaultPayload = requestPayload;
            this.minimalPayload = null;
            return this.parent;
        }
    }

    public P setRequestPayload(Collection<? extends PartialPayload> requestPayload) {
        val unwrapped = PayloadUtils.payload(requestPayload);
        if (unwrapped instanceof GenericPayload) {
            return setRequestPayload((GenericPayload) unwrapped);
        } else {
            this.genericPayload = null;
            this.defaultPayload = requestPayload;
            this.minimalPayload = null;
            return this.parent;
        }
    }

    public P setRequestPayload(@Nonnull JsonNode requestPayload) {
        this.genericPayload = null;
        this.defaultPayload = requestPayload;
        this.minimalPayload = null;
        return this.parent;
    }

    public boolean hasGenericPayload() {
        return this.genericPayload != null;
    }

    public boolean hasDefaultPayload() {
        return this.defaultPayload != null;
    }

    public boolean hasMinimalPayload() {
        return this.minimalPayload != null;
    }

    public boolean hasPayloadsWithoutRequiredFields() {
        return this.genericPayload != null && this.genericPayload.hasRequiredFields();
    }

    public boolean hasPayloadsWithOptionalFields() {
        return this.genericPayload != null && this.genericPayload.hasOptionalFields();
    }

    public boolean hasPayloadsWithRedundantFields() {
        return this.genericPayload != null && this.genericPayload.hasRedundantFields();
    }

    public P putValidPayload(String name, Object validPayload,
                             BasePayload jsonMatchingPayload) {
        val resultMatcher = parent.getResultType()
                .matcherOf(jsonMatchingPayload);
        return putValidPayload(name, validPayload, resultMatcher);
    }

    public P putValidPayload(@Nonnull String name, @Nonnull Object validPayload, ResultMatcher resultMatcher) {
        this.validPayloads.add(new VerifiablePayload(name, validPayload, result -> {
            throw new AssertionError("Should never be called for valid payloads");
        }, resultMatcher));
        return this.parent;
    }

    public P putInvalidPayload(@Nonnull String name, @Nonnull Object invalidPayload,
                               InvalidPayloadStatus invalidPayloadStatus, ResultMatcher resultMatcher) {
        this.invalidPayloads.add(new VerifiablePayload(name, invalidPayload,
                invalidPayloadStatus.getStatusMatcher(), resultMatcher));
        return this.parent;
    }

}
