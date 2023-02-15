package com.seregamorph.restapi.test.base.support;

import static com.seregamorph.restapi.test.base.ResultType.LIST;
import static com.seregamorph.restapi.test.base.ResultType.PAGE;

import com.seregamorph.restapi.test.base.ResultType;

public interface PaginationSupportDelegate<P> {

    PaginationSupport<P> getPaginationSupport();

    default P setPaginationSupported(boolean paginationSupported) {
        return getPaginationSupport().setPaginationSupported(paginationSupported);
    }

    default P setTotalElements(int totalElements) {
        return getPaginationSupport().setTotalElements(totalElements);
    }

    default boolean isPaginationSupported() {
        return getPaginationSupport().isPaginationSupported();
    }

    default ResultType getResultType() {
        return isPaginationSupported() ? PAGE : LIST;
    }

    default int getTotalElements() {
        return getPaginationSupport().getTotalElements();
    }

    default QuantityVerificationMode getQuantityVerificationMode() {
        return getPaginationSupport().getQuantityVerificationMode();
    }

    default P setQuantityVerificationMode(QuantityVerificationMode mode) {
        return getPaginationSupport().setQuantityVerificationMode(mode);
    }
}
