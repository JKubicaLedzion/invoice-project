package pl.invoice.database.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.invoice.model.Invoice;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Service
public final class FileHelper {

  private final JsonConverter jsonConverter;

  @Autowired
  public FileHelper(JsonConverter jsonConverter) {
    this.jsonConverter = jsonConverter;
  }

  public int saveInvoiceToFile(Invoice invoice, File file) throws IOException {
    String invoiceAsString = jsonConverter.convertInvoiceToJson(invoice);
    Files.write(file.toPath(), (invoiceAsString + System.lineSeparator()).getBytes(Charset.forName("UTF-8")),
        StandardOpenOption.APPEND);
    return invoice.getId();
  }

  public List<Invoice> readInvoicesFromFile(File file) throws IOException {
    List<Invoice> invoiceList = new ArrayList<>();
    try (Scanner sc = new Scanner(file)) {
      while (sc.hasNext()) {
        Invoice invoice = jsonConverter.convertJsonToInvoice(sc.nextLine());
        invoiceList.add(invoice);
      }
      return invoiceList;
    }
  }
}