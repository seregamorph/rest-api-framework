package com.seregamorph.restapi.test.base.setup.common.payload;

import static com.seregamorph.restapi.test.base.setup.common.payload.PayloadExtractor.extract;
import static com.seregamorph.restapi.test.base.setup.common.payload.PayloadPropertyAnalyzer.analyze;

import com.seregamorph.restapi.test.base.StackTraceHolder;
import java.util.List;

class GeneratedSinglePayloadIterator extends GeneratedPayloadIterator {

    private final List<PayloadProperty> properties;

    GeneratedSinglePayloadIterator(StackTraceHolder stackTraceHolder, List<PayloadProperty> properties,
                                   FieldType fieldType) {
        super(stackTraceHolder, analyze(properties, fieldType));
        this.properties = properties;
    }

    @Override
    GeneratedPayload payload(PayloadProperty property) {
        return new GeneratedPayload(
                property.getResourceClass(),
                property.getFieldName(),
                extract(properties, FieldType.REQUIRED, property)
        );
    }
}
