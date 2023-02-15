package com.seregamorph.restapi.test.base;

import static com.seregamorph.restapi.test.utils.AcceptUtils.extractIllegalValues;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import com.google.common.collect.Lists;
import com.seregamorph.restapi.config.spi.FrameworkConfigHolder;
import com.seregamorph.restapi.search.LogicalOperator;
import com.seregamorph.restapi.search.SearchOperator;
import com.seregamorph.restapi.search.SearchValue;
import com.seregamorph.restapi.sort.SortDirection;
import com.seregamorph.restapi.sort.SortParam;
import com.seregamorph.restapi.test.TestContext;
import com.seregamorph.restapi.test.base.request.DeleteRequest;
import com.seregamorph.restapi.test.base.setup.BaseSetup;
import com.seregamorph.restapi.test.base.setup.DeleteSetup;
import com.seregamorph.restapi.test.base.setup.GetAllSetup;
import com.seregamorph.restapi.test.base.setup.GetOneSetup;
import com.seregamorph.restapi.test.base.setup.PatchSetup;
import com.seregamorph.restapi.test.base.setup.PostSetup;
import com.seregamorph.restapi.test.base.setup.PutSetup;
import com.seregamorph.restapi.test.base.setup.common.NamedExecution;
import com.seregamorph.restapi.test.base.setup.common.VerifiablePathVariables;
import com.seregamorph.restapi.test.base.setup.common.VerifiableProjection;
import com.seregamorph.restapi.test.base.setup.common.VerifiableSearch;
import com.seregamorph.restapi.test.base.setup.common.VerifiableSearchField;
import com.seregamorph.restapi.test.base.setup.common.VerifiableSortFieldDirection;
import com.seregamorph.restapi.test.base.setup.common.payload.GeneratedPayload;
import com.seregamorph.restapi.test.base.setup.common.payload.GenericArrayPayload;
import com.seregamorph.restapi.test.base.setup.common.payload.GenericArrayPayloadElement;
import com.seregamorph.restapi.test.base.setup.common.payload.GenericPayloads;
import com.seregamorph.restapi.test.base.setup.common.payload.GenericSinglePayload;
import com.seregamorph.restapi.test.base.support.AroundRequestAction;
import com.seregamorph.restapi.test.base.support.AroundRequestActionSupportDelegate;
import com.seregamorph.restapi.test.base.support.RequestType;
import com.seregamorph.restapi.test.utils.TestLambdaUtils;
import com.seregamorph.restapi.test.utils.ThrowingConsumer;
import com.seregamorph.restapi.test.utils.WebTestUtils;
import com.seregamorph.restapi.utils.TypeUtils;
import com.seregamorph.restapi.validators.Accept;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.model.MultipleFailureException;
import org.springframework.core.MethodParameter;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Base MockMvc tests. Notice: If a test is NOT enabled, it should not be run at all.
 */
@RunWith(FrameworkRunner.class)
public abstract class AbstractBaseSpringWebIT extends FrameworkIT {

    private static final String PROPERTY_FRAMEWORK_ALL_TEST_CASES = "framework.all-test-cases";

    @AfterClass
    public static void cleanupAfterTestClass() {
        JsonMatcherProxyFactory.clearCurrentMatcher();
        GenericPayloads.clearCurrentField();
    }

    protected static TestSetup forController(Class<?> controllerClass) {
        val endpoint = FrameworkConfigHolder.getFrameworkConfig()
                .getControllerMapping(controllerClass);
        return new TestSetup(controllerClass, endpoint);
    }

    interface SingleParamCallback<S extends BaseSetup<S, ?>> extends BaseCallback {

        void execute(AbstractBaseSpringWebIT test, S setup) throws Exception;
    }

    interface DoubleParamCallback<S extends BaseSetup<S, ?>, T> extends BaseCallback {

        void execute(AbstractBaseSpringWebIT test, S setup, T param) throws Exception;
    }

    interface TripleParamCallback<S extends BaseSetup<S, ?>, T, U> extends BaseCallback {

        void execute(AbstractBaseSpringWebIT test, S setup, T param, U param2) throws Exception;
    }

    @RequiredArgsConstructor
    static class ExecutionList {

        private final MockMvcTestSetup rootSetup;

        @Getter
        private final Map<String, List<FrameworkExecution>> children = new TreeMap<>();

        private void add(BaseCallback callback, BaseSetup<?, ?> setup, StackTraceHolder stackTraceHolder,
                         String executionEndpointPath, String executionId,
                         ThrowingConsumer<AbstractBaseSpringWebIT> action) {
            val executionName = executionEndpointPath
                    + (executionEndpointPath.isEmpty() || executionId.isEmpty() ? "" : ";")
                    + executionId;
            val execution = new CallbackFrameworkExecution(callback, rootSetup, executionName,
                    setup, action, stackTraceHolder);
            // note: at this moment execution.getName() does not have numeric suffix for sure
            val list = children.computeIfAbsent(execution.getName(), key -> new ArrayList<>());
            list.add(execution);
            if (list.size() > 1) {
                // if there are repeated execution names, each should have a suffix (1-based)
                for (int i = 0, listSize = list.size(); i < listSize; i++) {
                    list.get(i).setExecutionSuffix(i + 1);
                }
            }
        }

        private void add(BaseCallback callback, BaseSetup<?, ?> setup,
                         String executionEndpointPath, String executionId,
                         ThrowingConsumer<AbstractBaseSpringWebIT> action) {
            add(callback, setup, setup, executionEndpointPath, executionId, action);
        }

        // With parameters, callbacks, paths and execution ids

        <S extends BaseSetup<S, ?>> void add(S setup,
                                             SingleParamCallback<S> callback,
                                             String executionEndpointPath) {
            ThrowingConsumer<AbstractBaseSpringWebIT> action = test -> callback.execute(test, setup);
            add(callback, setup, executionEndpointPath, "", action);
        }

        <S extends BaseSetup<S, ?>> void add(S setup,
                                             SingleParamCallback<S> callback) {
            add(setup, callback, executionEndpointPath(setup));
        }

        <S extends BaseSetup<S, ?>, T> void add(S setup,
                                                T param,
                                                DoubleParamCallback<S, T> callback,
                                                String executionEndpointPath,
                                                String executionId) {
            ThrowingConsumer<AbstractBaseSpringWebIT> action = test -> callback.execute(test, setup, param);
            add(callback, setup, executionEndpointPath, executionId, action);
        }

        <S extends BaseSetup<S, ?>, T extends StackTraceHolder> void add(
                S setup, T param,
                DoubleParamCallback<S, T> callback,
                String executionEndpointPath
        ) {
            add(setup, param, callback, executionEndpointPath, "");
        }

