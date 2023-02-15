package com.seregamorph.restapi.test.base.request;

import com.seregamorph.restapi.test.base.request.support.BaseRequest;
import com.seregamorph.restapi.test.base.request.support.RequestHeaderSupport;
import com.seregamorph.restapi.test.base.request.support.RequestHeaderSupportDelegate;
import com.seregamorph.restapi.test.base.request.support.RequestParameterSupport;
import com.seregamorph.restapi.test.base.request.support.RequestParameterSupportDelegate;
import com.seregamorph.restapi.test.base.request.support.RequestPayloadSupport;
import com.seregamorph.restapi.test.base.request.support.RequestPayloadSupportDelegate;
import com.seregamorph.restapi.test.base.request.support.RequestResultMatcherSupport;
import com.seregamorph.restapi.test.base.request.support.RequestResultMatcherSupportDelegate;
import javax.annotation.Nonnull;
import lombok.Getter;

@Getter
public class PatchRequest extends BaseRequest<PatchRequest> implements
        RequestHeaderSupportDelegate<PatchRequest>,
        RequestParameterSupportDelegate<PatchRequest>,
        RequestPayloadSupportDelegate<PatchRequest>,
        RequestResultMatcherSupportDelegate<PatchRequest> {

    private final RequestHeaderSupport<PatchRequest> requestHeaderSupport;
    private final RequestParameterSupport<PatchRequest> requestParameterSupport;
    private final RequestPayloadSupport<PatchRequest> requestPayloadSupport;
    private final RequestResultMatcherSupport<PatchRequest> requestResultMatcherSupport;

    @SuppressWarnings("WeakerAccess")
    public PatchRequest(@Nonnull String name) {
        super(name);
        this.requestHeaderSupport = new RequestHeaderSupport<>(this);
        this.requestParameterSupport = new RequestParameterSupport<>(this);
        this.requestPayloadSupport = new RequestPayloadSupport<>(this);
        this.requestResultMatcherSupport = new RequestResultMatcherSupport<>(this);
    }
}
