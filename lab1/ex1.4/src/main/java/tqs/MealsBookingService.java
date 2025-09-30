package tqs;

import java.util.*;
import java.time.LocalDate;  

public class MealsBookingService {

    private final int maxCapacityPerDate;
    private final Map<LocalDate, List<MealBookingRequest>> reservations = new HashMap<>();
    private final Map<String, MealBookingRequest> tokenMap = new HashMap<>();

    public MealsBookingService(int maxCapacityPerDate) {
        this.maxCapacityPerDate = maxCapacityPerDate;
    }

    // Book a meal: returns a token
    public String bookMeal(MealBookingRequest request) {
        List<MealBookingRequest> dailyReservations = reservations.computeIfAbsent(
                request.getDate(), k -> new ArrayList<>());

        // Check for double booking
        boolean alreadyBooked = dailyReservations.stream()
                .anyMatch(r -> r.getUserId().equals(request.getUserId()) && r.getState() == MealBookingRequest.RESERVATION_STATE.ACTIVE);
        if (alreadyBooked) {
            throw new IllegalStateException("User already has a booking for this date");
        }

        // Check capacity
        long activeCount = dailyReservations.stream()
                .filter(r -> r.getState() == MealBookingRequest.RESERVATION_STATE.ACTIVE)
                .count();
        if (activeCount >= maxCapacityPerDate) {
            throw new IllegalStateException("No capacity for this date");
        }

        // Generate token
        String token = UUID.randomUUID().toString();
        tokenMap.put(token, request);
        dailyReservations.add(request);
        return token;
    }

    // Find reservation by token
    public Optional<MealBookingRequest> findReservation(String token) {
        return Optional.ofNullable(tokenMap.get(token));
    }

    // Cancel reservation
    public void cancelReservation(String token) {
        MealBookingRequest req = tokenMap.get(token);
        if (req == null) throw new NoSuchElementException("Reservation not found");
        if (req.getState() != MealBookingRequest.RESERVATION_STATE.ACTIVE) {
            throw new IllegalStateException("Cannot cancel non-active reservation");
        }
        req.cancel();
    }

    // Check-in
    public void checkin(String token) {
        MealBookingRequest req = tokenMap.get(token);
        if (req == null) throw new NoSuchElementException("Reservation not found");
        if (req.getState() != MealBookingRequest.RESERVATION_STATE.ACTIVE) {
            throw new IllegalStateException("Reservation is not active");
        }
        req.markUsed();
    }
}