package pl.invoice.service.tax;

import java.math.BigDecimal;

public final class CalculationResult {

  private String message;
  private BigDecimal value;

  public CalculationResult(String message, BigDecimal value) {
    this.message = message;
    this.value = value;
  }

  public CalculationResult(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public BigDecimal getValue() {
    return value;
  }
}
