package pl.invoice.service.invoice.rest;

import static pl.invoice.service.invoice.ValidationErrorMessages.DATABASE_EMPTY;
import static pl.invoice.service.invoice.Validator.validateDates;
import static pl.invoice.service.invoice.Validator.validateInvoice;

import com.itextpdf.text.DocumentException;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.invoice.exception.InvoiceNotFoundException;
import pl.invoice.model.Invoice;
import pl.invoice.service.invoice.InvoiceService;
import pl.invoice.service.pdf.PdfGenerator;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequestMapping("/invoices")
@RestController
public class InvoiceController {

  private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceController.class);

  private InvoiceService invoiceService;

  private PdfGenerator pdfGenerator;

  @Autowired
  public InvoiceController(InvoiceService invoiceService, PdfGenerator pdfGenerator) {
    this.invoiceService = invoiceService;
    this.pdfGenerator = pdfGenerator;
  }

  @GetMapping
  @ApiOperation(
      value = "Returns all invoices.",
      notes = "Returns a complete list of invoices saved in Database.",
      response = Invoice.class,
      responseContainer = "list")
  public ResponseEntity getInvoices() throws IOException {
    List<Invoice> invoiceList = invoiceService.getInvoices();
    if (invoiceList.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(DATABASE_EMPTY);
    }
    return ResponseEntity.status(HttpStatus.OK).body(invoiceService.getInvoices());
  }

  @GetMapping("{id}")
  @ApiOperation(
      value = "Returns invoice by Id.",
      notes = "Returns a only one invoice with requested Id.",
      response = Invoice.class)
  public ResponseEntity getInvoiceById(@PathVariable int id) throws IOException {
    Optional invoice = invoiceService.getInvoiceById(id);
    if (!invoice.isPresent()) {
      LOGGER.debug("Invoice with id {} not found.", id);
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("Invoice with Id " + id + " not found.");
    }
    return ResponseEntity.status(HttpStatus.OK).body(invoice.get());
  }

  @GetMapping(value = "/dates")
  @ApiOperation(
      value = "Returns invoices within date range.",
      notes = "Returns a list of invoices which issue date is within requested date range.",
      response = Invoice.class,
      responseContainer = "list")
  public ResponseEntity getInvoicesWithinDateRange(
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("fromDate") LocalDate fromDate,
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam("toDate") LocalDate toDate)
      throws IOException {
    String errorMessage = validateDates(fromDate, toDate);
    if (!errorMessage.isEmpty()) {
      LOGGER.debug("Incorrect dates provided.", errorMessage);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }
    List<Invoice> invoiceList = invoiceService.getInvoicesWithinDateRange(fromDate, toDate);
    return ResponseEntity.status(HttpStatus.OK).body(invoiceList);
  }

  @PostMapping
  @ApiOperation(
      value = "Saves invoice to Database.",
      notes = "Saves invoice to Database if all required information are provided.")
  public ResponseEntity addInvoice(@RequestBody Invoice invoice) throws IOException {
    List<String> errorList = validateInvoice(invoice);
    if (!errorList.isEmpty()) {
      LOGGER.debug("Incorrect data provided while adding invoice.", errorList);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorList);
    }
    invoiceService.addInvoice(invoice);
    return ResponseEntity.status(HttpStatus.OK).body(invoice.getId());
  }

  @PutMapping
  @ApiOperation(
      value = "Updates existing invoice.",
      notes = "Updates existing invoice in Database if all required information are provided.")
  public ResponseEntity updateInvoice(@RequestBody Invoice invoice) throws InvoiceNotFoundException, IOException {
    List<String> errorList = validateInvoice(invoice);
    if (!errorList.isEmpty()) {
      LOGGER.debug("Incorrect data provided while updating invoice.", errorList);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorList);
    }
    invoiceService.updateInvoice(invoice);
    return ResponseEntity.status(HttpStatus.OK).body(invoice.getId());
  }

  @DeleteMapping("{id}")
  @ApiOperation(
      value = "Deletes invoice by Id",
      notes = "Deletes invoice with requested Id from database.")
  public ResponseEntity deleteInvoice(@PathVariable int id) throws IOException, InvoiceNotFoundException {
    if (!invoiceService.getInvoiceById(id).isPresent()) {
      LOGGER.debug("Invoice with id {} not found.", id);
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("Invoice with Id " + id + " not found.");
    }
    invoiceService.deleteInvoice(id);
    return ResponseEntity.status(HttpStatus.OK).body("Invoice with id " + id + " deleted.");
  }

  @GetMapping(value = "{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
  @ApiOperation(value = "Get invoice in pdf format", notes = "Generates pdf file for invoice with requested Id.")
  public ResponseEntity getPdfInvoice(@PathVariable int id, @RequestParam String language)
      throws IOException, DocumentException {
    Optional<Invoice> invoice = invoiceService.getInvoiceById(id);
    if (!invoice.isPresent()) {
      LOGGER.debug("Invoice with id {} not found.", id);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invoice with Id " + id + " not found.");
    }
    return ResponseEntity.status(HttpStatus.OK).body(pdfGenerator.getPdfDocument(invoice.get(), language));
  }
}
