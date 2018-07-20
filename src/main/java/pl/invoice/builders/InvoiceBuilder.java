package pl.invoice.builders;

import pl.invoice.model.Company;
import pl.invoice.model.Invoice;
import pl.invoice.model.InvoiceEntry;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class InvoiceBuilder {

  private Invoice invoice;

  public InvoiceBuilder() {
    invoice = new Invoice();
  }

  public InvoiceBuilder id(int id) {
    invoice.setId(id);
    return this;
  }

  public InvoiceBuilder customer(Company customer) {
    invoice.setCustomer(customer);
    return this;
  }

  public InvoiceBuilder supplier(Company customer) {
    invoice.setSupplier(customer);
    return this;
  }

  public InvoiceBuilder entryList(List<InvoiceEntry> entryList) {
    invoice.setEntryList(entryList);
    return this;
  }

  public InvoiceBuilder issueDate(LocalDate issueDate) {
    invoice.setIssueDate(issueDate);
    return this;
  }

  public InvoiceBuilder dueDate(LocalDate dueDate) {
    invoice.setDueDate(dueDate);
    return this;
  }

  public InvoiceBuilder isPaid(boolean isPaid) {
    invoice.setPaid(isPaid);
    return this;
  }

  public InvoiceBuilder modificationDate(LocalDateTime modificationDate) {
    invoice.setModificationTime(modificationDate);
    return this;
  }

  public Invoice build() {
    return invoice;
  }
}