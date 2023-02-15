package com.seregamorph.restapi.demo.mappers;

import com.seregamorph.restapi.test.base.BaseMappersIT;
import lombok.extern.slf4j.Slf4j;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
@Import({MapperITConfig.class, JacksonAutoConfiguration.class})
public abstract class AbstractMappersIT extends BaseMappersIT {

    public AbstractMappersIT() {
        super(true);
    }
}
