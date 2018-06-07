package pl.invoice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity(name = "invoice_entry")
public class InvoiceEntry {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @NotNull
  private double quantity;

  @NotNull
  private String description;

  @NotNull
  private BigDecimal unitPrice;

  @Enumerated(EnumType.STRING)
  private Vat vatRate;

  public InvoiceEntry() {
  }

  public InvoiceEntry(int id, double quantity, String description, BigDecimal unitPrice, Vat vat) {
    this.id = id;
    this.quantity = quantity;
    this.description = description;
    this.unitPrice = unitPrice;
    this.vatRate = vat;
  }

  @ApiModelProperty(example = "15.50", required = true)
  public double getQuantity() {
    return quantity;
  }

  public void setQuantity(double quantity) {
    this.quantity = quantity;
  }

  @ApiModelProperty(required = true)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @ApiModelProperty(example = "123.99", required = true)
  public BigDecimal getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(BigDecimal unitPrice) {
    this.unitPrice = unitPrice;
  }

  @ApiModelProperty(required = true)
  public Vat getVatRate() {
    return vatRate;
  }

  public void setVatRate(Vat vatRate) {
    this.vatRate = vatRate;
  }

  @ApiModelProperty("Autoincremented unique number of invoice entry assigned by SQL / Hibernate database.")
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @JsonIgnore
  public BigDecimal getVatAmount() {
    return getNetAmount().multiply(BigDecimal.valueOf(vatRate.getRate()));
  }

  @JsonIgnore
  public BigDecimal getNetAmount() {
    return unitPrice.multiply(BigDecimal.valueOf(quantity));
  }

  @JsonIgnore
  public BigDecimal getTotalAmount() {
    return getNetAmount().add(getVatAmount());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    InvoiceEntry that = (InvoiceEntry) obj;
    return id == that.id
        && Double.compare(that.quantity, quantity) == 0
        && Objects.equals(description, that.description)
        && unitPrice.compareTo(that.unitPrice) == 0
        && vatRate == that.vatRate;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, quantity, description, unitPrice, vatRate);
  }
}
