package repositories;

import entities.Customer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@ComponentScan(basePackages = "entities")
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Transactional
    Customer findCustomerById(Long id);
}
