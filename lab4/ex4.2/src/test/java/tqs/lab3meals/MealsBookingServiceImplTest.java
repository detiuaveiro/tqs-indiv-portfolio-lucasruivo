package tqs.lab3meals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import tqs.lab3meals.data.MealBookingRepository;
import tqs.lab3meals.data.MealBookingRequest;
import tqs.lab3meals.services.MealsBookingServiceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static java.lang.invoke.MethodHandles.lookup;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MealsBookingServiceImplTest {

    static final Logger log = org.slf4j.LoggerFactory.getLogger(lookup().lookupClass());

    @DisplayName("Booking a meal returns a token")
    @Test
    void bookMealReturnsToken() {
        MealBookingRepository repo = mock(MealBookingRepository.class);
        MealsBookingServiceImpl service = new MealsBookingServiceImpl(repo);

        MealBookingRequest request = new MealBookingRequest("user123", LocalDate.now());
        when(repo.findAll()).thenReturn(List.of());
        when(repo.save(request)).thenReturn(request);

        String token = service.bookMeal(request);
        assertNotNull(token);
        verify(repo, times(1)).save(request);
    }

    @DisplayName("Cannot book if date is full")
    @Test
    void bookMealThrowsIfFull() {
        MealBookingRepository repo = mock(MealBookingRepository.class);
        MealsBookingServiceImpl service = new MealsBookingServiceImpl(repo);

        LocalDate date = LocalDate.now();
        // Cria 10 reservas ativas para a mesma data
        List<MealBookingRequest> existing = 
            java.util.stream.IntStream.range(0, 10)
                .mapToObj(i -> new MealBookingRequest("user" + i, date))
                .toList(); // não marcar como usada, mantém ativo

        when(repo.findAll()).thenReturn(existing);

        MealBookingRequest newRequest = new MealBookingRequest("newUser", date);
        assertThrows(IllegalStateException.class, () -> service.bookMeal(newRequest));
    }

    @DisplayName("Find reservation by token")
    @Test
    void findReservationByToken() {
        MealBookingRepository repo = mock(MealBookingRepository.class);
        MealsBookingServiceImpl service = new MealsBookingServiceImpl(repo);

        MealBookingRequest request = new MealBookingRequest("user123", LocalDate.now());
        when(repo.findByToken("token123")).thenReturn(Optional.of(request));

        Optional<MealBookingRequest> found = service.findReservation("token123");
        assertTrue(found.isPresent());
        assertEquals(request, found.get());
    }

    @DisplayName("Cancel active reservation changes its state")
    @Test
    void cancelActiveReservation() {
        MealBookingRepository repo = mock(MealBookingRepository.class);
        MealsBookingServiceImpl service = new MealsBookingServiceImpl(repo);

        MealBookingRequest request = new MealBookingRequest("user123", LocalDate.now());
        when(repo.findByToken(request.getToken())).thenReturn(Optional.of(request));

        service.cancelReservation(request.getToken());
        assertTrue(request.isCancelled());
        verify(repo).save(request);
    }

    @DisplayName("Cannot cancel non-active reservation")
    @Test
    void cancelNonActiveThrows() {
        MealBookingRepository repo = mock(MealBookingRepository.class);
        MealsBookingServiceImpl service = new MealsBookingServiceImpl(repo);

        MealBookingRequest request = new MealBookingRequest("user123", LocalDate.now());
        request.markUsed(); // simula que já foi usada

        when(repo.findByToken(request.getToken())).thenReturn(Optional.of(request));
        assertThrows(IllegalStateException.class, () -> service.cancelReservation(request.getToken()));
        verify(repo, never()).save(any());
    }

    @DisplayName("Check-in active reservation")
    @Test
    void checkinActiveReservation() {
        MealBookingRepository repo = mock(MealBookingRepository.class);
        MealsBookingServiceImpl service = new MealsBookingServiceImpl(repo);

        MealBookingRequest request = new MealBookingRequest("user123", LocalDate.now());
        when(repo.findByToken(request.getToken())).thenReturn(Optional.of(request));

        service.checkin(request.getToken());
        assertTrue(request.isUsed());
        verify(repo).save(request);
    }

    @DisplayName("Cannot check-in non-active reservation")
    @Test
    void checkinNonActiveThrows() {
        MealBookingRepository repo = mock(MealBookingRepository.class);
        MealsBookingServiceImpl service = new MealsBookingServiceImpl(repo);

        MealBookingRequest request = new MealBookingRequest("user123", LocalDate.now());
        request.cancel(); // simula que já está cancelada

        when(repo.findByToken(request.getToken())).thenReturn(Optional.of(request));
        assertThrows(IllegalStateException.class, () -> service.checkin(request.getToken()));
        verify(repo, never()).save(any());
    }
}
