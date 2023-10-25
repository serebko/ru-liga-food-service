package repositories;

import entities.CustomerEntity;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@ComponentScan(basePackages = "entities")
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
}
