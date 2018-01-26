package pl.sparkbit.security.dao.mybatis;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.operation.Operation;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.Assert;
import pl.sparkbit.security.dao.mybatis.data.TransactionAwareDataSourceDestination;

import javax.sql.DataSource;

import static com.ninja_squad.dbsetup.Operations.sequenceOf;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SecurityMappersTestConfig.class)
public abstract class MapperTestBase extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PlatformTransactionManager transactionManager;

    void insertTestData(Operation... operations) {
        new DbSetup(new TransactionAwareDataSourceDestination(dataSource, transactionManager), sequenceOf(operations))
                .launch();
    }

    int countRowsInTableWhereColumnsEquals(String tableName, Object... colsAndValues) {
        Assert.isTrue(colsAndValues.length % 2 == 0, "There should be even number of columns and values");

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < colsAndValues.length - 1; i += 2) {
            Object key = colsAndValues[i];
            Object value = colsAndValues[i + 1];

            if (value == null) {
                sb.append(key).append(" IS NULL ").append(" AND ");
            } else {
                sb.append(key).append("=").append(value).append(" AND ");
            }
        }
        return countRowsInTableWhere(tableName, sb.substring(0, sb.length() - 5));
    }

    static String quote(Object arg) {
        return "'" + arg + "'";
    }
}
