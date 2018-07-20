package pl.invoice.database.impl.sql.mapper;

import static pl.invoice.database.impl.sql.TableLabel.DUE_DATE;
import static pl.invoice.database.impl.sql.TableLabel.ID;
import static pl.invoice.database.impl.sql.TableLabel.ISSUE_DATE;
import static pl.invoice.database.impl.sql.TableLabel.IS_PAID;
import static pl.invoice.database.impl.sql.TableLabel.MODIFICATION_DATE;

import pl.invoice.builders.InvoiceBuilder;
import pl.invoice.model.Invoice;
import pl.invoice.model.InvoiceEntry;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ResultSetToInvoiceMapper {

  public static Invoice buildInvoice(ResultSet resultSet, List<InvoiceEntry> entryList) throws SQLException {
    return new InvoiceBuilder()
        .id(resultSet.getInt(ID))
        .customer(ResultSetToCompanyMapper.getCustomer(resultSet))
        .supplier(ResultSetToCompanyMapper.getSupplier(resultSet))
        .issueDate(LocalDate.parse(resultSet.getString(ISSUE_DATE), DateTimeFormatter.ISO_DATE))
        .dueDate(LocalDate.parse(resultSet.getString(DUE_DATE), DateTimeFormatter.ISO_DATE))
        .modificationDate(resultSet.getTimestamp(MODIFICATION_DATE).toLocalDateTime())
        .entryList(entryList)
        .isPaid(resultSet.getBoolean(IS_PAID))
        .build();
  }
}
