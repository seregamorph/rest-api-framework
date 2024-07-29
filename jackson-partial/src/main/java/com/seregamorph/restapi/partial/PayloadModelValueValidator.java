package com.seregamorph.restapi.partial;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PayloadModelValueValidator implements ConstraintValidator<PayloadModel, PartialPayload> {

    private PayloadModel payloadModel;

    @Override
    public void initialize(PayloadModel payloadModel) {
        this.payloadModel = payloadModel;
    }

    @Override
    public boolean isValid(PartialPayload body, ConstraintValidatorContext context) {
        try {
            if (body instanceof PartialResource) {
                PartialResourceUtils.validate((PartialResource) body, payloadModel.value());
            } else {
                assert payloadModel.value() == Object.class : "PayloadModel " + payloadModel.value().getSimpleName()
                        + " cannot be used with non-PartialResource body, annotation should have default value";
                PartialPayloadUtils.validate(body);
            }
            return true;
        } catch (RedundantFieldsException | RequiredFieldsException e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage())
                    .addConstraintViolation();
            return false;
        }
    }
}