        <S extends BaseSetup<S, ?>, T, U> void add(S setup,
                                                   T param,
                                                   U param2,
                                                   TripleParamCallback<S, T, U> callback,
                                                   String executionEndpointPath,
                                                   String executionId) {
            ThrowingConsumer<AbstractBaseSpringWebIT> action = test -> callback.execute(test, setup, param, param2);
            add(callback, setup, executionEndpointPath, executionId, action);
        }

        <S extends BaseSetup<S, ?>, T, U> void add(S setup,
                                                   T param,
                                                   U param2,
                                                   TripleParamCallback<S, T, U> callback,
                                                   String executionId) {
            ThrowingConsumer<AbstractBaseSpringWebIT> action = test -> callback.execute(test, setup, param, param2);
            add(callback, setup, executionEndpointPath(setup), executionId, action);
        }

        // With only parameters and callbacks

        <S extends BaseSetup<S, ?>, E extends NamedExecution> void add(
                S setup, E execution,
                DoubleParamCallback<S, E> callback
        ) {
            String executionEndpointPath = executionEndpointPath(setup);
            String executionId = execution.getName(rootSetup, setup);
            add(setup, execution, callback, executionEndpointPath, executionId);
        }

        <S extends BaseSetup<S, ?>> void add(S setup, VerifiablePathVariables variables,
                                             DoubleParamCallback<S, VerifiablePathVariables> callback) {
            String executionEndpointPath = executionEndpointPath(setup, variables);
            add(setup, variables, callback, executionEndpointPath);
        }

        void add(GetAllSetup setup, VerifiableSearchField searchField,
                 DoubleParamCallback<GetAllSetup, VerifiableSearchField> callback) {
            val fieldName = searchField.getSearchCondition().getField();
            add(setup, searchField, callback, executionEndpointPath(setup), fieldName);
        }

        <S extends BaseSetup<S, ?>> void add(
                S setup,
                VerifiablePathVariables variables,
                GeneratedPayload generatedPayload,
                TripleParamCallback<S, VerifiablePathVariables, GeneratedPayload> callback) {
            val executionId = generatedPayload.getResourceClass().getSimpleName()
                    + "." + generatedPayload.getFieldName();
            add(setup, variables, generatedPayload, callback, executionEndpointPath(setup, variables), executionId);
        }

        void add(GetAllSetup setup,
                 VerifiableSortFieldDirection sortField1,
                 VerifiableSortFieldDirection sortField2,
                 TripleParamCallback<GetAllSetup, VerifiableSortFieldDirection, VerifiableSortFieldDirection> callback) {
            val executionId = String.format("%s;%s", sortField1, sortField2);
            add(setup, sortField1, sortField2, callback, executionId);
        }

        void add(PatchSetup patchSetup,
                 GetOneSetup getOneSetup,
                 VerifiableProjection projection,
                 TripleParamCallback<PatchSetup, GetOneSetup, VerifiableProjection> callback) {
            val executionId = projection.getName(rootSetup, patchSetup);
            add(patchSetup, getOneSetup, projection, callback, executionId);
        }

        String executionEndpointPath(BaseSetup<?, ?> setup) {
            return NamedExecution.buildExecutionEndpointPath(rootSetup, setup);
        }

        String executionEndpointPath(BaseSetup<?, ?> setup, VerifiablePathVariables variables) {
            return NamedExecution.buildExecutionEndpointPath(
                    rootSetup, setup.getPathTemplate(), variables.getPathVariables());
        }
    }

    private static class CallbackFrameworkExecution extends FrameworkExecution {

        private final BaseSetup<?, ?> setup;
        private final ThrowingConsumer<AbstractBaseSpringWebIT> action;
        private final StackTraceHolder stackTraceHolder;

        CallbackFrameworkExecution(BaseCallback callback, MockMvcTestSetup rootSetup, String executionName,
                                   BaseSetup<?, ?> setup, ThrowingConsumer<AbstractBaseSpringWebIT> action,
                                   StackTraceHolder stackTraceHolder) {
            super(Objects.requireNonNull(TestLambdaUtils.unreferenceLambdaMethod(callback),
                    "Could not unreference callback method, it should be "
                            + "smth like `FrameworkIT::postShouldManageDefaultPayload`"),
                    rootSetup, setup, executionName);
            this.setup = setup;
            this.action = action;
            this.stackTraceHolder = stackTraceHolder;
        }

        @Override
        void invoke(AbstractBaseSpringWebIT test) throws Exception {
            boolean aroundRequestActionsEnabled = super.getMethod()
                    .getAnnotation(ParameterizedTest.class)
                    .aroundRequestActionsEnabled();
            test.prepareForSetup(setup, aroundRequestActionsEnabled);
            TestContext.getCurrentTest().setExecutionId(getExecutionName());

            val errors = new ArrayList<Throwable>();
            try {
                action.accept(test);
            } catch (Throwable e) {
                errors.add(e);
            }
            try {
                // we should execute after-actions even if test action failed
                test.finish(setup, aroundRequestActionsEnabled);
            } catch (Throwable e) {
                errors.add(e);
            }
            errors.forEach(error -> {
                if (error instanceof MultipleFailureException) {
                    ((MultipleFailureException) error).getFailures()
                            .forEach(ex -> addSuppressed(stackTraceHolder, ex));
                } else {
                    addSuppressed(stackTraceHolder, error);
                }
            });

            MultipleFailureException.assertEmpty(errors);
        }

        private static void addSuppressed(StackTraceHolder stackTraceHolder, Throwable failure) {
            val trace = stackTraceHolder.getTrace();
            if (Stream.of(failure.getSuppressed())
                    .noneMatch(ex -> Arrays.equals(ex.getStackTrace(), trace))) {
                val error = new AssertionError();
                error.setStackTrace(trace);
                failure.addSuppressed(error);
            }
        }
    }

    static ExecutionList getTestExecutions(TestSetup rootSetup, boolean fullTransactionalSupport) {
        verifyRootSetup(rootSetup);

        val executions = new ExecutionList(rootSetup);

        eachSetup(rootSetup.getGetAllSetups(), setup -> {
            prepareGetAllExecutions(executions, setup);
        });

        eachSetup(rootSetup.getGetOneSetups(), setup -> {
            prepareGetOneExecutions(executions, setup);
        });

        eachSetup(rootSetup.getPostSetups(), setup -> {
            preparePostExecutions(executions, setup, fullTransactionalSupport);
        });

        eachSetup(rootSetup.getPatchSetups(), setup -> {
            preparePatchExecutions(executions, setup, rootSetup.getGetOneSetups(), fullTransactionalSupport);
        });

        eachSetup(rootSetup.getPutSetups(), setup -> {
            preparePutExecutions(executions, setup);
        });

        eachSetup(rootSetup.getDeleteSetups(), setup -> {
            prepareDeleteExecutions(executions, setup, fullTransactionalSupport);
        });

        return executions;
    }

