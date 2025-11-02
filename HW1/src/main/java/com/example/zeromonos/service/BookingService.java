package com.example.zeromonos.service;

import com.example.zeromonos.data.Booking;
import com.example.zeromonos.data.BookingRepository;
import com.example.zeromonos.data.BookingState;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository repository;
    private final MunicipioService municipioService;
    private static final int LIMITE_DIARIO = 5; // limite de bookings por dia
    private static final int MAX_ACTIVE_BOOKINGS = 3; // limite de reservas ativas por cidadão

    public BookingService(BookingRepository repository, MunicipioService municipioService) {
        this.repository = repository;
        this.municipioService = municipioService;
    }

    // --- Criação de booking com todas as regras ---
    public Booking createBooking(Booking booking) {
        // Valida campos obrigatórios
        validateBookingFields(booking);

        // Valida município
        if (!municipioService.isValidMunicipality(booking.getMunicipality())) {
            throw new IllegalArgumentException("Município inválido: " + booking.getMunicipality());
        }

        if (booking.getRequestedDate().getDayOfWeek() == java.time.DayOfWeek.SATURDAY
            || booking.getRequestedDate().getDayOfWeek() == java.time.DayOfWeek.SUNDAY) {
        throw new IllegalArgumentException("Não é permitido fazer pedidos ao fim de semana.");
        }

        // Valida antecedência mínima de 3 dias
        if (booking.getRequestedDate().isBefore(LocalDate.now().plusDays(3))) {
            throw new IllegalArgumentException("O pedido deve ser feito com pelo menos 3 dias de antecedência");
        }

        // Limite diário por município
        long count = repository.findByMunicipality(booking.getMunicipality()).stream()
                .filter(b -> b.getRequestedDate().equals(booking.getRequestedDate()))
                .count();
        if (count >= LIMITE_DIARIO) {
            throw new IllegalArgumentException("Limite de pedidos atingido para este dia");
        }

        // Conflito de horário para o mesmo usuário
        boolean hasConflict = repository.findAll().stream()
                .anyMatch(b -> b.getRequestedDate().equals(booking.getRequestedDate())
                        && b.getTimeSlot().equals(booking.getTimeSlot())
                        && b.getStatus() != BookingState.CANCELADO);

        if (hasConflict) {
            throw new IllegalArgumentException("Não é possível reservar dois serviços no mesmo horário.");
        }

        // Limite de reservas ativas por usuário
        long activeCount = repository.findAll().stream()
                .filter(b -> b.getStatus() != BookingState.CANCELADO && b.getStatus() != BookingState.CONCLUIDO)
                .count();

        if (activeCount >= MAX_ACTIVE_BOOKINGS) {
            throw new IllegalArgumentException("O cidadão já atingiu o limite de reservas ativas.");
        }

        // Persistir booking
        return repository.save(booking);
    }

    // --- Buscar booking pelo token ---
    public Optional<Booking> getBookingByToken(String token) {
        return repository.findByToken(token);
    }

    // --- Listar bookings por município ---
    public List<Booking> getBookingsByMunicipality(String municipality) {
        return repository.findByMunicipality(municipality);
    }

    public List<Booking> getAllBookings() {
        return repository.findAll();
    }

    // --- Atualizar estado com transições válidas ---
    public Booking updateBookingStatus(String token, BookingState novoEstado) {
        Booking booking = repository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Reserva não encontrada para o token fornecido."));
    
        BookingState estadoAtual = booking.getStatus();
    
        boolean validTransition = false;
    
        if (estadoAtual == BookingState.RECEBIDO) {
            validTransition = (novoEstado == BookingState.EM_PROG || novoEstado == BookingState.CANCELADO);
        } else if (estadoAtual == BookingState.EM_PROG) {
            validTransition = (novoEstado == BookingState.CONCLUIDO || novoEstado == BookingState.CANCELADO);
        } else if (estadoAtual == BookingState.CONCLUIDO || estadoAtual == BookingState.CANCELADO) {
            validTransition = false; // estados finais, nenhuma transição permitida
        }
    
        if (!validTransition) {
            throw new IllegalArgumentException("Transição inválida de " + estadoAtual + " para " + novoEstado);
        }
    
        booking.addState(novoEstado);
        return repository.save(booking);
    }

    // --- Validação de campos obrigatórios ---
    private void validateBookingFields(Booking booking) {
        if (booking.getDescription() == null || booking.getDescription().isBlank()) {
            throw new IllegalArgumentException("Descrição é obrigatória.");
        }
        if (booking.getRequestedDate() == null) {
            throw new IllegalArgumentException("Data solicitada é obrigatória.");
        }
        if (booking.getTimeSlot() == null || booking.getTimeSlot().isBlank()) {
            throw new IllegalArgumentException("Time slot é obrigatório.");
        }
        if (booking.getMunicipality() == null || booking.getMunicipality().isBlank()) {
            throw new IllegalArgumentException("Município é obrigatório.");
        }
    }
}