package pl.invoice.model;

import java.util.NoSuchElementException;

public enum Vat {

  VAT_23(0.23),
  VAT_8(0.8),
  VAT_5(0.5),
  VAT_7(0.7),
  VAT_4(0.4),
  VAT_0(0.0);

  private double rate;

  Vat(double rate) {
    this.rate = rate;
  }

  public double getRate() {
    return rate;
  }

  public static Vat getByValue(double rate) {
    for (Vat vat : Vat.values()) {
      if (vat.getRate() == rate) {
        return vat;
      }
    }
    throw new NoSuchElementException("Wrong Vat rate provided.");
  }
}
