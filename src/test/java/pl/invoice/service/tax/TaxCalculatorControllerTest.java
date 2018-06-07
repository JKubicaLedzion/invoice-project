package pl.invoice.service.tax;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.invoice.TestInvoiceGenerator.getFirstTestInvoice;
import static pl.invoice.TestInvoiceGenerator.getForthTestInvoice;
import static pl.invoice.TestInvoiceGenerator.getJsonFormatForCalculationResult;
import static pl.invoice.TestInvoiceGenerator.getThirdTestInvoice;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.invoice.database.Database;
import pl.invoice.model.Invoice;
import pl.invoice.service.invoice.InvoiceService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@WebMvcTest(TaxCalculatorController.class)
public class TaxCalculatorControllerTest {

  private static final String PATH = "/invoices";
  private static final String EMPTY_LIST_MSG = "Invoice list is empty.";
  private static final String VAT_NO = "PL000000";

  @MockBean
  Database database;

  @Autowired
  private MockMvc mvc;

  @MockBean
  private TaxCalculatorService taxService;

  @MockBean
  private InvoiceService invoiceService;

  @Autowired
  private TaxCalculatorController taxController;

  private List<Invoice> invoiceList;

  @Before
  public void setup() {
    invoiceList = new ArrayList<>();
    invoiceList.add(getFirstTestInvoice());
    invoiceList.add(getThirdTestInvoice());
    invoiceList.add(getForthTestInvoice());

    mvc = MockMvcBuilders
        .standaloneSetup(taxController)
        .build();
  }

  @Test
  public void getIncomeAmountShouldReturnNotFoundWhenEmptyInvoiceList() throws Exception {
    // given:
    Mockito.when(invoiceService.getInvoices()).thenReturn(Collections.emptyList());
    // when:
    mvc.perform(get(PATH + "/income/" + VAT_NO)
        .accept(MediaType.APPLICATION_JSON))
        //then:
        .andExpect(status().isNotFound())
        .andExpect(content().string(getJsonFormatForCalculationResult(new CalculationResult(EMPTY_LIST_MSG))));
  }

  @Test
  public void getCostAmountShouldReturnNotFoundWhenEmptyInvoiceList() throws Exception {
    // given:
    Mockito.when(invoiceService.getInvoices()).thenReturn(Collections.emptyList());
    // when:
    mvc.perform(get(PATH + "/cost/" + VAT_NO)
        .accept(MediaType.APPLICATION_JSON))
        //then:
        .andExpect(status().isNotFound())
        .andExpect(content().string(getJsonFormatForCalculationResult(new CalculationResult(EMPTY_LIST_MSG))));
  }

  @Test
  public void getOutputVatAmountShouldReturnNotFoundWhenEmptyInvoiceList() throws Exception {
    // given:
    Mockito.when(invoiceService.getInvoices()).thenReturn(Collections.emptyList());
    // when:
    mvc.perform(get(PATH + "/outputVat/" + VAT_NO)
        .accept(MediaType.APPLICATION_JSON))
        //then:
        .andExpect(status().isNotFound())
        .andExpect(content().string(getJsonFormatForCalculationResult(new CalculationResult(EMPTY_LIST_MSG))));
  }

  @Test
  public void getInputVatAmountShouldReturnNotFoundWhenEmptyInvoiceList() throws Exception {
    // given:
    Mockito.when(invoiceService.getInvoices()).thenReturn(Collections.emptyList());
    // when:
    mvc.perform(get(PATH + "/inputVat/" + VAT_NO)
        .accept(MediaType.APPLICATION_JSON))
        //then:
        .andExpect(status().isNotFound())
        .andExpect(content().string(getJsonFormatForCalculationResult(new CalculationResult(EMPTY_LIST_MSG))));
  }

  @Test
  public void getVatPayableShouldReturnNotFoundWhenEmptyInvoiceList() throws Exception {
    // given:
    Mockito.when(invoiceService.getInvoices()).thenReturn(Collections.emptyList());
    // when:
    mvc.perform(get(PATH + "/vatPayable/" + VAT_NO)
        .accept(MediaType.APPLICATION_JSON))
        //then:
        .andExpect(status().isNotFound())
        .andExpect(content().string(getJsonFormatForCalculationResult(new CalculationResult(EMPTY_LIST_MSG))));
  }

