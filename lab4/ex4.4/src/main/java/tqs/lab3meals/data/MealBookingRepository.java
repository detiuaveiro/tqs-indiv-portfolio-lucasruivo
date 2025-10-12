package tqs.lab3meals.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MealBookingRepository extends JpaRepository<MealBookingRequest, Long> {
    Optional<MealBookingRequest> findByToken(String token);

    @Query("SELECT m FROM MealBookingRequest m WHERE m.userId = :userId AND m.date = :date")
    List<MealBookingRequest> findByUserAndDate(@Param("userId") String userId, @Param("date") LocalDate date);
}

