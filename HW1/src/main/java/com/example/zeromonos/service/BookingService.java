package com.example.zeromonos.service;

import com.example.zeromonos.data.Booking;
import com.example.zeromonos.data.BookingRepository;
import com.example.zeromonos.data.BookingState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository repository;
    private final MunicipioService municipioService;
    private static final int LIMITE_DIARIO = 5; // limite de bookings por dia
    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    public BookingService(BookingRepository repository, MunicipioService municipioService) {
        this.repository = repository;
        this.municipioService = municipioService;
    }

    public Booking createBooking(Booking booking) {
        if (!municipioService.isValidMunicipality(booking.getMunicipality())) {
            throw new IllegalArgumentException("Município inválido: " + booking.getMunicipality());
        }
    
        if (booking.getRequestedDate().isBefore(LocalDate.now().plusDays(3))) {
            throw new IllegalArgumentException("O pedido deve ser feito com pelo menos 3 dias de antecedência");
        }
    
        long count = repository.findByMunicipality(booking.getMunicipality()).stream()
                .filter(b -> b.getRequestedDate().equals(booking.getRequestedDate()))
                .count();
        if (count >= LIMITE_DIARIO) {
            throw new IllegalArgumentException("Limite de pedidos atingido para este dia");
        }
    
        return repository.save(booking);
    }

    public Optional<Booking> getBookingByToken(String token) {
        return repository.findByToken(token);
    }

    public List<Booking> getBookingsByMunicipality(String municipality) {
        return repository.findByMunicipality(municipality);
    }

    public List<Booking> getAllBookings() {
        return repository.findAll();
    }

    public Booking updateBookingStatus(String token, BookingState novoEstado) {
        Optional<Booking> optional = repository.findByToken(token);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("Reserva não encontrada para o token fornecido.");
        }

        Booking booking = optional.get();
        booking.addState(novoEstado);
        logger.info("Booking {} atualizado para estado {}", token, novoEstado);
        return repository.save(booking);
    }
}