package com.seregamorph.restapi.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.SimpleTransactionStatus;

public class EmptyTransactionManagerConfig {

    @Bean
    public PlatformTransactionManager simpleTransactionManager() {
        return new PlatformTransactionManager() {

            @Override
            public TransactionStatus getTransaction(TransactionDefinition definition) throws TransactionException {
                return new SimpleTransactionStatus();
            }

            @Override
            public void commit(TransactionStatus status) throws TransactionException {
                // no op
            }

            @Override
            public void rollback(TransactionStatus status) throws TransactionException {
                // no op
            }
        };
    }
}
