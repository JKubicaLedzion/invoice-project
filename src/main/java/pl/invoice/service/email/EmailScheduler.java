package pl.invoice.service.email;

import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.invoice.service.invoice.InvoiceService;

import java.io.IOException;
import java.time.LocalDateTime;
import javax.mail.MessagingException;

@EnableScheduling
@Component
public class EmailScheduler {

  /*
  E-mail with pdf invoices updated or added during last 24 hours sent every day at 23:59.
  */
  private static final String CRON_EXPRESSION = "0 07 15 * * *";

  @Value("${emailLanguage}")
  private String language;

  private EmailService emailService;
  private InvoiceService invoiceService;

  @Autowired
  public EmailScheduler(EmailService emailService, InvoiceService invoiceService) {
    this.emailService = emailService;
    this.invoiceService = invoiceService;
  }

  @Scheduled(cron = CRON_EXPRESSION)
  public void sendScheduledEmail() throws IOException, MessagingException, DocumentException {
    LocalDateTime toDate = LocalDateTime.now();
    LocalDateTime fromDate = toDate.minusHours(24);
    emailService.sendScheduledMail(invoiceService.getLastUpdatedInvoices(fromDate, toDate), language);
  }
}
