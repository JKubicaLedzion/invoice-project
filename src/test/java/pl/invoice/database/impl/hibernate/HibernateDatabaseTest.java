package pl.invoice.database.impl.hibernate;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import pl.invoice.database.Database;
import pl.invoice.database.impl.DatabaseTest;

import java.sql.SQLException;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(value = "classpath:application-hibernate.properties")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class HibernateDatabaseTest extends DatabaseTest {

  @Autowired
  private InvoiceRepository invoiceRepository;

  @Autowired
  private CompanyRepository companyRepository;

  @Autowired
  private InvoiceEntryRepository invoiceEntryRepository;

  @Override
  public Database getDatabase() {
    return new HibernateDatabase(invoiceRepository, companyRepository, invoiceEntryRepository);
  }

  @Before
  public void setup() throws SQLException {
    database = getDatabase();
  }
}