package tqs.lab3meals.data;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "bookings")
public class MealBookingRequest {

    public enum RESERVATION_STATE {
        ACTIVE,
        USED,
        CANCELED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String token;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private RESERVATION_STATE state;

    public MealBookingRequest() {
        this.token = UUID.randomUUID().toString();
        this.state = RESERVATION_STATE.ACTIVE;
    }

    public MealBookingRequest(String userId, LocalDate date) {
        this.userId = userId;
        this.date = date;
        this.token = UUID.randomUUID().toString();
        this.state = RESERVATION_STATE.ACTIVE;
    }

    // --- getters e setters ---
    public Long getId() { return id; }
    public String getToken() { return token; }
    public String getUserId() { return userId; }
    public LocalDate getDate() { return date; }
    public RESERVATION_STATE getState() { return state; }

    public void setUserId(String userId) { this.userId = userId; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setState(RESERVATION_STATE state) { this.state = state; }

    // --- métodos utilitários ---
    public void markUsed() { this.state = RESERVATION_STATE.USED; }
    public void cancel() { this.state = RESERVATION_STATE.CANCELED; }
}
