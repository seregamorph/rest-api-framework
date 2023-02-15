package com.seregamorph.restapi.services.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.seregamorph.restapi.test.common.AbstractUnitTest;
import java.util.Collections;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ObjectValidationServiceImplTest extends AbstractUnitTest {

    @Mock
    private Validator validator;

    @InjectMocks
    private ObjectValidationServiceImpl service;

    @Test
    @SuppressWarnings("unchecked")
    public void validateShouldThrowConstraintViolationException() {
        expectedException.expect(ConstraintViolationException.class);

        Object object = new Object();
        when(validator.validate(object)).thenReturn(Collections.singleton(mock(ConstraintViolation.class)));

        service.validate(object);
    }

    @Test
    public void validateShouldNotThrowConstraintViolationException() {
        service.validate(new Object());
    }
}
