package pl.invoice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import pl.invoice.builders.AddressBuilder;
import pl.invoice.builders.CompanyBuilder;
import pl.invoice.builders.InvoiceBuilder;
import pl.invoice.model.Address;
import pl.invoice.model.Company;
import pl.invoice.model.Invoice;
import pl.invoice.model.InvoiceEntry;
import pl.invoice.model.Vat;
import pl.invoice.service.tax.CalculationResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class TestInvoiceGenerator {

  private static ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

  public static Invoice getFirstTestInvoice() {

    Address addressCustomer =
        new AddressBuilder()
            .street("Street")
            .postalCode("1111")
            .city("City")
            .country("PL")
            .build();

    Address addressSupplier =
        new AddressBuilder()
            .street("Street")
            .postalCode("1111")
            .city("City")
            .country("PL")
            .build();

    Company customer =
        new CompanyBuilder()
            .address(addressCustomer)
            .name("Customer")
            .bankAccount("PL123456789")
            .phoneNumber(1111111)
            .vatNo("PL000000")
            .build();

    Company supplier =
        new CompanyBuilder()
            .address(addressSupplier)
            .name("Supplier")
            .bankAccount("PL987654321")
            .phoneNumber(2222222)
            .vatNo("PL222222")
            .build();

    List<InvoiceEntry> entryList = new ArrayList<>();
    entryList.add(new InvoiceEntry(1, 1, "Groceries", BigDecimal.valueOf(100.00), Vat.VAT_23));

    return new InvoiceBuilder()
        .customer(customer)
        .supplier(supplier)
        .entryList(entryList)
        .issueDate(LocalDate.of(2017, 12, 12))
        .dueDate(LocalDate.of(2018, 01, 31))
        .modificationDate(LocalDateTime.of(2018, 12, 12, 16, 30, 1))
        .isPaid(true)
        .id(1)
        .build();
  }

  public static Invoice getSecondTestInvoice() {

    Address addressCustomer =
        new AddressBuilder()
            .street("Street2")
            .postalCode("1111")
            .city("City")
            .country("PL")
            .build();

    Address addressSupplier =
        new AddressBuilder()
            .street("Street2")
            .postalCode("2222")
            .city("City2")
            .country("PL")
            .build();

    Company customer =
        new CompanyBuilder()
            .address(addressCustomer)
            .name("Customer2")
            .bankAccount("PL123456789")
            .phoneNumber(1111111)
            .vatNo("PL000005")
            .build();

    Company supplier =
        new CompanyBuilder()
            .address(addressSupplier)
            .name("Supplier2")
            .bankAccount("PL987654321")
            .phoneNumber(2222222)
            .vatNo("PL222221")
            .build();

    List<InvoiceEntry> entryList = new ArrayList<>();
    entryList.add(new InvoiceEntry(2, 1, "Groceries", BigDecimal.valueOf(200.00), Vat.VAT_23));

    return new InvoiceBuilder()
        .customer(customer)
        .supplier(supplier)
        .issueDate(LocalDate.of(2017, 12, 30))
        .dueDate(LocalDate.of(2018, 01, 31))
        .modificationDate(LocalDateTime.of(2018, 12, 12, 16, 30, 1))
        .entryList(entryList)
        .isPaid(true)
        .id(2)
        .build();
  }

  public static Invoice getThirdTestInvoice() {

    Address addressCustomer =
        new AddressBuilder()
            .street("Street4")
            .postalCode("7777")
            .city("City7")
            .country("PL")
            .build();

    Address addressSupplier =
        new AddressBuilder()
            .street("Street5")
            .postalCode("3333")
            .city("City3")
            .country("DE")
            .build();

    Company customer =
        new CompanyBuilder()
            .address(addressCustomer)
            .name("Customer3")
            .bankAccount("PL123456789")
            .phoneNumber(1111111)
            .vatNo("PL000003")
            .build();

    Company supplier =
        new CompanyBuilder()
            .address(addressSupplier)
            .name("Supplier3")
            .bankAccount("PL987654325")
            .phoneNumber(2222222)
            .vatNo("PL000000")
            .build();

    List<InvoiceEntry> entryList = new ArrayList<>();
    entryList.add(new InvoiceEntry(3, 1, "Bread", BigDecimal.valueOf(1500.00), Vat.VAT_23));

    return new InvoiceBuilder()
        .customer(customer)
        .supplier(supplier)
        .issueDate(LocalDate.of(2017, 11, 29))
        .dueDate(LocalDate.of(2018, 01, 31))
        .modificationDate(LocalDateTime.of(2018, 12, 12, 16, 30, 1))
        .entryList(entryList)
        .isPaid(false)
        .id(3)
        .build();
  }

  public static Invoice getForthTestInvoice() {

    Address addressCustomer =
        new AddressBuilder()
            .street("Street2")
            .postalCode("1111")
            .city("City")
            .country("PL")
            .build();

    Address addressSupplier =
        new AddressBuilder()
            .street("Street2")
            .postalCode("2222")
            .city("City2")
            .country("PL")
            .build();

    Company customer =
        new CompanyBuilder()
            .address(addressCustomer)
            .name("Customer2")
            .bankAccount("PL123456789")
            .phoneNumber(1111111)
            .vatNo("PL000000")
            .build();

    Company supplier =
        new CompanyBuilder()
            .address(addressSupplier)
            .name("Supplier2")
            .bankAccount("PL987654321")
            .phoneNumber(2222222)
            .vatNo("PL222221")
            .build();

    List<InvoiceEntry> entryList = new ArrayList<>();
    entryList.add(new InvoiceEntry(2, 1, "Groceries", BigDecimal.valueOf(200.00), Vat.VAT_23));

    return new InvoiceBuilder()
        .customer(customer)
        .supplier(supplier)
        .issueDate(LocalDate.of(2017, 12, 30))
        .dueDate(LocalDate.of(2018, 01, 31))
        .modificationDate(LocalDateTime.of(2018, 12, 12, 16, 30, 1))
        .entryList(entryList)
        .isPaid(true)
        .id(2)
        .build();
  }

  public static Invoice getUpdatedFirstInvoice() {

    Address addressCustomer =
        new AddressBuilder()
            .street("Street3")
            .postalCode("1111")
            .city("City")
            .country("PL")
            .build();

    Address addressSupplier =
        new AddressBuilder()
            .street("Street3")
            .postalCode("2222")
            .city("City2")
            .country("PL")
            .build();

    Company customer =
        new CompanyBuilder()
            .address(addressCustomer)
            .name("Customer3")
            .bankAccount("PL123456789")
            .phoneNumber(1111111)
            .vatNo("PL000000")
            .build();

    Company supplier =
        new CompanyBuilder()
            .address(addressSupplier)
            .name("Supplier3")
            .bankAccount("PL987654321")
            .phoneNumber(2222222)
            .vatNo("PL222222")
            .build();

    List<InvoiceEntry> entryList = new ArrayList<>();
    entryList.add(new InvoiceEntry(1, 1, "Groceries", BigDecimal.valueOf(200.00), Vat.VAT_23));

    return new InvoiceBuilder()
        .customer(customer)
        .supplier(supplier)
        .entryList(entryList)
        .issueDate(LocalDate.of(2017, 11, 29))
        .dueDate(LocalDate.of(2018, 01, 31))
        .modificationDate(LocalDateTime.of(2018, 12, 12, 16, 30, 1))
        .isPaid(true)
        .id(1)
        .build();
  }

  public static Invoice getInvoiceWithoutRequiredData() {

    Address addressCustomer =
        new AddressBuilder()
            .postalCode("1111")
            .city("City")
            .country("PL")
            .build();

    Address addressSupplier =
        new AddressBuilder()
            .street("Street")
            .postalCode("2222")
            .city("City2")
            .build();

    Company customer =
        new CompanyBuilder()
            .address(addressCustomer)
            .name("Customer")
            .phoneNumber(1111111)
            .vatNo("PL000000")
            .build();

    Company supplier =
        new CompanyBuilder()
            .address(addressSupplier)
            .name("Supplier")
            .bankAccount("PL987654321")
            .phoneNumber(2222222)
            .build();

    List<InvoiceEntry> entryList = new ArrayList<>();
    entryList.add(new InvoiceEntry(1, 1, "Groceries", BigDecimal.valueOf(100.00), Vat.VAT_23));

    return new InvoiceBuilder()
        .customer(customer)
        .supplier(supplier)
        .entryList(entryList)
        .dueDate(LocalDate.of(2018, 01, 31))
        .modificationDate(LocalDateTime.of(2018, 12, 12, 16, 30, 1))
        .id(1)
        .build();
  }

  public static List<InvoiceEntry> getInvoiceEntry() {
    List<InvoiceEntry> invoiceEntryList = new ArrayList<>();
    InvoiceEntry firstEntry =
        new InvoiceEntry(1, 1, "Phone", BigDecimal.valueOf(4000.00), Vat.VAT_23);
    InvoiceEntry secondEntry =
        new InvoiceEntry(2, 1, "Shirt", BigDecimal.valueOf(200.00), Vat.VAT_23);
    invoiceEntryList.add(firstEntry);
    invoiceEntryList.add(secondEntry);
    return invoiceEntryList;
  }

  public static String getJsonFormatStringForFirstInv() throws JsonProcessingException {
    return mapper.writeValueAsString(getFirstTestInvoice());
  }

  public static String getJsonFormatStringForSecondInv() throws JsonProcessingException {
    return mapper.writeValueAsString(getSecondTestInvoice());
  }

  public static String getJsonFormatStringForUpdatedFirstInv() throws JsonProcessingException {
    return mapper.writeValueAsString(getUpdatedFirstInvoice());
  }

  public static String getJsonFormatStringForIncorrectInv() throws JsonProcessingException {
    return mapper.writeValueAsString(getInvoiceWithoutRequiredData());
  }

  public static String getJsonFormatStringForThirdInv() throws JsonProcessingException {
    return mapper.writeValueAsString(getThirdTestInvoice());
  }

  public static String getJsonFormatForCalculationResult(CalculationResult calculationResult)
      throws JsonProcessingException {
    return mapper.writeValueAsString(calculationResult);
  }
}
