package pl.invoice.database.impl.sql;

import static pl.invoice.database.impl.sql.TestQueryBuilder.ADD_TABLES_QUERY;
import static pl.invoice.database.impl.sql.TestQueryBuilder.DROP_TABLES_QUERY;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import pl.invoice.configuration.DbPropertiesConfig;
import pl.invoice.database.Database;
import pl.invoice.database.impl.DatabaseTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(value = "classpath:application-sql.properties")
public class SqlWithDriverDatabaseTest extends DatabaseTest {

  @Autowired
  private DbPropertiesConfig dbPropertiesConfig;

  @Override
  public Database getDatabase() throws SQLException {
    return new SqlWithDriverDatabase(dbPropertiesConfig);
  }

  @Before
  public void setup() throws SQLException {
    Connection connection = DriverManager
        .getConnection(dbPropertiesConfig.getSqlDatabase(), dbPropertiesConfig.getSqlDbUser(),
            dbPropertiesConfig.getSqlDbPassword());

    Statement statement = connection.createStatement();
    statement.executeUpdate(DROP_TABLES_QUERY);

    PreparedStatement statementAddTables = connection.prepareStatement(ADD_TABLES_QUERY);
    statementAddTables.execute();
    database = getDatabase();
  }
}