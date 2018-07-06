package pl.invoice.service.pdf;

import static pl.invoice.service.pdf.PdfElement.TEXT_ADDRESS;
import static pl.invoice.service.pdf.PdfElement.TEXT_BANK;
import static pl.invoice.service.pdf.PdfElement.TEXT_BUYER;
import static pl.invoice.service.pdf.PdfElement.TEXT_DESCRIPTION;
import static pl.invoice.service.pdf.PdfElement.TEXT_DUE_DATE;
import static pl.invoice.service.pdf.PdfElement.TEXT_GROSS;
import static pl.invoice.service.pdf.PdfElement.TEXT_INVOICE;
import static pl.invoice.service.pdf.PdfElement.TEXT_ISSUE_DATE;
import static pl.invoice.service.pdf.PdfElement.TEXT_NET;
import static pl.invoice.service.pdf.PdfElement.TEXT_PAID;
import static pl.invoice.service.pdf.PdfElement.TEXT_PHONE;
import static pl.invoice.service.pdf.PdfElement.TEXT_PRODUCTS;
import static pl.invoice.service.pdf.PdfElement.TEXT_QUANTITY;
import static pl.invoice.service.pdf.PdfElement.TEXT_SELLER;
import static pl.invoice.service.pdf.PdfElement.TEXT_TOTAL_GROSS;
import static pl.invoice.service.pdf.PdfElement.TEXT_TOTAL_NET;
import static pl.invoice.service.pdf.PdfElement.TEXT_TOTAL_VAT;
import static pl.invoice.service.pdf.PdfElement.TEXT_UNIT_PRICE;
import static pl.invoice.service.pdf.PdfElement.TEXT_VAT;
import static pl.invoice.service.pdf.PdfElement.TEXT_VAT_NO;
import static pl.invoice.service.pdf.PdfElement.TEXT_VAT_RATE;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import pl.invoice.model.Invoice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Properties;

@Service
public class PdfGenerator {

  private static final String PDF = "pdf_";
  private static final String EXTENSION = ".properties";

