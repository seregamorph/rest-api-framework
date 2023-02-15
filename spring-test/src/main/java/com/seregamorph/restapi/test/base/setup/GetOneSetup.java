package com.seregamorph.restapi.test.base.setup;

import static com.seregamorph.restapi.test.base.ResultType.SINGLE;
import static org.springframework.http.HttpMethod.GET;

import com.seregamorph.restapi.test.base.ResultType;
import com.seregamorph.restapi.test.base.request.GetOneRequest;
import com.seregamorph.restapi.test.base.support.AroundRequestActionSupport;
import com.seregamorph.restapi.test.base.support.AroundRequestActionSupportDelegate;
import com.seregamorph.restapi.test.base.support.HeaderSupport;
import com.seregamorph.restapi.test.base.support.HeaderSupportDelegate;
import com.seregamorph.restapi.test.base.support.ParameterSupport;
import com.seregamorph.restapi.test.base.support.ParameterSupportDelegate;
import com.seregamorph.restapi.test.base.support.ProjectionSupport;
import com.seregamorph.restapi.test.base.support.ProjectionSupportDelegate;
import com.seregamorph.restapi.test.base.support.RepeatedCheckSupport;
import com.seregamorph.restapi.test.base.support.RepeatedCheckSupportDelegate;
import javax.annotation.Nonnull;
import lombok.Getter;

@Getter
public class GetOneSetup extends BaseSetup<GetOneSetup, GetOneRequest> implements
        ProjectionSupportDelegate<GetOneSetup>,
        ParameterSupportDelegate<GetOneSetup>,
        HeaderSupportDelegate<GetOneSetup>,
        RepeatedCheckSupportDelegate<GetOneSetup>,
        AroundRequestActionSupportDelegate<GetOneSetup> {

    private final ProjectionSupport<GetOneSetup> projectionSupport;
    private final ParameterSupport<GetOneSetup> parameterSupport;
    private final HeaderSupport<GetOneSetup> headerSupport;
    private final RepeatedCheckSupport<GetOneSetup> repeatedCheckSupport;
    private final AroundRequestActionSupport<GetOneSetup> aroundRequestActionSupport;

    public GetOneSetup(@Nonnull String pathTemplate, Object... pathVariables) {
        super(GET, pathTemplate, pathVariables);
        projectionSupport = new ProjectionSupport<>(this);
        parameterSupport = new ParameterSupport<>(this);
        headerSupport = new HeaderSupport<>(this);
        repeatedCheckSupport = new RepeatedCheckSupport<>(this);
        aroundRequestActionSupport = new AroundRequestActionSupport<>(this);
    }

    @Override
    ResultType getDefaultResultType() {
        return SINGLE;
    }
}
