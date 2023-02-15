package com.seregamorph.restapi.test.base.setup.common.payload;

import com.seregamorph.restapi.test.base.StackTraceHolder;
import java.util.Iterator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
abstract class GeneratedPayloadIterator implements Iterator<GeneratedPayload> {

    private final StackTraceHolder stackTraceHolder;
    private final List<PayloadProperty> properties;

    private int index = -1;

    @Override
    public boolean hasNext() {
        return index + 1 < properties.size();
    }

    @Override
    public GeneratedPayload next() {
        val payload = payload(properties.get(++index));
        payload.setTrace(stackTraceHolder.getTrace());
        return payload;
    }

    abstract GeneratedPayload payload(PayloadProperty property);
}
