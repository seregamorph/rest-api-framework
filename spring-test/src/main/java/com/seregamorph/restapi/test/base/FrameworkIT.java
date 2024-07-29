package com.seregamorph.restapi.test.base;

import static com.seregamorph.restapi.common.Constants.ENDPOINT_ID;
import static com.seregamorph.restapi.common.Constants.PARAM_PAGE;
import static com.seregamorph.restapi.common.Constants.PARAM_SEARCH;
import static com.seregamorph.restapi.common.Constants.PARAM_SIZE;
import static com.seregamorph.restapi.common.Constants.PARAM_SORT;
import static com.seregamorph.restapi.test.base.BaseJsonPathUtils.mergeExistingFields;
import static com.seregamorph.restapi.test.base.BaseJsonPathUtils.removeIgnoredNodes;
import static com.seregamorph.restapi.test.base.BaseResultMatchers.jsonPathNullMissing;
import static com.seregamorph.restapi.test.base.ResultMatchers.collect;
import static com.seregamorph.restapi.test.base.ResultMatchers.success;
import static com.seregamorph.restapi.test.base.SearchResultMatchers.eachMatches;
import static com.seregamorph.restapi.test.base.SearchResultMatchers.matches;
import static com.seregamorph.restapi.test.base.support.QuantityVerificationMode.EXACT_QTY;
import static com.seregamorph.restapi.test.utils.JsonMatchers.jsonNodeEquals;
import static com.seregamorph.restapi.test.utils.MoreMatchers.predicate;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.common.collect.Lists;
import com.seregamorph.restapi.base.BasePayload;
import com.seregamorph.restapi.base.BaseProjection;
import com.seregamorph.restapi.base.ProjectionName;
import com.seregamorph.restapi.config.spi.FrameworkConfigHolder;
import com.seregamorph.restapi.search.SearchOperator;
import com.seregamorph.restapi.search.SearchValue;
import com.seregamorph.restapi.sort.SortDirection;
import com.seregamorph.restapi.test.AbstractSpringWebIT;
import com.seregamorph.restapi.test.JsonConstants;
import com.seregamorph.restapi.test.TestApplicationContextHolder;
import com.seregamorph.restapi.test.base.request.DeleteRequest;
import com.seregamorph.restapi.test.base.request.GetAllRequest;
import com.seregamorph.restapi.test.base.request.GetOneRequest;
import com.seregamorph.restapi.test.base.request.PatchRequest;
import com.seregamorph.restapi.test.base.request.PostRequest;
import com.seregamorph.restapi.test.base.request.PutRequest;
import com.seregamorph.restapi.test.base.setup.BaseSetup;
import com.seregamorph.restapi.test.base.setup.DeleteSetup;
import com.seregamorph.restapi.test.base.setup.GetAllSetup;
import com.seregamorph.restapi.test.base.setup.GetOneSetup;
import com.seregamorph.restapi.test.base.setup.PatchSetup;
import com.seregamorph.restapi.test.base.setup.PostSetup;
import com.seregamorph.restapi.test.base.setup.PutSetup;
import com.seregamorph.restapi.test.base.setup.common.VerifiableHeader;
import com.seregamorph.restapi.test.base.setup.common.VerifiableParameter;
import com.seregamorph.restapi.test.base.setup.common.VerifiablePathVariables;
import com.seregamorph.restapi.test.base.setup.common.VerifiablePayload;
import com.seregamorph.restapi.test.base.setup.common.VerifiableProjection;
import com.seregamorph.restapi.test.base.setup.common.VerifiableSearch;
import com.seregamorph.restapi.test.base.setup.common.VerifiableSearchField;
import com.seregamorph.restapi.test.base.setup.common.VerifiableSortField;
import com.seregamorph.restapi.test.base.setup.common.VerifiableSortFieldDirection;
import com.seregamorph.restapi.test.base.setup.common.payload.GeneratedPayload;
import com.seregamorph.restapi.test.base.support.QuantityVerificationMode;
import com.seregamorph.restapi.test.base.support.RequestType;
import com.seregamorph.restapi.test.config.spi.TestFrameworkConfigHolder;
import com.seregamorph.restapi.test.utils.BaseTestUtils;
import com.seregamorph.restapi.utils.ContentUtils;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.ComparisonFailure;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.method.HandlerMethod;

abstract class FrameworkIT extends AbstractSpringWebIT {

    private enum SampleProjection implements ProjectionName {
        BAD_PROJECTION_VALUE;

        @Override
        public Class<? extends BaseProjection> getProjectionClass() {
            return BaseProjection.class;
        }
    }

    private static final String BAD_PERMISSION = "bad-permission";
    private static final String BAD_PAGE = "bad-page";
    private static final String BAD_SIZE = "bad-size";

    protected Rest rest;

    @Autowired
    private ServletContext servletContext;

    /**
     * initialized by {@link FrameworkRunner}
     */
    MockMvcTestSetup rootSetup;

    /**
     * initialized by {@link FrameworkRunner}
     */
    @Nullable
    BaseSetup<?, ?> setup;

    @Before
    public void beforeTest() throws Exception {
        if (BaseTestUtils.isMockWebEnvironment(getClass())) {
            try {
                applicationContext().getBean(TestRestTemplate.class);
                fail("Context has wrong configuration: should have either MockMvc or TestRestTemplate bean, "
                        + "but not both");
            } catch (NoClassDefFoundError | NoSuchBeanDefinitionException e) {
                log.trace("Expected", e);
            }
            rest = new MockMvcRest(rootSetup.getEndpoint(), objectMapper(), setup,
                    applicationContext().getBean(MockMvc.class), servletContext, null);
        } else {
            try {
                applicationContext().getBean(MockMvc.class);
                fail("Context has wrong configuration: should have either MockMvc or TestRestTemplate bean");
            } catch (NoSuchBeanDefinitionException e) {
                log.trace("Expected", e);
            }
            rest = new TestRestTemplateRest(rootSetup.getEndpoint(), objectMapper(), setup,
                    applicationContext().getBean(TestRestTemplate.class), null);
        }
        assertTrue("Transaction should not be closed while initTest", TestTransaction.isActive());
        onStartTransaction();
    }

    protected void onStartTransaction() throws Exception {
        // no op by default
    }

    // Base integration tests for getAll

    @ParameterizedTest
    public void getAllShouldReturn401WhenAnonymous(GetAllSetup setup) throws Exception {
        rest
                .get(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    public void getAllShouldReturn403WhenNoPermissions(GetAllSetup setup) throws Exception {
        rest
                .withAuthorities()
                .get(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    public void getAllShouldReturn403WhenInvalidPermissions(GetAllSetup setup) throws Exception {
        rest
                .withAuthorities(BAD_PERMISSION)
                .get(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    public void getAllShouldReturn400WhenIllegalPathVariables(
            GetAllSetup setup, VerifiablePathVariables entry) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup, entry)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .andExpect(status().isBadRequest())
                .andExpect(collect(entry.getResultMatchers()));
    }

    @ParameterizedTest
    public void getAllShouldReturn404WhenInvalidPathVariables(
            GetAllSetup setup, VerifiablePathVariables entry) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup, entry)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .andExpect(status().isNotFound())
                .andExpect(collect(entry.getResultMatchers()));
    }

    @ParameterizedTest
    public void getAllShouldReturn400WhenProjectionInvalid(GetAllSetup setup) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .projection(SampleProjection.BAD_PROJECTION_VALUE)
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    public void getAllShouldReturn400WhenNonNumericPage(GetAllSetup setup) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .param(PARAM_PAGE, BAD_PAGE)
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    public void getAllShouldReturn400WhenNegativePage(GetAllSetup setup) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .param(PARAM_PAGE, -1)
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    public void getAllShouldReturn400WhenNonNumericSize(GetAllSetup setup) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .param(PARAM_SIZE, BAD_SIZE)
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    public void getAllShouldReturn400WhenNegativeSize(GetAllSetup setup) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .param(PARAM_SIZE, -1)
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    public void getAllShouldReturn400WhenZeroSize(GetAllSetup setup) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .param(PARAM_SIZE, 0)
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    public void getAllShouldReturn400WhenMissingRequiredParameters(
            GetAllSetup setup, VerifiableParameter parameter) throws Exception {
        // Remove 1 required parameter
        Map<String, Object> parameters = new HashMap<>(setup.getParameters());
        parameters.remove(parameter.getName());

        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(setup.getHeaders())
                .params(parameters)
                .andExpect(status().isBadRequest())
                .andExpect(collect(parameter.getResultMatchers()));
    }

    @ParameterizedTest
    public void getAllShouldReturn400WhenBadParameters(
            GetAllSetup setup, VerifiableParameter parameter) throws Exception {
        Map<String, Object> parameters = new HashMap<>(setup.getParameters());
        parameters.put(parameter.getName(), parameter.getParameterValue());

        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(setup.getHeaders())
                .params(parameters)
                .andExpect(status().isBadRequest())
                .andExpect(collect(parameter.getResultMatchers()));
    }

    @ParameterizedTest
    public void getAllShouldReturn400WhenMissingRequiredHeaders(
            GetAllSetup setup, VerifiableHeader header) throws Exception {
        // Remove 1 required header
        Map<String, Object> headers = new HashMap<>(setup.getHeaders());
        headers.remove(header.getName());

        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(headers)
                .params(setup.getParameters())
                .andExpect(status().isBadRequest())
                .andExpect(collect(header.getResultMatchers()));
    }

    @ParameterizedTest
    public void getAllShouldReturn400WhenBadHeaders(
            GetAllSetup setup, VerifiableHeader header) throws Exception {
        Map<String, Object> headers = new HashMap<>(setup.getHeaders());
        headers.put(header.getName(), header.getHeaderValue());

        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(headers)
                .params(setup.getParameters())
                .andExpect(status().isBadRequest())
                .andExpect(collect(header.getResultMatchers()));
    }

    @ParameterizedTest
    public void getAllShouldReturn403WhenForbiddenProjection(
            GetAllSetup setup, VerifiableProjection projection) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .projection(projection.getProjection())
                .andExpect(status().isForbidden())
                .andExpect(collect(projection.getResultMatchers()));
    }

    @ParameterizedTest(aroundRequestActionsEnabled = true)
    public void getAllShouldManageSpecificPageSize(GetAllSetup setup) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .param(PARAM_SIZE, 1)
                .andExpect(status().isOk())
                .andExpect(collect(setup.getDefaultResultMatchers()))
                .andExpect(jsonPath(setup.getRootElement(), hasSize(1)))
                .andExpect(collect(paginationMatchers(0, 1, 1, setup.getTotalElements(),
                        setup.getQuantityVerificationMode())))
                .andExpect(buildDefaultSearchResultMatcher(setup))
                .andExpect(getDefaultSortMatcher(setup));
    }

