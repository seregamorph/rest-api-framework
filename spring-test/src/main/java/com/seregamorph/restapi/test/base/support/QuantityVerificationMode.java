package com.seregamorph.restapi.test.base.support;

public enum QuantityVerificationMode {

    /**
     * Verifies the exact quantity.
     */
    EXACT_QTY,
    /**
     * Verifies the minimum quantity. Useful in the case we work with live db and the number of elements may fluctuate.
     */
    MIN_QTY
}
