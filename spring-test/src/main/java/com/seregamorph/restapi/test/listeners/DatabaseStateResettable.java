package com.seregamorph.restapi.test.listeners;

/**
 * A product-neutral and platform-neutral interface to reset database state. Make sure you implement your own
 * {@link org.springframework.stereotype.Component} that implements this interface.
 */
public interface DatabaseStateResettable {

    void resetDatabaseState() throws Exception;
}