    @ParameterizedTest
    public void getAllShouldReturn400WhenSearchParamWithoutOperator(GetAllSetup setup) throws Exception {
        String error = getFullBadSearchParamErrorMessage("whatever", "Unable to find search operator.");
        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .param(PARAM_SEARCH, "whatever")
                .andExpect(status().isBadRequest())
                .andExpect(typeMismatchMatcher(error));
    }

    @ParameterizedTest
    public void getAllShouldReturn400WhenSearchParamWithoutValue(GetAllSetup setup, VerifiableSearchField field) throws Exception {
        String argument = String.format("%s %s",
                field.getSearchCondition().getField(), field.getSearchCondition().getOperator().getOperator());
        String error = getFullBadSearchParamErrorMessage(argument, "Unable to find search value.");
        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .param(PARAM_SEARCH, argument)
                .andExpect(status().isBadRequest())
                .andExpect(typeMismatchMatcher(error));
    }

    @ParameterizedTest
    public void getAllShouldReturn400WhenSearchParamWithIllegalSearchField(
            GetAllSetup setup, VerifiableSearchField field) throws Exception {
        String argument = String.format("invalidSearchField %s whatever", field.getSearchCondition().getOperator().getOperator());
        String error = getFullBadSearchParamErrorMessage(argument,
                "Field [invalidSearchField] is not searchable.");
        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .param(PARAM_SEARCH, argument)
                .andExpect(status().isBadRequest())
                .andExpect(typeMismatchMatcher(error));
    }

    @ParameterizedTest
    public void getAllShouldReturn400WhenSearchParamWithRedundantLogicalOperator(
            GetAllSetup setup, VerifiableSearchField field, String logicalOperator) throws Exception {
        String argument = String.format("%s %s", field.getSearchCondition(), logicalOperator);
        String error = getFullBadSearchParamErrorMessage(argument,
                "Format: <fieldName><operator><value>. "
                        + "Use logical operators and round brackets to build more complex search argument.");
        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .param(PARAM_SEARCH, argument)
                .andExpect(status().isBadRequest())
                .andExpect(typeMismatchMatcher(error));
    }

    @ParameterizedTest
    public void getAllShouldReturn400WhenSearchParamWithoutOperatorInSecondClause(
            GetAllSetup setup, VerifiableSearchField field, String logicalOperator) throws Exception {
        String argument = String.format("(%s) %s (%s)",
                field.getSearchCondition(), logicalOperator, field.getSearchCondition().getField());
        String error = getFullBadSearchParamErrorMessage(argument, "Unable to find search operator.");
        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .param(PARAM_SEARCH, argument)
                .andExpect(status().isBadRequest())
                .andExpect(typeMismatchMatcher(error));
    }

    @ParameterizedTest
    public void getAllShouldReturn400WhenSearchParamWithoutValueInSecondClause(
            GetAllSetup setup, VerifiableSearchField field, String logicalOperator) throws Exception {
        String argument = String.format("(%s) %s (%s %s)",
                field.getSearchCondition(),
                logicalOperator,
                field.getSearchCondition().getField(),
                field.getSearchCondition().getOperator().getOperator());
        String error = getFullBadSearchParamErrorMessage(argument, "Unable to find search value.");
        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .param(PARAM_SEARCH, argument)
                .andExpect(status().isBadRequest())
                .andExpect(typeMismatchMatcher(error));
    }

    @ParameterizedTest
    public void getAllShouldReturn400WhenSearchParamWithIllegalOperator(
            GetAllSetup setup, VerifiableSearchField searchField) throws Exception {
        val fieldName = searchField.getSearchCondition().getField();

        String argument = String.format("%s ? whatever", fieldName);
        String error = getFullBadSearchParamErrorMessage(argument,
                "Unable to find search operator from string: [ ? whatever].");

        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .param(PARAM_SEARCH, argument)
                .andExpect(status().isBadRequest())
                .andExpect(typeMismatchMatcher(error));
    }

    @ParameterizedTest
    public void getAllShouldReturn400WhenSearchParamWithInvalidOperator(
            GetAllSetup setup, VerifiableSearchField searchField, SearchOperator operator) throws Exception {
        val fieldName = searchField.getSearchCondition().getField();

        String argument = String.format("%s %s whatever",
                fieldName, operator.getOperator());
        String error = getFullBadSearchParamErrorMessage(argument,
                String.format("Operator [%s] is not supported", operator.getOperator()));

        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .param(PARAM_SEARCH, argument)
                .andExpect(status().isBadRequest())
                .andExpect(typeMismatchMatcher(error));
    }

    @ParameterizedTest
    public void getAllShouldReturn400WhenSearchParamWithInvalidSpecialSearchValue(
            GetAllSetup setup, VerifiableSearchField searchField, SearchValue value) throws Exception {
        val fieldName = searchField.getSearchCondition().getField();

        String argument = String.format("%s is %s", fieldName, value);
        String error = getFullBadSearchParamErrorMessage(argument,
                String.format("Special value [%s] is not supported", value));

        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .param(PARAM_SEARCH, argument)
                .andExpect(status().isBadRequest())
                .andExpect(typeMismatchMatcher(error));
    }

    @ParameterizedTest
    public void getAllShouldReturn400WhenSortParamWithIllegalSortField(GetAllSetup setup) throws Exception {
        String error = getFullBadSortParamErrorMessage("whatever", "Field [whatever] is not sortable");
        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .param(PARAM_SORT, "whatever")
                .andExpect(status().isBadRequest())
                .andExpect(typeMismatchMatcher(error));
    }

    @ParameterizedTest(aroundRequestActionsEnabled = true)
    public void getAllShouldManageSpecificPageNumber(GetAllSetup setup) throws Exception {
        // No result validation here as our matchers are for the first element only
        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .param(PARAM_PAGE, 1)
                .param(PARAM_SIZE, 1)
                .andExpect(status().isOk())
                .andExpect(jsonPath(setup.getRootElement(), hasSize(1)))
                .andExpect(collect(paginationMatchers(1, 1, 1, setup.getTotalElements(),
                        setup.getQuantityVerificationMode())))
                .andExpect(buildDefaultSearchResultMatcher(setup))
                .andExpect(getDefaultSortMatcher(setup));
    }

    @ParameterizedTest(aroundRequestActionsEnabled = true)
    public void getAllShouldManageProjections(
            GetAllSetup setup, VerifiableProjection projection) throws Exception {

        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .projection(projection.getProjection())
                .andExpect(status().isOk())
                .andExpect(collect(projection.getResultMatchers()))
                .andExpect(getDefaultRootPathMatcher(setup))
                .andExpect(buildDefaultSearchResultMatcher(setup))
                .andExpect(getDefaultSortMatcher(setup));
    }