  @Test
  public void getProfitShouldReturnNotFoundWhenEmptyInvoiceList() throws Exception {
    // given:
    Mockito.when(invoiceService.getInvoices()).thenReturn(Collections.emptyList());
    // when:
    mvc.perform(get(PATH + "/profit/" + VAT_NO)
        .accept(MediaType.APPLICATION_JSON))
        //then:
        .andExpect(status().isNotFound())
        .andExpect(content().string(getJsonFormatForCalculationResult(new CalculationResult(EMPTY_LIST_MSG))));
  }

  @Test
  public void getIncomeAmount() throws Exception {
    // given:
    Mockito.when(invoiceService.getInvoices()).thenReturn(invoiceList);
    Mockito.when(taxService.calculateIncome(VAT_NO, invoiceList)).thenReturn(BigDecimal.valueOf(1500));
    // when:
    mvc.perform(get(PATH + "/income/" + VAT_NO)
        .accept(MediaType.APPLICATION_JSON))
        //then:
        .andExpect(status().isOk())
        .andExpect(content().string(getJsonFormatForCalculationResult(new CalculationResult("Income amount",
            BigDecimal.valueOf(1500)))));
  }

  @Test
  public void getCostAmount() throws Exception {
    // given:
    Mockito.when(invoiceService.getInvoices()).thenReturn(invoiceList);
    Mockito.when(taxService.calculateCost(VAT_NO, invoiceList)).thenReturn(BigDecimal.valueOf(300));
    // when:
    mvc.perform(get(PATH + "/cost/" + VAT_NO)
        .accept(MediaType.APPLICATION_JSON))
        //then:
        .andExpect(status().isOk())
        .andExpect(content().string(getJsonFormatForCalculationResult(new CalculationResult("Cost amount",
            BigDecimal.valueOf(300)))));
  }

  @Test
  public void getOutputVatAmount() throws Exception {
    // given:
    Mockito.when(invoiceService.getInvoices()).thenReturn(invoiceList);
    Mockito.when(taxService.calculateOutputVat(VAT_NO, invoiceList)).thenReturn(BigDecimal.valueOf(345));
    // when:
    mvc.perform(get(PATH + "/outputVat/" + VAT_NO)
        .accept(MediaType.APPLICATION_JSON))
        //then:
        .andExpect(status().isOk())
        .andExpect(content().string(getJsonFormatForCalculationResult(new CalculationResult("Output VAT amount",
            BigDecimal.valueOf(345)))));
  }

  @Test
  public void getInputVatAmount() throws Exception {
    // given:
    Mockito.when(invoiceService.getInvoices()).thenReturn(invoiceList);
    Mockito.when(taxService.calculateInputVat(VAT_NO, invoiceList)).thenReturn(BigDecimal.valueOf(69));
    // when:
    mvc.perform(get(PATH + "/inputVat/" + VAT_NO)
        .accept(MediaType.APPLICATION_JSON))
        //then:
        .andExpect(status().isOk())
        .andExpect(content().string(getJsonFormatForCalculationResult(new CalculationResult("Input VAT amount",
            BigDecimal.valueOf(69)))));
  }

  @Test
  public void getVatPayableAmount() throws Exception {
    // given:
    Mockito.when(invoiceService.getInvoices()).thenReturn(invoiceList);
    Mockito.when(taxService.calculateVatPayable(VAT_NO, invoiceList)).thenReturn(BigDecimal.valueOf(276));
    // when:
    mvc.perform(get(PATH + "/vatPayable/" + VAT_NO)
        .accept(MediaType.APPLICATION_JSON))
        //then:
        .andExpect(status().isOk())
        .andExpect(content().string(
            getJsonFormatForCalculationResult(new CalculationResult("VAT payable amount", BigDecimal.valueOf(276)))));
  }

  @Test
  public void getProfit() throws Exception {
    // given:
    Mockito.when(invoiceService.getInvoices()).thenReturn(invoiceList);
    Mockito.when(taxService.calculateProfit(VAT_NO, invoiceList)).thenReturn(BigDecimal.valueOf(1200));
    // when:
    mvc.perform(get(PATH + "/profit/" + VAT_NO)
        .accept(MediaType.APPLICATION_JSON))
        //then:
        .andExpect(status().isOk())
        .andExpect(content().string(getJsonFormatForCalculationResult(new CalculationResult("Profit amount",
            BigDecimal.valueOf(1200)))));
  }
}