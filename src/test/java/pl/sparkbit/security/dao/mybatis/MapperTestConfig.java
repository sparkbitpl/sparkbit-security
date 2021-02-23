package pl.sparkbit.security.dao.mybatis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import pl.sparkbit.commons.test.db.MySQLMapperTestConfigBase;

@Configuration
@MapperScan("pl.sparkbit.security.dao.mybatis")
public class MapperTestConfig extends MySQLMapperTestConfigBase {
}
