package com.seregamorph.restapi.test.base.setup;

import static org.springframework.http.HttpMethod.DELETE;

import com.seregamorph.restapi.test.base.ResultType;
import com.seregamorph.restapi.test.base.request.DeleteRequest;
import com.seregamorph.restapi.test.base.support.AroundRequestActionSupport;
import com.seregamorph.restapi.test.base.support.AroundRequestActionSupportDelegate;
import javax.annotation.Nonnull;
import lombok.Getter;
import lombok.Setter;

@Getter
public class DeleteSetup extends BaseSetup<DeleteSetup, DeleteRequest> implements
        AroundRequestActionSupportDelegate<DeleteSetup> {

    private final AroundRequestActionSupport<DeleteSetup> aroundRequestActionSupport;

    /**
     * Note: in both cases DELETE is idempotent (by definition: final state is the same),
     * but in some APIs success 2xx code may be expected on the client.
     */
    @Setter
    private boolean handle204onMissingEntity = false;

    public DeleteSetup(@Nonnull String pathTemplate, Object... pathVariables) {
        super(DELETE, pathTemplate, pathVariables);
        this.aroundRequestActionSupport = new AroundRequestActionSupport<>(this);
    }

    @Override
    ResultType getDefaultResultType() {
        throw new UnsupportedOperationException();
    }

}