    @ParameterizedTest(aroundRequestActionsEnabled = true)
    public void getAllShouldManageProvidedHeadersAndParameters(GetAllSetup setup) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .andExpect(status().isOk())
                .andExpect(collect(setup.getDefaultResultMatchers()))
                .andExpect(getDefaultRootPathMatcher(setup))
                .andExpect(buildDefaultSearchResultMatcher(setup))
                .andExpect(getDefaultSortMatcher(setup));
    }

    @ParameterizedTest(aroundRequestActionsEnabled = true)
    public void getAllShouldRepeatedManageProvidedHeadersAndParameters(GetAllSetup setup) throws Exception {
        val getOneFirstResp = rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .andExpect(status().isOk())
                .andExpect(collect(setup.getDefaultResultMatchers()))
                .andExpect(getDefaultRootPathMatcher(setup))
                .andExpect(buildDefaultSearchResultMatcher(setup))
                .andExpect(getDefaultSortMatcher(setup))
                .andReturn().getResponse();

        flush();

        val getOneSecondResp = rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .andExpect(status().isOk())
                .andExpect(collect(setup.getDefaultResultMatchers()))
                .andExpect(getDefaultRootPathMatcher(setup))
                .andExpect(buildDefaultSearchResultMatcher(setup))
                .andExpect(getDefaultSortMatcher(setup))
                .andReturn().getResponse();

        val getOneFirst = readJson(getOneFirstResp.getContentAsString());
        val getOneSecond = readJson(getOneSecondResp.getContentAsString());

        removeIgnoredNodes(getOneFirst, setup.getIgnoredJsonPaths());
        removeIgnoredNodes(getOneSecond, setup.getIgnoredJsonPaths());

        assertThat("Non-equal content of getAll response first and second time, "
                        + "so either there is unexpected changes in the entity, "
                        + "or you should adjust ignored JSON pointers",
                getOneSecond, jsonNodeEquals(writeJson(getOneFirst)));
    }

    @ParameterizedTest(aroundRequestActionsEnabled = true)
    public void getAllShouldManageSearchFields(GetAllSetup setup, VerifiableSearchField field) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .param(PARAM_SEARCH, field.getSearchCondition())
                .andExpect(status().isOk())
                .andExpect(buildSearchResultMatcher(setup, field))
                .andExpect(getDefaultSortMatcher(setup));
    }

    @ParameterizedTest(aroundRequestActionsEnabled = true)
    public void getAllShouldManageSearches(GetAllSetup setup, VerifiableSearch search) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .param(PARAM_SEARCH, search.getSearch())
                .andExpect(status().isOk())
                .andExpect(collect(search.getResultMatchers()));
    }

    @ParameterizedTest(aroundRequestActionsEnabled = true)
    public void getAllShouldManageExtraRequests(GetAllSetup setup, GetAllRequest request) throws Exception {
        Map<String, Object> paginationParams = new HashMap<>();

        ResultMatcher elementQtyMatcher;
        if (setup.isPaginationSupported()) {
            paginationParams.put(PARAM_SIZE, 1);
            elementQtyMatcher = jsonPath(JsonConstants.ROOT_CONTENT, hasSize(1));
        } else if (request.getQuantityVerificationMode(setup) == EXACT_QTY) {
            elementQtyMatcher = jsonPath(JsonConstants.ROOT, hasSize(request.getTotalElements(setup)));
        } else {
            elementQtyMatcher = jsonPath(JsonConstants.ROOT,
                    hasSize(greaterThanOrEqualTo(request.getTotalElements(setup))));
        }

        if (request.isBadRequest()) {
            rest
                    .withAuthorities(request.getAuthorities(setup))
                    .get(setup, request)
                    .headers(request.getHeaders(setup))
                    .params(request.getParameters(setup))
                    .params(paginationParams)
                    .andExpect(status().isBadRequest())
                    .andExpect(collect(request.getResultMatchers(setup)))
                    .andExpect(getDefaultSortMatcher(setup));
        } else {
            rest
                    .withAuthorities(request.getAuthorities(setup))
                    .get(setup, request)
                    .headers(request.getHeaders(setup))
                    .params(request.getParameters(setup))
                    .params(paginationParams)
                    .andExpect(status().isOk())
                    .andExpect(collect(request.getResultMatchers(setup)))
                    .andExpect(elementQtyMatcher)
                    .andExpect(getDefaultSortMatcher(setup));
        }
    }

    @ParameterizedTest(aroundRequestActionsEnabled = true)
    public void getAllShouldManageSingleSortFields(
            GetAllSetup setup, VerifiableSortFieldDirection sortField) throws Exception {
        val orderMatcher = jsonPathNullMissing(setup.getFieldMappingSupport(), Collections.singletonList(sortField));

        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .param(PARAM_SORT, sortField.toString())
                .andExpect(status().isOk())
                .andExpect(getDefaultRootPathMatcher(setup))
                .andExpect(orderMatcher);
    }

    @ParameterizedTest(aroundRequestActionsEnabled = true)
    public void getAllShouldManageExtraMixedSortFields(
            GetAllSetup setup,
            VerifiableSortFieldDirection sortField1, VerifiableSortFieldDirection sortField2) throws Exception {
        val orderMatcher = jsonPathNullMissing(setup.getFieldMappingSupport(), Arrays.asList(sortField1, sortField2));

        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .param(PARAM_SORT,
                        sortField1.toString(),
                        sortField2.toString())
                .andExpect(status().isOk())
                .andExpect(getDefaultRootPathMatcher(setup))
                .andExpect(orderMatcher);
    }

    /**
     * Lite version of {@link #getAllShouldManageExtraMixedSortFields(GetAllSetup, VerifiableSortFieldDirection,
     * VerifiableSortFieldDirection)} - finds up to 3 paths with unique values and validates paired sorting on it.
     */
    @ParameterizedTest(aroundRequestActionsEnabled = true)
    public void getAllShouldManageBasicPairedSortFields(GetAllSetup setup) throws Exception {
        val content = rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .andExpect(status().isOk())
                .andExpect(getDefaultRootPathMatcher(setup))
                .andReturn().getResponse().getContentAsString();

        val fieldsToValidate = new ArrayList<VerifiableSortField>();
        for (val sortField : setup.getSortFields()) {
            val list = BaseResultMatchers.evaluateJsonPath(setup.getJsonPath(sortField.getFieldName()), content);

            if (new HashSet<Object>(list).size() > 1) {
                // has unique values
                fieldsToValidate.add(sortField);
                if (fieldsToValidate.size() >= 3) {
                    // enough
                    break;
                }
            }
        }

        log.info("Fields to validate: {}", fieldsToValidate);

        for (val sortField1 : fieldsToValidate) {
            for (val sortField2 : fieldsToValidate) {
                if (sortField1 != sortField2) {
                    for (val direction1 : SortDirection.values()) {
                        for (val direction2 : SortDirection.values()) {
                            getAllShouldManageExtraMixedSortFields(setup,
                                    sortField1.with(direction1), sortField2.with(direction2));
                        }
                    }
                }
            }
        }
    }

    // Base integration tests for getOne

    @ParameterizedTest
    public void getOneShouldReturn401WhenAnonymous(GetOneSetup setup) throws Exception {
        rest
                .get(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    public void getOneShouldReturn403WhenNoPermission(GetOneSetup setup) throws Exception {
        rest
                .withAuthorities()
                .get(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    public void getOneShouldReturn403WhenInvalidPermissions(GetOneSetup setup) throws Exception {
        rest
                .withAuthorities(BAD_PERMISSION)
                .get(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    public void getOneShouldReturn400WhenIllegalPathVariables(
            GetOneSetup setup, VerifiablePathVariables entry) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup, entry)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .andExpect(status().isBadRequest())
                .andExpect(collect(entry.getResultMatchers()));
    }

    @ParameterizedTest
    public void getOneShouldReturn404WhenInvalidPathVariables(
            GetOneSetup setup, VerifiablePathVariables entry) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup, entry)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .andExpect(status().isNotFound())
                .andExpect(collect(entry.getResultMatchers()));
    }

    @ParameterizedTest
    public void getOneShouldReturn400WhenProjectionInvalid(GetOneSetup setup) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .projection(SampleProjection.BAD_PROJECTION_VALUE)
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    public void getOneShouldReturn400WhenMissingRequiredParameters(
            GetOneSetup setup, VerifiableParameter parameter) throws Exception {
        // Remove 1 required parameter
        Map<String, Object> parameters = new HashMap<>(setup.getParameters());
        parameters.remove(parameter.getName());

        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(setup.getHeaders())
                .params(parameters)
                .andExpect(status().isBadRequest())
                .andExpect(collect(parameter.getResultMatchers()));
    }

    @ParameterizedTest
    public void getOneShouldReturn400WhenBadParameters(
            GetOneSetup setup, VerifiableParameter parameter) throws Exception {
        Map<String, Object> parameters = new HashMap<>(setup.getParameters());
        parameters.put(parameter.getName(), parameter.getParameterValue());

        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(setup.getHeaders())
                .params(parameters)
                .andExpect(status().isBadRequest())
                .andExpect(collect(parameter.getResultMatchers()));
    }

    @ParameterizedTest
    public void getOneShouldReturn400WhenMissingRequiredHeaders(
            GetOneSetup setup, VerifiableHeader header) throws Exception {
        // Remove 1 required header
        Map<String, Object> headers = new HashMap<>(setup.getHeaders());
        headers.remove(header.getName());

        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(headers)
                .params(setup.getParameters())
                .andExpect(status().isBadRequest())
                .andExpect(collect(header.getResultMatchers()));
    }

    @ParameterizedTest
    public void getOneShouldReturn400WhenBadHeaders(
            GetOneSetup setup, VerifiableHeader header) throws Exception {
        Map<String, Object> headers = new HashMap<>(setup.getHeaders());
        headers.put(header.getName(), header.getHeaderValue());

        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(headers)
                .params(setup.getParameters())
                .andExpect(status().isBadRequest())
                .andExpect(collect(header.getResultMatchers()));
    }

    @ParameterizedTest
    public void getOneShouldReturn403WhenForbiddenProjection(
            GetOneSetup setup, VerifiableProjection projection) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .projection(projection.getProjection())
                .andExpect(status().isForbidden())
                .andExpect(collect(projection.getResultMatchers()));
    }

    @ParameterizedTest(aroundRequestActionsEnabled = true)
    public void getOneShouldManageProjections(
            GetOneSetup setup, VerifiableProjection projection) throws Exception {

        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .projection(projection.getProjection())
                .andExpect(status().isOk())
                .andExpect(collect(projection.getResultMatchers()));
    }

    @ParameterizedTest(aroundRequestActionsEnabled = true)
    public void getOneShouldManageProvidedHeadersAndParameters(GetOneSetup setup) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .andExpect(status().isOk())
                .andExpect(collect(setup.getDefaultResultMatchers()));
    }

    @ParameterizedTest(aroundRequestActionsEnabled = true)
    public void getOneShouldRepeatedManageProvidedHeadersAndParameters(GetOneSetup setup) throws Exception {
        val getOneFirstResp = rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .andExpect(status().isOk())
                .andExpect(collect(setup.getDefaultResultMatchers()))
                .andReturn().getResponse();

        flush();

        val getOneSecondResp = rest
                .withAuthorities(setup.getAuthorities())
                .get(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .andExpect(status().isOk())
                .andExpect(collect(setup.getDefaultResultMatchers()))
                .andReturn().getResponse();

        if (isJson(getOneFirstResp)) {
            val getOneFirstNode = readJson(getOneFirstResp.getContentAsString());
            val getOneSecondNode = readJson(getOneSecondResp.getContentAsString());

            removeIgnoredNodes(getOneFirstNode, setup.getIgnoredJsonPaths());
            removeIgnoredNodes(getOneSecondNode, setup.getIgnoredJsonPaths());

            assertThat("Non-equal content of getOne response first and second time, "
                            + "so either there is unexpected changes in the entity, "
                            + "or you should adjust ignored JSON pointers",
                    getOneSecondNode, jsonNodeEquals(writeJson(getOneFirstNode)));
        } else {
            // xml, csv, binary data
            val getOneFirst = getOneFirstResp.getContentAsString();
            val getOneSecond = getOneSecondResp.getContentAsString();

            if (!getOneFirst.equals(getOneSecond)) {
                throw new ComparisonFailure("Non-equal content of getOne response first and second time, "
                        + "so either there is unexpected changes in the resource, "
                        + "or you should skip this validation (see setRepeatable() flag)",
                        ContentUtils.thresholdPrintableContent(getOneSecond, -1),
                        ContentUtils.thresholdPrintableContent(getOneFirst, -1));
            }
        }
    }

    @ParameterizedTest(aroundRequestActionsEnabled = true)
    public void getOneShouldManageExtraRequests(GetOneSetup setup, GetOneRequest request) throws Exception {
        rest
                .withAuthorities(request.getAuthorities(setup))
                .get(setup, request)
                .headers(request.getHeaders(setup))
                .params(request.getParameters(setup))
                .andExpect(request.isBadRequest() ? status().isBadRequest() : status().isOk())
                .andExpect(collect(request.getResultMatchers(setup)));
    }

    // Base integration tests for post

    @ParameterizedTest
    public void postShouldReturn401WithAnonymous(PostSetup setup) throws Exception {
        rest
                .post(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(setup.getDefaultPayload())
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    public void postShouldReturn403WhenNoPermissions(PostSetup setup) throws Exception {
        rest
                .withAuthorities()
                .post(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(setup.getDefaultPayload())
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    public void postShouldReturn403WhenInvalidPermissions(PostSetup setup) throws Exception {
        rest
                .withAuthorities(BAD_PERMISSION)
                .post(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(setup.getDefaultPayload())
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    public void postShouldReturn400WhenIllegalPathVariablesAndEmptyPayload(PostSetup setup, VerifiablePathVariables entry) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .post(setup, entry)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content("")
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    public void postShouldReturn400WhenInvalidPathVariablesAndEmptyPayload(
            PostSetup setup, VerifiablePathVariables entry) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .post(setup, entry)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content("")
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    public void postShouldReturn400WhenEmptyPayload(PostSetup setup) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .post(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content("")
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    public void postShouldReturn400WhenIllegalPathVariablesAndPayloadWithoutRequiredFields(
            PostSetup setup, VerifiablePathVariables entry, GeneratedPayload generatedPayload) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .post(setup, entry)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(generatedPayload.getPayload())
                .andExpect(status().isBadRequest())
                .andExpect(collect(entry.getResultMatchers()));
    }

    @ParameterizedTest
    public void postShouldReturn400WhenIllegalPathVariablesAndPayloadWithRedundantFields(
            PostSetup setup, VerifiablePathVariables entry, GeneratedPayload generatedPayload) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .post(setup, entry)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(generatedPayload.getPayload())
                .andExpect(status().isBadRequest())
                .andExpect(collect(entry.getResultMatchers()));
    }

    @ParameterizedTest
    public void postShouldReturn400WhenInvalidPathVariablesAndPayloadWithoutRequiredFields(
            PostSetup setup, VerifiablePathVariables entry, GeneratedPayload generatedPayload) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .post(setup, entry)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(generatedPayload.getPayload())
                .andExpect(status().isBadRequest())
                .andExpect(requiredMatcher(generatedPayload));
    }

    @ParameterizedTest
    public void postShouldReturn400WhenInvalidPathVariablesAndPayloadWithRedundantFields(
            PostSetup setup, VerifiablePathVariables entry, GeneratedPayload generatedPayload) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .post(setup, entry)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(generatedPayload.getPayload())
                .andExpect(status().isBadRequest())
                .andExpect(redundantMatcher(generatedPayload));
    }

    @ParameterizedTest
    public void postShouldReturn400WhenPayloadWithoutRequiredFields(
            PostSetup setup, GeneratedPayload generatedPayload) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .post(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(generatedPayload.getPayload())
                .andExpect(status().isBadRequest())
                .andExpect(requiredMatcher(generatedPayload));
    }

    @ParameterizedTest
    public void postShouldReturn400WhenPayloadWithRedundantFields(
            PostSetup setup, GeneratedPayload generatedPayload) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .post(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(generatedPayload.getPayload())
                .andExpect(status().isBadRequest())
                .andExpect(redundantMatcher(generatedPayload));
    }

    @ParameterizedTest
    public void postShouldReturn400WhenIllegalPathVariablesAndDefaultPayload(
            PostSetup setup, VerifiablePathVariables entry) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .post(setup, entry)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(setup.getDefaultPayload())
                .andExpect(status().isBadRequest())
                .andExpect(collect(entry.getResultMatchers()));
    }

    @ParameterizedTest
    public void postShouldReturn400WhenIllegalPathVariablesAndMinimalPayload(
            PostSetup setup, VerifiablePathVariables entry) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .post(setup, entry)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(setup.getMinimalPayload())
                .andExpect(status().isBadRequest())
                .andExpect(collect(entry.getResultMatchers()));
    }

    @ParameterizedTest
    public void postShouldReturn404WhenInvalidPathVariablesAndDefaultPayload(
            PostSetup setup, VerifiablePathVariables entry) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .post(setup, entry)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(setup.getDefaultPayload())
                .andExpect(status().isNotFound())
                .andExpect(collect(entry.getResultMatchers()));
    }

    @ParameterizedTest
    public void postShouldReturn404WhenInvalidPathVariablesAndMinimalPayload(
            PostSetup setup, VerifiablePathVariables entry) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .post(setup, entry)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(setup.getMinimalPayload())
                .andExpect(status().isNotFound())
                .andExpect(collect(entry.getResultMatchers()));
    }

    @ParameterizedTest
    public void postShouldReturn400WhenMissingRequiredParameters(
            PostSetup setup, VerifiableParameter parameter) throws Exception {
        // Remove 1 required parameter
        Map<String, Object> parameters = new HashMap<>(setup.getParameters());
        parameters.remove(parameter.getName());

        rest
                .withAuthorities(setup.getAuthorities())
                .post(setup)
                .headers(setup.getHeaders())
                .params(parameters)
                .content(setup.getDefaultPayload())
                .andExpect(status().isBadRequest())
                .andExpect(collect(parameter.getResultMatchers()));
    }

    @ParameterizedTest
    public void postShouldReturn400WhenBadParameters(
            PostSetup setup, VerifiableParameter parameter) throws Exception {
        Map<String, Object> parameters = new HashMap<>(setup.getParameters());
        parameters.put(parameter.getName(), parameter.getParameterValue());

        rest
                .withAuthorities(setup.getAuthorities())
                .post(setup)
                .headers(setup.getHeaders())
                .params(parameters)
                .content(setup.getDefaultPayload())
                .andExpect(status().isBadRequest())
                .andExpect(collect(parameter.getResultMatchers()));
    }

    @ParameterizedTest
    public void postShouldReturn400WhenMissingRequiredHeaders(
            PostSetup setup, VerifiableHeader header) throws Exception {
        // Remove 1 required header
        Map<String, Object> headers = new HashMap<>(setup.getHeaders());
        headers.remove(header.getName());

        rest
                .withAuthorities(setup.getAuthorities())
                .post(setup)
                .headers(headers)
                .params(setup.getParameters())
                .content(setup.getDefaultPayload())
                .andExpect(status().isBadRequest())
                .andExpect(collect(header.getResultMatchers()));
    }

    @ParameterizedTest
    public void postShouldReturn400WhenBadHeaders(
            PostSetup setup, VerifiableHeader header) throws Exception {
        Map<String, Object> headers = new HashMap<>(setup.getHeaders());
        headers.put(header.getName(), header.getHeaderValue());

        rest
                .withAuthorities(setup.getAuthorities())
                .post(setup)
                .headers(headers)
                .params(setup.getParameters())
                .content(setup.getDefaultPayload())
                .andExpect(status().isBadRequest())
                .andExpect(collect(header.getResultMatchers()));
    }

    @ParameterizedTest
    public void postShouldReturn400WhenProjectionInvalid(PostSetup setup) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .post(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(setup.getDefaultPayload())
                .projection(SampleProjection.BAD_PROJECTION_VALUE)
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    public void postShouldReturn403WhenForbiddenProjection(
            PostSetup setup, VerifiableProjection projection) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .post(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .projection(projection.getProjection())
                .content(setup.getDefaultPayload())
                .andExpect(status().isForbidden())
                .andExpect(collect(projection.getResultMatchers()));
    }

    @ParameterizedTest(aroundRequestActionsEnabled = true)
    public void postShouldManageDefaultPayload(PostSetup setup) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .post(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(setup.getDefaultPayload())
                .andExpect(statusMatcher(setup))
                .andExpect(collect(setup.getDefaultResultMatchers()));
    }

    @ParameterizedTest(aroundRequestActionsEnabled = true)
    public void postShouldCreateResource(PostSetup setup) throws Exception {
        val resp = rest
                .withAuthorities(setup.getAuthorities())
                .post(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(setup.getDefaultPayload())
                .andExpect(status().isCreated())
                .andExpect(collect(setup.getDefaultResultMatchers()))
                .andReturn().getResponse();

        flush();

        val location = location(resp);

        // todo REST-53: verify response body (should match POST request body)
        rest.withAuthorities(setup.getAuthorities())
                .perform(get(location))
                .andExpect(status().isOk());
    }

    @ParameterizedTest(aroundRequestActionsEnabled = true)
    public void postShouldManageMinimalPayload(PostSetup setup) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .post(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(setup.getMinimalPayload())
                .andExpect(statusMatcher(setup));
    }

    @ParameterizedTest(aroundRequestActionsEnabled = true)
    public void postShouldManagePayloadWithOptionalFields(
            PostSetup setup, GeneratedPayload generatedPayload) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .post(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(generatedPayload.getPayload())
                .andExpect(statusMatcher(setup));
    }

    @ParameterizedTest(aroundRequestActionsEnabled = true)
    public void postShouldManageProjections(
            PostSetup setup, VerifiableProjection projection) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .post(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(setup.getDefaultPayload())
                .projection(projection.getProjection())
                .andExpect(statusMatcher(setup))
                .andExpect(collect(projection.getResultMatchers()));
    }

    @ParameterizedTest(aroundRequestActionsEnabled = true)
    public void postShouldManageValidPayloads(PostSetup setup, VerifiablePayload payload) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .post(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(payload.getPayload())
                .andExpect(statusMatcher(setup))
                .andExpect(payload.getResultMatcher());
    }

    @ParameterizedTest
    public void postShouldRejectInvalidPayloads(PostSetup setup, VerifiablePayload payload) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .post(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(payload.getPayload())
                .andExpect(payload.getStatusMatcher())
                .andExpect(payload.getResultMatcher());
    }

    @ParameterizedTest(aroundRequestActionsEnabled = true)
    public void postShouldManageExtraRequests(PostSetup setup, PostRequest request) throws Exception {
        rest
                .withAuthorities(request.getAuthorities(setup))
                .post(setup, request)
                .headers(request.getHeaders(setup))
                .params(request.getParameters(setup))
                .content(request.getValidPayload(setup))
                .andExpect(statusMatcher(setup))
                .andExpect(collect(request.getResultMatchers(setup)));
    }

    // Base integration tests for patch

    @ParameterizedTest
    public void patchShouldReturn401WithAnonymous(PatchSetup setup) throws Exception {
        rest
                .patch(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(setup.getDefaultPayload())
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    public void patchShouldReturn403WhenNoPermissions(PatchSetup setup) throws Exception {
        rest
                .withAuthorities()
                .patch(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(setup.getDefaultPayload())
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    public void patchShouldReturn403WhenInvalidPermissions(PatchSetup setup) throws Exception {
        rest
                .withAuthorities(BAD_PERMISSION)
                .patch(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(setup.getDefaultPayload())
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    public void patchShouldReturn400WhenIllegalPathVariablesAndEmptyPayload(
            PatchSetup setup, VerifiablePathVariables entry) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .patch(setup, entry)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content("")
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    public void patchShouldReturn400WhenInvalidPathVariablesAndEmptyPayload(
            PatchSetup setup, VerifiablePathVariables entry) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .patch(setup, entry)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content("")
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    public void patchShouldReturn400WhenEmptyPayload(PatchSetup setup) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .patch(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content("")
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    public void patchShouldReturn400WhenIllegalPathVariablesAndPayloadWithoutRequiredFields(
            PatchSetup setup, VerifiablePathVariables entry, GeneratedPayload generatedPayload) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .patch(setup, entry)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(generatedPayload.getPayload())
                .andExpect(status().isBadRequest())
                .andExpect(collect(entry.getResultMatchers()));
    }

    @ParameterizedTest
    public void patchShouldReturn400WhenIllegalPathVariablesAndPayloadWithRedundantFields(
            PatchSetup setup, VerifiablePathVariables entry, GeneratedPayload generatedPayload) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .patch(setup, entry)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(generatedPayload.getPayload())
                .andExpect(status().isBadRequest())
                .andExpect(collect(entry.getResultMatchers()));
    }

    @ParameterizedTest
    public void patchShouldReturn400WhenInvalidPathVariablesAndPayloadWithoutRequiredFields(
            PatchSetup setup, VerifiablePathVariables entry, GeneratedPayload generatedPayload) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .patch(setup, entry)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(generatedPayload.getPayload())
                .andExpect(status().isBadRequest())
                .andExpect(requiredMatcher(generatedPayload));
    }

    @ParameterizedTest
    public void patchShouldReturn400WhenInvalidPathVariablesAndPayloadWithRedundantFields(
            PatchSetup setup, VerifiablePathVariables entry, GeneratedPayload generatedPayload) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .patch(setup, entry)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(generatedPayload.getPayload())
                .andExpect(status().isBadRequest())
                .andExpect(redundantMatcher(generatedPayload));
    }

    @ParameterizedTest
    public void patchShouldReturn400WhenPayloadWithoutRequiredFields(
            PatchSetup setup, GeneratedPayload generatedPayload) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .patch(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(generatedPayload.getPayload())
                .andExpect(status().isBadRequest())
                .andExpect(requiredMatcher(generatedPayload));
    }

    @ParameterizedTest
    public void patchShouldReturn400WhenPayloadWithRedundantFields(
            PatchSetup setup, GeneratedPayload generatedPayload) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .patch(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(generatedPayload.getPayload())
                .andExpect(status().isBadRequest())
                .andExpect(redundantMatcher(generatedPayload));
    }

    @ParameterizedTest
    public void patchShouldReturn400WhenIllegalPathVariablesAndDefaultPayload(
            PatchSetup setup, VerifiablePathVariables entry) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .patch(setup, entry)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(setup.getDefaultPayload())
                .andExpect(status().isBadRequest())
                .andExpect(collect(entry.getResultMatchers()));
    }

    @ParameterizedTest
    public void patchShouldReturn400WhenIllegalPathVariablesAndMinimalPayload(
            PatchSetup setup, VerifiablePathVariables entry) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .patch(setup, entry)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(setup.getMinimalPayload())
                .andExpect(status().isBadRequest())
                .andExpect(collect(entry.getResultMatchers()));
    }

    @ParameterizedTest
    public void patchShouldReturn404WhenInvalidPathVariablesAndDefaultPayload(
            PatchSetup setup, VerifiablePathVariables entry) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .patch(setup, entry)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(setup.getDefaultPayload())
                .andExpect(status().isNotFound())
                .andExpect(collect(entry.getResultMatchers()));
    }

    @ParameterizedTest
    public void patchShouldReturn404WhenInvalidPathVariablesAndMinimalPayload(
            PatchSetup setup, VerifiablePathVariables entry) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .patch(setup, entry)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(setup.getMinimalPayload())
                .andExpect(status().isNotFound())
                .andExpect(collect(entry.getResultMatchers()));
    }

    @ParameterizedTest
    public void patchShouldReturn400WhenMissingRequiredParameters(
            PatchSetup setup, VerifiableParameter parameter) throws Exception {
        // Remove 1 required parameter
        Map<String, Object> parameters = new HashMap<>(setup.getParameters());
        parameters.remove(parameter.getName());

        rest
                .withAuthorities(setup.getAuthorities())
                .patch(setup)
                .headers(setup.getHeaders())
                .params(parameters)
                .content(setup.getDefaultPayload())
                .andExpect(status().isBadRequest())
                .andExpect(collect(parameter.getResultMatchers()));
    }

    @ParameterizedTest
    public void patchShouldReturn400WhenBadParameters(
            PatchSetup setup, VerifiableParameter parameter) throws Exception {
        Map<String, Object> parameters = new HashMap<>(setup.getParameters());
        parameters.put(parameter.getName(), parameter.getParameterValue());

        rest
                .withAuthorities(setup.getAuthorities())
                .patch(setup)
                .headers(setup.getHeaders())
                .params(parameters)
                .content(setup.getDefaultPayload())
                .andExpect(status().isBadRequest())
                .andExpect(collect(parameter.getResultMatchers()));
    }

    @ParameterizedTest
    public void patchShouldReturn400WhenMissingRequiredHeaders(
            PatchSetup setup, VerifiableHeader header) throws Exception {
        // Remove 1 required header
        Map<String, Object> headers = new HashMap<>(setup.getHeaders());
        headers.remove(header.getName());

        rest
                .withAuthorities(setup.getAuthorities())
                .patch(setup)
                .headers(headers)
                .params(setup.getParameters())
                .content(setup.getDefaultPayload())
                .andExpect(status().isBadRequest())
                .andExpect(collect(header.getResultMatchers()));
    }

    @ParameterizedTest
    public void patchShouldReturn400WhenBadHeaders(
            PatchSetup setup, VerifiableHeader header) throws Exception {
        Map<String, Object> headers = new HashMap<>(setup.getHeaders());
        headers.put(header.getName(), header.getHeaderValue());

        rest
                .withAuthorities(setup.getAuthorities())
                .patch(setup)
                .headers(headers)
                .params(setup.getParameters())
                .content(setup.getDefaultPayload())
                .andExpect(status().isBadRequest())
                .andExpect(collect(header.getResultMatchers()));
    }

    @ParameterizedTest
    public void patchShouldReturn400WhenProjectionInvalid(PatchSetup setup) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .patch(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .projection(SampleProjection.BAD_PROJECTION_VALUE)
                .content(setup.getDefaultPayload())
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    public void patchShouldReturn403WhenForbiddenProjection(
            PatchSetup setup, VerifiableProjection projection) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .patch(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .projection(projection.getProjection())
                .content(setup.getDefaultPayload())
                .andExpect(status().isForbidden())
                .andExpect(collect(projection.getResultMatchers()));
    }

    @ParameterizedTest(aroundRequestActionsEnabled = true)
    public void patchShouldManageDefaultPayload(PatchSetup setup) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .patch(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(setup.getDefaultPayload())
                .andExpect(statusMatcher(setup))
                .andExpect(collect(setup.getDefaultResultMatchers()));
    }

    @ParameterizedTest(aroundRequestActionsEnabled = true)
    public void patchShouldManageMinimalPayload(PatchSetup setup) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .patch(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(setup.getMinimalPayload())
                .andExpect(statusMatcher(setup));
    }

    @ParameterizedTest(aroundRequestActionsEnabled = true)
    public void patchShouldManagePayloadWithOptionalFields(
            PatchSetup setup, GeneratedPayload generatedPayload) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .patch(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(generatedPayload.getPayload())
                .andExpect(statusMatcher(setup));
    }

    @ParameterizedTest(aroundRequestActionsEnabled = true)
    public void patchShouldManageProjections(
            PatchSetup setup, VerifiableProjection projection) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .patch(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .projection(projection.getProjection())
                .content(setup.getDefaultPayload())
                .andExpect(statusMatcher(setup))
                .andExpect(collect(projection.getResultMatchers()));
    }

    @ParameterizedTest(aroundRequestActionsEnabled = true)
    public void patchShouldManageValidPayloads(PatchSetup setup, VerifiablePayload payload) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .patch(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(payload.getPayload())
                .andExpect(statusMatcher(setup))
                .andExpect(payload.getResultMatcher());
    }

    @ParameterizedTest
    public void patchShouldRejectInvalidPayloads(PatchSetup setup, VerifiablePayload payload) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .patch(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(payload.getPayload())
                .andExpect(payload.getStatusMatcher())
                .andExpect(payload.getResultMatcher());
    }

    @ParameterizedTest(aroundRequestActionsEnabled = true)
    public void patchShouldManageExtraRequests(PatchSetup setup, PatchRequest request) throws Exception {
        rest
                .withAuthorities(request.getAuthorities(setup))
                .patch(setup, request)
                .headers(request.getHeaders(setup))
                .params(request.getParameters(setup))
                .content(request.getValidPayload(setup))
                .andExpect(statusMatcher(setup))
                .andExpect(collect(request.getResultMatchers(setup)));
    }

    @ParameterizedTest(aroundRequestActionsEnabled = true)
    public void patchShouldNotSurplusChangeEntry(
            PatchSetup patchSetup, GetOneSetup getOneSetup,
            VerifiableProjection projection) throws Exception {

        val getOneBefore = readJson(rest
                .withAuthorities(getOneSetup.getAuthorities())
                .get(getOneSetup)
                .projection(projection.getProjection())
                .headers(getOneSetup.getHeaders())
                .params(getOneSetup.getParameters())
                .andExpect(status().isOk())
                .andExpect(collect(projection.getResultMatchers()))
                .andReturn().getResponse().getContentAsString());

        val patchPayload = patchSetup.hasMinimalPayload() ? patchSetup.getMinimalPayload()
                : patchSetup.getDefaultPayload();
        val patchResult = rest
                .withAuthorities(patchSetup.getAuthorities())
                .patch(patchSetup)
                .headers(patchSetup.getHeaders())
                .params(patchSetup.getParameters())
                .content(patchPayload)
                .andExpect(statusMatcher(patchSetup))
                .andReturn();

        val requestBodyParameter = getRequestBodyBasePayloadParameter(patchResult)
                .orElseThrow(() -> new IllegalStateException("Missing required @RequestBody PartialPayload "
                        + "parameter for " + patchResult.getHandler()));

        flush();

        val ignoredJsonPaths = new ArrayList<>(patchSetup.getIgnoredJsonPaths());
        val updateNode = objectMapper()
                .readTree(objectMapper().writeValueAsString(patchPayload));
        ignoredJsonPaths.addAll(mergeExistingFields(getOneBefore, updateNode, requestBodyParameter.getParameterType()));

        val getOneAfter = readJson(rest
                .withAuthorities(getOneSetup.getAuthorities())
                .get(getOneSetup)
                .projection(projection.getProjection())
                .headers(getOneSetup.getHeaders())
                .params(getOneSetup.getParameters())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());

        removeIgnoredNodes(getOneBefore, ignoredJsonPaths);
        removeIgnoredNodes(getOneAfter, ignoredJsonPaths);

        assertThat("Non-equal content of getOne response before and after PATCH request "
                        + "for projection " + projection + ", so either there is unexpected changes in the entity, "
                        + "or you should adjust ignored JSON pointers",
                getOneAfter, jsonNodeEquals(writeJson(getOneBefore)));
    }

    // Base integration tests for put

    @ParameterizedTest
    public void putShouldReturn401WithAnonymous(PutSetup setup) throws Exception {
        rest
                .put(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(setup.getDefaultPayload())
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    public void putShouldReturn403WhenNoPermissions(PutSetup setup) throws Exception {
        rest
                .withAuthorities()
                .put(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(setup.getDefaultPayload())
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    public void putShouldReturn403WhenInvalidPermissions(PutSetup setup) throws Exception {
        rest
                .withAuthorities(BAD_PERMISSION)
                .put(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(setup.getDefaultPayload())
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    public void putShouldReturn400WhenIllegalPathVariablesAndEmptyPayload(
            PutSetup setup, VerifiablePathVariables entry) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .put(setup, entry)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content("")
                .andExpect(status().isBadRequest())
                .andExpect(collect(entry.getResultMatchers()));
    }

    @ParameterizedTest
    public void putShouldReturn400WhenInvalidPathVariablesAndEmptyPayload(
            PutSetup setup, VerifiablePathVariables entry) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .put(setup, entry)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content("")
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    public void putShouldReturn400WhenEmptyPayload(PutSetup setup) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .put(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content("")
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    public void putShouldReturn400WhenIllegalPathVariablesAndPayloadWithoutRequiredFields(
            PutSetup setup, VerifiablePathVariables entry, GeneratedPayload generatedPayload) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .put(setup, entry)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(generatedPayload.getPayload())
                .andExpect(status().isBadRequest())
                .andExpect(collect(entry.getResultMatchers()));
    }

    @ParameterizedTest
    public void putShouldReturn400WhenIllegalPathVariablesAndPayloadWithRedundantFields(
            PutSetup setup, VerifiablePathVariables entry, GeneratedPayload generatedPayload) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .put(setup, entry)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(generatedPayload.getPayload())
                .andExpect(status().isBadRequest())
                .andExpect(collect(entry.getResultMatchers()));
    }

    @ParameterizedTest
    public void putShouldReturn400WhenInvalidPathVariablesAndPayloadWithoutRequiredFields(
            PutSetup setup, VerifiablePathVariables entry, GeneratedPayload generatedPayload) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .put(setup, entry)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(generatedPayload.getPayload())
                .andExpect(status().isBadRequest())
                .andExpect(requiredMatcher(generatedPayload));
    }

    @ParameterizedTest
    public void putShouldReturn400WhenInvalidPathVariablesAndPayloadWithRedundantFields(
            PutSetup setup, VerifiablePathVariables entry, GeneratedPayload generatedPayload) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .put(setup, entry)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(generatedPayload.getPayload())
                .andExpect(status().isBadRequest())
                .andExpect(redundantMatcher(generatedPayload));
    }

    @ParameterizedTest
    public void putShouldReturn400WhenPayloadWithoutRequiredFields(
            PutSetup setup, GeneratedPayload generatedPayload) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .put(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(generatedPayload.getPayload())
                .andExpect(status().isBadRequest())
                .andExpect(requiredMatcher(generatedPayload));
    }

    @ParameterizedTest
    public void putShouldReturn400WhenPayloadWithRedundantFields(
            PutSetup setup, GeneratedPayload generatedPayload) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .put(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(generatedPayload.getPayload())
                .andExpect(status().isBadRequest())
                .andExpect(redundantMatcher(generatedPayload));
    }

    @ParameterizedTest
    public void putShouldReturn400WhenIllegalPathVariablesAndDefaultPayload(
            PutSetup setup, VerifiablePathVariables entry) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .put(setup, entry)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(setup.getDefaultPayload())
                .andExpect(status().isBadRequest())
                .andExpect(collect(entry.getResultMatchers()));
    }

    @ParameterizedTest
    public void putShouldReturn400WhenIllegalPathVariablesAndMinimalPayload(
            PutSetup setup, VerifiablePathVariables entry) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .put(setup, entry)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(setup.getMinimalPayload())
                .andExpect(status().isBadRequest())
                .andExpect(collect(entry.getResultMatchers()));
    }

    @ParameterizedTest
    public void putShouldReturn404WhenInvalidPathVariablesAndDefaultPayload(
            PutSetup setup, VerifiablePathVariables entry) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .put(setup, entry)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(setup.getDefaultPayload())
                .andExpect(status().isNotFound())
                .andExpect(collect(entry.getResultMatchers()));
    }

    @ParameterizedTest
    public void putShouldReturn404WhenInvalidPathVariablesAndMinimalPayload(
            PutSetup setup, VerifiablePathVariables entry) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .put(setup, entry)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(setup.getMinimalPayload())
                .andExpect(status().isNotFound())
                .andExpect(collect(entry.getResultMatchers()));
    }

    @ParameterizedTest
    public void putShouldReturn400WhenMissingRequiredParameters(
            PutSetup setup, VerifiableParameter parameter) throws Exception {
        // Remove 1 required parameter
        Map<String, Object> parameters = new HashMap<>(setup.getParameters());
        parameters.remove(parameter.getName());

        rest
                .withAuthorities(setup.getAuthorities())
                .put(setup)
                .headers(setup.getHeaders())
                .params(parameters)
                .content(setup.getDefaultPayload())
                .andExpect(status().isBadRequest())
                .andExpect(collect(parameter.getResultMatchers()));
    }

    @ParameterizedTest
    public void putShouldReturn400WhenBadParameters(
            PutSetup setup, VerifiableParameter parameter) throws Exception {
        Map<String, Object> parameters = new HashMap<>(setup.getParameters());
        parameters.put(parameter.getName(), parameter.getParameterValue());

        rest
                .withAuthorities(setup.getAuthorities())
                .put(setup)
                .headers(setup.getHeaders())
                .params(parameters)
                .content(setup.getDefaultPayload())
                .andExpect(status().isBadRequest())
                .andExpect(collect(parameter.getResultMatchers()));
    }

    @ParameterizedTest
    public void putShouldReturn400WhenMissingRequiredHeaders(
            PutSetup setup, VerifiableHeader header) throws Exception {
        // Remove 1 required header
        Map<String, Object> headers = new HashMap<>(setup.getHeaders());
        headers.remove(header.getName());

        rest
                .withAuthorities(setup.getAuthorities())
                .put(setup)
                .headers(headers)
                .params(setup.getParameters())
                .content(setup.getDefaultPayload())
                .andExpect(status().isBadRequest())
                .andExpect(collect(header.getResultMatchers()));
    }

    @ParameterizedTest
    public void putShouldReturn400WhenBadHeaders(
            PutSetup setup, VerifiableHeader header) throws Exception {
        Map<String, Object> headers = new HashMap<>(setup.getHeaders());
        headers.put(header.getName(), header.getHeaderValue());

        rest
                .withAuthorities(setup.getAuthorities())
                .put(setup)
                .headers(headers)
                .params(setup.getParameters())
                .content(setup.getDefaultPayload())
                .andExpect(status().isBadRequest())
                .andExpect(collect(header.getResultMatchers()));
    }

    @ParameterizedTest
    public void putShouldReturn400WhenProjectionInvalid(PutSetup setup) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .put(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .projection(SampleProjection.BAD_PROJECTION_VALUE)
                .content(setup.getDefaultPayload())
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    public void putShouldReturn403WhenForbiddenProjection(
            PutSetup setup, VerifiableProjection projection) throws Exception {

        rest
                .withAuthorities(setup.getAuthorities())
                .put(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .projection(projection.getProjection())
                .content(setup.getDefaultPayload())
                .andExpect(status().isForbidden())
                .andExpect(collect(projection.getResultMatchers()));
    }

    @ParameterizedTest(aroundRequestActionsEnabled = true)
    public void putShouldManageDefaultPayload(PutSetup setup) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .put(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(setup.getDefaultPayload())
                .andExpect(statusMatcher(setup))
                .andExpect(collect(setup.getDefaultResultMatchers()));
    }

    @ParameterizedTest(aroundRequestActionsEnabled = true)
    public void putShouldManageMinimalPayload(PutSetup setup) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .put(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(setup.getMinimalPayload())
                .andExpect(statusMatcher(setup));
    }

    @ParameterizedTest(aroundRequestActionsEnabled = true)
    public void putShouldManagePayloadWithOptionalFields(
            PutSetup setup, GeneratedPayload generatedPayload) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .put(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(generatedPayload.getPayload())
                .andExpect(statusMatcher(setup));
    }

    @ParameterizedTest(aroundRequestActionsEnabled = true)
    public void putShouldManageProjections(
            PutSetup setup, VerifiableProjection projection) throws Exception {

        rest
                .withAuthorities(setup.getAuthorities())
                .put(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .projection(projection.getProjection())
                .content(setup.getDefaultPayload())
                .andExpect(statusMatcher(setup))
                .andExpect(collect(projection.getResultMatchers()));
    }

    @ParameterizedTest(aroundRequestActionsEnabled = true)
    public void putShouldManageValidPayloads(PutSetup setup, VerifiablePayload payload) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .put(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(payload.getPayload())
                .andExpect(statusMatcher(setup))
                .andExpect(payload.getResultMatcher());
    }

    @ParameterizedTest
    public void putShouldRejectInvalidPayloads(PutSetup setup, VerifiablePayload payload) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .put(setup)
                .headers(setup.getHeaders())
                .params(setup.getParameters())
                .content(payload.getPayload())
                .andExpect(payload.getStatusMatcher())
                .andExpect(payload.getResultMatcher());
    }

    @ParameterizedTest(aroundRequestActionsEnabled = true)
    public void putShouldManageExtraRequests(PutSetup setup, PutRequest request) throws Exception {
        rest
                .withAuthorities(request.getAuthorities(setup))
                .put(setup, request)
                .headers(request.getHeaders(setup))
                .params(request.getParameters(setup))
                .content(request.getValidPayload(setup))
                .andExpect(statusMatcher(setup))
                .andExpect(collect(request.getResultMatchers(setup)));
    }

    // Base integration tests for delete

    @ParameterizedTest
    public void deleteShouldReturn401WithAnonymous(DeleteSetup setup) throws Exception {
        rest
                .delete(setup)
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest
    public void deleteShouldReturn403WhenNoPermissions(DeleteSetup setup) throws Exception {
        rest
                .withAuthorities()
                .delete(setup)
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    public void deleteShouldReturn403WhenInvalidPermissions(DeleteSetup setup) throws Exception {
        rest
                .withAuthorities(BAD_PERMISSION)
                .delete(setup)
                .andExpect(status().isForbidden());
    }

    @ParameterizedTest
    public void deleteShouldReturn400WhenIllegalPathVariables(DeleteSetup setup, VerifiablePathVariables entry) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .delete(setup, entry)
                .andExpect(status().isBadRequest())
                .andExpect(collect(entry.getResultMatchers()));
    }

    @ParameterizedTest
    public void deleteShouldReturn404WhenInvalidPathVariables(DeleteSetup setup, VerifiablePathVariables entry) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .delete(setup, entry)
                .andExpect(status().isNotFound())
                .andExpect(collect(entry.getResultMatchers()));
    }

    @ParameterizedTest(aroundRequestActionsEnabled = true)
    public void deleteShouldReturn204WhenValidId(DeleteSetup setup) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .delete(setup)
                .andExpect(status().isNoContent());
    }

    @ParameterizedTest(aroundRequestActionsEnabled = true)
    public void deleteShouldReturn204WhenRepeated(DeleteSetup setup) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .delete(setup)
                .andExpect(status().isNoContent());

        rest
                .withAuthorities(setup.getAuthorities())
                .delete(setup)
                .andExpect(status().isNoContent());
    }

    @ParameterizedTest(aroundRequestActionsEnabled = true)
    public void deleteShouldReturn404WhenRepeated(DeleteSetup setup) throws Exception {
        rest
                .withAuthorities(setup.getAuthorities())
                .delete(setup)
                .andExpect(status().isNoContent());

        rest
                .withAuthorities(setup.getAuthorities())
                .delete(setup)
                .andExpect(status().isNotFound())
                .andExpect(notFoundMatcher(setup.getPathVariables()));
    }

    @ParameterizedTest(aroundRequestActionsEnabled = true)
    public void deleteShouldManageExtraRequests(DeleteSetup setup, DeleteRequest request) throws Exception {
        rest
                .withAuthorities(request.getAuthorities(setup))
                .delete(setup, request)
                .andExpect(status().isNoContent());
    }

    protected List<ResultMatcher> paginationMatchers(
            int pageNumber, int pageSize, int numberOfElements, int totalElements, QuantityVerificationMode mode) {
        Page<Object> page = new PageImpl<>(Collections.nCopies(numberOfElements, "whatever"),
                new TestPageRequest(pageNumber, pageSize), totalElements);
        if (mode == EXACT_QTY) {
            return Lists.newArrayList(
                    jsonPath("$.totalElements").value((int) page.getTotalElements()),
                    jsonPath("$.totalPages").value(page.getTotalPages()),
                    jsonPath("$.numberOfElements").value(page.getNumberOfElements()),
                    jsonPath("$.last").value(page.isLast()),
                    jsonPath("$.first").value(page.isFirst()),
                    jsonPath("$.size").value(page.getSize()),
                    jsonPath("$.number").value(page.getNumber()));
        } else {
            return Lists.newArrayList(
                    jsonPath("$.totalElements").value(greaterThanOrEqualTo(page.getTotalElements())),
                    jsonPath("$.totalPages").value(greaterThanOrEqualTo(page.getTotalPages())),
                    jsonPath("$.numberOfElements").value(page.getNumberOfElements()),
                    jsonPath("$.last").value(instanceOf(Boolean.class)),
                    jsonPath("$.first").value(page.isFirst()),
                    jsonPath("$.size").value(page.getSize()),
                    jsonPath("$.number").value(page.getNumber()));
        }
    }

    protected abstract String endpoint();

    void flush() {
        try {
            val entityManager = applicationContext().getBean(EntityManager.class);
            entityManager.flush();
            entityManager.clear();
        } catch (NoClassDefFoundError | NoSuchBeanDefinitionException ignored) {
            // bean not present in the context - just skip the action
        }
    }

    /**
     * Get localhost address with server port for RANDOM_PORT context (empty port for MockMvc).
     *
     * @return localhost uri
     */
    protected String localhostUri() {
        Integer port = localServerPort(applicationContext());
        return localhostUri(port);
    }

    /**
     * URL matcher that validates startsWith(localhostUri()), and the second part of string is validated
     * via passed pathMatcher.
     *
     * @param pathMatcher matcher for url path part (after "https://localhost[:port]" beginning)
     */
    protected static Matcher<String> localhostUriMatcher(Matcher<String> pathMatcher) {
        return new TypeSafeMatcher<String>() {

            @Override
            protected boolean matchesSafely(String uri) {
                val localhostUri = getLocalhostUri();
                if (uri.startsWith(localhostUri)) {
                    val suffix = uri.substring(localhostUri.length());
                    // if suffix starts with ":", it means that uri has port, while localhostUri does not
                    return !suffix.startsWith(":") && pathMatcher.matches(suffix);
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("URI starts with \"" + getLocalhostUri() + "\" "
                        + "and path matches ");
                description.appendDescriptionOf(pathMatcher);
            }

            private String getLocalhostUri() {
                Integer port = TestApplicationContextHolder.getApplicationContext()
                        .map(FrameworkIT::localServerPort)
                        .orElse(null);
                return localhostUri(port);
            }
        };
    }

    @Nonnull
    protected static URI location(MockHttpServletResponse response) {
        val location = response.getHeader(LOCATION);
        assertNotNull("Missing `Location` header", location);
        return URI.create(location);
    }

    private ResultMatcher requiredMatcher(GeneratedPayload generatedPayload) {
        return TestFrameworkConfigHolder.getTestFrameworkConfig()
                .getRequiredMatcher(generatedPayload.getResourceClass(), generatedPayload.getFieldName());
    }

    private ResultMatcher redundantMatcher(GeneratedPayload generatedPayload) {
        return TestFrameworkConfigHolder.getTestFrameworkConfig()
                .getRedundantMatcher(generatedPayload.getResourceClass(), generatedPayload.getFieldName());
    }

    private ResultMatcher typeMismatchMatcher(String error) {
        return TestFrameworkConfigHolder.getTestFrameworkConfig()
                .getTypeMismatchMatcher(error);
    }

    private ResultMatcher notFoundMatcher(Object[] pathVariables) {
        return TestFrameworkConfigHolder.getTestFrameworkConfig()
                .getNotFoundMatcher(pathVariables);
    }

    private ResultMatcher statusMatcher(RequestType requestType) {
        switch (requestType) {
            case NO_RETRIEVAL:
                return status().isNoContent();
            case RETRIEVAL:
                return status().isOk();
            default:
                return status().isMultiStatus();
        }
    }

    private ResultMatcher statusMatcher(PostSetup setup) {
        if (setup.getRequestType() == null) {
            String locationHeaderAntPattern = setup.getLocationHeaderAntPattern();
            if (locationHeaderAntPattern == null) {
                locationHeaderAntPattern = endpoint() + ENDPOINT_ID;
            }
            return collect(
                    status().isCreated(),
                    header().string(LOCATION, locationHeaderMatcher(localhostUri(), locationHeaderAntPattern))
            );
        }
        return statusMatcher(setup.getRequestType());
    }

    static Matcher<String> locationHeaderMatcher(String localhostUri, String locationHeaderAntPattern) {
        return predicate(location -> {
            if (location.startsWith(localhostUri)) {
                // note: in most cases suffix is path, but if localhostUri has port, but location does not,
                // suffix will start with port (like ":8080") and antPatchMatcher will also fail to match (expected)
                val suffix = location.substring(localhostUri.length());
                val antPathMatcher = new AntPathMatcher();
                return antPathMatcher.match(locationHeaderAntPattern, suffix);
            }
            return false;
        }, "Location header starts with \"" + localhostUri + "\" "
                + "and path matches ant pattern \"" + locationHeaderAntPattern + "\"");
    }

    private ResultMatcher statusMatcher(PatchSetup setup) {
        return statusMatcher(setup.getRequestType());
    }

    private ResultMatcher statusMatcher(PutSetup setup) {
        return statusMatcher(setup.getRequestType());
    }

    private ResultMatcher getDefaultRootPathMatcher(GetAllSetup setup) {
        val resultMatchers = new ArrayList<ResultMatcher>();
        if (setup.isPaginationSupported()) {
            int defaultPageSize = FrameworkConfigHolder.getFrameworkConfig().getDefaultPageSize();

            if (setup.getQuantityVerificationMode() == EXACT_QTY
                    || setup.getTotalElements() > defaultPageSize) {
                int expectedSize = Math.min(setup.getTotalElements(), defaultPageSize);

                resultMatchers.add(jsonPath(setup.getRootElement(), hasSize(expectedSize)));
                resultMatchers.addAll(paginationMatchers(0, defaultPageSize, expectedSize, setup.getTotalElements(),
                        setup.getQuantityVerificationMode()));
            } else {
                resultMatchers.add(jsonPath(setup.getRootElement(), hasSize(greaterThanOrEqualTo(1))));
            }
        } else if (setup.getQuantityVerificationMode() == EXACT_QTY) {
            resultMatchers.add(jsonPath(setup.getRootElement(), hasSize(setup.getTotalElements())));
        } else {
            resultMatchers.add(jsonPath(setup.getRootElement(), hasSize(greaterThanOrEqualTo(setup.getTotalElements()))));
        }
        return collect(resultMatchers);
    }

    private static ResultMatcher buildDefaultSearchResultMatcher(GetAllSetup setup) {
        if (setup.getParameters().containsKey(PARAM_SEARCH)) {
            // Param 'search' is explicitly specified. Default 'search' won't be used and therefore, no need
            // to verify against default result matchers for default 'search'.
            return success();
        }

        return collect(setup.getDefaultSearchFields().stream()
                .map(field -> buildSearchResultMatcher(setup, field))
                .collect(Collectors.toList()));
    }

    private static ResultMatcher buildSearchResultMatcher(GetAllSetup setup, VerifiableSearchField field) {
        val jsonPath = setup.getJsonPath(field.getSearchCondition().getField());

        // jsonPath might be blank - e.g. the search is on field F and field F is NOT sent back to clients
        if (StringUtils.isBlank(jsonPath)) {
            return success();
        }

        if (field.getValueMatcher() != null) {
            return new DelegateResultMatcher(jsonPath(jsonPath, matches(field.getValueMatcher())), field);
        }

        if (field.getResultMatchers() != null) {
            return new DelegateResultMatcher(collect(field.getResultMatchers()), field);
        }

        Matcher<Object> matcher = eachMatches(
                field.getSearchCondition().getOperator(),
                field.getSearchCondition().getValue());
        return new DelegateResultMatcher(jsonPath(jsonPath, matcher), field);
    }

    private static ResultMatcher getDefaultSortMatcher(GetAllSetup setup) {
        if (setup.getDefaultSortFields() == null || setup.getParameters().containsKey(PARAM_SORT)) {
            return success();
        }

        return jsonPathNullMissing(setup.getFieldMappingSupport(), setup.getDefaultSortFields());
    }

    private static Optional<MethodParameter> getRequestBodyBasePayloadParameter(MvcResult result) {
        val handler = (HandlerMethod) result.getHandler();
        return handler == null ? Optional.empty() : Stream.of(handler.getMethodParameters())
                .filter(param -> param.hasParameterAnnotation(RequestBody.class))
                .filter(param -> BasePayload.class.isAssignableFrom(param.getParameterType()))
                .findFirst();
    }

    private static String getFullBadSearchParamErrorMessage(String paramValue, String errorMessage) {
        return String.format("Argument [%s] for parameter [search] is invalid: [%s]", paramValue, errorMessage);
    }

    @SuppressWarnings("SameParameterValue")
    private static String getFullBadSortParamErrorMessage(String paramValue, String errorMessage) {
        return String.format("Argument [%s] for parameter [sort] is invalid: [%s", paramValue, errorMessage);
    }

    private static boolean isJson(MockHttpServletResponse response) {
        val contentType = response.getContentType();
        return contentType != null && MediaType.APPLICATION_JSON.includes(MediaType.parseMediaType(contentType));
    }

    @Nullable
    private static Integer localServerPort(ApplicationContext applicationContext) {
        return applicationContext.getEnvironment()
                .getProperty("local.server.port", Integer.class);
    }

    private static String localhostUri(@Nullable Integer port) {
        return "http://localhost" + (port == null ? "" : ":" + port);
    }

}
