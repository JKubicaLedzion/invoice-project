package pl.invoice.database.impl.hibernate;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.invoice.model.Invoice;

import java.time.LocalDate;
import javax.transaction.Transactional;

@Repository
public interface InvoiceRepository extends CrudRepository<Invoice, Integer> {

  @Transactional
  @Modifying
  @Query("update invoice i set "
      + "supplier.vatIdentificationNumber = :supplierVatIdentificationNumber, "
      + "customer.vatIdentificationNumber = :customerVatIdentificationNumber, "
      + "i.issueDate = :issueDate, "
      + "i.dueDate = :dueDate, "
      + "i.isPaid = :isPaid "
      + "where i.id = :id")
  void updateById(
      @Param("supplierVatIdentificationNumber") String supplierVatIdentificationNumber,
      @Param("customerVatIdentificationNumber") String customerVatIdentificationNumber,
      @Param("issueDate") LocalDate issueDate,
      @Param("dueDate") LocalDate dueDate,
      @Param("isPaid") boolean isPaid,
      @Param("id") int id);
}