package com.example.zeromonos.data;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Booking {

    @Id
    @GeneratedValue
    private UUID id;

    private String municipality;
    private String description;
    private LocalDate requestedDate;
    private String timeSlot;
    private String token; // token de acesso

    @Enumerated(EnumType.STRING)
    private BookingState status;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingStateHistory> stateHistory = new ArrayList<>();

    public Booking() {
        this.token = UUID.randomUUID().toString();
        addState(BookingState.RECEBIDO);
    }

    public void addState(BookingState status) {
        this.status = status;
        BookingStateHistory history = new BookingStateHistory(this, status);
        this.stateHistory.add(history);
    }

    // Getters e Setters
    public UUID getId() { return id; }
    public String getMunicipality() { return municipality; }
    public void setMunicipality(String municipality) { this.municipality = municipality; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getRequestedDate() { return requestedDate; }
    public void setRequestedDate(LocalDate requestedDate) { this.requestedDate = requestedDate; }
    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
    public String getToken() { return token; }
    public BookingState getStatus() { return status; }
    public List<BookingStateHistory> getStateHistory() { return stateHistory; }
}