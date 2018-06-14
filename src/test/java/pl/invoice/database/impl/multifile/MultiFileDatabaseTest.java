package pl.invoice.database.impl.multifile;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import pl.invoice.configuration.DbPropertiesConfig;
import pl.invoice.database.Database;
import pl.invoice.database.impl.DatabaseTest;
import pl.invoice.database.impl.FileHelper;
import pl.invoice.database.impl.IdGenerator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(value = "classpath:application-multifile.properties")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class MultiFileDatabaseTest extends DatabaseTest {

  @Autowired
  private DbPropertiesConfig dbPropertiesConfig;

  @Autowired
  private FileHelper fileHelper;

  @Autowired
  private IdGenerator idGenerator;

  @Autowired
  private PathGenerator pathGenerator;

  private File file;

  @Override
  @Autowired
  public Database getDatabase() throws IOException {
    file = new File(dbPropertiesConfig.getMultiFileRootFolder());
    return new MultiFileDatabase(dbPropertiesConfig, fileHelper, idGenerator, pathGenerator);
  }

  @Before
  public void setup() throws IOException {
    Files.createDirectories(file.toPath());
    database = getDatabase();
  }

  @After
  public void cleanup() throws IOException {
    FileUtils.deleteDirectory(file);
  }
}