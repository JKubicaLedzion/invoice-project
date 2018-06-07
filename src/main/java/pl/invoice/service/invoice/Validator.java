package pl.invoice.service.invoice;

import static org.apache.commons.lang3.StringUtils.isBlank;

import pl.invoice.model.Invoice;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Validator {


  public static String validateDates(LocalDate fromDate, LocalDate toDate) {
    if (toDate.isBefore(fromDate)) {
      return ValidationErrorMessages.TO_DATE_IS_BEFORE_FROM_DATE.getMessage();
    }
    return "";
  }

  public static List<String> validateInvoice(Invoice invoice) {
    List<String> errorList = new ArrayList<>();

    if (Objects.isNull(invoice.getIssueDate())) {
      errorList.add(ValidationErrorMessages.ISSUE_DATE_NOT_PROVIDED.getMessage());
    }
    if (Objects.isNull(invoice.getDueDate())) {
      errorList.add(ValidationErrorMessages.DUE_DATE_NOT_PROVIDED.getMessage());
    }
    if (Objects.isNull(invoice.getEntryList())) {
      errorList.add(ValidationErrorMessages.NO_ENTRIES_PROVIDED.getMessage());
    }
    errorList.addAll(validateCustomer(invoice));
    errorList.addAll(validateSupplier(invoice));

    return errorList;
  }

  private static List<String> validateCustomer(Invoice invoice) {
    List<String> errorList = new ArrayList<>();

    if (isBlank(invoice.getCustomer().getName())) {
      errorList.add(ValidationErrorMessages.CUSTOMER_NAME_NOT_PROVIDED.getMessage());
    }
    if (Objects.isNull(invoice.getCustomer().getPhoneNumber()) || invoice.getCustomer().getPhoneNumber() == 0) {
      errorList.add(ValidationErrorMessages.CUSTOMER_PHONE_NUMBER_NOT_PROVIDED.getMessage());
    }
    if (isBlank(invoice.getCustomer().getBankAccount())) {
      errorList.add(ValidationErrorMessages.CUSTOMER_BANK_ACCOUNT_NOT_PROVIDED.getMessage());
    }
    if (isBlank(invoice.getCustomer().getAddress().getCity())) {
      errorList.add(ValidationErrorMessages.CUSTOMER_CITY_NOT_PROVIDED.getMessage());
    }
    if (isBlank(invoice.getCustomer().getAddress().getPostalCode())) {
      errorList.add(ValidationErrorMessages.CUSTOMER_POSTAL_CODE_NOT_PROVIDED.getMessage());
    }
    if (isBlank(invoice.getCustomer().getAddress().getStreet())) {
      errorList.add(ValidationErrorMessages.CUSTOMER_STREET_NOT_PROVIDED.getMessage());
    }
    if (isBlank(invoice.getCustomer().getAddress().getCountry())) {
      errorList.add(ValidationErrorMessages.CUSTOMER_COUNTRY_NOT_PROVIDED.getMessage());
    }

    return errorList;
  }

  private static List<String> validateSupplier(Invoice invoice) {
    List<String> errorList = new ArrayList<>();

    if (isBlank(invoice.getSupplier().getName())) {
      errorList.add(ValidationErrorMessages.SUPPLIER_NAME_NOT_PROVIDED.getMessage());
    }
    if (Objects.isNull(invoice.getSupplier().getPhoneNumber()) || invoice.getSupplier().getPhoneNumber() == 0) {
      errorList.add(ValidationErrorMessages.SUPPLIER_PHONE_NUMBER_NOT_PROVIDED.getMessage());
    }
    if (isBlank(invoice.getSupplier().getBankAccount())) {
      errorList.add(ValidationErrorMessages.SUPPLIER_BANK_ACCOUNT_NOT_PROVIDED.getMessage());
    }
    if (isBlank(invoice.getSupplier().getAddress().getCity())) {
      errorList.add(ValidationErrorMessages.SUPPLIER_CITY_NOT_PROVIDED.getMessage());
    }
    if (isBlank(invoice.getSupplier().getAddress().getPostalCode())) {
      errorList.add(ValidationErrorMessages.SUPPLIER_POSTAL_CODE_NOT_PROVIDED.getMessage());
    }
    if (isBlank(invoice.getSupplier().getAddress().getStreet())) {
      errorList.add(ValidationErrorMessages.SUPPLIER_STREET_NOT_PROVIDED.getMessage());
    }
    if (isBlank(invoice.getSupplier().getAddress().getCountry())) {
      errorList.add(ValidationErrorMessages.SUPPLIER_COUNTRY_NOT_PROVIDED.getMessage());
    }

    return errorList;
  }
}