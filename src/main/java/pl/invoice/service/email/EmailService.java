package pl.invoice.service.email;

import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import pl.invoice.model.Invoice;
import pl.invoice.service.pdf.PdfGenerator;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@EnableScheduling
@Service
public class EmailService {

  private static final String TEXT_NEW_INVOICE = "newInvoice";
  private static final String TEXT_MODIFIED_INVOICES = "lastModifiedInvoices";
  private static final String TEXT_MODIFIED_INVOICES_LIST = "lastModifiedInvoicesList";
  private static final String TEXT_INVOICE = "invoice";
  private static final String EMAIL = "email_";
  private static final String EXTENSION = ".properties";

  Properties properties;

  private JavaMailSender javaMailSender;
  private String mailFrom;
  private String mailTo;

  @Autowired
  public EmailService(JavaMailSender javaMailSender, @Value("${spring.mail.username}") String mailFrom,
      @Value("${spring.mail.sent.to}") String mailTo) {
    this.javaMailSender = javaMailSender;
    this.mailFrom = mailFrom;
    this.mailTo = mailTo;
    properties = new Properties();
  }

  public void sendMailWithNewInvoice(Invoice invoice, String language) throws MessagingException, IOException,
      DocumentException {

    properties.load(getClass().getClassLoader().getResourceAsStream(EMAIL + language + EXTENSION));

    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
    String text = getText(TEXT_NEW_INVOICE) + invoice.getId();
    MimeMessageHelper helper = getMimeMessageHelper(mimeMessage, text, text);
    addAttachment(helper, invoice, language);

    javaMailSender.send(mimeMessage);
  }

  public void sendScheduledMail(List<Invoice> invoiceList, String language) throws MessagingException, IOException,
      DocumentException {

    properties.load(getClass().getClassLoader().getResourceAsStream(EMAIL + language + EXTENSION));

    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
    String text = getText(TEXT_MODIFIED_INVOICES_LIST) + invoiceList.stream()
            .map(Invoice::getId)
            .map(id -> id.toString())
            .collect(Collectors.joining(", "));
    String subject = getText(TEXT_MODIFIED_INVOICES);

    MimeMessageHelper helper = getMimeMessageHelper(mimeMessage, text, subject);
    for (Invoice invoice : invoiceList) {
      addAttachment(helper, invoice, language);
    }
    javaMailSender.send(mimeMessage);
  }

  private MimeMessageHelper getMimeMessageHelper( MimeMessage mimeMessage, String text, String subject)
      throws MessagingException {
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
    helper.setText(text);
    helper.setFrom(mailFrom);
    helper.setTo(mailTo);
    helper.setSubject(subject);
    return helper;
  }

  private void addAttachment(MimeMessageHelper helper, Invoice invoice, String language) throws IOException,
      DocumentException, MessagingException {
    PdfGenerator pdfGenerator = new PdfGenerator();
    byte[] in = pdfGenerator.getPdfDocument(invoice, language);
    helper.addAttachment(getText(TEXT_INVOICE) + invoice.getId(), new ByteArrayResource(in),
        MediaType.APPLICATION_PDF_VALUE);
  }

  private String getText(String key) {
    return properties.get(key).toString();
  }
}
