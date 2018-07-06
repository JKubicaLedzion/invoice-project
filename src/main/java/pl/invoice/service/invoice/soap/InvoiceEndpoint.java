package pl.invoice.service.invoice.soap;

import static pl.invoice.service.invoice.Validator.validateDates;
import static pl.invoice.service.invoice.Validator.validateInvoice;
import static pl.invoice.service.invoice.soap.SoapConverter.invoiceToSoap;
import static pl.invoice.service.invoice.soap.SoapConverter.soapToModelInvoice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.client.WebServiceFaultException;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import pl.invoice.exception.InvoiceNotFoundException;
import pl.invoice.model.Invoice;
import pl.invoice.service.invoice.InvoiceService;
import pl.invoice.service.invoice.rest.InvoiceController;
import pl.invoice_project.invoices.AddInvoiceRequest;
import pl.invoice_project.invoices.AddInvoiceResponse;
import pl.invoice_project.invoices.DeleteInvoiceRequest;
import pl.invoice_project.invoices.GetInvoiceByIdRequest;
import pl.invoice_project.invoices.GetInvoiceByIdResponse;
import pl.invoice_project.invoices.GetInvoicesRequest;
import pl.invoice_project.invoices.GetInvoicesResponse;
import pl.invoice_project.invoices.GetInvoicesWithinDateRangeRequest;
import pl.invoice_project.invoices.GetInvoicesWithinDateRangeResponse;
import pl.invoice_project.invoices.UpdateInvoiceRequest;
import pl.invoice_project.invoices.UpdateInvoiceResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Endpoint
public class InvoiceEndpoint {

  private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceEndpoint.class);
  private static final String NAMESPACE_URI = "http://invoice-project.pl/invoices";

  private InvoiceService invoiceService;

  @Autowired
  public InvoiceEndpoint(InvoiceService invoiceService) {
    this.invoiceService = invoiceService;
  }

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getInvoicesRequest")
  @ResponsePayload
  public GetInvoicesResponse getInvoices(@RequestPayload GetInvoicesRequest request)
      throws IOException {
    GetInvoicesResponse response = new GetInvoicesResponse();
    List<pl.invoice_project.invoices.Invoice> invoices =
        invoiceService
            .getInvoices()
            .stream()
             .map(invoice -> invoiceToSoap(invoice))
            .collect(Collectors.toList());
    response.getInvoiceList().addAll(invoices);
    return response;
  }

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getInvoiceByIdRequest")
  @ResponsePayload
  public GetInvoiceByIdResponse getInvoiceById(@RequestPayload GetInvoiceByIdRequest request)
      throws IOException, InvoiceNotFoundException {
    GetInvoiceByIdResponse response = new GetInvoiceByIdResponse();
    Optional<Invoice> invoice = invoiceService.getInvoiceById(request.getId());
    ifInvoiceNotFoundThrowException(request.getId());
    response.setInvoice(invoiceToSoap(invoice.get()));
    return response;
  }

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getInvoicesWithinDateRangeRequest")
  @ResponsePayload
  public GetInvoicesWithinDateRangeResponse getInvoicesWithinDateRange(
      @RequestPayload GetInvoicesWithinDateRangeRequest request) throws IOException {
    String errorMessage = validateDates(
        LocalDate.parse(request.getFromDate(), DateTimeFormatter.ISO_DATE),
        LocalDate.parse(request.getToDate(), DateTimeFormatter.ISO_DATE));
    if (!errorMessage.isEmpty()) {
      LOGGER.debug("Incorrect dates provided: {}.", errorMessage);
      throw new WebServiceFaultException("Incorrect dates provided:\n" + errorMessage);
    }

    GetInvoicesWithinDateRangeResponse response = new GetInvoicesWithinDateRangeResponse();
    List<pl.invoice_project.invoices.Invoice> invoices =
        invoiceService
            .getInvoicesWithinDateRange(
                LocalDate.parse(request.getFromDate(), DateTimeFormatter.ISO_DATE),
                LocalDate.parse(request.getToDate(), DateTimeFormatter.ISO_DATE))
            .stream()
            .map(invoice -> invoiceToSoap(invoice))
            .collect(Collectors.toList());
    response.getInvoiceList().addAll(invoices);
    return response;
  }

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "addInvoiceRequest")
  @ResponsePayload
  public AddInvoiceResponse addInvoice(@RequestPayload AddInvoiceRequest request)
      throws IOException {
    Invoice invoice = soapToModelInvoice(request.getInvoice());
    validate(invoice);
    AddInvoiceResponse response = new AddInvoiceResponse();
    response.setId(invoiceService.addInvoice(invoice));
    return response;
  }

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "updateInvoiceRequest")
  @ResponsePayload
  public UpdateInvoiceResponse updateInvoice(@RequestPayload UpdateInvoiceRequest request)
      throws IOException, InvoiceNotFoundException {
    Invoice invoice = soapToModelInvoice(request.getInvoice());
    validate(invoice);
    UpdateInvoiceResponse response = new UpdateInvoiceResponse();
    response.setId(invoiceService.updateInvoice(invoice));
    return response;
  }

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteInvoiceRequest")
  @ResponsePayload
  public void deleteInvoice(@RequestPayload DeleteInvoiceRequest request)
      throws IOException, InvoiceNotFoundException {
    ifInvoiceNotFoundThrowException(request.getId());
    invoiceService.deleteInvoice(request.getId());
  }

  private void ifInvoiceNotFoundThrowException(int id)
      throws IOException, InvoiceNotFoundException {
    if (!invoiceService.getInvoiceById(id).isPresent()) {
      LOGGER.debug("Invoice with id {} not found.", id);
      throw new InvoiceNotFoundException("Invoice with id " + id + " not found.");
    }
  }

  private void validate(Invoice invoice) {
    List<String> errorList = validateInvoice(invoice);
    if (!errorList.isEmpty()) {
      LOGGER.debug("Incorrect invoice data provided.", errorList);
      throw new WebServiceFaultException("Incorrect invoice data provided:\n" + errorList);
    }
  }
}
