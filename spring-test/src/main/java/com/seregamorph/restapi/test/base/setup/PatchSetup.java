package com.seregamorph.restapi.test.base.setup;

import static com.seregamorph.restapi.test.base.ResultType.SINGLE;
import static com.seregamorph.restapi.test.base.support.RequestType.NO_RETRIEVAL;
import static org.springframework.http.HttpMethod.PATCH;

import com.seregamorph.restapi.test.base.ResultType;
import com.seregamorph.restapi.test.base.request.PatchRequest;
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
import com.seregamorph.restapi.test.base.support.RepeatedCheckSupport;
import com.seregamorph.restapi.test.base.support.RepeatedCheckSupportDelegate;
import com.seregamorph.restapi.test.base.support.RequestTypeSupport;
import com.seregamorph.restapi.test.base.support.RequestTypeSupportDelegate;
import javax.annotation.Nonnull;
import lombok.Getter;

@Getter
public class PatchSetup extends BaseSetup<PatchSetup, PatchRequest> implements
        ProjectionSupportDelegate<PatchSetup>,
        ParameterSupportDelegate<PatchSetup>,
        HeaderSupportDelegate<PatchSetup>,
        PayloadSupportDelegate<PatchSetup>,
        RequestTypeSupportDelegate<PatchSetup>,
        AroundRequestActionSupportDelegate<PatchSetup>,
        RepeatedCheckSupportDelegate<PatchSetup> {

    private final ProjectionSupport<PatchSetup> projectionSupport;
    private final ParameterSupport<PatchSetup> parameterSupport;
    private final HeaderSupport<PatchSetup> headerSupport;
    private final PayloadSupport<PatchSetup> payloadSupport;
    private final RequestTypeSupport<PatchSetup> requestTypeSupport;
    private final AroundRequestActionSupport<PatchSetup> aroundRequestActionSupport;
    private final RepeatedCheckSupport<PatchSetup> repeatedCheckSupport;

    public PatchSetup(@Nonnull String pathTemplate, Object... pathVariables) {
        super(PATCH, pathTemplate, pathVariables);
        projectionSupport = new ProjectionSupport<>(this);
        parameterSupport = new ParameterSupport<>(this);
        headerSupport = new HeaderSupport<>(this);
        payloadSupport = new PayloadSupport<>(this);
        requestTypeSupport = new RequestTypeSupport<>(this, NO_RETRIEVAL);
        aroundRequestActionSupport = new AroundRequestActionSupport<>(this);
        repeatedCheckSupport = new RepeatedCheckSupport<>(this);
    }

    @Override
    ResultType getDefaultResultType() {
        return SINGLE;
    }
}
