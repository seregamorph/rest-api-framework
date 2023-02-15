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
public class PutRequest extends BaseRequest<PutRequest> implements
        RequestHeaderSupportDelegate<PutRequest>,
        RequestParameterSupportDelegate<PutRequest>,
        RequestPayloadSupportDelegate<PutRequest>,
        RequestResultMatcherSupportDelegate<PutRequest> {

    private final RequestHeaderSupport<PutRequest> requestHeaderSupport;
    private final RequestParameterSupport<PutRequest> requestParameterSupport;
    private final RequestPayloadSupport<PutRequest> requestPayloadSupport;
    private final RequestResultMatcherSupport<PutRequest> requestResultMatcherSupport;

    @SuppressWarnings("WeakerAccess")
    public PutRequest(@Nonnull String name) {
        super(name);
        this.requestHeaderSupport = new RequestHeaderSupport<>(this);
        this.requestParameterSupport = new RequestParameterSupport<>(this);
        this.requestPayloadSupport = new RequestPayloadSupport<>(this);
        this.requestResultMatcherSupport = new RequestResultMatcherSupport<>(this);
    }
}
