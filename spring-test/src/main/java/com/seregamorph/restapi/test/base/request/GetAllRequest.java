package com.seregamorph.restapi.test.base.request;

import static lombok.AccessLevel.NONE;

import com.seregamorph.restapi.test.base.request.support.BaseRequest;
import com.seregamorph.restapi.test.base.request.support.RequestHeaderSupport;
import com.seregamorph.restapi.test.base.request.support.RequestHeaderSupportDelegate;
import com.seregamorph.restapi.test.base.request.support.RequestParameterSupport;
import com.seregamorph.restapi.test.base.request.support.RequestParameterSupportDelegate;
import com.seregamorph.restapi.test.base.request.support.RequestResultMatcherSupport;
import com.seregamorph.restapi.test.base.request.support.RequestResultMatcherSupportDelegate;
import com.seregamorph.restapi.test.base.request.support.RequestStatusSupport;
import com.seregamorph.restapi.test.base.request.support.RequestStatusSupportDelegate;
import com.seregamorph.restapi.test.base.support.PaginationSupportDelegate;
import com.seregamorph.restapi.test.base.support.QuantityVerificationMode;
import javax.annotation.Nonnull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GetAllRequest extends BaseRequest<GetAllRequest> implements
        RequestHeaderSupportDelegate<GetAllRequest>,
        RequestParameterSupportDelegate<GetAllRequest>,
        RequestStatusSupportDelegate<GetAllRequest>,
        RequestResultMatcherSupportDelegate<GetAllRequest> {

    private final RequestHeaderSupport<GetAllRequest> requestHeaderSupport;
    private final RequestParameterSupport<GetAllRequest> requestParameterSupport;
    private final RequestStatusSupport<GetAllRequest> requestStatusSupport;
    private final RequestResultMatcherSupport<GetAllRequest> requestResultMatcherSupport;

    @Getter(NONE)
    private Integer totalElements;

    @Getter(NONE)
    private QuantityVerificationMode quantityVerificationMode;

    @SuppressWarnings("WeakerAccess")
    public GetAllRequest(@Nonnull String name) {
        super(name);
        this.requestHeaderSupport = new RequestHeaderSupport<>(this);
        this.requestParameterSupport = new RequestParameterSupport<>(this);
        this.requestStatusSupport = new RequestStatusSupport<>(this);
        this.requestResultMatcherSupport = new RequestResultMatcherSupport<>(this);
    }

    public Integer getTotalElements(PaginationSupportDelegate<?> setup) {
        return totalElements == null ? setup.getTotalElements() : totalElements;
    }

    public QuantityVerificationMode getQuantityVerificationMode(PaginationSupportDelegate<?> setup) {
        return quantityVerificationMode == null ? setup.getQuantityVerificationMode() : quantityVerificationMode;
    }
}
