package com.seregamorph.restapi.test.listeners;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DatabaseStateManagementUtils {

    public static void resetDatabaseState(DatabaseStateResettable resetter) throws Exception {
        if (resetter == null) {
            throw new IllegalStateException(String.format(
                    "A reset of database state is necessary, but no bean of type %s can be found.",
                    DatabaseStateResettable.class));
        }

        resetter.resetDatabaseState();
    }
}
