package com.example.carins.web;

import com.example.carins.model.Car;
import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.InsurancePolicyRepository;
import com.example.carins.service.CarService;
import com.example.carins.web.dto.InsurancePolicyDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/insurance")
public class InsurancePolicyController {

    private final InsurancePolicyRepository insurancePolicyRepository;
    private final CarService carService;

    public InsurancePolicyController(InsurancePolicyRepository insurancePolicyRepository, CarService carService) {
        this.insurancePolicyRepository = insurancePolicyRepository;
        this.carService = carService;
    }

    @PostMapping
    public ResponseEntity<InsurancePolicyDto> createPolicy(@Valid @RequestBody InsurancePolicyDto policyDto) {
        Car car = carService.findCarById(policyDto.carId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found!"));

        InsurancePolicy policy = new InsurancePolicy(
                car,
                policyDto.provider(),
                policyDto.startDate(),
                policyDto.endDate()
        );
        insurancePolicyRepository.save(policy);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(policy));
    }

    private InsurancePolicyDto toDto(InsurancePolicy policy) {
        return new InsurancePolicyDto(
                policy.getCar().getId(),
                policy.getProvider(),
                policy.getStartDate(),
                policy.getEndDate()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<InsurancePolicyDto> updatePolicy(
            @PathVariable Long id,
            @Valid @RequestBody InsurancePolicyDto policyDto) {

        InsurancePolicy current = insurancePolicyRepository.findById(id).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Policy not found"));

        Car car = carService.findCarById(policyDto.carId()).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found"));

        current.setCar(car);
        current.setProvider(policyDto.provider());
        current.setStartDate(policyDto.startDate());
        current.setEndDate(policyDto.endDate());

        InsurancePolicy updated = insurancePolicyRepository.save(current);

        return ResponseEntity.ok(toDto(updated));
    }

}
