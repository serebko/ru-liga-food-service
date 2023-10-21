package repositories;

import entities.Courier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@ComponentScan(basePackages = "entities")
public interface CourierRepository extends JpaRepository<Courier, Long> {
    Courier findCourierById(Long id);
}
