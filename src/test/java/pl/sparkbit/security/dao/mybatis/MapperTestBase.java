package pl.sparkbit.security.dao.mybatis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import pl.sparkbit.commons.test.AbstractMapperTests;

@SpringBootTest(classes = MapperTestConfig.class)
@ContextConfiguration(initializers = MapperTestBase.Initializer.class)
@Slf4j
public abstract class MapperTestBase extends AbstractMapperTests {

    private static final Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(log);
    private static final MySQLContainer mysql = new MySQLContainer()
            .withDatabaseName("security")
            .withUsername("security")
            .withPassword("security");

    static {
        mysql.start();
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            mysql.followOutput(logConsumer);
            TestPropertyValues values = TestPropertyValues.of(
                    "mapperTest.db.port=" + mysql.getMappedPort(3306)
            );
            values.applyTo(configurableApplicationContext);
        }
    }
}
