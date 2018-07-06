package pl.invoice.service.invoice.soap;

import static org.springframework.ws.test.server.RequestCreators.withPayload;
import static org.springframework.ws.test.server.ResponseMatchers.noFault;
import static org.springframework.ws.test.server.ResponseMatchers.payload;
import static pl.invoice.service.invoice.soap.XmlEnvelopePath.ADD_FIRST_INVOICE_REQUEST;
import static pl.invoice.service.invoice.soap.XmlEnvelopePath.ADD_INVOICE_RESPONSE;
import static pl.invoice.service.invoice.soap.XmlEnvelopePath.ADD_INVOICE_WRONG_REQUEST;
import static pl.invoice.service.invoice.soap.XmlEnvelopePath.ADD_SECOND_INVOICE_REQUEST;
import static pl.invoice.service.invoice.soap.XmlEnvelopePath.ADD_UPDATE_INVOICE_WRONG_DATA_RESPONSE;
import static pl.invoice.service.invoice.soap.XmlEnvelopePath.BASE_PATH_REQUEST;
import static pl.invoice.service.invoice.soap.XmlEnvelopePath.BASE_PATH_RESPONSE;
import static pl.invoice.service.invoice.soap.XmlEnvelopePath.DELETE_INVOICE_REQUEST;
import static pl.invoice.service.invoice.soap.XmlEnvelopePath.GET_INVOICES_REQUEST;
import static pl.invoice.service.invoice.soap.XmlEnvelopePath.GET_INVOICES_RESPONSE;
import static pl.invoice.service.invoice.soap.XmlEnvelopePath.GET_INVOICES_WITHIN_DATE_RANGE_REQUEST;
import static pl.invoice.service.invoice.soap.XmlEnvelopePath.GET_INVOICES_WITHIN_DATE_RANGE_RESPONSE;
import static pl.invoice.service.invoice.soap.XmlEnvelopePath.GET_INVOICE_BY_ID_REQUEST;
import static pl.invoice.service.invoice.soap.XmlEnvelopePath.GET_INVOICE_BY_ID_REQUEST_INVOICE_NOT_FOUND;
import static pl.invoice.service.invoice.soap.XmlEnvelopePath.GET_INVOICE_BY_ID_RESPONSE;
import static pl.invoice.service.invoice.soap.XmlEnvelopePath.GET_INVOICE_BY_ID_RESPONSE_AFTER_UPDATE;
import static pl.invoice.service.invoice.soap.XmlEnvelopePath.GET_INVOICE_BY_ID_RESPONSE_INVOICE_NOT_FOUND;
import static pl.invoice.service.invoice.soap.XmlEnvelopePath.UPDATE_INVOICE_REQUEST;
import static pl.invoice.service.invoice.soap.XmlEnvelopePath.UPDATE_INVOICE_RESPONSE;
import static pl.invoice.service.invoice.soap.XmlEnvelopePath.UPDATE_INVOICE_WRONG_REQUEST;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.ws.test.server.MockWebServiceClient;
import org.springframework.xml.transform.StringSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import javax.xml.transform.Source;

