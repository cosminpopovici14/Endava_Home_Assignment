package com.example.carins.web;

import com.example.carins.model.Car;
import com.example.carins.model.Claim;
import com.example.carins.repo.ClaimRepository;
import com.example.carins.service.CarService;
import com.example.carins.web.dto.ClaimDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/cars/{carId}")
public class ClaimController {

    private final ClaimRepository claimRepository;
    private final CarService carService;

    public ClaimController(ClaimRepository claimRepository, CarService carService) {
        this.claimRepository = claimRepository;
        this.carService = carService;
    }

    @PostMapping("/claims")
    public ResponseEntity<ClaimDto> createClaim(
            @PathVariable Long carId,
            @Valid @RequestBody ClaimDto claimDto
    ) {
        Car car = carService.findCarById(carId).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found"));

        Claim claim = new Claim(
                car,
                claimDto.claimDate(),
                claimDto.description(),
                claimDto.amount()
        );

        Claim saved = claimRepository.save(claim);

        ClaimDto responseDto = new ClaimDto(
                saved.getId(),
                saved.getCar().getId(),
                saved.getClaimDate(),
                saved.getDescription(),
                saved.getAmount()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/history")
    public ResponseEntity<List<ClaimDto>> getCarHistory(@PathVariable Long carId){
        Car car = carService.findCarById(carId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found"));

        List<Claim> claims = claimRepository.findByCarIdOrderByClaimDateAsc(carId);

        List<ClaimDto> dtos = claims.stream()
                .map(c -> new ClaimDto(
                        c.getId(),
                        c.getCar().getId(),
                        c.getClaimDate(),
                        c.getDescription(),
                        c.getAmount()
                ))
                .toList();

        return ResponseEntity.ok(dtos);
    }

}
