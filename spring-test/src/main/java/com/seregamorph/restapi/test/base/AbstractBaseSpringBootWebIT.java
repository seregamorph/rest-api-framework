package com.seregamorph.restapi.test.base;

import com.seregamorph.restapi.test.config.EmbeddedWebTestRestTemplateConfig;
import com.seregamorph.restapi.test.config.MockMvcBootConfig;
import com.seregamorph.restapi.test.config.SkipTestFilterBootConfig;
import com.seregamorph.restapi.test.config.TestContextFilterBootConfig;
import com.seregamorph.restapi.test.config.TransactionRollbackFilterBootConfig;
import org.springframework.context.annotation.Import;

@Import({
        MockMvcBootConfig.class,
        EmbeddedWebTestRestTemplateConfig.class,
        SkipTestFilterBootConfig.class,
        TestContextFilterBootConfig.class,
        TransactionRollbackFilterBootConfig.class
})
public abstract class AbstractBaseSpringBootWebIT extends AbstractBaseSpringWebIT {

}
