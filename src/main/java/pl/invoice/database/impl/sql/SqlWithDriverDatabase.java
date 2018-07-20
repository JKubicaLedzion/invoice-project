package pl.invoice.database.impl.sql;

import static pl.invoice.database.impl.sql.QueryBuilder.QUERY_ALL_INVOICES;
import static pl.invoice.database.impl.sql.QueryBuilder.QUERY_DELETE_ENTRIES;
import static pl.invoice.database.impl.sql.QueryBuilder.QUERY_DELETE_INVOICE;
import static pl.invoice.database.impl.sql.QueryBuilder.QUERY_ENTRIES;
import static pl.invoice.database.impl.sql.QueryBuilder.QUERY_INVOICE;
import static pl.invoice.database.impl.sql.QueryBuilder.QUERY_INVOICE_IDS;
import static pl.invoice.database.impl.sql.QueryBuilder.QUERY_VAT_NUMBERS;
import static pl.invoice.database.impl.sql.QueryBuilder.insertCompanyQuery;
import static pl.invoice.database.impl.sql.QueryBuilder.insertEntryQuery;
import static pl.invoice.database.impl.sql.QueryBuilder.insertInvoiceQuery;
import static pl.invoice.database.impl.sql.QueryBuilder.updateCompanyQuery;
import static pl.invoice.database.impl.sql.QueryBuilder.updateEntryQuery;
import static pl.invoice.database.impl.sql.QueryBuilder.updateInvoiceQuery;
import static pl.invoice.database.impl.sql.TableLabel.ID;
import static pl.invoice.database.impl.sql.TableLabel.VAT_NO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import pl.invoice.configuration.DbPropertiesConfig;
import pl.invoice.database.Database;
import pl.invoice.database.impl.sql.mapper.ResultSetToInvoiceEntriesMapper;
import pl.invoice.database.impl.sql.mapper.ResultSetToInvoiceMapper;
import pl.invoice.exception.InvoiceNotFoundException;
import pl.invoice.exception.SqlDatabaseException;
import pl.invoice.model.Company;
import pl.invoice.model.Invoice;
import pl.invoice.model.InvoiceEntry;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
@ConditionalOnProperty(name = "pl.invoice.database.Database", havingValue = "sql")
public class SqlWithDriverDatabase implements Database {

  private static final Logger LOGGER = LoggerFactory.getLogger(SqlWithDriverDatabase.class);

  private Connection connection;

  @Autowired
  public SqlWithDriverDatabase(DbPropertiesConfig dbPropertiesConfig) throws SQLException {
    connection = DriverManager.getConnection(dbPropertiesConfig.getSqlDatabase(), dbPropertiesConfig.getSqlDbUser(),
        dbPropertiesConfig.getSqlDbPassword());
  }

  @Override
  public synchronized int saveInvoice(Invoice invoice) {
    addCompanyIfNotExists(invoice.getCustomer());
    addCompanyIfNotExists(invoice.getSupplier());

    executeStatement(insertInvoiceQuery(invoice));

    invoice.setId(getIdsFromAllInvoices().get(0));
    LOGGER.info("Saving invoice {} to Db.", invoice);
    for (InvoiceEntry invoiceEntry : invoice.getEntryList()) {
      executeStatement(insertEntryQuery(invoice.getId(), invoiceEntry));
    }
    return invoice.getId();
  }

