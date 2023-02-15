package com.seregamorph.restapi.test.base.setup;

import static com.seregamorph.restapi.test.base.ResultType.SINGLE;
import static org.springframework.http.HttpMethod.POST;

import com.seregamorph.restapi.test.base.ResultType;
import com.seregamorph.restapi.test.base.request.PostRequest;
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
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

@Getter
public class PostSetup extends BaseSetup<PostSetup, PostRequest> implements
        ProjectionSupportDelegate<PostSetup>,
        ParameterSupportDelegate<PostSetup>,
        HeaderSupportDelegate<PostSetup>,
        PayloadSupportDelegate<PostSetup>,
        RequestTypeSupportDelegate<PostSetup>,
        AroundRequestActionSupportDelegate<PostSetup> {

    private final ProjectionSupport<PostSetup> projectionSupport;
    private final ParameterSupport<PostSetup> parameterSupport;
    private final HeaderSupport<PostSetup> headerSupport;
    private final PayloadSupport<PostSetup> payloadSupport;
    private final RequestTypeSupport<PostSetup> requestTypeSupport;
    private final AroundRequestActionSupport<PostSetup> aroundRequestActionSupport;

    private ResultType resultType;

    /**
     * Location header ant matcher pattern (for path). If null, default matcher is used. Note: this field may be
     * set only when requestType is null.
     *
     * @see org.springframework.util.AntPathMatcher
     */
    @Setter
    @Nullable
    private String locationHeaderAntPattern;

    public PostSetup() {
        this("");
    }

    public PostSetup(@Nonnull String pathTemplate, Object... pathVariables) {
        super(POST, pathTemplate, pathVariables);
        projectionSupport = new ProjectionSupport<>(this);
        parameterSupport = new ParameterSupport<>(this);
        headerSupport = new HeaderSupport<>(this);
        payloadSupport = new PayloadSupport<>(this);
        requestTypeSupport = new RequestTypeSupport<>(this, null);
        aroundRequestActionSupport = new AroundRequestActionSupport<>(this);
    }

    public PostSetup setResultType(ResultType resultType) {
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
