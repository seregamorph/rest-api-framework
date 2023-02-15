package com.seregamorph.restapi.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiDescription {

    private String generalInfo = "general-info.md";
    private String errorInfo = "error-info.md";
    private String conventionInfo = "convention-info.md";
    private String projectionInfo = "projection-info.md";
    private String collectionInfo = "collection-info.md";
    private String paginationInfo = "pagination-info.md";
    private String searchInfo = "search-info.md";
    private String sortInfo = "sort-info.md";
    private String authInfo = "auth-info.md";
}
