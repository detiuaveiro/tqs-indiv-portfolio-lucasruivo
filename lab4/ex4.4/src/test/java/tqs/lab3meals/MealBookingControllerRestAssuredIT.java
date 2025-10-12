package tqs.lab3meals;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import tqs.lab3meals.data.MealBookingRequest;
import tqs.lab3meals.data.MealBookingRepository;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MealBookingControllerRestAssuredIT {

    @LocalServerPort
    private int port;

    @Autowired
    private MealBookingRepository repository;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
        repository.deleteAll();
    }

    @Test
    @DisplayName("Book a meal via REST API")
    void testCreateBooking() {
        String token =
            given()
                .contentType(ContentType.JSON)
                .body("{ \"userId\": \"integrationUser\", \"date\": \"" + LocalDate.now() + "\" }")
            .when()
                .post("/api/bookings")
            .then()
                .statusCode(200)
                .body("$", hasKey("token"))
                .extract()
                .path("token");

        MealBookingRequest saved = repository.findByToken(token).orElseThrow();
        assertThat(saved.getUserId()).isEqualTo("integrationUser");
        assertThat(saved.getState()).isEqualTo(MealBookingRequest.RESERVATION_STATE.ACTIVE);
    }

    @Test
    @DisplayName("Cancel a reservation via REST API")
    void testCancelBooking() {
        MealBookingRequest request = new MealBookingRequest("cancelUser", LocalDate.now());
        repository.save(request);

        given()
            .when()
                .delete("/api/bookings/" + request.getToken())
            .then()
                .statusCode(200)
                .body("status", equalTo("cancelled"));

        MealBookingRequest updated = repository.findByToken(request.getToken()).orElseThrow();
        assertThat(updated.getState()).isEqualTo(MealBookingRequest.RESERVATION_STATE.CANCELED);
    }

    @Test
    @DisplayName("Check-in a reservation via REST API")
    void testCheckinBooking() {
        MealBookingRequest request = new MealBookingRequest("checkinUser", LocalDate.now());
        repository.save(request);

        given()
            .when()
                .post("/api/bookings/" + request.getToken() + "/checkin")
            .then()
                .statusCode(200)
                .body("status", equalTo("used"));

        MealBookingRequest updated = repository.findByToken(request.getToken()).orElseThrow();
        assertThat(updated.getState()).isEqualTo(MealBookingRequest.RESERVATION_STATE.USED);
    }
}