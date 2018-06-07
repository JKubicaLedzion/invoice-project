package pl.invoice.service.invoice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;
import pl.invoice.database.Database;
import pl.invoice.exception.InvoiceNotFoundException;
import pl.invoice.model.Invoice;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

@RunWith(SpringRunner.class)
public class InvoiceServiceTest {

  @Mock
  Database database;

  @InjectMocks
  InvoiceService invoiceService;

  private Invoice invoice = new Invoice();

  @Test
  public void addInvoice() throws IOException {
    // given:
    Mockito.when(database.saveInvoice(invoice)).thenReturn(1);
    // when:
    invoiceService.addInvoice(invoice);
    // then:
    Mockito.verify(database, Mockito.times(1)).saveInvoice(invoice);
  }

  @Test
  public void getInvoiceById() throws IOException {
    // given:
    Mockito.when(database.getInvoiceById(Mockito.anyInt())).thenReturn(Optional.of(invoice));
    // when:
    invoiceService.getInvoiceById(1);
    // then:
    Mockito.verify(database, Mockito.times(1)).getInvoiceById(1);
  }

  @Test
  public void updateInvoice() throws InvoiceNotFoundException, IOException {
    // given:
    Mockito.when(database.updateInvoice(invoice)).thenReturn(2);
    // when:
    invoiceService.updateInvoice(invoice);
    // then:
    Mockito.verify(database, Mockito.times(1)).updateInvoice(invoice);
  }

  @Test(expected = InvoiceNotFoundException.class)
  public void updateInvoiceWhenInvoiceNotFoundThrowException() throws InvoiceNotFoundException, IOException {
    Mockito.when(database.updateInvoice(invoice)).thenThrow(InvoiceNotFoundException.class);
    invoiceService.updateInvoice(invoice);
  }

  @Test
  public void getInvoices() throws IOException {
    // given:
    Mockito.when(database.getInvoices()).thenReturn(new ArrayList<>());
    // when:
    invoiceService.getInvoices();
    // then:
    Mockito.verify(database, Mockito.times(1)).getInvoices();
  }

  @Test
  public void getInvoicesWithinDateRange() throws IOException {
    // given:
    Mockito.when(database.getInvoicesWithinDateRange(LocalDate.now(), LocalDate.now())).thenReturn(new ArrayList<>());
    // when:
    invoiceService.getInvoicesWithinDateRange(LocalDate.now(), LocalDate.now());
    // then:
    Mockito.verify(database, Mockito.times(1)).getInvoicesWithinDateRange(LocalDate.now(), LocalDate.now());
  }
}