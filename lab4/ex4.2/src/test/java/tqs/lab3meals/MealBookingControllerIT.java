package tqs.lab3meals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import tqs.lab3meals.data.MealBookingRequest;
import tqs.lab3meals.data.MealBookingRepository;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) 
class MealBookingControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MealBookingRepository repository;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/bookings";
    }

    @Test
    @DisplayName("Book a meal via REST API")
    void testCreateBooking() {
        Map<String, String> requestBody = Map.of(
                "userId", "integrationUser",
                "date", LocalDate.now().toString()
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(baseUrl(), requestBody, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("token");

        String token = (String) response.getBody().get("token");

        MealBookingRequest saved = repository.findByToken(token).orElseThrow();
        assertThat(saved.getUserId()).isEqualTo("integrationUser");
        assertThat(saved.getState()).isEqualTo(MealBookingRequest.RESERVATION_STATE.ACTIVE);
    }

    @Test
    @DisplayName("Cancel a reservation via REST API")
    void testCancelBooking() {
        // Cria reserva diretamente no banco
        MealBookingRequest request = new MealBookingRequest("cancelUser", LocalDate.now());
        repository.save(request);

        ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl() + "/" + request.getToken(),
                HttpMethod.DELETE,
                null,
                Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("status", "cancelled");

        MealBookingRequest updated = repository.findByToken(request.getToken()).orElseThrow();
        assertThat(updated.getState()).isEqualTo(MealBookingRequest.RESERVATION_STATE.CANCELED);
    }

    @Test
    @DisplayName("Check-in a reservation via REST API")
    void testCheckinBooking() {
        MealBookingRequest request = new MealBookingRequest("checkinUser", LocalDate.now());
        repository.save(request);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl() + "/" + request.getToken() + "/checkin",
                null,
                Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("status", "used");

        MealBookingRequest updated = repository.findByToken(request.getToken()).orElseThrow();
        assertThat(updated.getState()).isEqualTo(MealBookingRequest.RESERVATION_STATE.USED);
    }
}
