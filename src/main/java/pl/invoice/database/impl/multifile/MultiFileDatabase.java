package pl.invoice.database.impl.multifile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import pl.invoice.configuration.DbPropertiesConfig;
import pl.invoice.database.Database;
import pl.invoice.database.impl.FileHelper;
import pl.invoice.database.impl.IdGenerator;
import pl.invoice.exception.InvoiceNotFoundException;
import pl.invoice.model.Invoice;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@ConditionalOnProperty(name = "pl.invoice.database.Database", havingValue = "multiFile")
public class MultiFileDatabase implements Database {

  private static final Logger LOGGER = LoggerFactory.getLogger(MultiFileDatabase.class);

  private FileHelper fileHelper;
  private PathGenerator pathGenerator;
  private IdGenerator idGenerator;
  private Map<Integer, String> fileCache;
  private String path;

  @Autowired
  public MultiFileDatabase(
      DbPropertiesConfig dbPropertiesConfig,
      FileHelper fileHelper,
      IdGenerator idGenerator,
      PathGenerator pathGenerator)
      throws IOException {
    this.fileHelper = fileHelper;
    this.pathGenerator = pathGenerator;
    this.idGenerator = idGenerator;
    this.path = dbPropertiesConfig.getMultiFileRootFolder();
    this.fileCache = initializeFileCache();
  }

  @Override
  public int saveInvoice(Invoice invoice) throws IOException {
    createPathToDirectoryIfNotExists(invoice);
    invoice.setId(idGenerator.generateNextId());

    String filePath = createPathToFileIfNotExists(invoice);
    LOGGER.info("Saving invoice {} to file {}.", invoice, filePath);
    return fileHelper.saveInvoiceToFile(invoice, new File(filePath));
  }

  @Override
  public int updateInvoice(Invoice invoice) throws InvoiceNotFoundException, IOException {
    LOGGER.info("Updating invoice {}.", invoice);
    isInvoiceInDatabase(invoice.getId());
    deleteInvoice(invoice.getId());

    createPathToDirectoryIfNotExists(invoice);
    return fileHelper.saveInvoiceToFile(invoice, new File(createPathToFileIfNotExists(invoice)));
  }

  @Override
  public List<Invoice> getInvoices() throws IOException {
    List<Invoice> invoiceList = new ArrayList<>();
    if (!Files.exists(Paths.get(path))) {
      Files.createDirectory(Paths.get(path));
    }
    List<File> files = pathGenerator.listFiles(path);
    for (File file : files) {
      invoiceList.addAll(fileHelper.readInvoicesFromFile(file));
    }

    invoiceList.sort(Comparator.comparingInt(Invoice::getId));
    return invoiceList;
  }

  @Override
  public Optional<Invoice> getInvoiceById(int id) throws IOException {
    return Objects.isNull(fileCache.get(id))
        ? Optional.empty()
        : fileHelper
            .readInvoicesFromFile(new File(fileCache.get(id)))
            .stream()
            .filter(inv -> inv.getId() == id)
            .findFirst();
  }

  @Override
  public void deleteInvoice(int id) throws InvoiceNotFoundException, IOException {
    isInvoiceInDatabase(id);

    File file = new File(fileCache.get(id));
    LOGGER.info("Deleting invoice with id {} from file {}.", id, file);
    List<Invoice> invoiceList = fileHelper.readInvoicesFromFile(file);

    fileCache.remove(id);
    file.delete();

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

  private void isInvoiceInDatabase(int id) throws InvoiceNotFoundException {
    if (!fileCache.containsKey(id)) {
      LOGGER.error("Invoice with id {} not found.", id);
      throw new InvoiceNotFoundException("Invoice not in database.");
    }
  }

  private Map<Integer, String> initializeFileCache() throws IOException {
    return getInvoices()
        .stream()
        .collect(Collectors.toMap(i -> i.getId(), i -> pathGenerator.getPathToFile(i)));
  }

  private void createPathToDirectoryIfNotExists(Invoice invoice) throws IOException {
    String pathToDirectory = pathGenerator.getPathToDirectory(invoice);
    if (!pathGenerator.pathExists(pathToDirectory)) {
      pathGenerator.createFolder(pathToDirectory);
    }
  }

  private String createPathToFileIfNotExists(Invoice invoice) throws IOException {
    String filePath = pathGenerator.getPathToFile(invoice);
    if (!pathGenerator.pathExists(filePath)) {
      pathGenerator.createFile(filePath);
    }
    fileCache.put(invoice.getId(), filePath);
    return filePath;
  }
}
