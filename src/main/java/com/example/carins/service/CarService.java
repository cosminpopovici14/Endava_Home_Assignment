package com.example.carins.service;

import com.example.carins.model.Car;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.InsurancePolicyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final InsurancePolicyRepository policyRepository;

    public CarService(CarRepository carRepository, InsurancePolicyRepository policyRepository) {
        this.carRepository = carRepository;
        this.policyRepository = policyRepository;
    }

    public List<Car> listCars() {
        return carRepository.findAll();
    }

    public Optional<Car> findCarById(Long carId) {
        return carRepository.findById(carId);
    }

    public boolean isInsuranceValid(Long carId, LocalDate date) {
        if (carId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Car Id must not be null");
        }
        if (date == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date must not be null");
        }
        if(!carRepository.existsById(carId)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found");
        }
        return policyRepository.existsActiveOnDate(carId, date);
    }
}
