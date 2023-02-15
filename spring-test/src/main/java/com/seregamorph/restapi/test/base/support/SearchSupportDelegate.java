package com.seregamorph.restapi.test.base.support;

import com.seregamorph.restapi.base.BasePayload;
import com.seregamorph.restapi.search.Search;
import com.seregamorph.restapi.search.SearchOperator;
import com.seregamorph.restapi.test.base.setup.BaseSetup;
import com.seregamorph.restapi.test.base.setup.common.VerifiableSearch;
import com.seregamorph.restapi.test.base.setup.common.VerifiableSearchField;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.hamcrest.Matcher;
import org.springframework.test.web.servlet.ResultMatcher;

public interface SearchSupportDelegate<P extends BaseSetup<P, ?>> {

    SearchSupport<P> getSearchSupport();

    default List<VerifiableSearch> getSearches() {
        return getSearchSupport().getSearches();
    }

    default List<VerifiableSearchField> getSearchFields() {
        return getSearchSupport().getSearchFields();
    }

    default List<VerifiableSearchField> getDefaultSearchFields() {
        return getSearchSupport().getDefaultSearchFields();
    }

    default P supportSearch(String search, BasePayload jsonMatchingPayload) {
        return getSearchSupport().supportSearch(search, jsonMatchingPayload);
    }

    default P supportSearch(String search, ResultMatcher... resultMatchers) {
        return supportSearch(search, Arrays.asList(resultMatchers));
    }

    default P supportSearch(String search, Collection<ResultMatcher> resultMatchers) {
        return getSearchSupport().supportSearch(search, resultMatchers);
    }

    default P supportSearch(Search search, BasePayload jsonMatchingPayload) {
        return getSearchSupport().supportSearch(search, jsonMatchingPayload);
    }

    default P supportSearch(Search search, ResultMatcher... resultMatchers) {
        return supportSearch(search, Arrays.asList(resultMatchers));
    }

    default P supportSearch(Search search, Collection<ResultMatcher> resultMatchers) {
        return getSearchSupport().supportSearch(search, resultMatchers);
    }

    default P supportSearchField(
            String fieldName, SearchOperator operator, Object value, Matcher<?> valueMatcher) {
        return getSearchSupport().supportSearchField(fieldName, operator, value, valueMatcher);
    }

    default P supportSearchField(
            String fieldName, SearchOperator operator, Object value, BasePayload jsonMatchingPayload) {
        return getSearchSupport().supportSearchField(fieldName, operator, value, jsonMatchingPayload);
    }

        default P supportSearchField(
            String fieldName, SearchOperator operator, Object value, ResultMatcher... resultMatchers) {
        return supportSearchField(fieldName, operator, value, Arrays.asList(resultMatchers));
    }

    default P supportSearchField(
            String fieldName, SearchOperator operator, Object value, Collection<ResultMatcher> resultMatchers) {
        return getSearchSupport().supportSearchField(fieldName, operator, value, resultMatchers);
    }

    default P supportSearchField(String fieldName, SearchOperator operator, Object value) {
        return getSearchSupport().supportSearchField(fieldName, operator, value);
    }

    default P addDefaultSearchField(
            String fieldName, SearchOperator operator, Object value, Matcher<?> valueMatcher) {
        return getSearchSupport().addDefaultSearchField(fieldName, operator, value, valueMatcher);
    }

    default P addDefaultSearchField(
            String fieldName, SearchOperator operator, Object value, ResultMatcher... resultMatchers) {
        return addDefaultSearchField(fieldName, operator, value, Arrays.asList(resultMatchers));
    }

    default P addDefaultSearchField(
            String fieldName, SearchOperator operator, Object value, Collection<ResultMatcher> resultMatchers) {
        return getSearchSupport().addDefaultSearchField(fieldName, operator, value, resultMatchers);
    }

    default P addDefaultSearchField(String fieldName, SearchOperator operator, Object value) {
        return getSearchSupport().addDefaultSearchField(fieldName, operator, value);
    }

    default boolean hasSearchesOrSearchFields() {
        return getSearchSupport().hasSearchesOrSearchFields();
    }

    default P setUndeclaredSearchFieldsForbidden(boolean undeclaredSearchFieldsForbidden) {
        return getSearchSupport().setUndeclaredSearchFieldsForbidden(undeclaredSearchFieldsForbidden);
    }

    default boolean isUndeclaredSearchFieldsForbidden() {
        return getSearchSupport().isUndeclaredSearchFieldsForbidden();
    }
}
