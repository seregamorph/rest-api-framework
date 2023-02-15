package com.seregamorph.restapi.test.base;

import org.springframework.data.domain.AbstractPageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Implementation note: Due to the difference between Spring Data Commons 1.10.0.RELEASE and 2.3.1.RELEASE,
 * we won't be able to use PageRequest directly.
 * We work around by extending directly from AbstractPageRequest instead.
 */
class TestPageRequest extends AbstractPageRequest {

    TestPageRequest(int pageNumber, int pageSize) {
        super(pageNumber, pageSize);
    }

    @Override
    public Sort getSort() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pageable next() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pageable previous() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pageable first() {
        throw new UnsupportedOperationException();
    }

}
