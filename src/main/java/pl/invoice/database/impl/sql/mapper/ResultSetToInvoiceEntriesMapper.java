package pl.invoice.database.impl.sql.mapper;

import static pl.invoice.database.impl.sql.TableLabel.DESCRIPTION;
import static pl.invoice.database.impl.sql.TableLabel.ENTRY_ID;
import static pl.invoice.database.impl.sql.TableLabel.QUANTITY;
import static pl.invoice.database.impl.sql.TableLabel.UNIT_PRICE;
import static pl.invoice.database.impl.sql.TableLabel.VAT_RATE;

import pl.invoice.model.InvoiceEntry;
import pl.invoice.model.Vat;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultSetToInvoiceEntriesMapper {
  public static InvoiceEntry getInvoiceEntry(ResultSet resultSet) throws SQLException {
    double quantity = resultSet.getDouble(QUANTITY);
    String description = resultSet.getString(DESCRIPTION);
    BigDecimal unitPrice = BigDecimal.valueOf(resultSet.getDouble(UNIT_PRICE));
    Vat vatRate = Vat.getByValue(resultSet.getDouble(VAT_RATE));
    int id = resultSet.getInt(ENTRY_ID);
    return new InvoiceEntry(id, quantity, description, unitPrice, vatRate);
  }
}
