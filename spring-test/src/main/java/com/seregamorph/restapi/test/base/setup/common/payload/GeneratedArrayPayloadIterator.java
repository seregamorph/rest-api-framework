package com.seregamorph.restapi.test.base.setup.common.payload;

import static com.seregamorph.restapi.test.base.setup.common.payload.PayloadExtractor.extractAll;
import static com.seregamorph.restapi.test.base.setup.common.payload.PayloadPropertyAnalyzer.analyzeAll;

import com.seregamorph.restapi.test.base.StackTraceHolder;
import java.util.List;

class GeneratedArrayPayloadIterator extends GeneratedPayloadIterator {

    private final List<List<PayloadProperty>> allProperties;

    GeneratedArrayPayloadIterator(StackTraceHolder stackTraceHolder, List<List<PayloadProperty>> allProperties,
                                  FieldType fieldType) {
        super(stackTraceHolder, analyzeAll(allProperties, fieldType));
        this.allProperties = allProperties;
    }

    @Override
    GeneratedPayload payload(PayloadProperty property) {
        return new GeneratedPayload(
                property.getResourceClass(),
                property.getFieldName(),
                extractAll(allProperties, FieldType.REQUIRED, property)
        );
    }
}
