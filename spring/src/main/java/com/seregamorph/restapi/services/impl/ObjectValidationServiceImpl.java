package com.seregamorph.restapi.services.impl;

import com.seregamorph.restapi.services.ObjectValidationService;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class ObjectValidationServiceImpl implements ObjectValidationService {

    private final Validator validator;

    @Override
    public void validate(Object object) {
        Set<ConstraintViolation<Object>> violations = validator.validate(object);

        if (!CollectionUtils.isEmpty(violations)) {
            throw new ConstraintViolationException(violations);
        }
    }
}
