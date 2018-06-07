package pl.invoice.database.impl.sql;

import static pl.invoice.database.impl.sql.TableLabel.BANK_ACCOUNT;
import static pl.invoice.database.impl.sql.TableLabel.BANK_ACCOUNT_CUSTOMER;
import static pl.invoice.database.impl.sql.TableLabel.BANK_ACCOUNT_SUPPLIER;
import static pl.invoice.database.impl.sql.TableLabel.CITY;
import static pl.invoice.database.impl.sql.TableLabel.CITY_CUSTOMER;
import static pl.invoice.database.impl.sql.TableLabel.CITY_SUPPLIER;
import static pl.invoice.database.impl.sql.TableLabel.COMPANY;
import static pl.invoice.database.impl.sql.TableLabel.COUNTRY;
import static pl.invoice.database.impl.sql.TableLabel.COUNTRY_CUSTOMER;
import static pl.invoice.database.impl.sql.TableLabel.COUNTRY_SUPPLIER;
import static pl.invoice.database.impl.sql.TableLabel.DESCRIPTION;
import static pl.invoice.database.impl.sql.TableLabel.DUE_DATE;
import static pl.invoice.database.impl.sql.TableLabel.ID;
import static pl.invoice.database.impl.sql.TableLabel.INVOICE;
import static pl.invoice.database.impl.sql.TableLabel.INVOICE_ENTRY;
import static pl.invoice.database.impl.sql.TableLabel.INVOICE_ID;
import static pl.invoice.database.impl.sql.TableLabel.ISSUE_DATE;
import static pl.invoice.database.impl.sql.TableLabel.IS_PAID;
import static pl.invoice.database.impl.sql.TableLabel.NAME_COMPANY;
import static pl.invoice.database.impl.sql.TableLabel.NAME_CUSTOMER;
import static pl.invoice.database.impl.sql.TableLabel.NAME_SUPPLIER;
import static pl.invoice.database.impl.sql.TableLabel.PHONE_NUMBER;
import static pl.invoice.database.impl.sql.TableLabel.PHONE_NUMBER_CUSTOMER;
import static pl.invoice.database.impl.sql.TableLabel.PHONE_NUMBER_SUPPLIER;
import static pl.invoice.database.impl.sql.TableLabel.POSTAL_CODE;
import static pl.invoice.database.impl.sql.TableLabel.POSTAL_CODE_CUSTOMER;
import static pl.invoice.database.impl.sql.TableLabel.POSTAL_CODE_SUPPLIER;
import static pl.invoice.database.impl.sql.TableLabel.QUANTITY;
import static pl.invoice.database.impl.sql.TableLabel.STREET;
import static pl.invoice.database.impl.sql.TableLabel.STREET_CUSTOMER;
import static pl.invoice.database.impl.sql.TableLabel.STREET_SUPPLIER;
import static pl.invoice.database.impl.sql.TableLabel.UNIT_PRICE;
import static pl.invoice.database.impl.sql.TableLabel.VAT_NO;
import static pl.invoice.database.impl.sql.TableLabel.VAT_NO_CUSTOMER;
import static pl.invoice.database.impl.sql.TableLabel.VAT_NO_SUPPLIER;
import static pl.invoice.database.impl.sql.TableLabel.VAT_RATE;

import pl.invoice.model.Company;
import pl.invoice.model.Invoice;
import pl.invoice.model.InvoiceEntry;

public final class QueryBuilder {

  private static final String QUERY_INVOICES = "SELECT invoice.id, invoice.issue_date, invoice.due_date, "
      + "invoice.is_paid, invoice.vat_no_customer, "
      + "customer.name AS " + NAME_CUSTOMER + ", "
      + "customer.street AS " + STREET_CUSTOMER + ","
      + "customer.postal_code AS " + POSTAL_CODE_CUSTOMER + ", "
      + "customer.city AS " + CITY_CUSTOMER + ", "
      + "customer.country AS " + COUNTRY_CUSTOMER + ","
      + "customer.phone_number AS " + PHONE_NUMBER_CUSTOMER + ", "
      + "customer.bank_account AS " + BANK_ACCOUNT_CUSTOMER + ", "
      + "invoice.vat_no_supplier, "
      + "supplier.name AS " + NAME_SUPPLIER + ", "
      + "supplier.street AS " + STREET_SUPPLIER + ","
      + "supplier.postal_code AS " + POSTAL_CODE_SUPPLIER + ", "
      + "supplier.city AS " + CITY_SUPPLIER + ", "
      + "supplier.country AS " + COUNTRY_SUPPLIER + ","
      + "supplier.phone_number AS " + PHONE_NUMBER_SUPPLIER + ", "
      + "supplier.bank_account AS " + BANK_ACCOUNT_SUPPLIER
      + " FROM " + INVOICE + " invoice "
      + "INNER JOIN " + COMPANY + " customer ON invoice.vat_no_customer = customer.vat_no "
      + "INNER JOIN " + COMPANY + " supplier ON invoice.vat_no_supplier = supplier.vat_no";

