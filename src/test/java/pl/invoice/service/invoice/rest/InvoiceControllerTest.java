package pl.invoice.service.invoice.rest;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.invoice.TestInvoiceGenerator.getFirstTestInvoice;
import static pl.invoice.TestInvoiceGenerator.getJsonFormatStringForFirstInv;
import static pl.invoice.TestInvoiceGenerator.getJsonFormatStringForIncorrectInv;
import static pl.invoice.TestInvoiceGenerator.getJsonFormatStringForSecondInv;
import static pl.invoice.TestInvoiceGenerator.getJsonFormatStringForThirdInv;
import static pl.invoice.TestInvoiceGenerator.getJsonFormatStringForUpdatedFirstInv;
import static pl.invoice.TestInvoiceGenerator.getSecondTestInvoice;
import static pl.invoice.TestInvoiceGenerator.getThirdTestInvoice;
import static pl.invoice.TestInvoiceGenerator.getUpdatedFirstInvoice;
import static pl.invoice.service.invoice.ValidationErrorMessages.CUSTOMER_BANK_ACCOUNT_NOT_PROVIDED;
import static pl.invoice.service.invoice.ValidationErrorMessages.CUSTOMER_STREET_NOT_PROVIDED;
import static pl.invoice.service.invoice.ValidationErrorMessages.DATABASE_EMPTY;
import static pl.invoice.service.invoice.ValidationErrorMessages.ISSUE_DATE_NOT_PROVIDED;
import static pl.invoice.service.invoice.ValidationErrorMessages.SUPPLIER_COUNTRY_NOT_PROVIDED;
import static pl.invoice.service.invoice.ValidationErrorMessages.TO_DATE_IS_BEFORE_FROM_DATE;

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
import pl.invoice.model.Invoice;
import pl.invoice.service.email.EmailService;
import pl.invoice.service.invoice.InvoiceService;
import pl.invoice.service.pdf.PdfGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@WebMvcTest(InvoiceController.class)
public class InvoiceControllerTest {

  private static final String PATH = "/invoices";
  private List<Invoice> invoiceList = new ArrayList<>();

  @Autowired
  private MockMvc mvc;

  @MockBean
  private InvoiceService invoiceService;

  @MockBean
  private PdfGenerator pdfGenerator;

  @MockBean
  private EmailService emailService;

  @Autowired
  private InvoiceController invoiceController;


  @Before
  public void setup() {
    invoiceList.clear();
    invoiceList.add(getFirstTestInvoice());
    invoiceList.add(getSecondTestInvoice());

    mvc = MockMvcBuilders
        .standaloneSetup(invoiceController)
        .build();
  }

  @Test
  public void getInvoicesShouldReturnOkResponseAndContentWithInvoiceList() throws Exception {
    // given:
    Mockito.when(invoiceService.getInvoices()).thenReturn(invoiceList);
    // when:
    mvc.perform(get(PATH)
        .accept(MediaType.APPLICATION_JSON))
        //then:
        .andExpect(status().isOk())
        .andExpect(content().string(is("[" + getJsonFormatStringForFirstInv() + ","
            + getJsonFormatStringForSecondInv() + "]")));
  }

  @Test
  public void getInvoicesShouldReturnNotFoundResponse() throws Exception {
    // given:
    Mockito.when(invoiceService.getInvoices()).thenReturn(Collections.emptyList());
    // when:
    mvc.perform(get(PATH)
        .accept(MediaType.APPLICATION_JSON))
        //then:
        .andExpect(status().isNotFound())
        .andExpect(content().string(is("\"" + DATABASE_EMPTY + "\"")));
  }

  @Test
  public void getInvoiceByIdShouldReturnOkResponseAndContentWithInvoice() throws Exception {
    // given:
    Mockito.when(invoiceService.getInvoiceById(2)).thenReturn(Optional.of(getSecondTestInvoice()));
    // when:
    mvc.perform(get(PATH + "/2")
        .accept(MediaType.APPLICATION_JSON))
        // then:
        .andExpect(status().isOk())
        .andExpect(content().string(is(getJsonFormatStringForSecondInv())));
  }

  @Test
  public void getInvoiceByIdShouldReturnBadRequestResponse() throws Exception {
    // given:
    Mockito.when(invoiceService.getInvoiceById(5)).thenReturn(Optional.empty());
    // when:
    mvc.perform(get(PATH + "/5")
        .accept(MediaType.APPLICATION_JSON))
        // then:
        .andExpect(status().isNotFound())
        .andExpect(content().string(is("Invoice with Id 5 not found.")));
  }

