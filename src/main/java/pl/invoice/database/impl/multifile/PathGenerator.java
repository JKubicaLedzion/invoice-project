package pl.invoice.database.impl.multifile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.invoice.configuration.DbPropertiesConfig;
import pl.invoice.model.Invoice;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class PathGenerator {

  private static final String EXTENSION_SUFFIX = ".json";

  private String rootPath;

  @Autowired
  public PathGenerator(DbPropertiesConfig dbPropertiesConfig) {
    this.rootPath = dbPropertiesConfig.getMultiFileRootFolder();
  }

  private String getInvoiceYear(Invoice invoice) {
    return String.valueOf(invoice.getIssueDate().getYear());
  }

  private String getInvoiceMonth(Invoice invoice) {
    return invoice.getIssueDate().getMonth().toString().toLowerCase();
  }

  private String getInvoiceDay(Invoice invoice) {
    return String.valueOf(invoice.getIssueDate().getDayOfMonth());
  }

  public Path createFolder(String path) throws IOException {
    Path directory = Paths.get(path);
    return Files.createDirectories(directory);
  }

  public boolean createFile(String path) throws IOException {
    File file = new File(path);
    return file.createNewFile();
  }

  public String getPathToDirectory(Invoice invoice) {
    return new StringBuilder(rootPath)
        .append(getInvoiceYear(invoice) + "/")
        .append(getInvoiceMonth(invoice) + "/")
        .toString();
  }

  public String getPathToFile(Invoice invoice) {
    return new StringBuilder(getPathToDirectory(invoice))
        .append(getInvoiceDay(invoice) + EXTENSION_SUFFIX)
        .toString();
  }

  public List<File> listFiles(String path) {
    File source = new File(path);
    return (List<File>) FileUtils.listFiles(source, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
  }

  public boolean pathExists(String path) {
    return Files.exists(Paths.get(path));
  }

}
