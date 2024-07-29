package com.seregamorph.restapi.common;

import lombok.experimental.UtilityClass;

@UtilityClass
@SuppressWarnings({"unused", "WeakerAccess"})
public class Constants {

    // Constants here are defined in the form of <type>_<name>, e.g. PATTERN_EMAIL

    public static final String PATTERN_ISO_LOCAL_DATE = "yyyy-MM-dd";

    public static final String MAPPER_QUALIFIER = "delegate";

    public static final String PARAM_PAGE = "page";
    public static final String PARAM_SIZE = "size";
    public static final String PARAM_SORT = "sort";
    public static final String PARAM_ID = "id";
    public static final String PARAM_SEARCH = "search";

    public static final String API_PARAM_PAGE = "Page number";
    public static final String API_PARAM_SIZE = "Page size";
    public static final String API_PARAM_SORT = "Sort";
    public static final String API_PARAM_ID = "ID";
    public static final String API_PARAM_SEARCH = "Search";

    public static final String DEFAULT_PROJECTION = "DEFAULT";

    public static final String PARAM_PROJECTION = "projection";
    public static final String HEADER_ACCEPT_PROJECTION = "Accept-Projection";

    /**
     * Constant for annotations (we can't just use {@link Boolean#toString()}.
     */
    public static final String TRUE = "true";
    /**
     * Constant for annotations (we can't just use {@link Boolean#toString()}).
     */
    public static final String FALSE = "false";

    public static final String RESPONSE_CONTAINER_LIST = "List";

    public static final String ENDPOINT_ID = "/{" + PARAM_ID + "}";
    public static final String ENDPOINT_SEARCH = "/search";
    public static final String ENDPOINT_BULK_CREATE = "/bulk-create";
    public static final String ENDPOINT_BULK_UPDATE = "/bulk-update";
    public static final String ENDPOINT_BULK_REPLACE = "/bulk-replace";
    public static final String ENDPOINT_BULK_CREATE_OR_UPDATE = "/bulk-create-or-update";
    public static final String ENDPOINT_BULK_DELETE = "/bulk-delete";
}
