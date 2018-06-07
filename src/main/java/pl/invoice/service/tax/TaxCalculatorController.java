package pl.invoice.service.tax;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.invoice.model.Invoice;
import pl.invoice.service.invoice.InvoiceService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RequestMapping("/invoices")
@RestController
public class TaxCalculatorController {

  private static final String EMPTY_LIST_MSG = "Invoice list is empty.";

  private TaxCalculatorService taxCalculatorService;
  private InvoiceService invoiceService;

  @Autowired
  public TaxCalculatorController(TaxCalculatorService taxCalculatorService,
      InvoiceService invoiceService) {
    this.taxCalculatorService = taxCalculatorService;
    this.invoiceService = invoiceService;
  }

  @GetMapping("/income/{vatNo}")
  @ApiOperation(
      value = "Returns income amount for chosen company.",
      notes = "Returns income amount from all invoices for company with VAT ID provided.",
      response = BigDecimal.class)
  public ResponseEntity<CalculationResult> getIncomeAmount(@PathVariable String vatNo) throws IOException {
    List<Invoice> invoiceList = invoiceService.getInvoices();
    if (invoiceList.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CalculationResult(EMPTY_LIST_MSG));
    }
    BigDecimal income = taxCalculatorService.calculateIncome(vatNo, invoiceList);
    return ResponseEntity.status(HttpStatus.OK).body(new CalculationResult("Income amount", income));
  }

  @GetMapping("/cost/{vatNo}")
  @ApiOperation(
      value = "Returns cost amount for chosen company.",
      notes = "Returns cost amount from all invoices for company with VAT ID provided.",
      response = BigDecimal.class)
  public ResponseEntity<CalculationResult> getCostAmount(@PathVariable String vatNo) throws IOException {
    List<Invoice> invoiceList = invoiceService.getInvoices();
    if (invoiceList.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CalculationResult(EMPTY_LIST_MSG));
    }
    BigDecimal cost = taxCalculatorService.calculateCost(vatNo, invoiceList);
    return ResponseEntity.status(HttpStatus.OK).body(new CalculationResult("Cost amount", cost));
  }

  @GetMapping("/outputVat/{vatNo}")
  @ApiOperation(
      value = "Returns output VAT amount for chosen company.",
      notes = "Returns output VAT amount from all invoices for company with VAT ID provided.",
      response = BigDecimal.class)
  public ResponseEntity<CalculationResult> getOutputVatAmount(@PathVariable String vatNo) throws IOException {
    List<Invoice> invoiceList = invoiceService.getInvoices();
    if (invoiceList.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CalculationResult(EMPTY_LIST_MSG));
    }
    BigDecimal outputVat = taxCalculatorService.calculateOutputVat(vatNo, invoiceList);
    return ResponseEntity.status(HttpStatus.OK).body(new CalculationResult("Output VAT amount", outputVat));
  }

  @GetMapping("/inputVat/{vatNo}")
  @ApiOperation(
      value = "Returns input VAT amount for chosen company.",
      notes = "Returns input VAT amount from all invoices for company with VAT ID provided.",
      response = BigDecimal.class)
  public ResponseEntity<CalculationResult> getInputVatAmount(@PathVariable String vatNo) throws IOException {
    List<Invoice> invoiceList = invoiceService.getInvoices();
    if (invoiceList.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CalculationResult(EMPTY_LIST_MSG));
    }
    BigDecimal inputVat = taxCalculatorService.calculateInputVat(vatNo, invoiceList);
    return ResponseEntity.status(HttpStatus.OK).body(new CalculationResult("Input VAT amount", inputVat));
  }

  @GetMapping("/vatPayable/{vatNo}")
  @ApiOperation(
      value = "Returns VAT payable amount for chosen company.",
      notes = "Returns VAT payable amount from all invoices for company with VAT ID provided.",
      response = BigDecimal.class)
  public ResponseEntity<CalculationResult> getVatPayableAmount(@PathVariable String vatNo) throws IOException {
    List<Invoice> invoiceList = invoiceService.getInvoices();
    if (invoiceList.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CalculationResult(EMPTY_LIST_MSG));
    }
    BigDecimal vatPayable = taxCalculatorService.calculateVatPayable(vatNo, invoiceList);
    return ResponseEntity.status(HttpStatus.OK).body(new CalculationResult("VAT payable amount", vatPayable));
  }

  @GetMapping("/profit/{vatNo}")
  @ApiOperation(
      value = "Returns profit amount for chosen company.",
      notes = "Returns profit amount from all invoices for company with VAT ID provided.",
      response = BigDecimal.class)
  public ResponseEntity<CalculationResult> getProfit(@PathVariable String vatNo) throws IOException {
    List<Invoice> invoiceList = invoiceService.getInvoices();
    if (invoiceList.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CalculationResult(EMPTY_LIST_MSG));
    }
    BigDecimal profit = taxCalculatorService.calculateProfit(vatNo, invoiceList);
    return ResponseEntity.status(HttpStatus.OK).body(new CalculationResult("Profit amount", profit));
  }
}
