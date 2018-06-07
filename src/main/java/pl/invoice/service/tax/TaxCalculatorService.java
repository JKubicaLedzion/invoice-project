package pl.invoice.service.tax;

import org.springframework.stereotype.Service;
import pl.invoice.model.Invoice;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

@Service
public class TaxCalculatorService {

  public BigDecimal calculateIncome(String vatNo, List<Invoice> invoiceList) {
    return calculateTaxes(
        invoice ->
            invoice.getSupplier().getVatNumber().equals(vatNo)
                ? invoice.getTotalNetAmount()
                : BigDecimal.valueOf(0),
        invoiceList);
  }

  public BigDecimal calculateCost(String vatNo, List<Invoice> invoiceList) {
    return calculateTaxes(
        invoice ->
            invoice.getCustomer().getVatNumber().equals(vatNo)
                ? invoice.getTotalNetAmount()
                : BigDecimal.valueOf(0),
        invoiceList);
  }

  public BigDecimal calculateOutputVat(String vatNo, List<Invoice> invoiceList) {
    return calculateTaxes(
        invoice ->
            invoice.getSupplier().getVatNumber().equals(vatNo)
                ? invoice.getTotalVatAmount()
                : BigDecimal.valueOf(0),
        invoiceList);
  }

  public BigDecimal calculateInputVat(String vatNo, List<Invoice> invoiceList) {
    return calculateTaxes(
        invoice ->
            invoice.getCustomer().getVatNumber().equals(vatNo)
                ? invoice.getTotalVatAmount()
                : BigDecimal.valueOf(0),
        invoiceList);
  }

  public BigDecimal calculateVatPayable(String vatNo, List<Invoice> invoiceList) {
    return calculateOutputVat(vatNo, invoiceList).subtract(calculateInputVat(vatNo, invoiceList));
  }

  public BigDecimal calculateProfit(String vatNo, List<Invoice> invoiceList) {
    return calculateIncome(vatNo, invoiceList).subtract(calculateCost(vatNo, invoiceList));
  }

  private BigDecimal calculateTaxes(Function<Invoice, BigDecimal> function, List<Invoice> invoiceList) {
    return invoiceList.stream()
        .map(invoice -> function.apply(invoice))
        .reduce((amount1, amount2) -> amount1.add(amount2))
        .orElseGet(() -> BigDecimal.valueOf(0));
  }
}