  @Test
  public void getInvoicesWithinDateRangeShouldReturnOkResponseAndContentWithInvoiceList() throws Exception {
    // given:
    Mockito.when(invoiceService.getInvoicesWithinDateRange(LocalDate.of(2017, 12, 11),
        LocalDate.of(2018, 12, 13)))
        .thenReturn(invoiceList);
    // when:
    mvc.perform(get("/invoices/dates?fromDate=2017-12-11&toDate=2018-12-13")
        .accept(MediaType.APPLICATION_JSON))
        // then:
        .andExpect(status().isOk())
        .andExpect(content().string(is("[" + getJsonFormatStringForFirstInv()
            + "," + getJsonFormatStringForSecondInv() + "]")));
  }

  @Test
  public void getInvoicesWhenIncorrectDatesWithinDateRangeShouldReturnBadRequestResponse() throws Exception {
    // given:
    Mockito.when(invoiceService.getInvoicesWithinDateRange(LocalDate.of(2017, 12, 12),
        LocalDate.of(2018, 12, 11)))
        .thenReturn(invoiceList);
    // when:
    mvc.perform(get(PATH + "/dates?fromDate=2017-12-13&toDate=2017-12-11")
        .accept(MediaType.APPLICATION_JSON))
        // then:
        .andExpect(status().isBadRequest())
        .andExpect(content().string(is(TO_DATE_IS_BEFORE_FROM_DATE.getMessage())));
  }

  @Test
  public void addInvoiceShouldReturnOkResponseAndContentWithInvoiceId() throws Exception {
    // given:
    Mockito.when(invoiceService.addInvoice(getThirdTestInvoice())).thenReturn(3);
    // when:
    mvc.perform(post(PATH)
        .content(getJsonFormatStringForThirdInv())
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        // then:
        .andExpect(status().isOk())
        .andExpect(content().string(is("3")));
  }

  @Test
  public void addInvoiceWhenIncorrectInvoiceShouldReturnBadRequestResponse() throws Exception {
    // when:
    mvc.perform(post(PATH)
        .content(getJsonFormatStringForIncorrectInv())
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        // then:
        .andExpect(status().isBadRequest())
        .andExpect(content().string(is("[\"" + ISSUE_DATE_NOT_PROVIDED.getMessage() + "\",\""
            + CUSTOMER_BANK_ACCOUNT_NOT_PROVIDED.getMessage() + "\",\""
            + CUSTOMER_STREET_NOT_PROVIDED.getMessage() + "\",\""
            + SUPPLIER_COUNTRY_NOT_PROVIDED.getMessage() + "\"]")));
  }

  @Test
  public void updateInvoiceShouldReturnOkResponse() throws Exception {
    // given:
    Mockito.when(invoiceService.updateInvoice(getUpdatedFirstInvoice())).thenReturn(1);
    Mockito.when(invoiceService.getInvoiceById(1)).thenReturn(Optional.of(getUpdatedFirstInvoice()));
    // when:
    mvc.perform(put(PATH)
        .content(getJsonFormatStringForUpdatedFirstInv())
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        // then:
        .andExpect(status().isOk())
        .andExpect(content().string(is("1")));
  }

  @Test
  public void updateInvoiceWhenIncorrectInvoiceShouldReturnBadRequestResponse() throws Exception {
    // given:
    Mockito.when(invoiceService.updateInvoice(getUpdatedFirstInvoice())).thenReturn(1);
    // when:
    mvc.perform(put(PATH)
        .content(getJsonFormatStringForIncorrectInv())
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        // then:
        .andExpect(status().isBadRequest())
        .andExpect(content().string(is("[\"" + ISSUE_DATE_NOT_PROVIDED.getMessage() + "\",\""
            + CUSTOMER_BANK_ACCOUNT_NOT_PROVIDED.getMessage() + "\",\""
            + CUSTOMER_STREET_NOT_PROVIDED.getMessage() + "\",\""
            + SUPPLIER_COUNTRY_NOT_PROVIDED.getMessage() + "\"]")));
  }

  @Test
  public void deleteInvoiceShouldReturnOkResponse() throws Exception {
    // given:
    Mockito.doNothing().when(invoiceService).deleteInvoice(1);
    Mockito.when(invoiceService.getInvoiceById(1)).thenReturn(Optional.of(getFirstTestInvoice()));
    // when:
    mvc.perform(delete(PATH + "/1")
        .accept(MediaType.APPLICATION_JSON))
        // then:
        .andExpect(status().isOk());
  }

  @Test
  public void deleteInvoiceShouldReturnBadRequestResponse() throws Exception {
    // given:
    Mockito.doNothing().when(invoiceService).deleteInvoice(1);
    Mockito.when(invoiceService.getInvoiceById(1)).thenReturn(Optional.empty());
    // when:
    mvc.perform(delete(PATH + "/1")
        .accept(MediaType.APPLICATION_JSON))
        // then:
        .andExpect(status().isNotFound())
        .andExpect(content().string(is("Invoice with Id 1 not found.")));
  }
}