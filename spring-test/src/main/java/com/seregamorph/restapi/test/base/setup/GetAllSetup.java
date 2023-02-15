package com.seregamorph.restapi.test.base.setup;

import static com.seregamorph.restapi.test.base.ResultType.LIST;
import static com.seregamorph.restapi.test.base.ResultType.PAGE;
import static org.springframework.http.HttpMethod.GET;

import com.seregamorph.restapi.test.JsonConstants;
import com.seregamorph.restapi.test.base.ResultType;
import com.seregamorph.restapi.test.base.request.GetAllRequest;
import com.seregamorph.restapi.test.base.setup.common.VerifiableSortField;
import com.seregamorph.restapi.test.base.setup.common.VerifiableSortFieldDirection;
import com.seregamorph.restapi.test.base.support.AroundRequestActionSupport;
import com.seregamorph.restapi.test.base.support.AroundRequestActionSupportDelegate;
import com.seregamorph.restapi.test.base.support.FieldMappingSupport;
import com.seregamorph.restapi.test.base.support.FieldMappingSupportDelegate;
import com.seregamorph.restapi.test.base.support.HeaderSupport;
import com.seregamorph.restapi.test.base.support.HeaderSupportDelegate;
import com.seregamorph.restapi.test.base.support.PaginationSupport;
import com.seregamorph.restapi.test.base.support.PaginationSupportDelegate;
import com.seregamorph.restapi.test.base.support.ParameterSupport;
import com.seregamorph.restapi.test.base.support.ParameterSupportDelegate;
import com.seregamorph.restapi.test.base.support.ProjectionSupport;
import com.seregamorph.restapi.test.base.support.ProjectionSupportDelegate;
import com.seregamorph.restapi.test.base.support.RepeatedCheckSupport;
import com.seregamorph.restapi.test.base.support.RepeatedCheckSupportDelegate;
import com.seregamorph.restapi.test.base.support.SearchSupport;
import com.seregamorph.restapi.test.base.support.SearchSupportDelegate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import lombok.Getter;
import lombok.Setter;

@Getter
public class GetAllSetup extends BaseSetup<GetAllSetup, GetAllRequest> implements
        ProjectionSupportDelegate<GetAllSetup>,
        ParameterSupportDelegate<GetAllSetup>,
        HeaderSupportDelegate<GetAllSetup>,
        PaginationSupportDelegate<GetAllSetup>,
        FieldMappingSupportDelegate<GetAllSetup>,
        SearchSupportDelegate<GetAllSetup>,
        RepeatedCheckSupportDelegate<GetAllSetup>,
        AroundRequestActionSupportDelegate<GetAllSetup> {

    private final ProjectionSupport<GetAllSetup> projectionSupport;
    private final ParameterSupport<GetAllSetup> parameterSupport;
    private final HeaderSupport<GetAllSetup> headerSupport;
    private final PaginationSupport<GetAllSetup> paginationSupport;
    private final FieldMappingSupport<GetAllSetup> fieldMappingSupport;
    private final SearchSupport<GetAllSetup> searchSupport;
    private final RepeatedCheckSupport<GetAllSetup> repeatedCheckSupport;
    private final AroundRequestActionSupport<GetAllSetup> aroundRequestActionSupport;

    private final List<VerifiableSortField> sortFields = new ArrayList<>();

    private List<VerifiableSortFieldDirection> defaultSortFields;

    @Getter
    @Setter
    private boolean undeclaredSortFieldsForbidden = true;

    /**
     * Custom root element json path. Can be used for ResourceSupport-based collection responses.
     */
    @Setter
    private String rootElement;

    public GetAllSetup() {
        this("");
    }

    public GetAllSetup(@Nonnull String pathTemplate, Object... pathVariables) {
        super(GET, pathTemplate, pathVariables);
        projectionSupport = new ProjectionSupport<>(this);
        parameterSupport = new ParameterSupport<>(this);
        headerSupport = new HeaderSupport<>(this);
        paginationSupport = new PaginationSupport<>(this);
        fieldMappingSupport = new FieldMappingSupport<>(this);
        searchSupport = new SearchSupport<>(this);
        repeatedCheckSupport = new RepeatedCheckSupport<>(this);
        aroundRequestActionSupport = new AroundRequestActionSupport<>(this);
    }

    @Override
    ResultType getDefaultResultType() {
        return isPaginationSupported() ? PAGE : LIST;
    }

    public GetAllSetup setDefaultSortFields(String... fieldNames) {
        defaultSortFields = Stream.of(fieldNames)
                .map(VerifiableSortFieldDirection::new)
                .collect(Collectors.toList());
        return this;
    }

    public GetAllSetup setDefaultSortFields(VerifiableSortFieldDirection... sortFields) {
        defaultSortFields = Arrays.asList(sortFields);
        return this;
    }

    public GetAllSetup supportSortField(String fieldName) {
        sortFields.add(new VerifiableSortField(fieldName));
        return this;
    }

    public GetAllSetup supportSortFields(String... fieldNames) {
        for (String fieldName : fieldNames) {
            supportSortField(fieldName);
        }
        return this;
    }

    public GetAllSetup supportSortField(VerifiableSortField sortField) {
        sortFields.add(sortField);
        return this;
    }

    public boolean hasDefaultSortFields() {
        return defaultSortFields != null;
    }

    public boolean hasSortFields() {
        return !sortFields.isEmpty();
    }

    public String getRootElement() {
        if (rootElement != null) {
            return rootElement;
        }
        return isPaginationSupported() ? JsonConstants.ROOT_CONTENT : JsonConstants.ROOT;
    }
}