@TestPropertySource(value = "classpath:application-soap.properties")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class InvoiceEndpointTest {

  @Autowired
  private WebApplicationContext applicationContext;

  private MockWebServiceClient mockClient;
  private Resource xsdSchema = new ClassPathResource("invoices.xsd");

  @Before
  public void setup() {
    mockClient = MockWebServiceClient.createClient(applicationContext);
  }

  @Test
  public void getInvoicesShouldReturnInvoiceList() {
    // given:
    Source requestAddInvoice = new StringSource(getSoapEnvelope(BASE_PATH_REQUEST + ADD_FIRST_INVOICE_REQUEST));
    Source requestAddSecondInvoice =
        new StringSource(getSoapEnvelope(BASE_PATH_REQUEST + ADD_SECOND_INVOICE_REQUEST));
    Source requestGetInvoice = new StringSource(getSoapEnvelope(BASE_PATH_REQUEST + GET_INVOICES_REQUEST));
    Source responseGetInvoice = new StringSource(getSoapEnvelope(BASE_PATH_RESPONSE + GET_INVOICES_RESPONSE));
    mockClient.sendRequest(withPayload(requestAddInvoice)).andExpect(noFault());
    mockClient.sendRequest(withPayload(requestAddSecondInvoice)).andExpect(noFault());
    // when:
    mockClient
        .sendRequest(withPayload(requestGetInvoice))
        // then:
        .andExpect(payload(responseGetInvoice));
  }

  @Test
  public void getInvoiceByIdShouldReturnResponseWithInvoice() {
    // given:
    Source requestAddInvoice = new StringSource(getSoapEnvelope(BASE_PATH_REQUEST + ADD_FIRST_INVOICE_REQUEST));
    Source requestGetInvoice = new StringSource(getSoapEnvelope(BASE_PATH_REQUEST + GET_INVOICE_BY_ID_REQUEST));
    Source responseGetInvoice =
        new StringSource(getSoapEnvelope(BASE_PATH_RESPONSE + GET_INVOICE_BY_ID_RESPONSE));
    mockClient.sendRequest(withPayload(requestAddInvoice)).andExpect(noFault());
    // when:
    mockClient
        .sendRequest(withPayload(requestGetInvoice))
        // then:
        .andExpect(payload(responseGetInvoice));
  }

  @Test
  public void getInvoicesWithinDateRangeShouldReturnResponseWithInvoiceList() {
    // given:
    Source requestAddFirstInvoice =
        new StringSource(getSoapEnvelope(BASE_PATH_REQUEST + ADD_FIRST_INVOICE_REQUEST));
    Source requestAddSecondInvoice =
        new StringSource(getSoapEnvelope(BASE_PATH_REQUEST + ADD_SECOND_INVOICE_REQUEST));
    Source requestGetInvoices =
        new StringSource(getSoapEnvelope(BASE_PATH_REQUEST + GET_INVOICES_WITHIN_DATE_RANGE_REQUEST));
    Source responseGetInvoices =
        new StringSource(getSoapEnvelope(BASE_PATH_RESPONSE + GET_INVOICES_WITHIN_DATE_RANGE_RESPONSE));
    mockClient.sendRequest(withPayload(requestAddFirstInvoice)).andExpect(noFault());
    mockClient.sendRequest(withPayload(requestAddSecondInvoice)).andExpect(noFault());
    // when:
    mockClient
        .sendRequest(withPayload(requestGetInvoices))
        // then:
        .andExpect(payload(responseGetInvoices));
  }

  @Test
  public void addInvoiceShouldAddInvoice() {
    // given:
    Source request = new StringSource(getSoapEnvelope(BASE_PATH_REQUEST + ADD_FIRST_INVOICE_REQUEST));
    Source response = new StringSource(getSoapEnvelope(BASE_PATH_RESPONSE + ADD_INVOICE_RESPONSE));
    // when:
    mockClient
        .sendRequest(withPayload(request))
        // then:
        .andExpect(payload(response));
  }

  @Test
  public void addInvoiceShouldReturnResponseWithFault() {
    // given:
    Source request = new StringSource(getSoapEnvelope(BASE_PATH_REQUEST + ADD_INVOICE_WRONG_REQUEST));
    Source response =
        new StringSource(getSoapEnvelope(BASE_PATH_RESPONSE + ADD_UPDATE_INVOICE_WRONG_DATA_RESPONSE));
    // when:
    mockClient
        .sendRequest(withPayload(request))
        // then:
        .andExpect(payload(response));
  }

  @Test
  public void updateInvoiceShouldReturnResponseWithFault() throws IOException {
    // given:
    Source requestAddInvoice = new StringSource(getSoapEnvelope(BASE_PATH_REQUEST + ADD_FIRST_INVOICE_REQUEST));
    Source requestUpdateInvoice =
        new StringSource(getSoapEnvelope(BASE_PATH_REQUEST + UPDATE_INVOICE_WRONG_REQUEST));
    Source responseUpdateInvoice =
        new StringSource(getSoapEnvelope(BASE_PATH_RESPONSE + ADD_UPDATE_INVOICE_WRONG_DATA_RESPONSE));
    mockClient.sendRequest(withPayload(requestAddInvoice)).andExpect(noFault());
    // when:
    mockClient
        .sendRequest(withPayload(requestUpdateInvoice))
        // then:
        .andExpect(payload(responseUpdateInvoice));
  }

  @Test
  public void updateInvoiceShouldUpdateExistingInvoice() {
    // given:
    Source requestAddInvoice = new StringSource(getSoapEnvelope(BASE_PATH_REQUEST + ADD_FIRST_INVOICE_REQUEST));
    Source requestUpdateInvoice = new StringSource(getSoapEnvelope(BASE_PATH_REQUEST + UPDATE_INVOICE_REQUEST));
    Source responseUpdateInvoice =
        new StringSource(getSoapEnvelope(BASE_PATH_RESPONSE + UPDATE_INVOICE_RESPONSE));
    Source requestGetInvoice =
        new StringSource(getSoapEnvelope(BASE_PATH_REQUEST + GET_INVOICE_BY_ID_REQUEST));
    Source responseGetInvoice =
        new StringSource(
            getSoapEnvelope(BASE_PATH_RESPONSE + GET_INVOICE_BY_ID_RESPONSE_AFTER_UPDATE));
    mockClient.sendRequest(withPayload(requestAddInvoice)).andExpect(noFault());
    // when:
    mockClient
        .sendRequest(withPayload(requestUpdateInvoice))
        // then:
        .andExpect(payload(responseUpdateInvoice));
    // when:
    mockClient
        .sendRequest(withPayload(requestGetInvoice))
        // then:
        .andExpect(payload(responseGetInvoice));
  }

  @Test
  public void getInvoiceByIdShouldReturnResponseWithFault() {
    // given:
    Source requestGetInvoice =
        new StringSource(
            getSoapEnvelope(BASE_PATH_REQUEST + GET_INVOICE_BY_ID_REQUEST_INVOICE_NOT_FOUND));
    Source responseGetInvoice =
        new StringSource(
            getSoapEnvelope(BASE_PATH_RESPONSE + GET_INVOICE_BY_ID_RESPONSE_INVOICE_NOT_FOUND));
    // when:
    mockClient
        .sendRequest(withPayload(requestGetInvoice))
        // then:
        .andExpect(payload(responseGetInvoice));
  }

  @Test
  public void deleteInvoiceShouldDeleteInvoice() {
    // given:
    Source requestAddInvoice =
        new StringSource(getSoapEnvelope(BASE_PATH_REQUEST + ADD_FIRST_INVOICE_REQUEST));
    Source requestDeleteInvoice =
        new StringSource(getSoapEnvelope(BASE_PATH_REQUEST + DELETE_INVOICE_REQUEST));
    Source requestGetInvoice =
        new StringSource(
            getSoapEnvelope(BASE_PATH_REQUEST + GET_INVOICE_BY_ID_REQUEST_INVOICE_NOT_FOUND));
    Source responseGetInvoiceBeforeDelete =
        new StringSource(getSoapEnvelope(BASE_PATH_RESPONSE + GET_INVOICE_BY_ID_RESPONSE));
    Source responseGetInvoiceAfterDelete =
        new StringSource(
            getSoapEnvelope(BASE_PATH_RESPONSE + GET_INVOICE_BY_ID_RESPONSE_INVOICE_NOT_FOUND));

    mockClient.sendRequest(withPayload(requestAddInvoice)).andExpect(noFault());
    mockClient
        .sendRequest(withPayload(requestGetInvoice))
        .andExpect(payload(responseGetInvoiceBeforeDelete));
    // when:
    mockClient
        .sendRequest(withPayload(requestDeleteInvoice))
        // then:
        .andExpect(noFault());
    mockClient
        .sendRequest(withPayload(requestGetInvoice))
        .andExpect(payload(responseGetInvoiceAfterDelete));
  }

  private String getSoapEnvelope(String filePath) {
    StringBuilder stringBuilder = new StringBuilder();
    try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
      stream.forEach(s -> stringBuilder.append(s));
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return stringBuilder.toString();
  }
}
