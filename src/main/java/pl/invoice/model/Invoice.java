package pl.invoice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.validation.constraints.NotNull;

@Entity(name = "invoice")
public class Invoice {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @ManyToOne
  private Company supplier;

  @ManyToOne
  private Company customer;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JoinColumn(name = "invoice", referencedColumnName = "id")
  @OrderColumn
  private List<InvoiceEntry> entryList;

  @NotNull
  private LocalDate issueDate;

  @NotNull
  private LocalDate dueDate;

  @NotNull
  private boolean isPaid;

  public Invoice(Company supplier, Company customer, List<InvoiceEntry> entryList, LocalDate issueDate,
      LocalDate dueDate, boolean isPaid) {
    this.supplier = supplier;
    this.customer = customer;
    this.entryList = entryList;
    this.issueDate = issueDate;
    this.dueDate = dueDate;
    this.isPaid = isPaid;
  }

  public Invoice() {
  }

  @ApiModelProperty(value = "Autoincremented unique number of invoice.")
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @ApiModelProperty(value = "Seller details: address, phone number, bank account, VAT number.", required = true)
  public Company getSupplier() {
    return supplier;
  }

  public void setSupplier(Company supplier) {
    this.supplier = supplier;
  }

  @ApiModelProperty(value = "Buyer details: address, phone number, bank account, VAT number.", required = true)
  public Company getCustomer() {
    return customer;
  }

  public void setCustomer(Company customer) {
    this.customer = customer;
  }

  @ApiModelProperty(value = "List of products / services.", required = true)
  public List<InvoiceEntry> getEntryList() {
    return entryList;
  }

  public void setEntryList(List<InvoiceEntry> entryList) {
    this.entryList = entryList;
  }

  @ApiModelProperty(value = "Date in format: yyyy-mm-dd.", example = "2999-01-01", required = true)
  public LocalDate getIssueDate() {
    return issueDate;
  }

  public void setIssueDate(LocalDate issueDate) {
    this.issueDate = issueDate;
  }

  @ApiModelProperty(value = "Date in format: yyyy-mm-dd.", example = "2999-01-01", required = true)
  public LocalDate getDueDate() {
    return dueDate;
  }

  public void setDueDate(LocalDate dueDate) {
    this.dueDate = dueDate;
  }

  @ApiModelProperty(value = "Invoice paid/invoice not paid indicator.", required = true)
  public boolean isPaid() {
    return isPaid;
  }

  public void setPaid(boolean paid) {
    isPaid = paid;
  }

  @JsonIgnore
  public BigDecimal getTotalNetAmount() {
    return entryList.stream().map(InvoiceEntry::getNetAmount)
        .reduce(new BigDecimal(0), (amountFirst, amountSecond) -> amountFirst.add(amountSecond));
  }

  @JsonIgnore
  public BigDecimal getTotalVatAmount() {
    return entryList.stream().map(InvoiceEntry::getVatAmount)
        .reduce(new BigDecimal(0), (amountFirst, amountSecond) -> amountFirst.add(amountSecond));
  }

  @JsonIgnore
  public BigDecimal getTotalGrossAmount() {
    return entryList.stream().map(InvoiceEntry::getTotalAmount)
        .reduce(new BigDecimal(0), (amountFirst, amountSecond) -> amountFirst.add(amountSecond));
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null || getClass() != object.getClass()) {
      return false;
    }
    Invoice invoice = (Invoice) object;
    return id == invoice.id
        && isPaid == invoice.isPaid
        && Objects.equals(supplier, invoice.supplier)
        && Objects.equals(customer, invoice.customer)
        && Objects.equals(entryList, invoice.entryList)
        && Objects.equals(issueDate, invoice.issueDate)
        && Objects.equals(dueDate, invoice.dueDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, supplier, customer, entryList, issueDate, dueDate, isPaid);
  }
}

