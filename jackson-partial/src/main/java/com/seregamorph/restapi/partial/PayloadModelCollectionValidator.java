package com.seregamorph.restapi.partial;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.val;

public class PayloadModelCollectionValidator implements ConstraintValidator<PayloadModel,
        Collection<? extends PartialPayload>> {

    private PayloadModel payloadModel;

    @Override
    public void initialize(PayloadModel payloadModel) {
        this.payloadModel = payloadModel;
    }

    @Override
    public boolean isValid(Collection<? extends PartialPayload> body, ConstraintValidatorContext context) {
        try {
            for (PartialPayload payload : body) {
                if (payload instanceof PartialResource) {
                    val collectionElementType = getCollectionElementType(payloadModel.value());
                    if (collectionElementType != null) {
                        PartialResourceUtils.validate((PartialResource) payload, collectionElementType);
                    }
                } else {
                    assert payloadModel.value() == Object.class : "PayloadModel " + payloadModel.value().getSimpleName()
                            + " cannot be used with non-PartialResource body, annotation should have default value";
                    PartialPayloadUtils.validate(payload);
                }
            }
            return true;
        } catch (RedundantFieldsException | RequiredFieldsException e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage())
                    .addConstraintViolation();
            return false;
        }
    }

    private static Class<?> getCollectionElementType(Class<?> collectionType) {
        for (val type : collectionType.getGenericInterfaces()) {
            if (type instanceof ParameterizedType) {
                val parameterizedType = ((ParameterizedType) type);
                val raw = parameterizedType.getRawType();
                if (raw instanceof Class && Collection.class.isAssignableFrom((Class<?>) raw)) {
                    val elementType = parameterizedType.getActualTypeArguments()[0];
                    if (elementType instanceof Class) {
                        return (Class<?>) elementType;
                    }
                }
            }
        }
        return null;
    }
}
