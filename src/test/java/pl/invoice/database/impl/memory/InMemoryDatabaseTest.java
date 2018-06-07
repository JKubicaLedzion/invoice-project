package pl.invoice.database.impl.memory;

import org.junit.Before;
import pl.invoice.database.Database;
import pl.invoice.database.impl.DatabaseTest;

public class InMemoryDatabaseTest extends DatabaseTest {

  @Override
  public Database getDatabase() {
    return new InMemoryDatabase();
  }

  @Before
  public void setup() {
    database = getDatabase();
  }
}