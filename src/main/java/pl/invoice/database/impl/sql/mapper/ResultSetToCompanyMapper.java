package pl.invoice.database.impl.sql.mapper;

import static pl.invoice.database.impl.sql.TableLabel.BANK_ACCOUNT_CUSTOMER;
import static pl.invoice.database.impl.sql.TableLabel.BANK_ACCOUNT_SUPPLIER;
import static pl.invoice.database.impl.sql.TableLabel.NAME_CUSTOMER;
import static pl.invoice.database.impl.sql.TableLabel.NAME_SUPPLIER;
import static pl.invoice.database.impl.sql.TableLabel.PHONE_NUMBER_CUSTOMER;
import static pl.invoice.database.impl.sql.TableLabel.PHONE_NUMBER_SUPPLIER;
import static pl.invoice.database.impl.sql.TableLabel.VAT_NO_CUSTOMER;
import static pl.invoice.database.impl.sql.TableLabel.VAT_NO_SUPPLIER;

import pl.invoice.builders.CompanyBuilder;
import pl.invoice.model.Company;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultSetToCompanyMapper {

  public static Company getCustomer(ResultSet resultSet) throws SQLException {
    return new CompanyBuilder()
        .name(resultSet.getString(NAME_CUSTOMER))
        .vatNo(resultSet.getString(VAT_NO_CUSTOMER))
        .bankAccount(resultSet.getString(BANK_ACCOUNT_CUSTOMER))
        .phoneNumber(resultSet.getInt(PHONE_NUMBER_CUSTOMER))
        .address(ResultSetToAddressMapper.getAddressCustomer(resultSet))
        .build();
  }

  public static Company getSupplier(ResultSet resultSet) throws SQLException {
    return new CompanyBuilder()
        .name(resultSet.getString(NAME_SUPPLIER))
        .vatNo(resultSet.getString(VAT_NO_SUPPLIER))
        .bankAccount(resultSet.getString(BANK_ACCOUNT_SUPPLIER))
        .phoneNumber(resultSet.getInt(PHONE_NUMBER_SUPPLIER))
        .address(ResultSetToAddressMapper.getAddressSupplier(resultSet))
        .build();
  }
}
