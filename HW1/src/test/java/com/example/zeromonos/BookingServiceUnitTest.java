package com.example.zeromonos;

import com.example.zeromonos.data.Booking;
import com.example.zeromonos.data.BookingState;
import com.example.zeromonos.data.BookingRepository;
import com.example.zeromonos.service.BookingService;
import com.example.zeromonos.service.MunicipioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceUnitTest {

    private BookingRepository repository;
    private MunicipioService municipioService;
    private BookingService bookingService;

    private Booking validBooking;

    @BeforeEach
    void setUp() {
        repository = mock(BookingRepository.class);
        municipioService = mock(MunicipioService.class);
        bookingService = new BookingService(repository, municipioService);

        validBooking = new Booking();
        validBooking.setMunicipality("Lisboa");
        validBooking.setDescription("Limpeza");
        validBooking.setRequestedDate(LocalDate.now().plusDays(5));
        validBooking.setTimeSlot("09:00-11:00");

        when(municipioService.isValidMunicipality("Lisboa")).thenReturn(true);
    }

    // --- Teste de criação normal ---
    @Test
    void shouldCreateValidBooking() {
        when(repository.findAll()).thenReturn(List.of());
        when(repository.findByMunicipality("Lisboa")).thenReturn(List.of());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Booking saved = bookingService.createBooking(validBooking);

        assertEquals("Lisboa", saved.getMunicipality());
        assertEquals(BookingState.RECEBIDO, saved.getStatus());
        assertNotNull(saved.getToken());
    }

    // --- Teste de antecedência mínima ---
    @Test
    void shouldRejectBookingIfRequestedDateTooSoon() {
        validBooking.setRequestedDate(LocalDate.now().plusDays(1));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(validBooking));
        assertEquals("O pedido deve ser feito com pelo menos 3 dias de antecedência", ex.getMessage());
    }

    // --- Teste de fim de semana ---
    @Test
    void shouldRejectBookingIfOnWeekend() {
        validBooking.setRequestedDate(LocalDate.of(2025, 11, 2)); // domingo

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(validBooking));
        assertEquals("Não é permitido fazer pedidos ao fim de semana.", ex.getMessage());
    }

    // --- Limite diário por município ---
    @Test
    void shouldRejectBookingIfDailyLimitReached() {
        List<Booking> existing = List.of(
                createBookingWithDateAndState(validBooking.getRequestedDate(), BookingState.RECEBIDO),
                createBookingWithDateAndState(validBooking.getRequestedDate(), BookingState.RECEBIDO),
                createBookingWithDateAndState(validBooking.getRequestedDate(), BookingState.RECEBIDO),
                createBookingWithDateAndState(validBooking.getRequestedDate(), BookingState.RECEBIDO),
                createBookingWithDateAndState(validBooking.getRequestedDate(), BookingState.RECEBIDO)
        );
        when(repository.findByMunicipality("Lisboa")).thenReturn(existing);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(validBooking));
        assertEquals("Limite de pedidos atingido para este dia", ex.getMessage());
    }

    // --- Conflito de horário ---
    @Test
    void shouldRejectBookingIfTimeSlotConflict() {
        List<Booking> existing = List.of(
                createBookingWithDateAndState(validBooking.getRequestedDate(), BookingState.RECEBIDO)
        );
        existing.get(0).setTimeSlot(validBooking.getTimeSlot());
        when(repository.findAll()).thenReturn(existing);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(validBooking));
        assertEquals("Não é possível reservar dois serviços no mesmo horário.", ex.getMessage());
    }

    // --- Limite de reservas ativas ---
    @Test
    void shouldRejectBookingIfActiveLimitExceeded() {
        List<Booking> activeBookings = List.of(
                createBookingWithDateAndState(LocalDate.now().plusDays(5), BookingState.RECEBIDO,"09:00-10:00"),
                createBookingWithDateAndState(LocalDate.now().plusDays(5), BookingState.EM_PROG,"10:00-11:00"),
                createBookingWithDateAndState(LocalDate.now().plusDays(6), BookingState.RECEBIDO,"11:00-12:00")
        );
        when(repository.findAll()).thenReturn(activeBookings);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(validBooking));
        assertEquals("O cidadão já atingiu o limite de reservas ativas.", ex.getMessage());
    }

    // --- Atualização de estado válida ---
    @Test
    void shouldUpdateBookingStatusValidTransition() {
        validBooking.addState(BookingState.RECEBIDO);
        when(repository.findByToken(validBooking.getToken())).thenReturn(Optional.of(validBooking));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Booking updated = bookingService.updateBookingStatus(validBooking.getToken(), BookingState.EM_PROG);
        assertEquals(BookingState.EM_PROG, updated.getStatus());
    }

    // --- Atualização de estado inválida ---
    @Test
    void shouldRejectInvalidStateTransition() {
        validBooking.addState(BookingState.RECEBIDO);
        when(repository.findByToken(validBooking.getToken())).thenReturn(Optional.of(validBooking));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> bookingService.updateBookingStatus(validBooking.getToken(), BookingState.CONCLUIDO));
        assertEquals("Transição inválida de RECEBIDO para CONCLUIDO", ex.getMessage());
    }

    private Booking createBookingWithDateAndState(LocalDate date, BookingState state) {
        Booking b = new Booking();
        b.setRequestedDate(date);
        b.setTimeSlot("09:00-11:00");
        b.setDescription("Teste");
        b.setMunicipality("Lisboa");
        b.addState(state);
        return b;
    }

    private Booking createBookingWithDateAndState(LocalDate date, BookingState state, String timeSlot) {
        Booking b = new Booking();
        b.setRequestedDate(date);
        b.setTimeSlot(timeSlot);
        b.setDescription("Teste");
        b.setMunicipality("Lisboa");
        b.addState(state);
        return b;
    }
}