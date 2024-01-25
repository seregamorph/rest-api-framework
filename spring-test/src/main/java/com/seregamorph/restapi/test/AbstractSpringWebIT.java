package com.seregamorph.restapi.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.seregamorph.testsmartcontext.junit4.AbstractJUnit4SpringIntegrationTest;
import com.seregamorph.restapi.test.listeners.DatabaseStateResetTestExecutionListener;
import com.seregamorph.restapi.test.utils.JsonExtensions;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

@TestExecutionListeners({
        TransactionalTestExecutionListener.class,
        WithSecurityContextTestExecutionListener.class,
        DatabaseStateResetTestExecutionListener.class
})
@Transactional
public abstract class AbstractSpringWebIT extends AbstractJUnit4SpringIntegrationTest
        implements JsonExtensions {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ObjectMapper objectMapper;

    @Rule
    public final TestRule watcher = new TestLifecycleWatcher();

    @Override
    public ObjectMapper objectMapper() {
        return objectMapper;
    }

    @Before
    public void storeApplicationContext() {
        TestApplicationContextHolder.setApplicationContext(applicationContext);
    }

    @After
    public void eraseApplicationContext() {
        TestApplicationContextHolder.setApplicationContext(null);
    }

    public ApplicationContext applicationContext() {
        return applicationContext;
    }

}
