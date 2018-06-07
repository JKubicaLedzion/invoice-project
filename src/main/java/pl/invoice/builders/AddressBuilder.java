package pl.invoice.builders;

import pl.invoice.model.Address;

public class AddressBuilder {

  private Address address;

  public AddressBuilder() {
    address = new Address();
  }

  public AddressBuilder street(String street) {
    address.setStreet(street);
    return this;
  }

  public AddressBuilder postalCode(String postalCode) {
    address.setPostalCode(postalCode);
    return this;
  }

  public AddressBuilder city(String city) {
    address.setCity(city);
    return this;
  }

  public AddressBuilder country(String country) {
    address.setCountry(country);
    return this;
  }

  public Address build() {
    return address;
  }
}
