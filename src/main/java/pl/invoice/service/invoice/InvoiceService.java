package pl.invoice.service.invoice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.invoice.database.Database;
import pl.invoice.exception.InvoiceNotFoundException;
import pl.invoice.model.Invoice;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {

  private Database database;

  @Autowired
  public InvoiceService(Database database) {
    this.database = database;
  }

  public int addInvoice(Invoice invoice) throws IOException {
    return database.saveInvoice(invoice);
  }

  public Optional<Invoice> getInvoiceById(int id) throws IOException {
    return database.getInvoiceById(id);
  }

  public int updateInvoice(Invoice newInvoice) throws InvoiceNotFoundException, IOException {
    return database.updateInvoice(newInvoice);
  }

  public List<Invoice> getInvoices() throws IOException {
    return database.getInvoices();
  }

  public List<Invoice> getInvoicesWithinDateRange(LocalDate fromDate, LocalDate toDate) throws IOException {
    return database.getInvoicesWithinDateRange(fromDate, toDate);
  }

  public void deleteInvoice(int id) throws IOException, InvoiceNotFoundException {
    database.deleteInvoice(id);
  }
}
