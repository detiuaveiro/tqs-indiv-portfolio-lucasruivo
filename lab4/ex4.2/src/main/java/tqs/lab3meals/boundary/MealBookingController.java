package tqs.lab3meals.boundary;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tqs.lab3meals.data.MealBookingRequest;
import tqs.lab3meals.services.MealsBookingServiceImpl;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class MealBookingController {

    private final MealsBookingServiceImpl bookingService;

    public MealBookingController(MealsBookingServiceImpl bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createBooking(@RequestBody Map<String, String> payload) {
        try {
            String userId = payload.get("userId");
            LocalDate date = LocalDate.parse(payload.get("date"));
            String token = bookingService.bookMeal(new MealBookingRequest(userId, date));

            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);

        } catch (IllegalStateException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping
    public ResponseEntity<List<MealBookingRequest>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("/{token}")
    public ResponseEntity<MealBookingRequest> getBooking(@PathVariable String token) {
        return bookingService.findReservation(token)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{token}")
    public ResponseEntity<Map<String, String>> cancelBooking(@PathVariable String token) {
        try {
            bookingService.cancelReservation(token);

            Map<String, String> response = new HashMap<>();
            response.put("status", "cancelled");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/{token}/checkin")
    public ResponseEntity<Map<String, String>> checkin(@PathVariable String token) {
        try {
            bookingService.checkin(token);

            Map<String, String> response = new HashMap<>();
            response.put("status", "used");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
