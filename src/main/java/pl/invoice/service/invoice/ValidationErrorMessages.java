package pl.invoice.service.invoice;

public enum ValidationErrorMessages {

  DATABASE_EMPTY("Database empty."),

  ISSUE_DATE_NOT_PROVIDED("Issue date not provided."),
  NO_ENTRIES_PROVIDED("No entries provided."),
  DUE_DATE_NOT_PROVIDED("Due date not provided."),

  CUSTOMER_NAME_NOT_PROVIDED("Customer name not provided."),
  CUSTOMER_PHONE_NUMBER_NOT_PROVIDED("Customer phone number not provided."),
  CUSTOMER_BANK_ACCOUNT_NOT_PROVIDED("Customer bank account not provided."),
  CUSTOMER_CITY_NOT_PROVIDED("Customer City not provided."),
  CUSTOMER_POSTAL_CODE_NOT_PROVIDED("Customer Postal code not provided."),
  CUSTOMER_STREET_NOT_PROVIDED("Customer Street not provided."),
  CUSTOMER_COUNTRY_NOT_PROVIDED("Customer country not provided."),

  SUPPLIER_NAME_NOT_PROVIDED("Supplier name not provided."),
  SUPPLIER_PHONE_NUMBER_NOT_PROVIDED("Supplier phone number not provided."),
  SUPPLIER_BANK_ACCOUNT_NOT_PROVIDED("Supplier bank account not provided."),
  SUPPLIER_CITY_NOT_PROVIDED("Supplier City not provided."),
  SUPPLIER_POSTAL_CODE_NOT_PROVIDED("Supplier Postal code not provided."),
  SUPPLIER_STREET_NOT_PROVIDED("Supplier Street not provided."),
  SUPPLIER_COUNTRY_NOT_PROVIDED("Supplier country not provided."),

  TO_DATE_IS_BEFORE_FROM_DATE("ToDate is before fromDate.");

  private String message;

  ValidationErrorMessages(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
