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
public class PostRequest extends BaseRequest<PostRequest> implements
        RequestHeaderSupportDelegate<PostRequest>,
        RequestParameterSupportDelegate<PostRequest>,
        RequestPayloadSupportDelegate<PostRequest>,
        RequestResultMatcherSupportDelegate<PostRequest> {

    private final RequestHeaderSupport<PostRequest> requestHeaderSupport;
    private final RequestParameterSupport<PostRequest> requestParameterSupport;
    private final RequestPayloadSupport<PostRequest> requestPayloadSupport;
    private final RequestResultMatcherSupport<PostRequest> requestResultMatcherSupport;

    @SuppressWarnings("WeakerAccess")
    public PostRequest(@Nonnull String name) {
        super(name);
        this.requestHeaderSupport = new RequestHeaderSupport<>(this);
        this.requestParameterSupport = new RequestParameterSupport<>(this);
        this.requestPayloadSupport = new RequestPayloadSupport<>(this);
        this.requestResultMatcherSupport = new RequestResultMatcherSupport<>(this);
    }
}
