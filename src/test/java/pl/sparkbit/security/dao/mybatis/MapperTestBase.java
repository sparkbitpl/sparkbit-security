package pl.sparkbit.security.dao.mybatis;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import pl.sparkbit.commons.test.AbstractMapperTests;

@SpringBootTest(classes = MapperTestConfig.class)
public abstract class MapperTestBase extends AbstractMapperTests {
}
