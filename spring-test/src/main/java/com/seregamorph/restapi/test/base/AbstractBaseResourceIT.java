package com.seregamorph.restapi.test.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seregamorph.restapi.base.BaseResource;
import com.seregamorph.restapi.test.components.InfiniteRecursionDetector;
import java.util.Collections;
import java.util.Set;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractBaseResourceIT extends AbstractBaseResourceTest {

    @Autowired
    protected InfiniteRecursionDetector infiniteRecursionDetector;

    protected void noProjectionsShouldHitInfiniteRecursionError(Class<? extends BaseResource> resourceType)
            throws JsonProcessingException {
        val preInitializedInstances = getPreInitializedInstances();
        val projectionEnumType = TestProjectionUtils.tryGetProjectionType(resourceType);
        if (projectionEnumType != null) {
            for (val projectionName : projectionEnumType.getEnumConstants()) {
                val projectionClass = projectionName.getProjectionClass();
                logger.info("Testing {} and {}...", resourceType.getName(), projectionClass.getName());
                infiniteRecursionDetector.detect(resourceType, projectionClass, preInitializedInstances);
            }
        }
    }

    protected Set<Object> getPreInitializedInstances() {
        return Collections.emptySet();
    }

}