    private static <S extends BaseSetup<S, ?>> void eachSetup(List<S> setups, Consumer<S> action) {
        for (S setup : setups) {
            try {
                action.accept(setup);
            } catch (RuntimeException | Error  e) {
                val failure = new IllegalStateException("Error while processing " + setup, e);
                val error = new AssertionError();
                error.setStackTrace(setup.getTrace());
                failure.addSuppressed(error);
                throw failure;
            }
        }
    }

    private static void prepareGetAllExecutions(ExecutionList executions, GetAllSetup setup) {
        executions.add(setup, FrameworkIT::getAllShouldManageProvidedHeadersAndParameters);
        executions.add(setup, FrameworkIT::getAllShouldRepeatedManageProvidedHeadersAndParameters);

        if (setup.isAuthenticationRequired()) {
            executions.add(setup, FrameworkIT::getAllShouldReturn401WhenAnonymous);
        }
        if (setup.hasAuthorities() && setup.getAuthKeyProvider() == null) {
            executions.add(setup, FrameworkIT::getAllShouldReturn403WhenNoPermissions);
            executions.add(setup, FrameworkIT::getAllShouldReturn403WhenInvalidPermissions);
        }

        for (VerifiablePathVariables entry : setup.getIllegalPathVariables()) {
            executions.add(setup, entry, FrameworkIT::getAllShouldReturn400WhenIllegalPathVariables);
        }

        for (VerifiablePathVariables entry : setup.getInvalidPathVariables()) {
            executions.add(setup, entry, FrameworkIT::getAllShouldReturn404WhenInvalidPathVariables);
        }

        if (setup.hasProjectionResultMatchers()) {
            executions.add(setup, FrameworkIT::getAllShouldReturn400WhenProjectionInvalid);
        }
        if (setup.isPaginationSupported()) {
            executions.add(setup, FrameworkIT::getAllShouldReturn400WhenNonNumericPage);
            executions.add(setup, FrameworkIT::getAllShouldReturn400WhenNegativePage);
            executions.add(setup, FrameworkIT::getAllShouldReturn400WhenNonNumericSize);
            executions.add(setup, FrameworkIT::getAllShouldReturn400WhenNegativeSize);
            executions.add(setup, FrameworkIT::getAllShouldReturn400WhenZeroSize);
            executions.add(setup, FrameworkIT::getAllShouldManageSpecificPageNumber);
            executions.add(setup, FrameworkIT::getAllShouldManageSpecificPageSize);
        }
        for (val parameter : setup.getRequiredParameters()) {
            executions.add(setup, parameter, FrameworkIT::getAllShouldReturn400WhenMissingRequiredParameters);
        }
        for (val parameter : setup.getBadParameters()) {
            executions.add(setup, parameter, FrameworkIT::getAllShouldReturn400WhenBadParameters);
        }
        for (val header : setup.getRequiredHeaders()) {
            executions.add(setup, header, FrameworkIT::getAllShouldReturn400WhenMissingRequiredHeaders);
        }
        for (val header : setup.getBadHeaders()) {
            executions.add(setup, header, FrameworkIT::getAllShouldReturn400WhenBadHeaders);
        }
        for (val projection : setup.getForbiddenProjectionResultMatchers()) {
            executions.add(setup, projection, FrameworkIT::getAllShouldReturn403WhenForbiddenProjection);
        }
        if (setup.hasSearchesOrSearchFields()) {
            executions.add(setup, FrameworkIT::getAllShouldReturn400WhenSearchParamWithoutOperator);

            if (setup.isUndeclaredSearchFieldsForbidden()) {
                // note: only first to avoid massive duplicated validations
                setup.getSearchFields().stream().findFirst().ifPresent(field -> {
                    executions.add(setup, field,
                            FrameworkIT::getAllShouldReturn400WhenSearchParamWithIllegalSearchField,
                            executions.executionEndpointPath(setup), String.format("invalidSearchField %s whatever",
                                    field.getSearchCondition().getOperator().getOperator()));
                });
            }

            for (VerifiableSearchField field : setup.getSearchFields()) {
                executions.add(setup, field, FrameworkIT::getAllShouldManageSearchFields);
            }

            // note: only first to avoid massive duplicated validations
            setup.getSearchFields().stream().findFirst().ifPresent(field -> {
                executions.add(setup, field, FrameworkIT::getAllShouldReturn400WhenSearchParamWithoutValue);
                // note: only first to avoid massive duplicated validations
                val logicalOperator = LogicalOperator.values()[0];
                // note: only first to avoid massive duplicated validations
                val operator = logicalOperator.getOperators()[0];
                executions.add(setup, field, operator,
                        FrameworkIT::getAllShouldReturn400WhenSearchParamWithRedundantLogicalOperator,
                        String.format("%s %s", field.getSearchCondition(), logicalOperator));
                executions.add(setup, field, operator,
                        FrameworkIT::getAllShouldReturn400WhenSearchParamWithoutOperatorInSecondClause,
                        String.format("(%s) %s (%s)", field.getSearchCondition(), logicalOperator, field.getSearchCondition().getField()));
                executions.add(setup, field, operator,
                        FrameworkIT::getAllShouldReturn400WhenSearchParamWithoutValueInSecondClause,
                        String.format("(%s) %s (%s %s)", field.getSearchCondition(), logicalOperator,
                                field.getSearchCondition().getField(), field.getSearchCondition().getOperator().getOperator()));
            });

            // note: only first to avoid massive duplicated validations
            setup.getSearchFields().stream().findFirst().ifPresent(searchField -> {
                executions.add(setup, searchField,
                        FrameworkIT::getAllShouldReturn400WhenSearchParamWithIllegalOperator);
                for (SearchOperator operator : getUnsupportedSearchOperators()) {
                    executions.add(setup, searchField, operator,
                            FrameworkIT::getAllShouldReturn400WhenSearchParamWithInvalidOperator,
                            searchField.getSearchCondition().getField() + " " + operator.getOperator());
                }
                for (SearchValue value : getUnsupportedSpecialSearchValues()) {
                    executions.add(setup, searchField, value,
                            FrameworkIT::getAllShouldReturn400WhenSearchParamWithInvalidSpecialSearchValue,
                            String.format("%s is %s", searchField.getSearchCondition().getField(), value));
                }
            });

            for (VerifiableSearch search : setup.getSearches()) {
                executions.add(setup, search, FrameworkIT::getAllShouldManageSearches);
            }
        }
        if (setup.hasSortFields()) {
            if (setup.isUndeclaredSortFieldsForbidden()) {
                executions.add(setup, FrameworkIT::getAllShouldReturn400WhenSortParamWithIllegalSortField);
            }
            executions.add(setup, FrameworkIT::getAllShouldManageBasicPairedSortFields);
        }
        for (val projection : setup.getProjectionResultMatchers()) {
            executions.add(setup, projection, FrameworkIT::getAllShouldManageProjections);
        }
        for (val req : setup.getExtraRequests()) {
            executions.add(setup, req, FrameworkIT::getAllShouldManageExtraRequests);
        }
        for (val sortField : setup.getSortFields()) {
            for (val direction : SortDirection.values()) {
                executions.add(setup, sortField.with(direction), FrameworkIT::getAllShouldManageSingleSortFields);
            }
        }

        if (Boolean.getBoolean(PROPERTY_FRAMEWORK_ALL_TEST_CASES)) {
            for (val sortField1 : setup.getSortFields()) {
                for (val sortField2 : setup.getSortFields()) {
                    if (sortField1 != sortField2) {
                        for (val direction1 : SortDirection.values()) {
                            for (val direction2 : SortDirection.values()) {
                                executions.add(setup, sortField1.with(direction1), sortField2.with(direction2),
                                        FrameworkIT::getAllShouldManageExtraMixedSortFields);
                            }
                        }
                    }
                }
            }
        }
    }

