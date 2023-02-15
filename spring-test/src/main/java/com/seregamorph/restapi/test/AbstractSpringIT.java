package com.seregamorph.restapi.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seregamorph.restapi.test.utils.JsonExtensions;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractSpringIT extends AbstractSpringTest implements JsonExtensions {

    @Autowired
    private ObjectMapper objectMapper;

    @Rule
    public final TestRule watcher = new TestLifecycleWatcher();

    @Override
    public ObjectMapper objectMapper() {
        return objectMapper;
    }
}
