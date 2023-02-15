package com.seregamorph.restapi.test.base.support;

import com.seregamorph.restapi.test.base.AbstractBaseSpringWebIT;

public interface AroundRequestActionSupportDelegate<P> {

    AroundRequestActionSupport<P> getAroundRequestActionSupport();

    default <T extends AbstractBaseSpringWebIT> P setPreRequestAction(AroundRequestAction<T> preRequestAction) {
        return getAroundRequestActionSupport().setPreRequestAction(preRequestAction);
    }

    default AroundRequestAction<?> getPreRequestAction() {
        return getAroundRequestActionSupport().getPreRequestAction();
    }

    default <T extends AbstractBaseSpringWebIT> P setPostRequestAction(AroundRequestAction<T> postRequestAction) {
        return getAroundRequestActionSupport().setPostRequestAction(postRequestAction);
    }

    default AroundRequestAction<?> getPostRequestAction() {
        return getAroundRequestActionSupport().getPostRequestAction();
    }

    default P resetDatabaseStateAfterRequests() {
        return getAroundRequestActionSupport().resetDatabaseStateAfterRequests();
    }
}
