package com.seregamorph.restapi.test.base.support;

import static com.seregamorph.restapi.test.base.support.QuantityVerificationMode.EXACT_QTY;

import com.seregamorph.restapi.test.config.spi.TestFrameworkConfigHolder;
import javax.annotation.Nonnull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;

@RequiredArgsConstructor
public class PaginationSupport<P> {

    private final P parent;

    @Getter
    private boolean paginationSupported = TestFrameworkConfigHolder.getTestFrameworkConfig()
            .isDefaultPaginationSupported();

    /**
     * The total number of elements that can be retrieved
     */
    @Getter
    private int totalElements = 2;

    @Getter
    private QuantityVerificationMode quantityVerificationMode = EXACT_QTY;

    public P setPaginationSupported(boolean paginationSupported) {
        validateSetup(paginationSupported, totalElements);
        this.paginationSupported = paginationSupported;
        return this.parent;
    }

    public P setTotalElements(int totalElements) {
        validateSetup(paginationSupported, totalElements);
        this.totalElements = totalElements;
        return this.parent;
    }

    public P setQuantityVerificationMode(@Nonnull QuantityVerificationMode mode) {
        this.quantityVerificationMode = mode;
        return this.parent;
    }

    private static void validateSetup(boolean paginationSupported, int totalElements) {
        Validate.isTrue(!paginationSupported || totalElements >= 2,
                "If pagination is supported, there must be at least 2 elements so that base tests can verify "
                        + "pagination with the second page.");
    }
}
