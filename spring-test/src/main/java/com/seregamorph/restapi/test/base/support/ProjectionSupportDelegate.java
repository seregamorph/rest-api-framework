package com.seregamorph.restapi.test.base.support;

import com.seregamorph.restapi.base.BasePayload;
import com.seregamorph.restapi.base.ProjectionName;
import com.seregamorph.restapi.test.base.ResultType;
import com.seregamorph.restapi.test.base.setup.BaseSetup;
import com.seregamorph.restapi.test.base.setup.common.VerifiableProjection;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import org.springframework.test.web.servlet.ResultMatcher;

public interface ProjectionSupportDelegate<P extends BaseSetup<P, ?>> {

    ProjectionSupport<P> getProjectionSupport();

    ResultType getResultType();

    default P setDefaultResultMatchers(BasePayload jsonMatchingPayload) {
        return getProjectionSupport().setDefaultResultMatchers(jsonMatchingPayload);
    }

    default P setDefaultResultMatchers(ResultMatcher... matchers) {
        return getProjectionSupport().setDefaultResultMatchers(Arrays.asList(matchers));
    }

    default P setDefaultResultMatchers(@Nonnull Collection<ResultMatcher> matchers) {
        return getProjectionSupport().setDefaultResultMatchers(matchers);
    }

    default P supportProjection(Enum<? extends ProjectionName> projection,
                                BasePayload jsonMatchingPayload) {
        return getProjectionSupport().supportProjection(projection, jsonMatchingPayload);
    }

    default P supportProjection(@Nonnull Enum<? extends ProjectionName> projection,
                                ResultMatcher... matchers) {
        return supportProjection(projection, Arrays.asList(matchers));
    }

    default P supportProjection(@Nonnull Enum<? extends ProjectionName> projection,
                                @Nonnull Collection<ResultMatcher> matchers) {
        return getProjectionSupport().supportProjection(projection, matchers);
    }

    default P forbidProjection(@Nonnull Enum<? extends ProjectionName> projection,
                               ResultMatcher... matchers) {
        return forbidProjection(projection, Arrays.asList(matchers));
    }

    default P forbidProjection(@Nonnull Enum<? extends ProjectionName> projection,
                               @Nonnull Collection<ResultMatcher> matchers) {
        return getProjectionSupport().forbidProjection(projection, matchers);
    }

    default boolean hasProjectionResultMatchers() {
        return getProjectionSupport().hasProjectionResultMatchers();
    }

    default List<VerifiableProjection> getForbiddenProjectionResultMatchers() {
        return getProjectionSupport().getForbiddenProjectionResultMatchers();
    }

    default Collection<ResultMatcher> getDefaultResultMatchers() {
        return getProjectionSupport().getDefaultResultMatchers();
    }

    default List<VerifiableProjection> getProjectionResultMatchers() {
        return getProjectionSupport().getProjectionResultMatchers();
    }

    default Set<Enum<?>> extractMissingProjections() {
        return getProjectionSupport().extractMissingProjections();
    }
}
