package com.example.carins;

import com.example.carins.service.CarService;
import com.example.carins.web.dto.InsurancePolicyDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc
class CarInsuranceApplicationTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    CarService service;

    @Test
    void insuranceValidityBasic() {
        assertTrue(service.isInsuranceValid(1L, LocalDate.parse("2024-06-01")));
        assertTrue(service.isInsuranceValid(1L, LocalDate.parse("2025-06-01")));
        assertFalse(service.isInsuranceValid(2L, LocalDate.parse("2025-02-01")));
    }

    @Test
    void createPolicyWithoutEndDate_ShouldReturnBadRequest() throws Exception {
        InsurancePolicyDto dto = new InsurancePolicyDto(
                1L,
                "Allianz",
                LocalDate.of(2025, 9, 10),
                null
        );

        mockMvc.perform(post("/api/insurance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.endDate").exists());
    }

    @Test
    void insuranceValidForExistingCar_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/cars/1/insurance-valid")
                .param("date", "2024-06-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.carId").value(1))
                .andExpect(jsonPath("$.valid").exists());
    }

    @Test
    void insuranceValidForNonExistingCar_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/cars/1000/insurance-valid")
                .param("date", "2024-06-01"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Car not found"));
    }

    @Test
    void insuranceValidWithInvalidDate_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/cars/1/insurance-valid")
                .param("date", "2024-99-99"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}
