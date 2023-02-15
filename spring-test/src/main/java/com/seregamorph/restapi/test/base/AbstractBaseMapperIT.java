package com.seregamorph.restapi.test.base;

import com.seregamorph.restapi.base.BaseMapper;
import com.seregamorph.restapi.base.BaseResource;
import com.seregamorph.restapi.base.PruningMapper;
import com.seregamorph.restapi.config.spi.FrameworkConfigHolder;
import com.seregamorph.restapi.test.components.InfiniteRecursionDetector;
import com.seregamorph.restapi.utils.RecursionPruner;
import java.util.Collections;
import java.util.List;
import org.junit.Assume;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractBaseMapperIT extends AbstractBaseMapperTest {

    @Autowired
    private InfiniteRecursionDetector infiniteRecursionDetector;

    private final boolean infiniteRecursionDetectionEnabled;

    protected AbstractBaseMapperIT() {
        // Mapper from an entity to a resource should handle infinite recursion. But, if projection is supported, then
        // the mapping result is NOT serialized to json directly - it needs to be projected via projections. Therefore,
        // no need to handle infinite recursion in mappers (it's the job of projections which are tested
        // in integration tests for resources - see com.seregamorph.restapi.test.base.AbstractBaseResourceIT)
        this(!FrameworkConfigHolder.getFrameworkConfig().isProjectionSupported());
    }

    protected AbstractBaseMapperIT(boolean infiniteRecursionDetectionEnabled) {
        this.infiniteRecursionDetectionEnabled = infiniteRecursionDetectionEnabled;
    }

    @SuppressWarnings("unchecked")
    protected void shouldHandleInfiniteRecursion(Class<? extends BaseMapper> mapperClass) throws Exception {
        Assume.assumeTrue(infiniteRecursionDetectionEnabled);
        Assume.assumeTrue(PruningMapper.class.isAssignableFrom(mapperClass));

        Class<?> entityClass = ClassTypeInformation.from(mapperClass)
                .getSuperTypeInformation(PruningMapper.class)
                .getTypeArguments()
                .get(0)
                .getType();

        // These class either can't be initialized by Class.newInstance() or needs special initialization logic.
        List<Object> preInitializedInstances = getPreInitializedInstances();
        PruningMapper<Object, ? extends BaseResource> mapper =
                (PruningMapper<Object, ? extends BaseResource>) getMapper(mapperClass);

        RecursionPruner recursionPruner = getRecursionPruner();

        if (recursionPruner == null) {
            infiniteRecursionDetector.detect(entityClass, mapper::pruningMap, preInitializedInstances);
        } else {
            infiniteRecursionDetector.detect(entityClass,
                    entity -> mapper.pruningMap(entity, recursionPruner),
                    preInitializedInstances);
        }
    }

    protected List<Object> getPreInitializedInstances() {
        return Collections.emptyList();
    }

    protected RecursionPruner getRecursionPruner() {
        return null;
    }
}
