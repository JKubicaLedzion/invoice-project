package pl.invoice.database.impl.hibernate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import pl.invoice.database.Database;
import pl.invoice.database.impl.sql.SqlWithDriverDatabase;
import pl.invoice.exception.InvoiceNotFoundException;
import pl.invoice.model.Company;
import pl.invoice.model.Invoice;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.transaction.Transactional;

@Repository
@ConditionalOnProperty(name = "pl.invoice.database.Database", havingValue = "hibernate")
public class HibernateDatabase implements Database {

  private static final Logger LOGGER = LoggerFactory.getLogger(HibernateDatabase.class);

  private InvoiceRepository invoiceRepository;
  private CompanyRepository companyRepository;
  private InvoiceEntryRepository invoiceEntryRepository;

  @Autowired
  public HibernateDatabase(InvoiceRepository invoiceRepository, CompanyRepository companyRepository,
      InvoiceEntryRepository invoiceEntryRepository) {
    this.invoiceRepository = invoiceRepository;
    this.companyRepository = companyRepository;
    this.invoiceEntryRepository = invoiceEntryRepository;
  }

  @Transactional(rollbackOn = Exception.class)
  @Override
  public int saveInvoice(Invoice invoice) {
    LOGGER.info("Saving invoice {} to Db.", invoice);

    addCompanyIfNotExists(invoice.getCustomer());
    addCompanyIfNotExists(invoice.getSupplier());
    return invoiceRepository.save(invoice).getId();
  }

  @Override
  public Optional<Invoice> getInvoiceById(int id) {
    return invoiceRepository.findById(id);
  }

  @Transactional(rollbackOn = Exception.class)
  @Override
  public int updateInvoice(Invoice invoice) throws InvoiceNotFoundException {
    LOGGER.info("Updating invoice {} in Db.", invoice);
    ifInvoiceNotFoundThrowException(invoice.getId());

    addOrUpdateCompany(invoice.getCustomer());
    addOrUpdateCompany(invoice.getSupplier());

    invoiceRepository.save(invoice).getId();
    return invoice.getId();
  }

  @Override
  public List<Invoice> getInvoices() {
    return StreamSupport.stream(invoiceRepository
        .findAll()
        .spliterator(), false)
        .collect(Collectors.toList());
  }

  @Transactional(rollbackOn = Exception.class)
  @Override
  public void deleteInvoice(int id) throws InvoiceNotFoundException {
    LOGGER.info("Deleting invoice with id {} from Database.", id);
    ifInvoiceNotFoundThrowException(id);

    invoiceEntryRepository.deleteAll(invoiceRepository.findById(id).get().getEntryList());
    invoiceRepository.deleteById(id);
  }

  private void addOrUpdateCompany(Company company) {
    if (companyRepository.findByVatIdentificationNumber(company.getVatNumber()).isPresent()) {
      updateCompany(company);
    } else {
      companyRepository.save(company);
    }
  }

  private void updateCompany(Company company) {
    companyRepository
        .updateByVatIdentificationNumber(company.getName(), company.getPhoneNumber(), company.getBankAccount(),
            company.getAddress().getStreet(), company.getAddress().getPostalCode(), company.getAddress().getCity(),
            company.getAddress().getCountry(), company.getVatNumber());
  }

  private void addCompanyIfNotExists(Company company) {
    if (!companyRepository.findByVatIdentificationNumber(company.getVatNumber()).isPresent()) {
      companyRepository.save(company);
    }
  }

  private void ifInvoiceNotFoundThrowException(int id) throws InvoiceNotFoundException {
    if (!getInvoiceById(id).isPresent()) {
      LOGGER.info("Invoice with id {} not found.", id);
      throw new InvoiceNotFoundException("Invoice not in database.");
    }
  }
}
