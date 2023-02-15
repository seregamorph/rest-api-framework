package com.seregamorph.restapi.test.base.setup;

import static com.seregamorph.restapi.test.base.ResultType.SINGLE;
import static com.seregamorph.restapi.test.base.support.RequestType.NO_RETRIEVAL;
import static org.springframework.http.HttpMethod.PUT;

import com.seregamorph.restapi.test.base.ResultType;
import com.seregamorph.restapi.test.base.request.PutRequest;
import com.seregamorph.restapi.test.base.support.AroundRequestActionSupport;
import com.seregamorph.restapi.test.base.support.AroundRequestActionSupportDelegate;
import com.seregamorph.restapi.test.base.support.HeaderSupport;
import com.seregamorph.restapi.test.base.support.HeaderSupportDelegate;
import com.seregamorph.restapi.test.base.support.ParameterSupport;
import com.seregamorph.restapi.test.base.support.ParameterSupportDelegate;
import com.seregamorph.restapi.test.base.support.PayloadSupport;
import com.seregamorph.restapi.test.base.support.PayloadSupportDelegate;
import com.seregamorph.restapi.test.base.support.ProjectionSupport;
import com.seregamorph.restapi.test.base.support.ProjectionSupportDelegate;
import com.seregamorph.restapi.test.base.support.RequestTypeSupport;
import com.seregamorph.restapi.test.base.support.RequestTypeSupportDelegate;
import javax.annotation.Nonnull;
import lombok.Getter;

@Getter
public class PutSetup extends BaseSetup<PutSetup, PutRequest> implements
        ProjectionSupportDelegate<PutSetup>,
        ParameterSupportDelegate<PutSetup>,
        HeaderSupportDelegate<PutSetup>,
        PayloadSupportDelegate<PutSetup>,
        RequestTypeSupportDelegate<PutSetup>,
        AroundRequestActionSupportDelegate<PutSetup> {

    private final ProjectionSupport<PutSetup> projectionSupport;
    private final ParameterSupport<PutSetup> parameterSupport;
    private final HeaderSupport<PutSetup> headerSupport;
    private final PayloadSupport<PutSetup> payloadSupport;
    private final RequestTypeSupport<PutSetup> requestTypeSupport;
    private final AroundRequestActionSupport<PutSetup> aroundRequestActionSupport;

    private ResultType resultType;

    public PutSetup(@Nonnull String pathTemplate, Object... pathVariables) {
        super(PUT, pathTemplate, pathVariables);
        projectionSupport = new ProjectionSupport<>(this);
        parameterSupport = new ParameterSupport<>(this);
        headerSupport = new HeaderSupport<>(this);
        payloadSupport = new PayloadSupport<>(this);
        requestTypeSupport = new RequestTypeSupport<>(this, NO_RETRIEVAL);
        aroundRequestActionSupport = new AroundRequestActionSupport<>(this);
    }

    public PutSetup setResultType(ResultType resultType) {
        this.resultType = resultType;
        return this;
    }

    @Override
    public ResultType getResultType() {
        return resultType == null ? getDefaultResultType() : resultType;
    }

    @Override
    ResultType getDefaultResultType() {
        return SINGLE;
    }
}
