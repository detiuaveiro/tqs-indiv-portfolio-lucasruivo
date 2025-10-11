package tqs.lab3meals.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MealBookingRepository extends JpaRepository<MealBookingRequest, Long> {
    Optional<MealBookingRequest> findByToken(String token);
}
