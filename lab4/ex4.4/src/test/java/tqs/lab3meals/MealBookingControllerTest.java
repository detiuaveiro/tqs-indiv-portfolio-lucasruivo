package tqs.lab3meals;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tqs.lab3meals.data.MealBookingRequest;
import tqs.lab3meals.services.MealsBookingServiceImpl;
import tqs.lab3meals.boundary.MealBookingController;


import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MealBookingController.class)
class MealBookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MealsBookingServiceImpl bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void whenCreateBooking_thenReturnToken() throws Exception {
        String userId = "user123";
        LocalDate date = LocalDate.of(2025, 10, 15);
        String token = UUID.randomUUID().toString();

        when(bookingService.bookMeal(any(MealBookingRequest.class))).thenReturn(token);

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": \"" + userId + "\", \"date\": \"" + date + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token));
    }

    @Test
    void whenGetBooking_thenReturnBooking() throws Exception {
        String token = UUID.randomUUID().toString();
        MealBookingRequest booking = new MealBookingRequest("userABC", LocalDate.of(2025, 10, 20));
        when(bookingService.findReservation(token)).thenReturn(Optional.of(booking));

        mockMvc.perform(get("/api/bookings/{token}", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("userABC"))
                .andExpect(jsonPath("$.state").value("ACTIVE"));
    }
}
