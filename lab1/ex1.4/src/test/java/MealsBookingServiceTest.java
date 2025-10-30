import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tqs.MealBookingRequest;
import tqs.MealsBookingService;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MealsBookingServiceExtendedTest {

    @Test
    @DisplayName("Check-in with invalid token should fail")
    void checkinInvalidToken() {
        MealsBookingService service = new MealsBookingService(2);
        assertThrows(IllegalArgumentException.class, () -> service.checkin("invalid-token"));
    }

    @Test
    @DisplayName("Cancel already used reservation should fail")
    void cancelUsedReservation() {
        MealsBookingService service = new MealsBookingService(2);
        LocalDate today = LocalDate.now();
        MealBookingRequest req = new MealBookingRequest("student1", today);
        String token = service.bookMeal(req);

        service.checkin(token);
        assertThrows(IllegalStateException.class, () -> service.cancelReservation(token));
    }

    @Test
    @DisplayName("Find reservation returns empty for unknown token")
    void findUnknownToken() {
        MealsBookingService service = new MealsBookingService(2);
        Optional<MealBookingRequest> result = service.findReservation("unknown-token");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Reservation token uniqueness")
    void tokenUniqueness() {
        MealsBookingService service = new MealsBookingService(10);
        LocalDate today = LocalDate.now();

        String token1 = service.bookMeal(new MealBookingRequest("student1", today));
        String token2 = service.bookMeal(new MealBookingRequest("student2", today));

        assertNotEquals(token1, token2);
    }

    @Test
    @DisplayName("Cannot double-book after canceling previous reservation")
    void rebookAfterCancel() {
        MealsBookingService service = new MealsBookingService(2);
        LocalDate today = LocalDate.now();

        MealBookingRequest req = new MealBookingRequest("student1", today);
        String token = service.bookMeal(req);
        service.cancelReservation(token);

        MealBookingRequest newReq = new MealBookingRequest("student1", today);
        assertDoesNotThrow(() -> service.bookMeal(newReq));
    }

    @Test
    @DisplayName("Booking multiple students until capacity reached")
    void multipleReservationsCapacity() {
        MealsBookingService service = new MealsBookingService(3);
        LocalDate today = LocalDate.now();

        MealBookingRequest r1 = new MealBookingRequest("s1", today);
        MealBookingRequest r2 = new MealBookingRequest("s2", today);
        MealBookingRequest r3 = new MealBookingRequest("s3", today);

        service.bookMeal(r1);
        service.bookMeal(r2);
        service.bookMeal(r3);

        MealBookingRequest r4 = new MealBookingRequest("s4", today);
        assertThrows(IllegalStateException.class, () -> service.bookMeal(r4));
    }

    @Test
    @DisplayName("Booking for a past date should fail")
    void bookingPastDate() {
        MealsBookingService service = new MealsBookingService(2);
        LocalDate yesterday = LocalDate.now().minusDays(1);
        MealBookingRequest req = new MealBookingRequest("student1", yesterday);

        assertThrows(IllegalArgumentException.class, () -> service.bookMeal(req));
    }

    @Test
    @DisplayName("Booking for today and tomorrow are treated separately")
    void bookingDifferentDays() {
        MealsBookingService service = new MealsBookingService(2);
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        MealBookingRequest r1 = new MealBookingRequest("student1", today);
        MealBookingRequest r2 = new MealBookingRequest("student1", tomorrow);

        assertDoesNotThrow(() -> service.bookMeal(r1));
        assertDoesNotThrow(() -> service.bookMeal(r2));
    }

    @Test
    @DisplayName("Cancel a non-existent token should fail")
    void cancelUnknownToken() {
        MealsBookingService service = new MealsBookingService(2);
        assertThrows(IllegalArgumentException.class, () -> service.cancelReservation("fake-token"));
    }
}