    private static void prepareGetOneExecutions(ExecutionList executions, GetOneSetup setup) {
        executions.add(setup, FrameworkIT::getOneShouldManageProvidedHeadersAndParameters);
        if (setup.isRepeatable()) {
            executions.add(setup, FrameworkIT::getOneShouldRepeatedManageProvidedHeadersAndParameters);
        }

        if (setup.isAuthenticationRequired()) {
            executions.add(setup, FrameworkIT::getOneShouldReturn401WhenAnonymous);
        }
        if (setup.hasAuthorities() && setup.getAuthKeyProvider() == null) {
            executions.add(setup, FrameworkIT::getOneShouldReturn403WhenNoPermission);
            executions.add(setup, FrameworkIT::getOneShouldReturn403WhenInvalidPermissions);
        }

        for (VerifiablePathVariables entry : setup.getIllegalPathVariables()) {
            executions.add(setup, entry, FrameworkIT::getOneShouldReturn400WhenIllegalPathVariables);
        }

        for (VerifiablePathVariables entry : setup.getInvalidPathVariables()) {
            executions.add(setup, entry, FrameworkIT::getOneShouldReturn404WhenInvalidPathVariables);
        }

        if (setup.hasProjectionResultMatchers()) {
            executions.add(setup, FrameworkIT::getOneShouldReturn400WhenProjectionInvalid);
        }

        for (val projection : setup.getForbiddenProjectionResultMatchers()) {
            executions.add(setup, projection, FrameworkIT::getOneShouldReturn403WhenForbiddenProjection);
        }

        for (val projection : setup.getProjectionResultMatchers()) {
            executions.add(setup, projection, FrameworkIT::getOneShouldManageProjections);
        }

        for (val req : setup.getExtraRequests()) {
            executions.add(setup, req, FrameworkIT::getOneShouldManageExtraRequests);
        }

        for (val parameter : setup.getRequiredParameters()) {
            executions.add(setup, parameter, FrameworkIT::getOneShouldReturn400WhenMissingRequiredParameters);
        }

        for (val parameter : setup.getBadParameters()) {
            executions.add(setup, parameter, FrameworkIT::getOneShouldReturn400WhenBadParameters);
        }

        for (val header : setup.getRequiredHeaders()) {
            executions.add(setup, header, FrameworkIT::getOneShouldReturn400WhenMissingRequiredHeaders);
        }

        for (val header : setup.getBadHeaders()) {
            executions.add(setup, header, FrameworkIT::getOneShouldReturn400WhenBadHeaders);
        }

    }

    private static void preparePostExecutions(ExecutionList executions, PostSetup setup,
                                              boolean fullTransactionalSupport) {
        executions.add(setup, FrameworkIT::postShouldManageDefaultPayload);

        if (fullTransactionalSupport && setup.getRequestType() == null) {
            executions.add(setup, FrameworkIT::postShouldCreateResource);
        }

        for (val parameter : setup.getRequiredParameters()) {
            executions.add(setup, parameter, FrameworkIT::postShouldReturn400WhenMissingRequiredParameters);
        }

        for (val parameter : setup.getBadParameters()) {
            executions.add(setup, parameter, FrameworkIT::postShouldReturn400WhenBadParameters);
        }

        for (val header : setup.getRequiredHeaders()) {
            executions.add(setup, header, FrameworkIT::postShouldReturn400WhenMissingRequiredHeaders);
        }

        for (val header : setup.getBadHeaders()) {
            executions.add(setup, header, FrameworkIT::postShouldReturn400WhenBadHeaders);
        }

        for (val projection : setup.getForbiddenProjectionResultMatchers()) {
            executions.add(setup, projection, FrameworkIT::postShouldReturn403WhenForbiddenProjection);
        }

        for (val projection : setup.getProjectionResultMatchers()) {
            executions.add(setup, projection, FrameworkIT::postShouldManageProjections);
        }

        for (val req : setup.getExtraRequests()) {
            executions.add(setup, req, FrameworkIT::postShouldManageExtraRequests);
        }

        for (VerifiablePathVariables entry : setup.getIllegalPathVariables()) {
            executions.add(setup, entry, FrameworkIT::postShouldReturn400WhenIllegalPathVariablesAndDefaultPayload);
        }

        for (VerifiablePathVariables entry : setup.getInvalidPathVariables()) {
            executions.add(setup, entry, FrameworkIT::postShouldReturn404WhenInvalidPathVariablesAndDefaultPayload);
        }

        if (setup.isAuthenticationRequired()) {
            executions.add(setup, FrameworkIT::postShouldReturn401WithAnonymous);
        }
        if (setup.hasAuthorities() && setup.getAuthKeyProvider() == null) {
            executions.add(setup, FrameworkIT::postShouldReturn403WhenNoPermissions);
            executions.add(setup, FrameworkIT::postShouldReturn403WhenInvalidPermissions);
        }
        if (setup.hasDefaultPayload()) {
            for (VerifiablePathVariables entry : setup.getIllegalPathVariables()) {
                executions.add(setup, entry, FrameworkIT::postShouldReturn400WhenIllegalPathVariablesAndEmptyPayload);
            }

            for (VerifiablePathVariables entry : setup.getInvalidPathVariables()) {
                executions.add(setup, entry, FrameworkIT::postShouldReturn400WhenInvalidPathVariablesAndEmptyPayload);
            }

            executions.add(setup, FrameworkIT::postShouldReturn400WhenEmptyPayload);
        }

        if (setup.hasPayloadsWithoutRequiredFields()) {
            for (VerifiablePathVariables entry : setup.getIllegalPathVariables()) {
                for (val generatedPayload : setup.getGenericPayload().iterateRequiredFields()) {
                    executions.add(setup, entry, generatedPayload,
                            FrameworkIT::postShouldReturn400WhenIllegalPathVariablesAndPayloadWithoutRequiredFields);
                }
            }

            for (VerifiablePathVariables entry : setup.getInvalidPathVariables()) {
                for (val generatedPayload : setup.getGenericPayload().iterateRequiredFields()) {
                    executions.add(setup, entry, generatedPayload,
                            FrameworkIT::postShouldReturn400WhenInvalidPathVariablesAndPayloadWithoutRequiredFields);
                }
            }

            for (val generatedPayload : setup.getGenericPayload().iterateRequiredFields()) {
                executions.add(setup, generatedPayload,
                        FrameworkIT::postShouldReturn400WhenPayloadWithoutRequiredFields);
            }
        }

        if (setup.hasPayloadsWithRedundantFields()) {
            for (VerifiablePathVariables entry : setup.getIllegalPathVariables()) {
                for (val generatedPayload : setup.getGenericPayload().iterateRedundantFields()) {
                    executions.add(setup, entry, generatedPayload,
                            FrameworkIT::postShouldReturn400WhenIllegalPathVariablesAndPayloadWithRedundantFields);
                }
            }

            for (VerifiablePathVariables entry : setup.getInvalidPathVariables()) {
                for (val generatedPayload : setup.getGenericPayload().iterateRedundantFields()) {
                    executions.add(setup, entry, generatedPayload,
                            FrameworkIT::postShouldReturn400WhenInvalidPathVariablesAndPayloadWithRedundantFields);
                }
            }

            for (val generatedPayload : setup.getGenericPayload().iterateRedundantFields()) {
                executions.add(setup, generatedPayload, FrameworkIT::postShouldReturn400WhenPayloadWithRedundantFields);
            }
        }

        if (setup.hasPayloadsWithOptionalFields()) {
            for (val generatedPayload : setup.getGenericPayload().iterateOptionalFields()) {
                executions.add(setup, generatedPayload, FrameworkIT::postShouldManagePayloadWithOptionalFields);
            }
        }

        for (val payload : setup.getValidPayloads()) {
            executions.add(setup, payload, FrameworkIT::postShouldManageValidPayloads);
        }

        for (val payload : setup.getInvalidPayloads()) {
            executions.add(setup, payload, FrameworkIT::postShouldRejectInvalidPayloads);
        }

        if (setup.hasMinimalPayload()) {
            for (VerifiablePathVariables entry : setup.getIllegalPathVariables()) {
                executions.add(setup, entry, FrameworkIT::postShouldReturn400WhenIllegalPathVariablesAndMinimalPayload);
            }

            for (VerifiablePathVariables entry : setup.getInvalidPathVariables()) {
                executions.add(setup, entry, FrameworkIT::postShouldReturn404WhenInvalidPathVariablesAndMinimalPayload);
            }

            executions.add(setup, FrameworkIT::postShouldManageMinimalPayload);
        }

        if (setup.hasProjectionResultMatchers()) {
            executions.add(setup, FrameworkIT::postShouldReturn400WhenProjectionInvalid);
        }
    }

