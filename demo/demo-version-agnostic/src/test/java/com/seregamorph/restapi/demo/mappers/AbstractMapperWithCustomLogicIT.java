package com.seregamorph.restapi.demo.mappers;

import static org.junit.rules.ExpectedException.none;

import com.seregamorph.restapi.test.components.InfiniteRecursionDetector;
import java.util.function.Function;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@Import({MapperITConfig.class, JacksonAutoConfiguration.class})
public abstract class AbstractMapperWithCustomLogicIT {

    @Rule
    public final ExpectedException expectedException = none();

    @Autowired
    protected InfiniteRecursionDetector infiniteRecursionDetector;

    protected <F, T> void shouldHitInfiniteRecursion(Class<F> fromClass, Function<F, T> mapper) throws Exception {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Infinite recursion (StackOverflowError)");
        infiniteRecursionDetector.detect(fromClass, mapper);
    }
}
