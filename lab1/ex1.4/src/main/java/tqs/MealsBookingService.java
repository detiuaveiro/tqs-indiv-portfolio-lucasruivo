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

    public String bookMeal(MealBookingRequest request) {
        if (request.getDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot book for a past date");
        }
    
        List<MealBookingRequest> dailyReservations = reservations.computeIfAbsent(
                request.getDate(), k -> new ArrayList<>());
    
        boolean alreadyBooked = dailyReservations.stream()
                .anyMatch(r -> r.getUserId().equals(request.getUserId()) && r.getState() == MealBookingRequest.RESERVATION_STATE.ACTIVE);
        if (alreadyBooked) {
            throw new IllegalStateException("User already has a booking for this date");
        }
    
        long activeCount = dailyReservations.stream()
                .filter(r -> r.getState() == MealBookingRequest.RESERVATION_STATE.ACTIVE)
                .count();
        if (activeCount >= maxCapacityPerDate) {
            throw new IllegalStateException("No capacity for this date");
        }
    
        // Garante token único (mesmo que raro colisão)
        String token;
        do {
            token = UUID.randomUUID().toString();
        } while (tokenMap.containsKey(token));
    
        tokenMap.put(token, request);
        dailyReservations.add(request);
        return token;
    }

    public Optional<MealBookingRequest> findReservation(String token) {
        return Optional.ofNullable(tokenMap.get(token));
    }
    
    public void cancelReservation(String token) {
        MealBookingRequest req = tokenMap.get(token);
        if (req == null) throw new IllegalArgumentException("Reservation token not found");
        if (req.getState() != MealBookingRequest.RESERVATION_STATE.ACTIVE) {
            throw new IllegalStateException("Cannot cancel non-active reservation");
        }
        req.cancel();
    }
    
    public void checkin(String token) {
        MealBookingRequest req = tokenMap.get(token);
        if (req == null) throw new IllegalArgumentException("Reservation token not found");
        if (req.getState() != MealBookingRequest.RESERVATION_STATE.ACTIVE) {
            throw new IllegalStateException("Reservation is not active");
        }
        req.markUsed();
    }
}