package tqs.lab3meals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import tqs.lab3meals.data.MealBookingRepository;
import tqs.lab3meals.data.MealBookingRequest;
import tqs.lab3meals.data.MealBookingRequest.RESERVATION_STATE;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MealBookingRepositoryTest {

    @Autowired
    private MealBookingRepository repository;

    @DisplayName("Save and retrieve a booking by token")
    @Test
    void saveAndFindByToken() {
        MealBookingRequest request = new MealBookingRequest("user1", LocalDate.now());
        repository.save(request);

        MealBookingRequest found = repository.findByToken(request.getToken()).orElseThrow();
        assertEquals("user1", found.getUserId());
        assertEquals(request.getToken(), found.getToken());
    }

    @DisplayName("Custom query: find by user and date")
    @Test
    void findByUserAndDate() {
        LocalDate today = LocalDate.now();
        MealBookingRequest request1 = new MealBookingRequest("userA", today);
        MealBookingRequest request2 = new MealBookingRequest("userA", today.plusDays(1));
        MealBookingRequest request3 = new MealBookingRequest("userB", today);

        repository.saveAll(List.of(request1, request2, request3));

        List<MealBookingRequest> results = repository.findByUserAndDate("userA", today);
        assertEquals(1, results.size());
        assertEquals("userA", results.get(0).getUserId());
        assertEquals(today, results.get(0).getDate());
    }
}
