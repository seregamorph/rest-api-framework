package com.seregamorph.restapi.demo.mappers;

import com.seregamorph.restapi.base.BaseMapper;
import com.seregamorph.restapi.test.base.BaseMapperTest;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@Import({MapperITConfig.class, JacksonAutoConfiguration.class})
public abstract class AbstractMapperTest extends BaseMapperTest {

    public AbstractMapperTest(Class<? extends BaseMapper> mapperClass) {
        super(mapperClass);
    }
}
