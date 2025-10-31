package com.example.zeromonos.boundary;

import com.example.zeromonos.data.Booking;
import com.example.zeromonos.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody Booking booking) {
        try {
            Booking saved = service.createBooking(booking);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{token}")
    public ResponseEntity<Booking> getBooking(@PathVariable String token) {
        return service.getBookingByToken(token)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Booking> getBookingsByMunicipality(@RequestParam String municipality) {
        return service.getBookingsByMunicipality(municipality);
    }

    @PutMapping("/{token}/status")
    public ResponseEntity<Booking> updateStatus(@PathVariable String token, @RequestParam String status) {
        Booking updated = service.updateBookingStatus(token, status);
        if (updated != null) return ResponseEntity.ok(updated);
        else return ResponseEntity.notFound().build();
    }
}