  public static final String QUERY_ALL_INVOICES = QUERY_INVOICES + ";";

  public static final String QUERY_INVOICE = QUERY_INVOICES + " WHERE invoice.id = ?;";

  public static final String QUERY_INVOICE_IDS = "SELECT * FROM " + INVOICE + " ORDER BY id DESC";

  public static final String QUERY_DELETE_ENTRIES = "DELETE FROM " + INVOICE_ENTRY
      + " WHERE " + INVOICE_ID + " = ?;";

  public static final String QUERY_DELETE_INVOICE = "DELETE FROM " + INVOICE
      + " WHERE " + ID + " = ?;";

  public static final String QUERY_ENTRIES = "SELECT * FROM " + INVOICE_ENTRY
      + " WHERE " + ID + " = ?;";

  public static final String QUERY_VAT_NUMBERS = "SELECT " + VAT_NO + " FROM " + COMPANY;

  public static String insertInvoiceQuery(Invoice invoice) {
    return "INSERT INTO " + INVOICE + " ("
        + ISSUE_DATE + ","
        + DUE_DATE + ","
        + VAT_NO_CUSTOMER + ","
        + VAT_NO_SUPPLIER + ","
        + IS_PAID + ") VALUES ("
        + "'" + invoice.getIssueDate() + "', "
        + "'" + invoice.getDueDate() + "', "
        + "'" + invoice.getCustomer().getVatNumber() + "', "
        + "'" + invoice.getSupplier().getVatNumber() + "', "
        + "'" + invoice.isPaid() + "'" + ");";
  }

  public static String insertCompanyQuery(Company company) {
    return "INSERT INTO " + COMPANY + " VALUES ("
        + "'" + company.getName() + "', "
        + "'" + company.getAddress().getStreet() + "', "
        + "'" + company.getAddress().getPostalCode() + "', "
        + "'" + company.getAddress().getCity() + "', "
        + "'" + company.getAddress().getCountry() + "', "
        + "'" + company.getPhoneNumber() + "', "
        + "'" + company.getVatNumber() + "', "
        + "'" + company.getBankAccount() + "');";
  }

  public static String insertEntryQuery(int id, InvoiceEntry invoiceEntry) {
    return "INSERT INTO " + INVOICE_ENTRY + "("
        + QUANTITY + ","
        + DESCRIPTION + ","
        + UNIT_PRICE + ","
        + VAT_RATE + ","
        + INVOICE_ID + ") VALUES ("
        + "'" + invoiceEntry.getQuantity() + "', "
        + "'" + invoiceEntry.getDescription() + "', "
        + "'" + invoiceEntry.getUnitPrice() + "', "
        + "'" + invoiceEntry.getVatRate().getRate() + "', "
        + "'" + id + "');";
  }

  public static String updateInvoiceQuery(Invoice invoice) {
    return "UPDATE " + INVOICE + "SET "
        + ISSUE_DATE + "='" + invoice.getIssueDate() + "', "
        + DUE_DATE + "='" + invoice.getDueDate() + "', "
        + VAT_NO_CUSTOMER + "='" + invoice.getCustomer().getVatNumber() + "', "
        + VAT_NO_SUPPLIER + "='" + invoice.getSupplier().getVatNumber() + "', "
        + IS_PAID + "='" + invoice.isPaid() + "'"
        + " WHERE " + ID + "=" + invoice.getId() + ";";
  }

  public static String updateEntryQuery(int id, InvoiceEntry invoiceEntry) {
    return "UPDATE " + INVOICE_ENTRY + " SET "
        + QUANTITY + "='" + invoiceEntry.getQuantity() + "', "
        + DESCRIPTION + "='" + invoiceEntry.getDescription() + "', "
        + UNIT_PRICE + "='" + invoiceEntry.getUnitPrice() + "', "
        + VAT_RATE + "='" + invoiceEntry.getVatRate().getRate() + "', "
        + INVOICE_ID + "='" + id + "'"
        + " WHERE " + INVOICE_ID + "=" + id + ";";
  }

  public static String updateCompanyQuery(Company company) {
    return "UPDATE " + COMPANY + " SET "
        + NAME_COMPANY + "='" + company.getName() + "', "
        + STREET + "='" + company.getAddress().getStreet() + "', "
        + POSTAL_CODE + "='" + company.getAddress().getPostalCode() + "', "
        + CITY + "='" + company.getAddress().getCity() + "', "
        + COUNTRY + "='" + company.getAddress().getCountry() + "', "
        + PHONE_NUMBER + "='" + company.getPhoneNumber() + "', "
        + BANK_ACCOUNT + "='" + company.getBankAccount() + "'"
        + " WHERE " + VAT_NO + "='" + company.getVatNumber() + "';";
  }
}
