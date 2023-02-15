package com.seregamorph.restapi.utils;

import com.seregamorph.restapi.config.spi.FrameworkConfigHolder;
import com.seregamorph.restapi.search.SearchOperator;
import com.seregamorph.restapi.search.SearchValue;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@UtilityClass
public class ApiDescriptionUtils {

    private static final ApiDescription STANDARD_DESCRIPTION = new ApiDescription();

    public static String standardDescription() {
        return generalInfo()
                + errorInfo()
                + conventionInfo()
                + projectionInfo()
                + collectionInfo()
                + paginationInfo()
                + searchInfo()
                + sortInfo()
                + authInfo();
    }

    public static String description(ApiDescription description) {
        return generalInfo(description.getGeneralInfo())
                + errorInfo(description.getErrorInfo())
                + conventionInfo(description.getConventionInfo())
                + projectionInfo(description.getProjectionInfo())
                + collectionInfo(description.getCollectionInfo())
                + paginationInfo(description.getPaginationInfo())
                + searchInfo(description.getSearchInfo())
                + sortInfo(description.getSortInfo())
                + authInfo(description.getAuthInfo());
    }

    public static String generalInfo() {
        return generalInfo(STANDARD_DESCRIPTION.getGeneralInfo());
    }

    public static String generalInfo(String resource) {
        return ClasspathResources.readString(resource);
    }

    public static String errorInfo() {
        return errorInfo(STANDARD_DESCRIPTION.getErrorInfo());
    }

    public static String errorInfo(String resource) {
        return ClasspathResources.readString(resource);
    }

    public static String conventionInfo() {
        return conventionInfo(STANDARD_DESCRIPTION.getConventionInfo());
    }

    public static String conventionInfo(String resource) {
        return ClasspathResources.readString(resource);
    }

    public static String projectionInfo() {
        return projectionInfo(STANDARD_DESCRIPTION.getProjectionInfo());
    }

    public static String projectionInfo(String resource) {
        if (!FrameworkConfigHolder.getFrameworkConfig().isProjectionSupported()) {
            return "";
        }

        return ClasspathResources.readString(resource);
    }

    public static String collectionInfo() {
        return collectionInfo(STANDARD_DESCRIPTION.getCollectionInfo());
    }

    public static String collectionInfo(String resource) {
        return ClasspathResources.readString(resource);
    }

    public static String paginationInfo() {
        return paginationInfo(STANDARD_DESCRIPTION.getPaginationInfo());
    }

    public static String paginationInfo(String resource) {
        int defaultPageSize = FrameworkConfigHolder.getFrameworkConfig().getDefaultPageSize();
        return ClasspathResources.readString(resource)
                .replace("{{default_page_size}}", String.valueOf(defaultPageSize));
    }

    public static String searchInfo() {
        return searchInfo(STANDARD_DESCRIPTION.getSearchInfo());
    }

    public static String searchInfo(String resource) {
        String additionalInfo = buildAdditionalInfoForSearchApis();

        if (StringUtils.isEmpty(additionalInfo)) {
            return "";
        }

        return ClasspathResources.readString(resource)
                .replace("{{additional_search_info}}", additionalInfo);
    }

    public static String sortInfo() {
        return sortInfo(STANDARD_DESCRIPTION.getSortInfo());
    }

    public static String sortInfo(String resource) {
        if (!FrameworkConfigHolder.getFrameworkConfig().isSortSupported()) {
            return "";
        }

        return ClasspathResources.readString(resource);
    }

    public static String authInfo() {
        return authInfo(STANDARD_DESCRIPTION.getAuthInfo());
    }

    public static String authInfo(String resource) {
        return ClasspathResources.readString(resource);
    }

    private static String buildAdditionalInfoForSearchApis() {
        List<SearchOperator> searchOperators = FrameworkConfigHolder.getFrameworkConfig().getSupportedSearchOperators();
        List<SearchValue> searchValues = FrameworkConfigHolder.getFrameworkConfig().getSupportedSpecialSearchValues();

        List<String> operatorLines = buildSearchOperatorLines(searchOperators);

        if (operatorLines.isEmpty()) {
            return "";
        }

        List<String> specialValueLines = buildSpecialSearchValueLines(searchValues);

        StringBuilder additionalInfo = new StringBuilder();
        additionalInfo.append("**Supported search operators:**\n");

        for (String line : operatorLines) {
            additionalInfo.append(line).append("\n");
        }

        if (!specialValueLines.isEmpty()) {
            if (additionalInfo.length() > 0) {
                additionalInfo.append("\n\n");
            }

            additionalInfo.append("**Supported special search values:**\n");

            for (String line : specialValueLines) {
                additionalInfo.append(line).append("\n");
            }
        }

        return additionalInfo.toString();
    }

    private static List<String> buildSearchOperatorLines(List<SearchOperator> searchOperators) {
        List<String> lines = new ArrayList<>();

        if (CollectionUtils.isEmpty(searchOperators)) {
            return lines;
        }

        for (SearchOperator operator : searchOperators) {
            lines.add("- `" + operator.getOperator() + "`: " + operator.getDesc());
        }

        return lines;
    }

    private static List<String> buildSpecialSearchValueLines(List<SearchValue> searchValues) {
        List<String> lines = new ArrayList<>();

        if (CollectionUtils.isEmpty(searchValues)) {
            return lines;
        }

        for (SearchValue value : searchValues) {
            lines.add("- `" + value.name().toLowerCase() + "`");
        }

        return lines;
    }
}
