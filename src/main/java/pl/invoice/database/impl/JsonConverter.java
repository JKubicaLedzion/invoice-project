package pl.invoice.database.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.invoice.model.Invoice;

import java.io.IOException;

@Component
public final class JsonConverter {

  private final ObjectMapper objectMapper;

  @Autowired
  public JsonConverter(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public String convertInvoiceToJson(Invoice invoice) throws JsonProcessingException {
    return objectMapper.writeValueAsString(invoice);
  }

  public Invoice convertJsonToInvoice(String invoiceAsString) throws IOException {
    return objectMapper.readValue(invoiceAsString, Invoice.class);
  }
}