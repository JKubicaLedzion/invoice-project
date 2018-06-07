package pl.invoice.database.impl.hibernate;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.invoice.model.Company;

import java.util.Optional;
import javax.transaction.Transactional;

@Repository
public interface CompanyRepository extends CrudRepository<Company, Integer> {

  Optional<Company> findByVatIdentificationNumber(String vatIdentificationNumber);

  @Transactional(rollbackOn = Exception.class)
  @Modifying
  @Query("update company c set "
      + "c.name = :name, "
      + "c.phoneNumber = :phoneNumber, "
      + "c.bankAccount = :bankAccount, "
      + "address.street = :street, "
      + "address.postalCode = :postalCode, "
      + "address.city = :city, "
      + "address.country = :country "
      + "where c.vatIdentificationNumber = :vatIdentificationNumber")
  void updateByVatIdentificationNumber(
      @Param("name") String name,
      @Param("phoneNumber") int phoneNumber,
      @Param("bankAccount") String bankAccount,
      @Param("street") String street,
      @Param("postalCode") String postalCode,
      @Param("city") String city,
      @Param("country") String country,
      @Param("vatIdentificationNumber") String vatIdentificationNumber);
}