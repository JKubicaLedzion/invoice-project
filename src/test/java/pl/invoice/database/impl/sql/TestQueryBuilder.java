package pl.invoice.database.impl.sql;

public class TestQueryBuilder {

  public static final String QUERY_TRUNCATE = "DROP TABLE IF EXISTS invoice_entry; DROP TABLE IF EXISTS invoice;"
      + "DROP TABLE IF EXISTS company;";

  public static final String DROP_TABLES_QUERY = "DROP TABLE IF EXISTS \"Invoice_Entry\";"
      + "DROP TABLE IF EXISTS \"Invoice\";"
      + "DROP TABLE IF EXISTS \"Company\";";

  public static final String ADD_TABLES_QUERY = "CREATE TABLE \"Company\" ("
      + " name VARCHAR(255) NOT NULL UNIQUE,"
      + " street VARCHAR(255) NOT NULL,"
      + " postal_code VARCHAR(255) NOT NULL,"
      + " city VARCHAR(255) NOT NULL,"
      + " country VARCHAR(255) NOT NULL,"
      + " phone_number INT,"
      + " vat_no VARCHAR(255) NOT NULL UNIQUE,"
      + " bank_account VARCHAR(255) NOT NULL,"
      + " PRIMARY KEY (vat_no));"
      + " CREATE TABLE \"Invoice\" ("
      + " id BIGSERIAL,"
      + " issue_date DATE NOT NULL,"
      + " modification_date TIMESTAMP NOT NULL,"
      + " due_date DATE NOT NULL,"
      + " vat_no_customer VARCHAR(255) NOT NULL,"
      + " vat_no_supplier VARCHAR(255) NOT NULL,"
      + " is_paid boolean,"
      + " FOREIGN KEY ( vat_no_customer) REFERENCES \"Company\"(vat_no),"
      + " FOREIGN KEY (vat_no_supplier) REFERENCES \"Company\"(vat_no),"
      + " PRIMARY KEY (id),"
      + " CHECK(due_date >= issue_date),"
      + " CHECK (vat_no_customer != vat_no_supplier));"
      + " CREATE TABLE \"Invoice_Entry\" ("
      + " id BIGSERIAL,"
      + " quantity DOUBLE PRECISION NOT NULL,"
      + " description VARCHAR (255) NOT NULL,"
      + " unit_price DECIMAL NOT NULL,"
      + " vat_rate DOUBLE PRECISION,"
      + " invoice_id INT NOT NULL,"
      + " PRIMARY KEY (id),"
      + " FOREIGN KEY (invoice_id) REFERENCES \"Invoice\"(id),"
      + " CHECK(quantity >= 0),"
      + " CHECK(unit_price >= 0));";

}
