package com.seregamorph.restapi.test.base.support;

import static java.util.Collections.singletonList;

import com.seregamorph.restapi.base.BasePayload;
import com.seregamorph.restapi.search.Search;
import com.seregamorph.restapi.search.SearchOperator;
import com.seregamorph.restapi.search.SearchValue;
import com.seregamorph.restapi.search.SingleSearchCondition;
import com.seregamorph.restapi.test.base.setup.BaseSetup;
import com.seregamorph.restapi.test.base.setup.common.VerifiableSearch;
import com.seregamorph.restapi.test.base.setup.common.VerifiableSearchField;
import com.seregamorph.restapi.test.utils.StandardValues;
import com.seregamorph.restapi.utils.ObjectUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.hamcrest.Matcher;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.util.CollectionUtils;

@SuppressWarnings({"unused", "WeakerAccess"})
@RequiredArgsConstructor
public class SearchSupport<P extends BaseSetup<P, ?>> {

    private final P parent;

    @Getter
    private final List<VerifiableSearchField> searchFields = new ArrayList<>();

    @Getter
    private final List<VerifiableSearchField> defaultSearchFields = new ArrayList<>();

    @Getter
    private final List<VerifiableSearch> searches = new ArrayList<>();

    @Getter
    private boolean undeclaredSearchFieldsForbidden = true;

    public P supportSearch(String search, BasePayload jsonMatchingPayload) {
        val resultMatcher = getResultMatcher(jsonMatchingPayload);
        return supportSearch(search, singletonList(resultMatcher));
    }

    public P supportSearch(String search, Collection<ResultMatcher> resultMatchers) {
        // A collection of ResultMatcher is required as Search could be very complex and it's not practical to build
        // result matchers automatically from it. Remember that not only we could have nested logical operators;
        // we could also have lots of json paths.
        this.searches.add(new VerifiableSearch(search, resultMatchers));
        return this.parent;
    }

    public P supportSearch(Search search, BasePayload jsonMatchingPayload) {
        val resultMatcher = getResultMatcher(jsonMatchingPayload);
        return supportSearch(search, singletonList(resultMatcher));
    }

    public P supportSearch(Search search, Collection<ResultMatcher> resultMatchers) {
        return supportSearch(search.toString(), resultMatchers);
    }

    public P supportSearchField(
            String fieldName, SearchOperator operator, Object value, Matcher<?> valueMatcher) {
        List<Object> values = StandardValues.jsonObjects(ObjectUtils.collection(value));
        validate(operator, values);
        SingleSearchCondition condition = getSearchCondition(fieldName, operator, values);
        VerifiableSearchField verifiableSearchField = new VerifiableSearchField(condition, valueMatcher);
        this.searchFields.add(verifiableSearchField);
        return this.parent;
    }

    public P supportSearchField(String fieldName, SearchOperator operator, Object value,
                                BasePayload jsonMatchingPayload) {
        val resultMatcher = getResultMatcher(jsonMatchingPayload);
        return supportSearchField(fieldName, operator, value, singletonList(resultMatcher));
    }

    public P supportSearchField(
            String fieldName, SearchOperator operator, Object value, Collection<ResultMatcher> resultMatchers) {
        List<Object> values = StandardValues.jsonObjects(ObjectUtils.collection(value));
        validate(operator, values);
        SingleSearchCondition condition = getSearchCondition(fieldName, operator, values);
        VerifiableSearchField verifiableSearchField = new VerifiableSearchField(condition, resultMatchers);
        this.searchFields.add(verifiableSearchField);
        return this.parent;
    }

    @SuppressWarnings("UnusedReturnValue")
    public P supportSearchField(String fieldName, SearchOperator operator, Object value) {
        List<Object> values = StandardValues.jsonObjects(ObjectUtils.collection(value));
        validate(operator, values);
        SingleSearchCondition condition = getSearchCondition(fieldName, operator, values);
        VerifiableSearchField verifiableSearchField = new VerifiableSearchField(condition);
        this.searchFields.add(verifiableSearchField);
        return this.parent;
    }

    public P addDefaultSearchField(String fieldName, SearchOperator operator, Object value, Matcher<?> valueMatcher) {
        List<Object> values = StandardValues.jsonObjects(ObjectUtils.collection(value));
        this.supportSearchField(fieldName, operator, values, valueMatcher);
        SingleSearchCondition condition = getSearchCondition(fieldName, operator, values);
        VerifiableSearchField verifiableSearchField = new VerifiableSearchField(condition, valueMatcher);
        this.defaultSearchFields.add(verifiableSearchField);
        return this.parent;
    }

    public P addDefaultSearchField(
            String fieldName, SearchOperator operator, Object value, Collection<ResultMatcher> resultMatchers) {
        List<Object> values = StandardValues.jsonObjects(ObjectUtils.collection(value));
        this.supportSearchField(fieldName, operator, values, resultMatchers);
        SingleSearchCondition condition = getSearchCondition(fieldName, operator, values);
        VerifiableSearchField verifiableSearchField = new VerifiableSearchField(condition, resultMatchers);
        this.defaultSearchFields.add(verifiableSearchField);
        return this.parent;
    }

    public P addDefaultSearchField(String fieldName, SearchOperator operator, Object value) {
        List<Object> values = StandardValues.jsonObjects(ObjectUtils.collection(value));
        this.supportSearchField(fieldName, operator, values);
        SingleSearchCondition condition = getSearchCondition(fieldName, operator, values);
        VerifiableSearchField verifiableSearchField = new VerifiableSearchField(condition);
        this.defaultSearchFields.add(verifiableSearchField);
        return this.parent;
    }

    public boolean hasSearchesOrSearchFields() {
        return !this.searches.isEmpty() || !this.searchFields.isEmpty();
    }

    public P setUndeclaredSearchFieldsForbidden(boolean undeclaredSearchFieldsForbidden) {
        this.undeclaredSearchFieldsForbidden = undeclaredSearchFieldsForbidden;
        return this.parent;
    }

    private ResultMatcher getResultMatcher(BasePayload jsonMatchingPayload) {
        return parent.getResultType()
                .matcherOf(jsonMatchingPayload);
    }

    private static SingleSearchCondition getSearchCondition(String fieldName, SearchOperator operator, Collection<?> collection) {
        if (operator.isMultipleValueSupported()) {
            return new SingleSearchCondition(fieldName, operator, collection);
        }

        return new SingleSearchCondition(fieldName, operator, collection.iterator().next());
    }

    private static void validate(SearchOperator operator, Collection<?> collection) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new IllegalArgumentException("Please specify at least 1 value.");
        }

        if (!operator.isMultipleValueSupported() && collection.size() > 1) {
            throw new IllegalArgumentException(
                    String.format("Operator [%s] doesn't support multiple values.", operator.name()));
        }

        if (collection.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException(String.format(
                    "Null is not supported. If you need null, use [%s.%s]",
                    SearchValue.class.getSimpleName(), SearchValue.NULL));
        }
    }
}
