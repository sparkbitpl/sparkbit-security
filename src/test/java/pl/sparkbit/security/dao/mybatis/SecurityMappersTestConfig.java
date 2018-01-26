package pl.sparkbit.security.dao.mybatis;

import net.sf.log4jdbc.sql.jdbcapi.DataSourceSpy;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;
import pl.sparkbit.commons.mybatis.handlers.InstantAsIntegerTypeHandler;

import javax.sql.DataSource;
import java.sql.Driver;
import java.time.Instant;

@Configuration
@MapperScan("pl.sparkbit.security")
public class SecurityMappersTestConfig {

    @Value("#{'${classpath:mybatis/rest-security-mapper.xml,classpath:mybatis/session-mapper.xml," +
            "classpath:mybatis/security-challenge-mapper.xml}'.split(',')}")
    private Resource[] mappers;

    @Value("${security_test_db_url:jdbc:mysql://localhost:3306/security}")
    private String url;

    @Value("${security_test_db_username:security}")
    private String username;

    @Value("${security_test_db_password:security}")
    private String password;

    @Bean
    public DataSource dataSourceSpied() throws Exception {
        Driver driver = (Driver) Class.forName("com.mysql.jdbc.Driver").newInstance();
        return new SimpleDriverDataSource(driver, url, username, password);
    }

    @Bean
    public DataSource dataSource() throws Exception {
        return new DataSourceSpy(dataSourceSpied());
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer() throws Exception {
        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(dataSource());
        dataSourceInitializer.setDatabasePopulator(databasePopulator());
        dataSourceInitializer.setDatabaseCleaner(connection -> {
        });
        return dataSourceInitializer;
    }

    private DatabasePopulator databasePopulator() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("security-schema.sql"));
        populator.addScript(new ClassPathResource("test-schema.sql"));
        return populator;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setMapperLocations(mappers);
        sessionFactory.setTypeAliasesPackage("pl.sparkbit.security.domain");
        sessionFactory.setTypeAliases(new Class[]{Instant.class});
        sessionFactory.setTypeHandlers(new TypeHandler[]{new InstantAsIntegerTypeHandler()});
        return sessionFactory.getObject();
    }

    @Bean
    public PlatformTransactionManager txManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