    private static void preparePatchExecutions(ExecutionList executions, PatchSetup setup,
                                               List<GetOneSetup> getOneSetups,
                                               boolean fullTransactionalSupport) {
        executions.add(setup, FrameworkIT::patchShouldManageDefaultPayload);
        executions.add(setup, FrameworkIT::patchShouldReturn400WhenEmptyPayload);

        for (val parameter : setup.getRequiredParameters()) {
            executions.add(setup, parameter, FrameworkIT::patchShouldReturn400WhenMissingRequiredParameters);
        }

        for (val parameter : setup.getBadParameters()) {
            executions.add(setup, parameter, FrameworkIT::patchShouldReturn400WhenBadParameters);
        }

        for (val header : setup.getRequiredHeaders()) {
            executions.add(setup, header, FrameworkIT::patchShouldReturn400WhenMissingRequiredHeaders);
        }

        for (val header : setup.getBadHeaders()) {
            executions.add(setup, header, FrameworkIT::patchShouldReturn400WhenBadHeaders);
        }

        if (setup.hasMinimalPayload()) {
            executions.add(setup, FrameworkIT::patchShouldManageMinimalPayload);
        }

        if (setup.isAuthenticationRequired()) {
            executions.add(setup, FrameworkIT::patchShouldReturn401WithAnonymous);
        }
        if (setup.hasAuthorities() && setup.getAuthKeyProvider() == null) {
            executions.add(setup, FrameworkIT::patchShouldReturn403WhenNoPermissions);
            executions.add(setup, FrameworkIT::patchShouldReturn403WhenInvalidPermissions);
        }

        for (VerifiablePathVariables entry : setup.getIllegalPathVariables()) {
            executions.add(setup, entry, FrameworkIT::patchShouldReturn400WhenIllegalPathVariablesAndEmptyPayload);
        }

        for (VerifiablePathVariables entry : setup.getInvalidPathVariables()) {
            executions.add(setup, entry, FrameworkIT::patchShouldReturn400WhenInvalidPathVariablesAndEmptyPayload);
        }

        if (setup.hasPayloadsWithoutRequiredFields()) {
            for (VerifiablePathVariables entry : setup.getIllegalPathVariables()) {
                for (val generatedPayload : setup.getGenericPayload().iterateRequiredFields()) {
                    executions.add(setup, entry, generatedPayload,
                            FrameworkIT::patchShouldReturn400WhenIllegalPathVariablesAndPayloadWithoutRequiredFields);
                }
            }

            for (VerifiablePathVariables entry : setup.getInvalidPathVariables()) {
                for (val generatedPayload : setup.getGenericPayload().iterateRequiredFields()) {
                    executions.add(setup, entry, generatedPayload,
                            FrameworkIT::patchShouldReturn400WhenInvalidPathVariablesAndPayloadWithoutRequiredFields);
                }
            }

            for (val generatedPayload : setup.getGenericPayload().iterateRequiredFields()) {
                executions.add(setup, generatedPayload,
                        FrameworkIT::patchShouldReturn400WhenPayloadWithoutRequiredFields);
            }
        }

        if (setup.hasPayloadsWithRedundantFields()) {
            for (VerifiablePathVariables entry : setup.getIllegalPathVariables()) {
                for (val generatedPayload : setup.getGenericPayload().iterateRedundantFields()) {
                    executions.add(setup, entry, generatedPayload,
                            FrameworkIT::patchShouldReturn400WhenIllegalPathVariablesAndPayloadWithRedundantFields);
                }
            }

            for (VerifiablePathVariables entry : setup.getInvalidPathVariables()) {
                for (val generatedPayload : setup.getGenericPayload().iterateRedundantFields()) {
                    executions.add(setup, entry, generatedPayload,
                            FrameworkIT::patchShouldReturn400WhenInvalidPathVariablesAndPayloadWithRedundantFields);
                }
            }

            for (val generatedPayload : setup.getGenericPayload().iterateRedundantFields()) {
                executions.add(setup, generatedPayload,
                        FrameworkIT::patchShouldReturn400WhenPayloadWithRedundantFields);
            }
        }

        if (setup.hasPayloadsWithOptionalFields()) {
            for (val generatedPayload : setup.getGenericPayload().iterateOptionalFields()) {
                executions.add(setup, generatedPayload,
                        FrameworkIT::patchShouldManagePayloadWithOptionalFields);
            }
        }

        for (VerifiablePathVariables entry : setup.getIllegalPathVariables()) {
            executions.add(setup, entry,
                    FrameworkIT::patchShouldReturn400WhenIllegalPathVariablesAndDefaultPayload);
        }

        if (setup.hasMinimalPayload()) {
            for (VerifiablePathVariables entry : setup.getIllegalPathVariables()) {
                executions.add(setup, entry,
                        FrameworkIT::patchShouldReturn400WhenIllegalPathVariablesAndMinimalPayload);
            }

            for (VerifiablePathVariables entry : setup.getInvalidPathVariables()) {
                executions.add(setup, entry,
                        FrameworkIT::patchShouldReturn404WhenInvalidPathVariablesAndMinimalPayload);
            }
        }

        for (VerifiablePathVariables entry : setup.getInvalidPathVariables()) {
            executions.add(setup, entry, FrameworkIT::patchShouldReturn404WhenInvalidPathVariablesAndDefaultPayload);
        }

        if (setup.hasProjectionResultMatchers()) {
            executions.add(setup, FrameworkIT::patchShouldReturn400WhenProjectionInvalid);
            for (val projection : setup.getProjectionResultMatchers()) {
                executions.add(setup, projection, FrameworkIT::patchShouldManageProjections);
            }
        }

        for (val projection : setup.getForbiddenProjectionResultMatchers()) {
            executions.add(setup, projection, FrameworkIT::patchShouldReturn403WhenForbiddenProjection);
        }

        for (val payload : setup.getValidPayloads()) {
            executions.add(setup, payload, FrameworkIT::patchShouldManageValidPayloads);
        }

        for (val payload : setup.getInvalidPayloads()) {
            executions.add(setup, payload, FrameworkIT::patchShouldRejectInvalidPayloads);
        }

        for (val req : setup.getExtraRequests()) {
            executions.add(setup, req, FrameworkIT::patchShouldManageExtraRequests);
        }

        if (fullTransactionalSupport) {
            val patchUrl = buildEndpoint(executions.rootSetup.getEndpoint(), setup);
            for (val getOneSetup : getOneSetups) {
                val getOneUrl = buildEndpoint(executions.rootSetup.getEndpoint(), getOneSetup);
                if (patchUrl.equals(getOneUrl)) {
                    executions.add(setup, getOneSetup,
                            new VerifiableProjection(null, getOneSetup.getDefaultResultMatchers()),
                            FrameworkIT::patchShouldNotSurplusChangeEntry);
                    for (val projection : getOneSetup.getProjectionResultMatchers()) {
                        executions.add(setup, getOneSetup, projection,
                                FrameworkIT::patchShouldNotSurplusChangeEntry);
                    }
                }
            }
        }
    }

