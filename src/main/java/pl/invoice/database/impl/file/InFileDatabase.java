package pl.invoice.database.impl.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import pl.invoice.DbPropertiesConfig;
import pl.invoice.database.Database;
import pl.invoice.database.impl.FileHelper;
import pl.invoice.database.impl.IdGenerator;
import pl.invoice.exception.InvoiceNotFoundException;
import pl.invoice.model.Invoice;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
@ConditionalOnProperty(name = "pl.invoice.database.Database", havingValue = "inFile")
public class InFileDatabase implements Database {

  private static final Logger LOGGER = LoggerFactory.getLogger(InFileDatabase.class);

  private FileHelper fileHelper;
  private IdGenerator idGenerator;
  private String path;
  private File file;

  @Autowired
  public InFileDatabase(DbPropertiesConfig dbPropertiesConfig, IdGenerator idGenerator, FileHelper fileHelper)
      throws IOException {
    this.fileHelper = fileHelper;
    this.idGenerator = idGenerator;
    this.path = dbPropertiesConfig.getFilePath();
    createDatabaseFile();
  }

  private void createDatabaseFile() throws IOException {
    file = new File(path);
    if (!file.exists()) {
      file.createNewFile();
    }
  }

  @Override
  public int saveInvoice(Invoice invoice) throws IOException {
    LOGGER.info("Saving invoice {} to file {}.", invoice, file);
    invoice.setId(idGenerator.generateNextId());
    return fileHelper.saveInvoiceToFile(invoice, file);
  }

  @Override
  public int updateInvoice(Invoice invoice) throws IOException, InvoiceNotFoundException {
    LOGGER.info("Updating invoice {} in file {}.", invoice, file);
    ifInvoiceNotFoundThrowException(invoice.getId());
    deleteInvoice(invoice.getId());
    return fileHelper.saveInvoiceToFile(invoice, file);
  }

  @Override
  public List<Invoice> getInvoices() throws IOException {
    List<Invoice> invoiceList = fileHelper.readInvoicesFromFile(file);
    invoiceList.sort(Comparator.comparingInt(Invoice::getId));
    return invoiceList;
  }

  @Override
  public Optional<Invoice> getInvoiceById(int id) throws IOException {
    return getInvoices().stream().filter(inv -> inv.getId() == id).findFirst();
  }

  @Override
  public void deleteInvoice(int id) throws InvoiceNotFoundException, IOException {
    LOGGER.info("Deleting invoice with id {} from file {}.", id, file);
    ifInvoiceNotFoundThrowException(id);

    List<Invoice> invoiceList = getInvoices();

    file.delete();
    file.createNewFile();

    invoiceList
        .stream()
        .filter(invoice -> invoice.getId() != id)
        .forEach(
            invoice -> {
              try {
                fileHelper.saveInvoiceToFile(invoice, file);
              } catch (IOException ex) {
                LOGGER.error(
                    "Error while saving invoice to file {}. Invoice: {}", file, invoice, ex);
              }
            });
  }

  private void ifInvoiceNotFoundThrowException(int id) throws IOException, InvoiceNotFoundException {
    if (getInvoices().stream().noneMatch(inv -> inv.getId() == id)) {
      LOGGER.error("Invoice with id {} not found.", id);
      throw new InvoiceNotFoundException("Invoice not in database.");
    }
  }
}
