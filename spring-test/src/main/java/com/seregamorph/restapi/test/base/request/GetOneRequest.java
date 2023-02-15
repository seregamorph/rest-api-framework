package com.seregamorph.restapi.test.base.request;

import com.seregamorph.restapi.test.base.request.support.BaseRequest;
import com.seregamorph.restapi.test.base.request.support.RequestHeaderSupport;
import com.seregamorph.restapi.test.base.request.support.RequestHeaderSupportDelegate;
import com.seregamorph.restapi.test.base.request.support.RequestParameterSupport;
import com.seregamorph.restapi.test.base.request.support.RequestParameterSupportDelegate;
import com.seregamorph.restapi.test.base.request.support.RequestResultMatcherSupport;
import com.seregamorph.restapi.test.base.request.support.RequestResultMatcherSupportDelegate;
import com.seregamorph.restapi.test.base.request.support.RequestStatusSupport;
import com.seregamorph.restapi.test.base.request.support.RequestStatusSupportDelegate;
import javax.annotation.Nonnull;
import lombok.Getter;

@Getter
public class GetOneRequest extends BaseRequest<GetOneRequest> implements
        RequestHeaderSupportDelegate<GetOneRequest>,
        RequestParameterSupportDelegate<GetOneRequest>,
        RequestStatusSupportDelegate<GetOneRequest>,
        RequestResultMatcherSupportDelegate<GetOneRequest> {

    private final RequestHeaderSupport<GetOneRequest> requestHeaderSupport;
    private final RequestParameterSupport<GetOneRequest> requestParameterSupport;
    private final RequestStatusSupport<GetOneRequest> requestStatusSupport;
    private final RequestResultMatcherSupport<GetOneRequest> requestResultMatcherSupport;

    @SuppressWarnings("WeakerAccess")
    public GetOneRequest(@Nonnull String name) {
        super(name);
        this.requestHeaderSupport = new RequestHeaderSupport<>(this);
        this.requestParameterSupport = new RequestParameterSupport<>(this);
        this.requestStatusSupport = new RequestStatusSupport<>(this);
        this.requestResultMatcherSupport = new RequestResultMatcherSupport<>(this);
    }
}
