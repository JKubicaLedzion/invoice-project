package pl.invoice.model;

import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;
import javax.persistence.Embeddable;

@Embeddable
public class Address {

  private String street;
  private String postalCode;
  private String city;
  private String country;

  public Address() {
  }

  public Address(String street, String postalCode, String city, String country) {
    this.street = street;
    this.postalCode = postalCode;
    this.city = city;
    this.country = country;
  }

  @ApiModelProperty(required = true)
  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  @ApiModelProperty(required = true)
  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  @ApiModelProperty(required = true)
  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  @ApiModelProperty(required = true)
  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null || getClass() != object.getClass()) {
      return false;
    }
    Address address = (Address) object;
    return Objects.equals(postalCode, address.postalCode)
        && Objects.equals(street, address.street)
        && Objects.equals(city, address.city)
        && Objects.equals(country, address.country);
  }

  @Override
  public int hashCode() {
    return Objects.hash(street, postalCode, city, country);
  }
}
