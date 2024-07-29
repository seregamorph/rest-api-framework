package com.seregamorph.restapi.validators;

import static com.seregamorph.restapi.validators.TestValidatorConstants.ERROR_MESSAGE_TEMPLATE;
import static com.seregamorph.restapi.validators.TestValidatorConstants.SAMPLE_MESSAGE;
import static com.seregamorph.restapi.validators.TestValidatorConstants.SAMPLE_STRING;
import static com.seregamorph.restapi.validators.TestValidatorConstants.SAMPLE_STRING_2;
import static com.seregamorph.restapi.validators.TestValidatorConstants.SAMPLE_STRING_3;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import com.seregamorph.restapi.test.common.AbstractUnitTest;
import javax.validation.ConstraintValidatorContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AcceptStringValueValidatorTest extends AbstractUnitTest {

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @Mock
    private Accept annotation;

    @Captor
    private ArgumentCaptor<String> messageCaptor;

    @Spy
    private AcceptStringValueValidator validator;

    @Test
    public void isValidShouldReturnFalseWithCustomMessage() {
        when(annotation.value()).thenReturn(new String[] {SAMPLE_STRING, SAMPLE_STRING_2});
        when(annotation.message()).thenReturn(SAMPLE_MESSAGE);
        when(context.buildConstraintViolationWithTemplate(messageCaptor.capture())).thenReturn(builder);

        validator.initialize(annotation);
        boolean result = validator.isValid(SAMPLE_STRING_3, context);

        collector.checkThat(result, is(false));
        collector.checkThat(messageCaptor.getValue(), is(SAMPLE_MESSAGE));
    }

    @Test
    public void isValidShouldReturnFalseWithDefaultMessage() {
        when(annotation.value()).thenReturn(new String[] {SAMPLE_STRING, SAMPLE_STRING_2});
        when(annotation.message()).thenReturn("");
        when(context.buildConstraintViolationWithTemplate(messageCaptor.capture())).thenReturn(builder);

        validator.initialize(annotation);
        boolean result = validator.isValid(SAMPLE_STRING_3, context);

        collector.checkThat(result, is(false));
        collector.checkThat(messageCaptor.getValue(), is(String.format(ERROR_MESSAGE_TEMPLATE,
                SAMPLE_STRING_3, SAMPLE_STRING, SAMPLE_STRING_2)));
    }

    @Test
    public void isValidShouldReturnTrueWhenValidValue() {
        when(annotation.value()).thenReturn(new String[] {SAMPLE_STRING, SAMPLE_STRING_2});

        validator.initialize(annotation);
        boolean result = validator.isValid(SAMPLE_STRING, context);

        collector.checkThat(result, is(true));
    }

    @Test
    public void isValidShouldReturnTrueForNullObject() {
        boolean result = validator.isValid(null, context);

        collector.checkThat(result, is(true));
    }
}
