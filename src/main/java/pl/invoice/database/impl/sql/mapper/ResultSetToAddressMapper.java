package pl.invoice.database.impl.sql.mapper;

import static pl.invoice.database.impl.sql.TableLabel.CITY_CUSTOMER;
import static pl.invoice.database.impl.sql.TableLabel.CITY_SUPPLIER;
import static pl.invoice.database.impl.sql.TableLabel.COUNTRY_CUSTOMER;
import static pl.invoice.database.impl.sql.TableLabel.COUNTRY_SUPPLIER;
import static pl.invoice.database.impl.sql.TableLabel.POSTAL_CODE_CUSTOMER;
import static pl.invoice.database.impl.sql.TableLabel.POSTAL_CODE_SUPPLIER;
import static pl.invoice.database.impl.sql.TableLabel.STREET_CUSTOMER;
import static pl.invoice.database.impl.sql.TableLabel.STREET_SUPPLIER;

import pl.invoice.builders.AddressBuilder;
import pl.invoice.model.Address;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultSetToAddressMapper {

  public static Address getAddressSupplier(ResultSet resultSet) throws SQLException {
    return new AddressBuilder()
        .street(resultSet.getString(STREET_SUPPLIER))
        .city(resultSet.getString(CITY_SUPPLIER))
        .country(resultSet.getString(COUNTRY_SUPPLIER))
        .postalCode(resultSet.getString(POSTAL_CODE_SUPPLIER))
        .build();
  }

  public static Address getAddressCustomer(ResultSet resultSet) throws SQLException {
    return new AddressBuilder()
        .street(resultSet.getString(STREET_CUSTOMER))
        .city(resultSet.getString(CITY_CUSTOMER))
        .country(resultSet.getString(COUNTRY_CUSTOMER))
        .postalCode(resultSet.getString(POSTAL_CODE_CUSTOMER))
        .build();
  }
}
