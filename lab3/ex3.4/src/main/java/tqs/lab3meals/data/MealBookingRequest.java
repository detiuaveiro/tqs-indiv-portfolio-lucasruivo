package tqs;

import java.time.LocalDate;

public class MealBookingRequest {

    public enum RESERVATION_STATE {
        ACTIVE,
        USED,
        CANCELED
    }

    private final String userId;
    private final LocalDate date;
    private RESERVATION_STATE state;

    public MealBookingRequest(String userId, LocalDate date) {
        this.userId = userId;
        this.date = date;
        this.state = RESERVATION_STATE.ACTIVE;
    }

    public String getUserId() { return userId; }
    public LocalDate getDate() { return date; }
    public RESERVATION_STATE getState() { return state; }

    public void markUsed() { this.state = RESERVATION_STATE.USED; }
    public void cancel() { this.state = RESERVATION_STATE.CANCELED; }
}