package com.seregamorph.restapi.demo.services;

import com.seregamorph.restapi.demo.resources.NestedSearchResource;
import com.seregamorph.restapi.demo.resources.SearchResource;
import com.seregamorph.restapi.search.SearchCondition;
import com.seregamorph.restapi.search.SearchConditionGroup;
import com.seregamorph.restapi.search.SingleSearchCondition;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

    public void updateMappedField(List<SearchCondition> searchConditions) {
        for (SearchCondition searchCondition : searchConditions) {
            if (searchCondition instanceof SingleSearchCondition) {
                SingleSearchCondition singleSearchCondition = (SingleSearchCondition) searchCondition;
                if (SearchResource.Fields.NESTED_SEARCH_FIELD.equals(singleSearchCondition.getField())) {
                    singleSearchCondition.setField(
                            SearchResource.Fields.NESTED_SEARCH_FIELD + "." + NestedSearchResource.Fields.NESTED_STRING_FIELD);
                }
            } else {
                updateMappedField(((SearchConditionGroup) searchCondition).getSearchConditions());
            }
        }
    }
}
