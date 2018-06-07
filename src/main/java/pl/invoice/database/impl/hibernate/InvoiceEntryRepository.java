package pl.invoice.database.impl.hibernate;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.invoice.model.InvoiceEntry;

@Repository
public interface InvoiceEntryRepository extends CrudRepository<InvoiceEntry, Integer> {
}