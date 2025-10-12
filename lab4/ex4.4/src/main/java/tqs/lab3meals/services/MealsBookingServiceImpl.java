package tqs.lab3meals.services;

import org.springframework.stereotype.Service;
import tqs.lab3meals.data.MealBookingRequest;
import tqs.lab3meals.data.MealBookingRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class MealsBookingServiceImpl {

    private final MealBookingRepository repository;
    private final int maxCapacityPerDate = 10;

    public MealsBookingServiceImpl(MealBookingRepository repository) {
        this.repository = repository;
    }

    public String bookMeal(MealBookingRequest request) {
        long activeCount = repository.findAll().stream()
                .filter(r -> r.getDate().equals(request.getDate()) &&
                             r.getState() == MealBookingRequest.RESERVATION_STATE.ACTIVE)
                .count();

        if (activeCount >= maxCapacityPerDate) {
            throw new IllegalStateException("No capacity for this date");
        }

        repository.save(request);
        return request.getToken();
    }

    public Optional<MealBookingRequest> findReservation(String token) {
        return repository.findByToken(token);
    }

    public List<MealBookingRequest> getAllBookings() {
        return repository.findAll();
    }

    public void cancelReservation(String token) {
        MealBookingRequest req = repository.findByToken(token)
                .orElseThrow(() -> new NoSuchElementException("Reservation not found"));
        if (req.getState() != MealBookingRequest.RESERVATION_STATE.ACTIVE) {
            throw new IllegalStateException("Cannot cancel non-active reservation");
        }
        req.cancel();
        repository.save(req);
    }

    public void checkin(String token) {
        MealBookingRequest req = repository.findByToken(token)
                .orElseThrow(() -> new NoSuchElementException("Reservation not found"));
        if (req.getState() != MealBookingRequest.RESERVATION_STATE.ACTIVE) {
            throw new IllegalStateException("Reservation is not active");
        }
        req.markUsed();
        repository.save(req);
    }
}