    private static void preparePutExecutions(ExecutionList executions, PutSetup setup) {
        executions.add(setup, FrameworkIT::putShouldReturn400WhenEmptyPayload);

        if (setup.isAuthenticationRequired()) {
            executions.add(setup, FrameworkIT::putShouldReturn401WithAnonymous);
        }

        if (setup.hasAuthorities() && setup.getAuthKeyProvider() == null) {
            executions.add(setup, FrameworkIT::putShouldReturn403WhenNoPermissions);
            executions.add(setup, FrameworkIT::putShouldReturn403WhenInvalidPermissions);
        }

        for (val parameter : setup.getRequiredParameters()) {
            executions.add(setup, parameter, FrameworkIT::putShouldReturn400WhenMissingRequiredParameters);
        }

        for (val parameter : setup.getBadParameters()) {
            executions.add(setup, parameter, FrameworkIT::putShouldReturn400WhenBadParameters);
        }

        for (val header : setup.getRequiredHeaders()) {
            executions.add(setup, header, FrameworkIT::putShouldReturn400WhenMissingRequiredHeaders);
        }

        for (val header : setup.getBadHeaders()) {
            executions.add(setup, header, FrameworkIT::putShouldReturn400WhenBadHeaders);
        }

        for (VerifiablePathVariables entry : setup.getIllegalPathVariables()) {
            executions.add(setup, entry, FrameworkIT::putShouldReturn400WhenIllegalPathVariablesAndEmptyPayload);
        }

        for (VerifiablePathVariables entry : setup.getInvalidPathVariables()) {
            executions.add(setup, entry, FrameworkIT::putShouldReturn400WhenInvalidPathVariablesAndEmptyPayload);
        }

        if (setup.hasPayloadsWithoutRequiredFields()) {
            for (VerifiablePathVariables entry : setup.getIllegalPathVariables()) {
                for (val generatedPayload : setup.getGenericPayload().iterateRequiredFields()) {
                    executions.add(setup, entry, generatedPayload,
                            FrameworkIT::putShouldReturn400WhenIllegalPathVariablesAndPayloadWithoutRequiredFields);
                }
            }
            for (VerifiablePathVariables entry : setup.getInvalidPathVariables()) {
                for (val generatedPayload : setup.getGenericPayload().iterateRequiredFields()) {
                    executions.add(setup, entry, generatedPayload,
                            FrameworkIT::putShouldReturn400WhenInvalidPathVariablesAndPayloadWithoutRequiredFields);
                }
            }
            for (val generatedPayload : setup.getGenericPayload().iterateRequiredFields()) {
                executions.add(setup, generatedPayload,
                        FrameworkIT::putShouldReturn400WhenPayloadWithoutRequiredFields);
            }
        }

        if (setup.hasPayloadsWithRedundantFields()) {
            for (VerifiablePathVariables entry : setup.getIllegalPathVariables()) {
                for (val generatedPayload : setup.getGenericPayload().iterateRedundantFields()) {
                    executions.add(setup, entry, generatedPayload,
                            FrameworkIT::putShouldReturn400WhenIllegalPathVariablesAndPayloadWithRedundantFields);
                }
            }
            for (VerifiablePathVariables entry : setup.getInvalidPathVariables()) {
                for (val generatedPayload : setup.getGenericPayload().iterateRedundantFields()) {
                    executions.add(setup, entry, generatedPayload,
                            FrameworkIT::putShouldReturn400WhenInvalidPathVariablesAndPayloadWithRedundantFields);
                }
            }
            for (val generatedPayload : setup.getGenericPayload().iterateRedundantFields()) {
                executions.add(setup, generatedPayload, FrameworkIT::putShouldReturn400WhenPayloadWithRedundantFields);
            }
        }

        for (VerifiablePathVariables entry : setup.getIllegalPathVariables()) {
            executions.add(setup, entry, FrameworkIT::putShouldReturn400WhenIllegalPathVariablesAndDefaultPayload);
        }

        if (setup.hasMinimalPayload()) {
            for (VerifiablePathVariables entry : setup.getIllegalPathVariables()) {
                executions.add(setup, entry, FrameworkIT::putShouldReturn400WhenIllegalPathVariablesAndMinimalPayload);
            }

            for (VerifiablePathVariables entry : setup.getInvalidPathVariables()) {
                executions.add(setup, entry, FrameworkIT::putShouldReturn404WhenInvalidPathVariablesAndMinimalPayload);
            }

            executions.add(setup, FrameworkIT::putShouldManageMinimalPayload);
        }

        for (VerifiablePathVariables entry : setup.getInvalidPathVariables()) {
            executions.add(setup, entry, FrameworkIT::putShouldReturn404WhenInvalidPathVariablesAndDefaultPayload);
        }

        if (setup.hasProjectionResultMatchers()) {
            for (val projection : setup.getProjectionResultMatchers()) {
                executions.add(setup, projection,
                        FrameworkIT::putShouldManageProjections);
            }
            executions.add(setup, FrameworkIT::putShouldReturn400WhenProjectionInvalid);
        }

        for (val projection : setup.getForbiddenProjectionResultMatchers()) {
            executions.add(setup, projection, FrameworkIT::putShouldReturn403WhenForbiddenProjection);
        }

        executions.add(setup, FrameworkIT::putShouldManageDefaultPayload);

        if (setup.hasPayloadsWithOptionalFields()) {
            for (val generatedPayload : setup.getGenericPayload().iterateOptionalFields()) {
                executions.add(setup, generatedPayload, FrameworkIT::putShouldManagePayloadWithOptionalFields);
            }
        }

        for (val payload : setup.getValidPayloads()) {
            executions.add(setup, payload, FrameworkIT::putShouldManageValidPayloads);
        }
        for (val payload : setup.getInvalidPayloads()) {
            executions.add(setup, payload, FrameworkIT::putShouldRejectInvalidPayloads);
        }
        for (val req : setup.getExtraRequests()) {
            executions.add(setup, req, FrameworkIT::putShouldManageExtraRequests);
        }
    }

