package pl.invoice.model;

import static org.hamcrest.CoreMatchers.is;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import pl.invoice.TestInvoiceGenerator;

import java.math.BigDecimal;
import java.math.RoundingMode;

@RunWith(MockitoJUnitRunner.class)
public class InvoiceTest {

  @Test
  public void getTotalNetAmountShouldReturnInvoiceNetAmount() {
    // given:
    Invoice invoice = TestInvoiceGenerator.getFirstTestInvoice();
    invoice.setEntryList(TestInvoiceGenerator.getInvoiceEntry());
    // when:
    BigDecimal result = invoice.getTotalNetAmount().setScale(2, RoundingMode.CEILING);
    // then:
    Assert.assertThat(result, is(BigDecimal.valueOf(4200.00).setScale(2, RoundingMode.CEILING)));
  }

  @Test
  public void getTotalVatAmountShouldReturnInvoiceNetAmount() {
    // given:
    Invoice invoice = TestInvoiceGenerator.getFirstTestInvoice();
    invoice.setEntryList(TestInvoiceGenerator.getInvoiceEntry());
    // when:
    BigDecimal result = invoice.getTotalVatAmount().setScale(2, RoundingMode.CEILING);
    // then:
    Assert.assertThat(result, is(BigDecimal.valueOf(966.00).setScale(2, RoundingMode.CEILING)));
  }

  @Test
  public void getTotalGrossAmountShouldReturnInvoiceNetAmount() {
    // given:
    Invoice invoice = TestInvoiceGenerator.getFirstTestInvoice();
    invoice.setEntryList(TestInvoiceGenerator.getInvoiceEntry());
    // when:
    BigDecimal result = invoice.getTotalGrossAmount().setScale(2, RoundingMode.CEILING);
    // then:
    Assert.assertThat(result, is(BigDecimal.valueOf(5166.00).setScale(2, RoundingMode.CEILING)));
  }
}