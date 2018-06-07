package pl.invoice.exception;

public class SqlDatabaseException extends RuntimeException {

  public SqlDatabaseException(String message) {
    super(message);
  }
}