    private static void prepareDeleteExecutions(ExecutionList executions, DeleteSetup setup,
                                                boolean fullTransactionalSupport) {
        if (setup.isAuthenticationRequired()) {
            executions.add(setup, FrameworkIT::deleteShouldReturn401WithAnonymous);
        }
        if (setup.hasAuthorities() && setup.getAuthKeyProvider() == null) {
            executions.add(setup, FrameworkIT::deleteShouldReturn403WhenNoPermissions);
            executions.add(setup, FrameworkIT::deleteShouldReturn403WhenInvalidPermissions);
        }
        executions.add(setup, FrameworkIT::deleteShouldReturn204WhenValidId);
        if (fullTransactionalSupport) {
            if (setup.isHandle204onMissingEntity()) {
                executions.add(setup, FrameworkIT::deleteShouldReturn204WhenRepeated);
            } else {
                executions.add(setup, FrameworkIT::deleteShouldReturn404WhenRepeated);
            }
        }
        for (DeleteRequest request : setup.getExtraRequests()) {
            executions.add(setup, request, FrameworkIT::deleteShouldManageExtraRequests);
        }

        for (VerifiablePathVariables entry : setup.getIllegalPathVariables()) {
            executions.add(setup, entry, FrameworkIT::deleteShouldReturn400WhenIllegalPathVariables);
        }

        for (VerifiablePathVariables entry : setup.getInvalidPathVariables()) {
            executions.add(setup, entry, FrameworkIT::deleteShouldReturn404WhenInvalidPathVariables);
        }
    }

    @Test
    public void controllerTestClassShouldHaveCorrectName() {
        if (rest instanceof MockMvcRest) {
            assertThat("Test class name should not have EmbeddedWebIT suffix",
                    getClass().getSimpleName(), not(endsWith("EmbeddedWebIT")));
        } else {
            assert rest instanceof TestRestTemplateRest;
            assertThat("Test class name should have EmbeddedWebIT suffix",
                    getClass().getSimpleName(), endsWith("EmbeddedWebIT"));
        }
    }

//    @Test
//    public void controllerClassShouldHaveSwaggerAnnotations() {
//        if (isSwaggerSupported()) {
//            val api = rootSetup.getControllerClass().getAnnotation(Api.class);
//            assertNotNull(rootSetup.getControllerClass() + " should have @Api annotation", api);
//        } else {
//            throw new AssumptionViolatedException("Swagger not supported");
//        }
//    }

    @Test
    public void mappedMethodsShouldBePublicAndHaveCorrectAnnotations() {
        for (val method : rootSetup.getControllerClass().getDeclaredMethods()) {
            val mapping = TestRequestMappingUtils.getMapping(method);
            if (mapping != null) {
                validateMappedMethod(method);
            } else {
                validateNonMappedMethod(method);
            }
        }
    }

    protected void validateMappedMethod(Method method) {
        assertThat("Mapped controller method " + method + " should be public",
                Modifier.isPublic(method.getModifiers()), equalTo(true));

//        if (isSwaggerSupported()) {
//            val apiOperation = method.getAnnotation(ApiOperation.class);
//            assertThat("Mapped controller method " + method + " should have @ApiOperation annotation",
//                    apiOperation, notNullValue());
//        }

        Parameter[] parameters = method.getParameters();
        for (int parameterIndex = 0; parameterIndex < parameters.length; parameterIndex++) {
            Parameter parameter = parameters[parameterIndex];

            val requestHeader = TestRequestMappingUtils.getAnnotation(parameter, RequestHeader.class);
            if (requestHeader != null) {
                // No soft assertion here. We want immediate feedback.
                assertThat("@RequestHeader of " + parameter + " of " + method + " should declare either name() "
                        + "or value()", requestHeader.value(), not(isEmptyString()));
            }

            val requestParam = TestRequestMappingUtils.getAnnotation(parameter, RequestParam.class);
            if (requestParam != null) {
                assertThat("@RequestParam of " + parameter + " of " + method + " should declare either name() "
                        + "or value()", requestParam.value(), not(isEmptyString()));
            }

            val pathVariable = TestRequestMappingUtils.getAnnotation(parameter, PathVariable.class);
            if (pathVariable != null) {
                assertThat("@PathVariable of " + parameter + " of " + method + " should declare either name() "
                        + "or value()", pathVariable.value(), not(isEmptyString()));
            }

            val sortParam = TestRequestMappingUtils.getAnnotation(parameter, SortParam.class);
            if (sortParam != null) {
                for (val defaultField : sortParam.defaultSort()) {
                    assertThat("@SortParam.defaultSort() of " + parameter + " of " + method
                                    + " field should be present in @SortParam.value()",
                            sortParam.value(), hasItemInArray(equalTo(defaultField.value())));
                }
            }

            val accept = parameter.getAnnotation(Accept.class);
            if (accept != null) {
                Class<?> elementClass = TypeUtils.extractElementClass(new MethodParameter(method, parameterIndex));
                if (Enum.class.isAssignableFrom(elementClass)) {
                    // We want immediate feedback. No soft assertions here.
                    assertThat("@Accept should not contain illegal values",
                            extractIllegalValues(accept, elementClass.asSubclass(Enum.class)), empty());
                }
            }
        }
    }

