package pl.invoice.service.invoice.soap;

import static org.apache.commons.lang3.StringUtils.isBlank;

import pl.invoice.model.Vat;
import pl.invoice_project.invoices.Address;
import pl.invoice_project.invoices.Company;
import pl.invoice_project.invoices.EntryList;
import pl.invoice_project.invoices.Invoice;
import pl.invoice_project.invoices.InvoiceEntry;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class SoapConverter {

  public static pl.invoice_project.invoices.Invoice invoiceToSoap(pl.invoice.model.Invoice invoice) {
    Invoice soapInvoice = new Invoice();
    soapInvoice.setSupplier(companyToSoap(invoice.getSupplier()));
    soapInvoice.setCustomer(companyToSoap(invoice.getCustomer()));
    soapInvoice.setDueDate(invoice.getDueDate().toString());
    soapInvoice.setIssueDate(invoice.getIssueDate().toString());
    List<InvoiceEntry> modelEntryList =
        invoice
            .getEntryList()
            .stream()
            .filter(invoiceEntry -> Objects.nonNull(invoiceEntry))
            .map(invoiceEntry -> invoiceEntryToSoap(invoiceEntry))
            .collect(Collectors.toList());
    EntryList soapEntryList = new EntryList();
    soapEntryList.getInvoiceEntry().addAll(modelEntryList);
    soapInvoice.setEntryList(soapEntryList);
    soapInvoice.setIsPaid(invoice.isPaid());
    soapInvoice.setId(invoice.getId());
    return soapInvoice;
  }

  private static Company companyToSoap(pl.invoice.model.Company company) {
    Company soapCompany = new Company();
    soapCompany.setName(company.getName());
    soapCompany.setAddress(addressToSoap(company.getAddress()));
    soapCompany.setPhoneNumber(company.getPhoneNumber());
    soapCompany.setVatIdentificationNumber(company.getVatNumber());
    soapCompany.setBankAccount(company.getBankAccount());
    return soapCompany;
  }

  private static Address addressToSoap(pl.invoice.model.Address address) {
    Address soapAddress = new Address();
    soapAddress.setCity(address.getCity());
    soapAddress.setCountry(address.getCountry());
    soapAddress.setPostalCode(address.getPostalCode());
    soapAddress.setStreet(address.getStreet());
    return soapAddress;
  }

  private static InvoiceEntry invoiceEntryToSoap(pl.invoice.model.InvoiceEntry invoiceEntry) {
    InvoiceEntry soapInvoiceEntry = new InvoiceEntry();
    soapInvoiceEntry.setDescription(invoiceEntry.getDescription());
    soapInvoiceEntry.setQuantity(invoiceEntry.getQuantity());
    soapInvoiceEntry.setUnitPrice(invoiceEntry.getUnitPrice().intValue());
    soapInvoiceEntry.setVatRate(invoiceEntry.getVatRate().getRate());
    return soapInvoiceEntry;
  }

  public static pl.invoice.model.Invoice soapToModelInvoice(Invoice invoice) {
    pl.invoice.model.Invoice modelInvoice = new pl.invoice.model.Invoice();
    modelInvoice.setSupplier(soapToModelCompany(invoice.getSupplier()));
    modelInvoice.setCustomer(soapToModelCompany(invoice.getCustomer()));
    modelInvoice.setDueDate(
        isBlank(invoice.getDueDate())
            ? null
            : LocalDate.parse(invoice.getDueDate(), DateTimeFormatter.ISO_DATE));
    modelInvoice.setIssueDate(
        isBlank(invoice.getIssueDate())
            ? null
            : LocalDate.parse(invoice.getIssueDate(), DateTimeFormatter.ISO_DATE));
    List<pl.invoice.model.InvoiceEntry> entryList =
        invoice
            .getEntryList()
            .getInvoiceEntry()
            .stream()
            .filter(invoiceEntry -> Objects.nonNull(invoiceEntry))
            .map(invoiceEntry -> soapToModelInvoiceEntry(invoiceEntry))
            .collect(Collectors.toList());
    modelInvoice.setEntryList(entryList);
    modelInvoice.setPaid(invoice.isIsPaid());
    modelInvoice.setId(invoice.getId());
    return modelInvoice;
  }

  private static pl.invoice.model.Company soapToModelCompany(Company company) {
    pl.invoice.model.Company soapCompany = new pl.invoice.model.Company();
    soapCompany.setName(company.getName());
    soapCompany.setAddress(soapToModelAddress(company.getAddress()));
    soapCompany.setPhoneNumber(company.getPhoneNumber());
    soapCompany.setVatNumber(company.getVatIdentificationNumber());
    soapCompany.setBankAccount(company.getBankAccount());
    return soapCompany;
  }

  private static pl.invoice.model.Address soapToModelAddress(Address address) {
    pl.invoice.model.Address modelAddress = new pl.invoice.model.Address();
    modelAddress.setCity(address.getCity());
    modelAddress.setCountry(address.getCountry());
    modelAddress.setPostalCode(address.getPostalCode());
    modelAddress.setStreet(address.getStreet());
    return modelAddress;
  }

  private static pl.invoice.model.InvoiceEntry soapToModelInvoiceEntry(
      InvoiceEntry invoiceEntry) {
    pl.invoice.model.InvoiceEntry modelInvoiceEntry = new pl.invoice.model.InvoiceEntry();
    modelInvoiceEntry.setDescription(invoiceEntry.getDescription());
    modelInvoiceEntry.setQuantity(invoiceEntry.getQuantity());
    modelInvoiceEntry.setUnitPrice(BigDecimal.valueOf(invoiceEntry.getUnitPrice()));
    modelInvoiceEntry.setVatRate(Vat.getByValue(invoiceEntry.getVatRate()));
    return modelInvoiceEntry;
  }
}