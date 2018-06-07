package pl.invoice.database.impl;

import static org.hamcrest.CoreMatchers.is;
import static pl.invoice.TestInvoiceGenerator.getFirstTestInvoice;
import static pl.invoice.TestInvoiceGenerator.getSecondTestInvoice;
import static pl.invoice.TestInvoiceGenerator.getThirdTestInvoice;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import pl.invoice.database.Database;
import pl.invoice.exception.InvoiceNotFoundException;
import pl.invoice.model.Invoice;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
public abstract class DatabaseTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  protected Database database;

  private Invoice firstInvoice = getFirstTestInvoice();
  private Invoice secondInvoice = getSecondTestInvoice();
  private Invoice thirdInvoice = getThirdTestInvoice();
  private LocalDate fromDate;
  private LocalDate toDate;

  public abstract Database getDatabase() throws IOException, SQLException;

  @Test
  public void saveInvoiceShouldSaveInvoiceAndReturnId() throws IOException {
    // given:
    database.saveInvoice(firstInvoice);
    // when:
    int resultId = database.saveInvoice(secondInvoice);
    Optional<Invoice> resultOptional = database.getInvoiceById(2);
    int resultIndex = database.getInvoices().indexOf(secondInvoice);
    // then:
    Assert.assertThat(resultId, is(2));
    Assert.assertThat(resultOptional, is(Optional.of(secondInvoice)));
    Assert.assertThat(resultIndex, is(1));
  }

  @Test
  public void getInvoiceByIdWhenInvoiceInDatabaseShouldReturnOptionalWithThisInvoice() throws IOException {
    // given:
    database.saveInvoice(firstInvoice);
    database.saveInvoice(secondInvoice);
    // when:
    Optional<Invoice> resultOptional = database.getInvoiceById(2);
    // then:
    Assert.assertThat(resultOptional, is(Optional.of(secondInvoice)));
  }

  @Test
  public void getInvoiceByIdWhenInvoiceNotInDatabaseShouldReturnEmptyOptional() throws IOException {
    // given:
    database.saveInvoice(firstInvoice);
    // when:
    Optional<Invoice> resultOptional = database.getInvoiceById(2);
    // then:
    Assert.assertThat(resultOptional, is(Optional.empty()));
  }

  @Test
  public void updateInvoiceWhenOldInvoiceInDatabaseShouldUpdateInvoice() throws InvoiceNotFoundException, IOException {
    // given:
    database.saveInvoice(firstInvoice);
    database.saveInvoice(secondInvoice);
    // when:
    thirdInvoice.setId(1);
    thirdInvoice.getEntryList().get(0).setId(1);
    int result = database.updateInvoice(thirdInvoice);
    Optional<Invoice> resultOptionalForSavedInv = database.getInvoiceById(1);
    int resultIndex = database.getInvoices().indexOf(thirdInvoice);
    // then:
    Assert.assertThat(result, is(1));
    Assert.assertThat(resultOptionalForSavedInv, is(Optional.of(thirdInvoice)));
    Assert.assertThat(resultIndex, is(0));
  }

  @Test
  public void updateInvoiceWhenOldInvoiceNotInDatabaseShouldThrowException() throws InvoiceNotFoundException,
      IOException {
    // given:
    database.saveInvoice(firstInvoice);
    // then:
    exception.expect(InvoiceNotFoundException.class);
    exception.expectMessage("Invoice not in database.");
    // when:
    database.updateInvoice(thirdInvoice);
  }

  @Test
  public void getInvoicesShouldReturnListOfInvoicesSaved() throws IOException {
    // given:
    database.saveInvoice(firstInvoice);
    database.saveInvoice(secondInvoice);
    List<Invoice> expectedResult = new ArrayList<>();
    expectedResult.add(firstInvoice);
    expectedResult.add(secondInvoice);
    // when:
    List<Invoice> result = database.getInvoices();
    // then:
    Assert.assertThat(result.get(0), is(expectedResult.get(0)));
    Assert.assertThat(result.get(1), is(expectedResult.get(1)));
  }

  @Test
  public void getInvoicesWhenNoInvoicesInDatabaseShouldReturnEmptyList() throws IOException {
    Assert.assertThat(database.getInvoices(), is(new ArrayList<>()));
  }

  @Test
  public void getInvoicesWithinDateRangeShouldReturnInvoiceList() throws IOException {
    // given:
    datesSetup();
    database.saveInvoice(firstInvoice);
    database.saveInvoice(secondInvoice);
    database.saveInvoice(thirdInvoice);
    List<Invoice> expectedResult = new ArrayList<>();
    expectedResult.add(firstInvoice);
    expectedResult.add(secondInvoice);
    // when:
    List<Invoice> result = database.getInvoicesWithinDateRange(fromDate, toDate);
    // then:
    Assert.assertThat(result.get(0), is(expectedResult.get(0)));
    Assert.assertThat(result.get(1), is(expectedResult.get(1)));
  }

  @Test
  public void getInvoicesWithinDateRangeShouldReturnEmptyList() throws IOException {
    // given:
    datesSetup();
    database.saveInvoice(thirdInvoice);
    List<Invoice> expectedResult = new ArrayList<>();
    // when:
    List<Invoice> result = database.getInvoicesWithinDateRange(fromDate, toDate);
    // then:
    Assert.assertThat(result, is(expectedResult));
  }

  @Test
  public void deleteInvoiceWhenInvoiceInDatabaseShouldDeleteIt() throws InvoiceNotFoundException, IOException {
    // given:
    database.saveInvoice(firstInvoice);
    database.saveInvoice(secondInvoice);
    // when:
    database.deleteInvoice(1);
    Optional<Invoice> resultOptionalForDeletedInv = database.getInvoiceById(1);
    int resultIndex = database.getInvoices().indexOf(secondInvoice);
    // then:
    Assert.assertThat(resultOptionalForDeletedInv, is(Optional.empty()));
    Assert.assertThat(resultIndex, is(0));
  }

  @Test
  public void deleteInvoiceWhenInvoiceNotInDatabaseShouldThrowException() throws InvoiceNotFoundException,
      IOException {
    // given:
    database.saveInvoice(firstInvoice);
    // then:
    exception.expect(InvoiceNotFoundException.class);
    exception.expectMessage("Invoice not in database.");
    // when:
    database.deleteInvoice(2);
  }

  private void datesSetup() {
    fromDate = LocalDate.of(2017, 12, 01);
    toDate = LocalDate.of(2017, 12, 31);
  }

  protected void filesCleanUp(File file) throws IOException {
    file.delete();
  }

  protected void filesSetUp(File file) throws IOException {
    file.createNewFile();
  }
}