  @Override
  public Optional<Invoice> getInvoiceById(int id) {
    try (PreparedStatement preparedStatement = connection.prepareStatement(QUERY_INVOICE)) {
      preparedStatement.setInt(1, id);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (!resultSet.next()) {
          return Optional.empty();
        }
        return Optional.ofNullable(buildInvoice(resultSet));
      }
    } catch (SQLException ex) {
      LOGGER.error("Error while getting invoice with id {} from ResultSet.", id, ex);
      throw new SqlDatabaseException(ex.getMessage());
    }
  }

  @Override
  public int updateInvoice(Invoice invoice) throws InvoiceNotFoundException {
    LOGGER.info("Updating invoice {} in Db.", invoice);
    isInvoiceInDatabase(invoice.getId());

    addOrUpdateCompany(invoice.getCustomer());
    addOrUpdateCompany(invoice.getSupplier());

    invoice.setModificationTime(LocalDateTime.now());
    executeStatement(updateInvoiceQuery(invoice));

    for (InvoiceEntry invoiceEntry : invoice.getEntryList()) {
      executeStatement(updateEntryQuery(invoice.getId(), invoiceEntry));
    }
    return invoice.getId();
  }

  @Override
  public List<Invoice> getInvoices() {
    List<Invoice> invoiceList = new ArrayList<>();

    try (Statement statement = connection.createStatement()) {
      try (ResultSet resultSet = statement.executeQuery(QUERY_ALL_INVOICES)) {
        if (!resultSet.next()) {
          return Collections.emptyList();
        }
        do {
          invoiceList.add(buildInvoice(resultSet));
        } while (resultSet.next());
      }
    } catch (SQLException ex) {
      LOGGER.error("Error while getting all invoices from ResultSet.", ex);
      throw new SqlDatabaseException(ex.getMessage());
    }
    invoiceList.sort(Comparator.comparingInt(Invoice::getId));

    return invoiceList;
  }

  @Override
  public void deleteInvoice(int id) throws InvoiceNotFoundException {
    LOGGER.info("Deleting invoice with id {} from Database.", id);
    isInvoiceInDatabase(id);
    executePreparedStatementWithParameter(id, QUERY_DELETE_ENTRIES);
    executePreparedStatementWithParameter(id, QUERY_DELETE_INVOICE);
  }

  private void isInvoiceInDatabase(int id) throws InvoiceNotFoundException {
    if (!getIdsFromAllInvoices().contains(id)) {
      LOGGER.info("Invoice with id {} not found.", id);
      throw new InvoiceNotFoundException("Invoice not in database.");
    }
  }

  private List<Integer> getIdsFromAllInvoices() {
    List<Integer> invoiceIds = new ArrayList<>();
    try (Statement statement = connection.createStatement()) {
      try (ResultSet resultSet = statement.executeQuery(QUERY_INVOICE_IDS)) {
        while (resultSet.next()) {
          invoiceIds.add(resultSet.getInt(ID));
        }
      }
    } catch (SQLException ex) {
      LOGGER.error("Error while getting invoice IDs numbers from ResultSet.", ex);
      throw new SqlDatabaseException(ex.getMessage());
    }
    return invoiceIds;
  }

  private List<InvoiceEntry> getEntryList(int id) throws SQLException {
    List<InvoiceEntry> entryList;

    try (PreparedStatement preparedStatementEntries = connection.prepareStatement(QUERY_ENTRIES)) {
      preparedStatementEntries.setInt(1, id);
      try (ResultSet resultSet = preparedStatementEntries.executeQuery()) {
        entryList = new ArrayList<>();
        while (resultSet.next()) {
          entryList.add(ResultSetToInvoiceEntriesMapper.getInvoiceEntry(resultSet));
        }
      }
    }
    return entryList;
  }

  private void addOrUpdateCompany(Company company) {
    if (!getCompanyVatNumbers().contains(company.getVatNumber())) {
      addCompany(company);
    } else {
      updateCompany(company);
    }
  }

  private void addCompanyIfNotExists(Company company) {
    if (!getCompanyVatNumbers().contains(company.getVatNumber())) {
      addCompany(company);
    }
  }

  private void addCompany(Company company) {
    executeStatement(insertCompanyQuery(company));
  }

  private void updateCompany(Company customer) {
    executeStatement(updateCompanyQuery(customer));
  }

  private void executeStatement(String query) {
    try (Statement statement = connection.createStatement()) {
      statement.executeUpdate(query);
    } catch (SQLException ex) {
      LOGGER.error("Error while executing statement with query {}.", query, ex);
      throw new SqlDatabaseException(ex.getMessage());
    }
  }

  private void executePreparedStatementWithParameter(int id, String query) {
    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setInt(1, id);
      preparedStatement.executeUpdate();
    } catch (SQLException ex) {
      LOGGER.error("Error while executing prepared statement with query {}.", query, ex);
      throw new SqlDatabaseException(ex.getMessage());
    }
  }

  private List<String> getCompanyVatNumbers() {
    List<String> companiesVtNumbers = new ArrayList<>();
    try (Statement statement = connection.createStatement()) {
      try (ResultSet resultSet = statement.executeQuery(QUERY_VAT_NUMBERS)) {
        while (resultSet.next()) {
          companiesVtNumbers.add(resultSet.getString(VAT_NO));
        }
      }
    } catch (SQLException ex) {
      LOGGER.error("Error while getting VAT numbers from ResultSet.", ex);
      throw new SqlDatabaseException(ex.getMessage());
    }
    return companiesVtNumbers;
  }

  private Invoice buildInvoice(ResultSet resultSet) throws SQLException {
    List<InvoiceEntry> entryList = getEntryList(resultSet.getInt(ID));
    return ResultSetToInvoiceMapper.buildInvoice(resultSet, entryList);
  }
}
