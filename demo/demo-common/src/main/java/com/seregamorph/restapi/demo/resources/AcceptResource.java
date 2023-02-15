package com.seregamorph.restapi.demo.resources;

import static com.seregamorph.restapi.demo.resources.AcceptConstants.FIRST;
import static com.seregamorph.restapi.demo.resources.AcceptConstants.FOURTH;
import static com.seregamorph.restapi.demo.resources.AcceptConstants.SECOND;

import com.seregamorph.restapi.base.BaseResource;
import com.seregamorph.restapi.validators.Accept;
import java.util.List;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants
public class AcceptResource implements BaseResource {

    @Accept({FIRST, SECOND, FOURTH})
    private String singleString;

    @Accept({FIRST, SECOND, FOURTH})
    private String[] stringArray;

    @Accept({FIRST, SECOND, FOURTH})
    private List<String> stringList;

    @Accept({FIRST, SECOND, FOURTH})
    private AcceptEnum singleEnum;

    @Accept({FIRST, SECOND, FOURTH})
    private AcceptEnum[] enumArray;

    @Accept({FIRST, SECOND, FOURTH})
    private List<AcceptEnum> enumList;
}
