package com.seregamorph.restapi.test.base.support;

import static java.util.Collections.singletonList;

import com.seregamorph.restapi.base.BasePayload;
import com.seregamorph.restapi.base.ProjectionName;
import com.seregamorph.restapi.test.base.setup.BaseSetup;
import com.seregamorph.restapi.test.base.setup.common.VerifiableProjection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.test.web.servlet.ResultMatcher;

/**
 * <ul>
 * <li>Having support for projection mechanism doesn't mean having support for projection headers. If you explicitly
 * set the result matchers for a specific projection enum, that means the endpoint supports projection headers.</li>
 * <li>In the case the endpoint returns a collection, result matchers should only validate the first element.</li>
 * </ul>
 */
@SuppressWarnings({"unused", "WeakerAccess"})
@RequiredArgsConstructor
public class ProjectionSupport<P extends BaseSetup<P, ?>> {

    private final P parent;

    @Getter
    private final List<VerifiableProjection> projectionResultMatchers = new ArrayList<>();

    @Getter
    private final List<VerifiableProjection> forbiddenProjectionResultMatchers = new ArrayList<>();

    @Getter
    private final Collection<ResultMatcher> defaultResultMatchers = new HashSet<>();

    public Set<Enum<?>> extractMissingProjections() {
        Enum<?>[] projections = new Enum[0];

        if (hasProjectionResultMatchers()) {
            projections = this.projectionResultMatchers.stream()
                    .map(VerifiableProjection::getProjection)
                    .filter(Objects::nonNull)
                    .map(projection -> projection.getClass().getEnumConstants())
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Should have at least one non-null projection"));
        }

        val missingProjections = new HashSet<>(Arrays.asList(projections));
        projectionResultMatchers.forEach(projection -> missingProjections.remove(projection.getProjection()));
        forbiddenProjectionResultMatchers.forEach(projection -> missingProjections.remove(projection.getProjection()));

        return missingProjections;
    }

    public boolean hasProjectionResultMatchers() {
        return !this.projectionResultMatchers.isEmpty();
    }

    public P supportProjection(@Nonnull Enum<? extends ProjectionName> projection, @Nonnull Collection<ResultMatcher> matchers) {
        projectionResultMatchers.add(new VerifiableProjection(projection, matchers));
        return this.parent;
    }

    public P supportProjection(Enum<? extends ProjectionName> projection, BasePayload jsonMatchingPayload) {
        val resultMatcher = getResultMatcher(jsonMatchingPayload);
        return supportProjection(projection, singletonList(resultMatcher));
    }

    public P forbidProjection(@Nonnull Enum<? extends ProjectionName> projection, @Nonnull Collection<ResultMatcher> matchers) {
        forbiddenProjectionResultMatchers.add(new VerifiableProjection(projection, matchers));
        return this.parent;
    }

    public P setDefaultResultMatchers(@Nonnull Collection<ResultMatcher> matchers) {
        this.defaultResultMatchers.clear();
        this.defaultResultMatchers.addAll(matchers);
        return this.parent;
    }

    public P setDefaultResultMatchers(BasePayload jsonMatchingPayload) {
        val resultMatcher = getResultMatcher(jsonMatchingPayload);
        return setDefaultResultMatchers(singletonList(resultMatcher));
    }

    private ResultMatcher getResultMatcher(BasePayload jsonMatchingPayload) {
        return parent.getResultType()
                .matcherOf(jsonMatchingPayload);
    }
}
