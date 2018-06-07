package pl.invoice.service.tax;

import static org.hamcrest.core.Is.is;
import static pl.invoice.TestInvoiceGenerator.getFirstTestInvoice;
import static pl.invoice.TestInvoiceGenerator.getForthTestInvoice;
import static pl.invoice.TestInvoiceGenerator.getThirdTestInvoice;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pl.invoice.model.Invoice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class TaxCalculatorServiceTest {

  private static final String VAT_NO = "PL000000";

  private TaxCalculatorService taxCalculator;
  private List<Invoice> invoiceList;

  @Before
  public void setUp() {
    // given:
    taxCalculator = new TaxCalculatorService();
    invoiceList = new ArrayList<>();
    invoiceList.add(getFirstTestInvoice());
    invoiceList.add(getForthTestInvoice());
    invoiceList.add(getThirdTestInvoice());
  }

  @Test
  public void calculateIncomeShouldReturnIncomeAmount() {
    // when:
    BigDecimal result = round(taxCalculator.calculateIncome(VAT_NO, invoiceList));
    // then:
    Assert.assertThat(result, is(round(BigDecimal.valueOf(1500))));
  }

  @Test
  public void calculateCostShouldReturnCostAmount() {
    // when:
    BigDecimal result = round(taxCalculator.calculateCost(VAT_NO, invoiceList));
    // then:
    Assert.assertThat(result, is(round(BigDecimal.valueOf(300))));
  }

  @Test
  public void calculateOutputVatShouldReturnOutputVatAmount() {
    // when:
    BigDecimal result = round(taxCalculator.calculateOutputVat(VAT_NO, invoiceList));
    // then:
    Assert.assertThat(result, is(round(BigDecimal.valueOf(345))));
  }

  @Test
  public void calculateInputVatShouldReturnInputVatAmount() {
    // when:
    BigDecimal result = round(taxCalculator.calculateInputVat(VAT_NO, invoiceList));
    // then:
    Assert.assertThat(result, is(round(BigDecimal.valueOf(69))));
  }

  @Test
  public void calculateVatPayableShouldReturnVatPayableAmount() {
    // when:
    BigDecimal result = round(taxCalculator.calculateVatPayable(VAT_NO, invoiceList));
    // then:
    Assert.assertThat(result, is(round(BigDecimal.valueOf(276))));
  }

  @Test
  public void calculateProfitShouldReturnProfitAmount() {
    // when:
    BigDecimal result = round(taxCalculator.calculateProfit(VAT_NO, invoiceList));
    // then:
    Assert.assertThat(result, is(round(BigDecimal.valueOf(1200))));
  }

  @Test
  public void calculateIncomeShouldReturnZeroWhenInvoiceListEmpty() {
    // when:
    invoiceList.clear();
    BigDecimal result = round(taxCalculator.calculateIncome(VAT_NO, invoiceList));
    // then:
    Assert.assertThat(result, is(round(BigDecimal.valueOf(0))));
  }

  @Test
  public void calculateCostShouldReturnZeroWhenInvoiceListEmpty() {
    // when:
    invoiceList.clear();
    BigDecimal result = round(taxCalculator.calculateCost(VAT_NO, invoiceList));
    // then:
    Assert.assertThat(result, is(round(BigDecimal.valueOf(0))));
  }

  @Test
  public void calculateOutputVatShouldReturnZeroWhenInvoiceListEmpty() {
    // when:
    invoiceList.clear();
    BigDecimal result = round(taxCalculator.calculateOutputVat(VAT_NO, invoiceList));
    // then:
    Assert.assertThat(result, is(round(BigDecimal.valueOf(0))));
  }

  @Test
  public void calculateInputVatShouldReturnZeroWhenInvoiceListEmpty() {
    // when:
    invoiceList.clear();
    BigDecimal result = round(taxCalculator.calculateInputVat(VAT_NO, invoiceList));
    // then:
    Assert.assertThat(result, is(round(BigDecimal.valueOf(0))));
  }

  @Test
  public void calculateVatPayableShouldReturnZeroWhenInvoiceListEmpty() {
    // when:
    invoiceList.clear();
    BigDecimal result = round(taxCalculator.calculateVatPayable(VAT_NO, invoiceList));
    // then:
    Assert.assertThat(result, is(round(BigDecimal.valueOf(0))));
  }

  @Test
  public void calculateProfitShouldReturnZeroWhenInvoiceListEmpty() {
    // when:
    invoiceList.clear();
    BigDecimal result = round(taxCalculator.calculateProfit(VAT_NO, invoiceList));
    // then:
    Assert.assertThat(result, is(round(BigDecimal.valueOf(0))));
  }

  private BigDecimal round(BigDecimal amount) {
    return amount.setScale(2, RoundingMode.CEILING);
  }
}