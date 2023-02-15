package com.seregamorph.restapi.test.base.request;

import com.seregamorph.restapi.test.base.request.support.BaseRequest;
import javax.annotation.Nonnull;

public class DeleteRequest extends BaseRequest<DeleteRequest> {

    @SuppressWarnings("WeakerAccess")
    public DeleteRequest(@Nonnull String name) {
        super(name);
    }
}
