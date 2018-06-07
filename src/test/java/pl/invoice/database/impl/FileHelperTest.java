package pl.invoice.database.impl;

import static org.hamcrest.CoreMatchers.is;
import static pl.invoice.TestInvoiceGenerator.getFirstTestInvoice;
import static pl.invoice.TestInvoiceGenerator.getSecondTestInvoice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pl.invoice.model.Invoice;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileHelperTest {

  private static final String FILE_PATH = "src/test/resources/fileHelperTest.txt";
  private static final String CURRENT_ID_FILE_PATH = "src/test/resources/fileHelperTestID.txt";
  private File file;
  private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
  private JsonConverter jsonConverter = new JsonConverter(objectMapper);
  private FileHelper fileHelper = new FileHelper(jsonConverter);

  public FileHelperTest() {
  }

  @Before
  public void setup() throws IOException {
    file = new File(FILE_PATH);
    file.createNewFile();

    File fileWithId = new File(CURRENT_ID_FILE_PATH);
    fileWithId.createNewFile();
  }

  @Test
  public void saveInvoiceToFileShouldSaveInvoiceAndReturnId() throws IOException {
    // given:
    List<Invoice> expectedInvoiceList = new ArrayList<>();
    expectedInvoiceList.add(getFirstTestInvoice());
    expectedInvoiceList.add(getSecondTestInvoice());
    fileHelper.saveInvoiceToFile(getFirstTestInvoice(), file);
    // when:
    int resultId = fileHelper.saveInvoiceToFile(getSecondTestInvoice(), file);
    List<Invoice> resultInvoiceList = fileHelper.readInvoicesFromFile(file);
    // then:
    Assert.assertThat(resultId, is(2));
    Assert.assertThat(resultInvoiceList.get(0), is(expectedInvoiceList.get(0)));
    Assert.assertThat(resultInvoiceList.get(1), is(expectedInvoiceList.get(1)));
  }

  @Test
  public void readInvoicesFromFileShouldReturnListOfInvoicesSaved() throws IOException {
    // given:
    fileHelper.saveInvoiceToFile(getFirstTestInvoice(), file);
    fileHelper.saveInvoiceToFile(getSecondTestInvoice(), file);
    List<Invoice> expectedResult = new ArrayList<>();
    expectedResult.add(getFirstTestInvoice());
    expectedResult.add(getSecondTestInvoice());
    // when:
    List<Invoice> result = fileHelper.readInvoicesFromFile(file);
    // then:
    Assert.assertThat(result.get(0), is(expectedResult.get(0)));
    Assert.assertThat(result.get(1), is(expectedResult.get(1)));
  }

  @Test
  public void readInvoicesFromFileWhenNoInvoicesInDatabaseShouldReturnEmptyList() throws IOException {
    Assert.assertThat(fileHelper.readInvoicesFromFile(file), is(new ArrayList<>()));
  }

  @After
  public void cleanUp() {
    file = new File(FILE_PATH);
    file.delete();

    File fileWithId = new File(CURRENT_ID_FILE_PATH);
    fileWithId.delete();
  }
}