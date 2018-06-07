package pl.invoice.database.impl.memory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import pl.invoice.database.Database;
import pl.invoice.exception.InvoiceNotFoundException;
import pl.invoice.model.Invoice;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
@ConditionalOnProperty(name = "pl.invoice.database.Database", havingValue = "inMemory")
public class InMemoryDatabase implements Database {

  private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryDatabase.class);

  private static AtomicInteger currentId;
  private List<Invoice> invoiceList;

  public InMemoryDatabase() {
    this.invoiceList = new ArrayList<>();
    currentId = new AtomicInteger();
  }


  @Override
  public int saveInvoice(Invoice invoice) {
    if (invoice.getId() == 0) {
      invoice.setId(generateNextId());
    }
    invoiceList.add(invoice);
    return invoice.getId();
  }

  @Override
  public Optional<Invoice> getInvoiceById(int id) {
    int index = getIndexOfInvoice(id);
    if (index == -1) {
      return Optional.empty();
    }
    return Optional.of(invoiceList.get(index));
  }

  @Override
  public int updateInvoice(Invoice invoice) throws InvoiceNotFoundException {
    if (getInvoices().stream()
        .noneMatch(inv -> inv.getId() == invoice.getId())) {
      LOGGER.error("Invoice {} not found.", invoice);
      throw new InvoiceNotFoundException("Invoice not in database.");
    }
    deleteInvoice(invoice.getId());
    return saveInvoice(invoice);
  }

  @Override
  public List<Invoice> getInvoices() {
    invoiceList.sort(Comparator.comparingInt(Invoice::getId));
    return invoiceList;
  }

  private int getIndexOfInvoice(int id) {
    int index = -1;
    for (int i = 0; i < invoiceList.size(); i++) {
      if (invoiceList.get(i).getId() == id) {
        index = i;
      }
    }
    return index;
  }

  @Override
  public void deleteInvoice(int id) throws InvoiceNotFoundException {
    if (getInvoices().stream()
        .noneMatch(inv -> inv.getId() == id)) {
      LOGGER.error("Invoice with id {} not found.", id);
      throw new InvoiceNotFoundException("Invoice not in database.");
    }
    invoiceList.remove(getIndexOfInvoice(id));
  }

  public int generateNextId() {
    return currentId.incrementAndGet();
  }
}
