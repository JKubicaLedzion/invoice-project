package pl.invoice.model;

import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity(name = "company")
public class Company {

  @NotBlank
  private String name;

  @Embedded
  private Address address;

  @NotNull
  private int phoneNumber;

  @Id
  @Column(unique = true, nullable = false)
  private String vatIdentificationNumber;

  @NotNull
  private String bankAccount;

  public Company() {
  }

  public Company(String name, Address address, int phoneNumber, String vatNumber, String bankAccount) {
    this.name = name;
    this.address = address;
    this.phoneNumber = phoneNumber;
    this.vatIdentificationNumber = vatNumber;
    this.bankAccount = bankAccount;
  }

  @ApiModelProperty(value = "Full name of the company.", required = true)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @ApiModelProperty(required = true)
  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public int getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(int phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  @ApiModelProperty(value = "Unique company identifier.", required = true)
  public String getVatNumber() {
    return vatIdentificationNumber;
  }

  public void setVatNumber(String vatNumber) {
    this.vatIdentificationNumber = vatNumber;
  }

  @ApiModelProperty(required = true)
  public String getBankAccount() {
    return bankAccount;
  }

  public void setBankAccount(String bankAccount) {
    this.bankAccount = bankAccount;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null || getClass() != object.getClass()) {
      return false;
    }
    Company company = (Company) object;
    return phoneNumber == company.phoneNumber
        && Objects.equals(name, company.name)
        && Objects.equals(address, company.address)
        && Objects.equals(vatIdentificationNumber, company.vatIdentificationNumber)
        && Objects.equals(bankAccount, company.bankAccount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, address, phoneNumber, vatIdentificationNumber, bankAccount);
  }
}
