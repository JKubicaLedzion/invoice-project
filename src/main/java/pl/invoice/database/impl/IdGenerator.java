package pl.invoice.database.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.invoice.DbPropertiesConfig;
import pl.invoice.database.impl.multifile.PathGenerator;
import pl.invoice.model.Invoice;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class IdGenerator {

  private static AtomicInteger currentId;
  private FileHelper fileHelper;
  private PathGenerator pathGenerator;

  @Autowired
  public IdGenerator(DbPropertiesConfig dbPropertiesConfig, FileHelper fileHelper, PathGenerator pathGenerator)
      throws IOException {
    this.fileHelper = fileHelper;
    this.pathGenerator = pathGenerator;
    currentId = new AtomicInteger(getCurrentId(dbPropertiesConfig).orElseGet(() -> 0));
  }

  public int generateNextId() {
    currentId.incrementAndGet();
    return currentId.get();
  }

  private OptionalInt getCurrentId(DbPropertiesConfig dbPropertiesConfig) throws IOException {
    if (dbPropertiesConfig.getDatabase().equals("inFile")) {
      String path = dbPropertiesConfig.getFilePath();
      if (!Files.exists(Paths.get(path))) {
        Files.createFile(Paths.get(path));
      }
      return getCurrentIdFromOneFile(path);
    }
    if (dbPropertiesConfig.getDatabase().equals("multiFile")) {
      return getCurrentIdFromMultiFile(dbPropertiesConfig.getMultiFileRootFolder());
    }
    return OptionalInt.empty();
  }

  private OptionalInt getCurrentIdFromOneFile(String path) throws IOException {
    return fileHelper
        .readInvoicesFromFile(new File(path))
        .stream()
        .mapToInt(inv -> inv.getId())
        .max();
  }

  private OptionalInt getCurrentIdFromMultiFile(String path) throws IOException {
    List<Invoice> invoiceList = new ArrayList<>();
    if (!Files.exists(Paths.get(path))) {
      Files.createDirectory(Paths.get(path));
    }
    List<File> files = pathGenerator.listFiles(path);
    for (File file : files) {
      invoiceList.addAll(fileHelper.readInvoicesFromFile(file));
    }
    return invoiceList.stream()
        .mapToInt(inv -> inv.getId())
        .max();
  }
}
