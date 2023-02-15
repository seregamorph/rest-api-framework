package com.seregamorph.restapi.test.base.support;

import com.seregamorph.restapi.test.base.AbstractBaseSpringWebIT;
import com.seregamorph.restapi.test.listeners.DatabaseStateManagementUtils;
import com.seregamorph.restapi.test.listeners.DatabaseStateResettable;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.test.context.transaction.TestTransaction;

@RequiredArgsConstructor
public class AroundRequestActionSupport<P> {

    private final P parent;

    @Getter
    @Nullable
    private AroundRequestAction<?> preRequestAction;

    @Getter
    @Nullable
    private AroundRequestAction<?> postRequestAction = endTransaction();

    public <T extends AbstractBaseSpringWebIT> P setPreRequestAction(AroundRequestAction<T> preRequestAction) {
        this.preRequestAction = preRequestAction;
        return this.parent;
    }

    public <T extends AbstractBaseSpringWebIT> P setPostRequestAction(AroundRequestAction<T> postRequestAction) {
        this.postRequestAction = postRequestAction;
        return this.parent;
    }

    public P resetDatabaseStateAfterRequests() {
        this.postRequestAction = resetDatabaseState();
        return this.parent;
    }

    private static AroundRequestAction<?> endTransaction() {
        return test -> {
            if (TestTransaction.isActive()) {
                TestTransaction.end();
            }
        };
    }

    private static AroundRequestAction<?> resetDatabaseState() {
        return test -> {
            val databaseStateResettable = test.applicationContext().getBean(DatabaseStateResettable.class);
            DatabaseStateManagementUtils.resetDatabaseState(databaseStateResettable);
        };
    }
}
