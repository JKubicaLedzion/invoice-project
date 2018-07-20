package pl.invoice.database;

import pl.invoice.exception.InvoiceNotFoundException;
import pl.invoice.model.Invoice;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface Database {

  int saveInvoice(Invoice invoice) throws IOException;

  Optional<Invoice> getInvoiceById(int id) throws IOException;

  int updateInvoice(Invoice invoice) throws InvoiceNotFoundException, IOException;

  List<Invoice> getInvoices() throws IOException;

  default List<Invoice> getInvoicesWithinDateRange(LocalDate fromDate, LocalDate toDate) throws IOException {
    return getInvoices().stream()
        .filter(invoice -> invoice.getIssueDate().isAfter(fromDate) && invoice.getIssueDate().isBefore(toDate))
        .collect(Collectors.toList());
  }

  void deleteInvoice(int id) throws InvoiceNotFoundException, IOException;

  default List<Invoice> getLastUpdatedInvoices(LocalDateTime fromDate, LocalDateTime toDate) throws IOException {
    return getInvoices().stream()
        .filter(invoice -> invoice.getModificationTime().isAfter(fromDate)
            && invoice.getModificationTime().isBefore(toDate))
        .collect(Collectors.toList());
  }
}