  private static Font boldFontHeader = new Font(FontFamily.HELVETICA, 16, Font.BOLD);
  private static Font boldFont = new Font(FontFamily.HELVETICA, 14, Font.BOLD);
  private static Font normalFont = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.NORMAL);
  private static Font tableFont = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.ITALIC);

  Properties properties;

  public byte[] getPdfDocument(Invoice invoice, String language) throws DocumentException, IOException {
    properties = new Properties();
    properties.load(getClass().getClassLoader().getResourceAsStream(PDF + language + EXTENSION));
    Document document = new Document();
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    PdfWriter.getInstance(document, stream);

    document.open();
    addContent(document, invoice);
    document.close();
    return stream.toByteArray();
  }

  private Paragraph getParagraph(String text, Font font) {
    return new Paragraph(text, font);
  }

  private void addContent(Document document, Invoice invoice) throws DocumentException {
    document.add(getParagraph(getText(TEXT_INVOICE) + invoice.getId() + "\n", boldFontHeader));
    document.add(getParagraph(getText(TEXT_ISSUE_DATE) + invoice.getIssueDate(), normalFont));
    document.add(getParagraph(getText(TEXT_DUE_DATE) + invoice.getDueDate(), normalFont));
    document.add(getParagraph(getText(TEXT_PAID) + invoice.isPaid() + "\n\n", normalFont));
    addCustomerSupplierTable(document, invoice);
    document.add(getParagraph("\n\n", normalFont));
    document.add(getParagraph(getText(TEXT_PRODUCTS) + "\n\n", boldFont));
    addEntryListTable(document, invoice);
    document.add(getParagraph("\n\n", normalFont));
    document.add(getParagraph(getText(TEXT_TOTAL_NET) + round(invoice.getTotalNetAmount()), normalFont));
    document.add(getParagraph(getText(TEXT_TOTAL_VAT) + round(invoice.getTotalVatAmount()), normalFont));
    document.add(getParagraph(getText(TEXT_TOTAL_GROSS) + round(invoice.getTotalGrossAmount()), normalFont));
  }

  private void addCustomerSupplierTable(Document document, Invoice invoice) throws DocumentException {
    PdfPTable tableCustomerSupplier = new PdfPTable(2);

    PdfPCell c1 = new PdfPCell(new Phrase(getText(TEXT_SELLER), tableFont));
    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
    tableCustomerSupplier.addCell(c1);

    c1 = new PdfPCell(new Phrase(getText(TEXT_BUYER), tableFont));
    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
    tableCustomerSupplier.addCell(c1);

    String customer =
        invoice.getCustomer().getName() + "\n"
            + getText(TEXT_VAT_NO) + invoice.getCustomer().getVatNumber() + "\n"
            + getText(TEXT_ADDRESS) + "\n"
            + invoice.getCustomer().getAddress().getStreet() + "\n"
            + invoice.getCustomer().getAddress().getPostalCode() + " \n"
            + invoice.getCustomer().getAddress().getCity() + "\n"
            + invoice.getCustomer().getAddress().getCountry() + "\n"
            + getText(TEXT_PHONE) + invoice.getCustomer().getPhoneNumber() + "\n"
            + getText(TEXT_BANK) + invoice.getCustomer().getBankAccount();

    String supplier =
        invoice.getSupplier().getName() + "\n"
            + getText(TEXT_VAT_NO) + invoice.getSupplier().getVatNumber() + "\n"
            + getText(TEXT_ADDRESS) + "\n"
            + invoice.getSupplier().getAddress().getStreet() + "\n"
            + invoice.getSupplier().getAddress().getPostalCode() + " \n"
            + invoice.getSupplier().getAddress().getCity() + "\n"
            + invoice.getSupplier().getAddress().getCountry() + "\n"
            + getText(TEXT_PHONE) + invoice.getSupplier().getPhoneNumber() + "\n"
            + getText(TEXT_BANK)  + invoice.getSupplier().getBankAccount();

    tableCustomerSupplier.addCell(new PdfPCell(new Phrase(customer, tableFont)));
    tableCustomerSupplier.addCell(new PdfPCell(new Phrase(supplier, tableFont)));
    document.add(tableCustomerSupplier);
  }

  private void addEntryListTable(Document document, Invoice invoice) throws DocumentException {
    PdfPTable tableEntryList = new PdfPTable(7);

    PdfPCell c1 = new PdfPCell(new Phrase(getText(TEXT_DESCRIPTION), tableFont));
    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
    tableEntryList.addCell(c1);

    c1 = new PdfPCell(new Phrase(getText(TEXT_QUANTITY), tableFont));
    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
    tableEntryList.addCell(c1);

    c1 = new PdfPCell(new Phrase(getText(TEXT_UNIT_PRICE), tableFont));
    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
    tableEntryList.addCell(c1);

    c1 = new PdfPCell(new Phrase(getText(TEXT_VAT_RATE), tableFont));
    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
    tableEntryList.addCell(c1);

    c1 = new PdfPCell(new Phrase(getText(TEXT_NET), tableFont));
    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
    tableEntryList.addCell(c1);

    c1 = new PdfPCell(new Phrase(getText(TEXT_VAT), tableFont));
    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
    tableEntryList.addCell(c1);

    c1 = new PdfPCell(new Phrase(getText(TEXT_GROSS), tableFont));
    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
    tableEntryList.addCell(c1);

    tableEntryList.setHeaderRows(1);

    for (int i = 0; i < invoice.getEntryList().size(); i++) {
      tableEntryList.addCell(
          new PdfPCell(new Phrase(invoice.getEntryList().get(i).getDescription(), tableFont)));
      tableEntryList.addCell(
          new PdfPCell(new Phrase(String.valueOf(invoice.getEntryList().get(i).getQuantity()), tableFont)));
      tableEntryList.addCell(
          new PdfPCell(new Phrase(invoice.getEntryList().get(i).getUnitPrice().toString(), tableFont)));
      tableEntryList.addCell(
          new PdfPCell(new Phrase(String.valueOf(invoice.getEntryList().get(i).getVatRate().getRate()), tableFont)));
      tableEntryList.addCell(
          new PdfPCell(new Phrase(round(invoice.getEntryList().get(i).getNetAmount()).toString(), tableFont)));
      tableEntryList.addCell(
          new PdfPCell(new Phrase(round(invoice.getEntryList().get(i).getVatAmount()).toString(), tableFont)));
      tableEntryList.addCell(
          new PdfPCell(new Phrase(round(invoice.getEntryList().get(i).getTotalAmount()).toString(), tableFont)));
    }
    document.add(tableEntryList);
  }

  private String getText(String key) {
    return properties.get(key).toString();
  }

  private BigDecimal round(BigDecimal amount) {
    return amount.setScale(2, RoundingMode.HALF_UP);
  }
}
