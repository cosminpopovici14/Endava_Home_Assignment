package com.example.carins.web.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record InsurancePolicyDto
        (Long carId, String provider, @NotNull(message = "Start date must not be null") LocalDate startDate, @NotNull(message = "end date must not be null") LocalDate endDate) {
}