    protected void validateNonMappedMethod(Method method) {
        assertThat("Non-mapped controller method " + method + " should not be public",
                Modifier.isPublic(method.getModifiers()) && !method.isSynthetic(), equalTo(false));

//        if (isSwaggerSupported()) {
//            val apiOperation = method.getAnnotation(ApiOperation.class);
//            assertThat("Non-mapped controller method " + method + " should not have @ApiOperation annotation",
//                    apiOperation, nullValue());
//        }
    }

    @Override
    protected String endpoint() {
        return rootSetup.getEndpoint();
    }

    protected static GenericArrayPayload genericArray(Class<?> resourceClass) {
        return GenericPayloads.array(resourceClass);
    }

    protected static GenericArrayPayloadElement genericObject() {
        return GenericPayloads.object();
    }

    protected static GenericSinglePayload genericObject(Class<?> resourceClass) {
        return GenericPayloads.object(resourceClass);
    }

    private static URI buildEndpoint(String endpoint, BaseSetup<?, ?> setup) {
        return WebTestUtils.buildEndpoint(endpoint + setup.getPathTemplate(), setup.getPathVariables(false));
    }

    private static void verifyRootSetup(TestSetup rootSetup) {
        eachSetup(rootSetup.getGetAllSetups(), setup -> {
            verifyNoMissingProjections(setup.extractMissingProjections());
            if (setup.hasDefaultSortFields() || setup.hasSortFields()) {
                if (setup.getTotalElements() < 2) {
                    throw new IllegalArgumentException("Total elements " + setup.getTotalElements() + " is not "
                            + "enough when there is defaultOrder or extra orders");
                }
            }
        });

        eachSetup(rootSetup.getGetOneSetups(), setup -> {
            verifyNoMissingProjections(setup.extractMissingProjections());
        });

        eachSetup(rootSetup.getPostSetups(), setup -> {
            verifyNoMissingProjections(setup.extractMissingProjections());
            verifyRequestType(setup.getRequestType(), setup.getDefaultResultMatchers());
            verifyRequestType(setup.getRequestType(), setup.getProjectionResultMatchers());
            verifyRequestType(setup.getRequestType(), setup.getForbiddenProjectionResultMatchers());
            if (setup.getLocationHeaderAntPattern() != null) {
                Assert.isNull(setup.getRequestType(), "Custom location header can be set only for `null` requestType");
            }
            // Default payload is not mandatory for Post
        });

        eachSetup(rootSetup.getPatchSetups(), setup -> {
            verifyNoMissingProjections(setup.extractMissingProjections());
            if (!setup.hasDefaultPayload()) {
                throw new IllegalArgumentException(String.format("No default payload for Patch: [%s]", setup));
            }
        });

        eachSetup(rootSetup.getPutSetups(), setup -> {
            verifyNoMissingProjections(setup.extractMissingProjections());
            verifyRequestType(setup.getRequestType(), setup.getDefaultResultMatchers());
            verifyRequestType(setup.getRequestType(), setup.getProjectionResultMatchers());
            verifyRequestType(setup.getRequestType(), setup.getForbiddenProjectionResultMatchers());
            if (!setup.hasDefaultPayload()) {
                throw new IllegalArgumentException(String.format("No default payload for Put: [%s]", setup));
            }
        });
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void finish(BaseSetup<?, ?> setup, boolean aroundRequestActionsEnabled) throws Exception {
        flush();

        if (aroundRequestActionsEnabled && setup instanceof AroundRequestActionSupportDelegate) {
            AroundRequestAction postRequestAction = ((AroundRequestActionSupportDelegate<?>) setup)
                    .getPostRequestAction();

            if (postRequestAction != null) {
                postRequestAction.perform(this);
            }
        }
    }

    private static void verifyNoMissingProjections(Collection<Enum<?>> projections) {
        if (!projections.isEmpty()) {
            String projectionNames = projections.stream().map(Enum::name).collect(Collectors.joining(", "));
            throw new IllegalArgumentException(String.format("There are missing projections: %s", projectionNames));
        }
    }

    private static void verifyRequestType(RequestType requestType, Collection<ResultMatcher> resultMatchers) {
        if (requestType == RequestType.NO_RETRIEVAL && CollectionUtils.isNotEmpty(resultMatchers)) {
            throw new IllegalArgumentException(String.format("Requests with requestType %s should not have any result matchers.", RequestType.NO_RETRIEVAL));
        }
    }

    private static void verifyRequestType(RequestType requestType, List<VerifiableProjection> projectionResultMatchers) {
        if (requestType == RequestType.NO_RETRIEVAL && CollectionUtils.isNotEmpty(projectionResultMatchers)) {
            throw new IllegalArgumentException(String.format("Requests with requestType %s should not have any projection result matchers.", RequestType.NO_RETRIEVAL));
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void prepareForSetup(BaseSetup<?, ?> setup, boolean aroundRequestActionsEnabled) throws Exception {
        Description description = TestContext.getCurrentTest().getDescription();
        log.info("Test: {}#{}.", description.getClassName(), description.getMethodName());
        log.info("Setup: {}", setup);

        assert TestTransaction.isActive();

        if (aroundRequestActionsEnabled && setup instanceof AroundRequestActionSupportDelegate) {
            AroundRequestAction preRequestAction = ((AroundRequestActionSupportDelegate<?>) setup)
                    .getPreRequestAction();

            if (preRequestAction != null) {
                preRequestAction.perform(this);
            }
        }
    }

    private static List<SearchOperator> getUnsupportedSearchOperators() {
        List<SearchOperator> operators = Lists.newArrayList(SearchOperator.values());
        operators.removeAll(FrameworkConfigHolder.getFrameworkConfig().getSupportedSearchOperators());
        return operators;
    }

    private static List<SearchValue> getUnsupportedSpecialSearchValues() {
        List<SearchValue> values = Lists.newArrayList(SearchValue.values());
        values.removeAll(FrameworkConfigHolder.getFrameworkConfig().getSupportedSpecialSearchValues());
        return values;
    }

//    private static boolean isSwaggerSupported() {
//        try {
//            Class.forName("io.swagger.annotations.Api");
//            return true;
//        } catch (ClassNotFoundException e) {
//            return false;
//        }
//    }

}
