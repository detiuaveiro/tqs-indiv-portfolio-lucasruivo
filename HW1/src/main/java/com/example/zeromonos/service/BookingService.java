package com.example.zeromonos.service;

import com.example.zeromonos.data.Booking;
import com.example.zeromonos.data.BookingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookingService {

    private final BookingRepository repository;
    private final MunicipioService municipioService;
    private static final int LIMITE_DIARIO = 5; // limite de bookings por dia

    public BookingService(BookingRepository repository, MunicipioService municipioService) {
        this.repository = repository;
        this.municipioService = municipioService;
    }

    public Booking createBooking(Booking booking) {
        // valida município
        if (!municipioService.isValidMunicipality(booking.getMunicipality())) {
            throw new IllegalArgumentException("Município inválido: " + booking.getMunicipality());
        }

        // valida limite diário
        long count = repository.findByMunicipality(booking.getMunicipality()).stream()
                .filter(b -> b.getRequestedDate().equals(booking.getRequestedDate()))
                .count();
        if (count >= LIMITE_DIARIO) {
            throw new IllegalArgumentException("Limite de pedidos atingido para este dia");
        }

        // define token único e status inicial
        booking.setToken(UUID.randomUUID().toString());
        booking.setStatus("RECEBIDO");

        return repository.save(booking);
    }

    public Optional<Booking> getBookingByToken(String token) {
        return repository.findByToken(token);
    }

    public List<Booking> getBookingsByMunicipality(String municipality) {
        return repository.findByMunicipality(municipality);
    }

    public Booking updateBookingStatus(String token, String status) {
        Optional<Booking> optional = repository.findByToken(token);
        if (optional.isPresent()) {
            Booking booking = optional.get();
            booking.setStatus(status);
            return repository.save(booking);
        }
        return null;
    }
}