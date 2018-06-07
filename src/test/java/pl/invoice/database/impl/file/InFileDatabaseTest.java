package pl.invoice.database.impl.file;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import pl.invoice.DbPropertiesConfig;
import pl.invoice.database.Database;
import pl.invoice.database.impl.DatabaseTest;
import pl.invoice.database.impl.FileHelper;
import pl.invoice.database.impl.IdGenerator;

import java.io.File;
import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(value = "classpath:application-infile.properties")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class InFileDatabaseTest extends DatabaseTest {

  @Autowired
  private DbPropertiesConfig dbPropertiesConfig;

  @Autowired
  private FileHelper fileHelper;

  @Autowired
  private IdGenerator idGenerator;

  @Override
  public Database getDatabase() throws IOException {
    return new InFileDatabase(dbPropertiesConfig, idGenerator, fileHelper);
  }

  @Before
  public void setup() throws IOException {
    filesSetUp(new File(dbPropertiesConfig.getFilePath()));

    database = getDatabase();
  }

  @After
  public void cleanUp() throws IOException {
    filesCleanUp(new File(dbPropertiesConfig.getFilePath()));
  }
}