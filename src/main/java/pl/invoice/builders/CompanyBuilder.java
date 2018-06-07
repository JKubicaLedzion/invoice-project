package pl.invoice.builders;

import pl.invoice.model.Address;
import pl.invoice.model.Company;

public class CompanyBuilder {

  private Company company;

  public CompanyBuilder() {
    company = new Company();
  }

  public CompanyBuilder name(String name) {
    company.setName(name);
    return this;
  }

  public CompanyBuilder address(Address address) {
    company.setAddress(address);
    return this;
  }

  public CompanyBuilder phoneNumber(int phoneNumber) {
    company.setPhoneNumber(phoneNumber);
    return this;
  }

  public CompanyBuilder vatNo(String vatIdentificationNumber) {
    company.setVatNumber(vatIdentificationNumber);
    return this;
  }

  public CompanyBuilder bankAccount(String bankAccount) {
    company.setBankAccount(bankAccount);
    return this;
  }

  public Company build() {
    return company;
  }
}
