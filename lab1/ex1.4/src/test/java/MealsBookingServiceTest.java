package test.java;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import tqs.MealBookingRequest;
import tqs.MealsBookingService;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MealsBookingServiceTest {

    static final Logger log = org.slf4j.LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @DisplayName("Book a meal and check-in")
    @Test
    void bookAndCheckIn() {
        MealsBookingService service = new MealsBookingService(2);
        LocalDate today = LocalDate.now();
        MealBookingRequest req = new MealBookingRequest("student1", today);

        log.debug("Booking meal for {}", req.getUserId());
        String token = service.bookMeal(req);
        assertNotNull(token);

        Optional<MealBookingRequest> found = service.findReservation(token);
        assertTrue(found.isPresent());
        assertEquals(MealBookingRequest.RESERVATION_STATE.ACTIVE, found.get().getState());

        log.debug("Checking in with token {}", token);
        service.checkin(token);
        assertEquals(MealBookingRequest.RESERVATION_STATE.USED, req.getState());

        log.debug("Attempting second check-in (should fail)");
        assertThrows(IllegalStateException.class, () -> service.checkin(token));
    }

    @DisplayName("Cancel reservation")
    @Test
    void cancelReservation() {
        MealsBookingService service = new MealsBookingService(1);
        LocalDate today = LocalDate.now();
        MealBookingRequest req = new MealBookingRequest("student2", today);

        String token = service.bookMeal(req);
        log.debug("Canceling reservation for token {}", token);
        service.cancelReservation(token);

        assertEquals(MealBookingRequest.RESERVATION_STATE.CANCELED, req.getState());
        assertThrows(IllegalStateException.class, () -> service.checkin(token));
    }

    @DisplayName("Prevent double booking per student per day")
    @Test
    void doubleBooking() {
        MealsBookingService service = new MealsBookingService(2);
        LocalDate today = LocalDate.now();
        MealBookingRequest req1 = new MealBookingRequest("student3", today);
        MealBookingRequest req2 = new MealBookingRequest("student3", today);

        log.debug("Booking first reservation for student {}", req1.getUserId());
        service.bookMeal(req1);

        log.debug("Attempting double booking (should fail)");
        assertThrows(IllegalStateException.class, () -> service.bookMeal(req2));
    }

    @DisplayName("Enforce maximum capacity")
    @Test
    void maxCapacity() {
        MealsBookingService service = new MealsBookingService(1);
        LocalDate today = LocalDate.now();
        MealBookingRequest req1 = new MealBookingRequest("s1", today);
        MealBookingRequest req2 = new MealBookingRequest("s2", today);

        log.debug("Booking first reservation for student {}", req1.getUserId());
        service.bookMeal(req1);

        log.debug("Booking second reservation should fail due to capacity");
        assertThrows(IllegalStateException.class, () -> service.bookMeal(req2));
    }
}