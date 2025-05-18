package com.zm.zmbackend.controllers;

import com.zm.zmbackend.entities.Car;
import com.zm.zmbackend.services.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cars")
@Tag(name = "Car Management", description = "APIs for car management operations")
public class CarController {

    private final CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping
    public ResponseEntity<List<Car>> getAllCars() {
        List<Car> cars = carService.getAllCars();
        return new ResponseEntity<>(cars, HttpStatus.OK);
    }

    @Operation(summary = "Get all cars with pagination and filtering", 
              description = "Returns a paginated list of cars with optional filtering by brand, model, rating range, and rental status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved cars",
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "400", description = "Invalid parameters supplied"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/paged")
    public ResponseEntity<Page<Car>> getAllCarsPaged(
            @Parameter(description = "Page number (zero-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by") @RequestParam(defaultValue = "id") String sort,
            @Parameter(description = "Sort direction (asc or desc)") @RequestParam(defaultValue = "asc") String direction,
            @Parameter(description = "Filter by car brand") @RequestParam(required = false) String brand,
            @Parameter(description = "Filter by car model") @RequestParam(required = false) String model,
            @Parameter(description = "Minimum rating value") @RequestParam(required = false) Long minRating,
            @Parameter(description = "Maximum rating value") @RequestParam(required = false) Long maxRating,
            @Parameter(description = "Filter by rental status") @RequestParam(required = false) String rentalStatus) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        // If no filters are provided, return all cars
        if (brand == null && model == null && minRating == null && maxRating == null && rentalStatus == null) {
            Page<Car> cars = carService.getAllCarsPaged(pageable);
            return new ResponseEntity<>(cars, HttpStatus.OK);
        }

        // Apply filters based on provided parameters
        if (brand != null) {
            return new ResponseEntity<>(carService.getCarsByBrandPaged(brand, pageable), HttpStatus.OK);
        } else if (model != null) {
            return new ResponseEntity<>(carService.getCarsByModelPaged(model, pageable), HttpStatus.OK);
        } else if (minRating != null && maxRating != null) {
            return new ResponseEntity<>(carService.getCarsByRatingRangePaged(minRating, maxRating, pageable), HttpStatus.OK);
        } else if (rentalStatus != null) {
            return new ResponseEntity<>(carService.getAvailableCarsPaged(pageable), HttpStatus.OK);
        }

        // Default fallback
        Page<Car> cars = carService.getAllCarsPaged(pageable);
        return new ResponseEntity<>(cars, HttpStatus.OK);
    }

    @GetMapping("/available/paged")
    public ResponseEntity<Page<Car>> getAvailableCarsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<Car> cars = carService.getAvailableCarsPaged(pageable);
        return new ResponseEntity<>(cars, HttpStatus.OK);
    }

    @GetMapping("/brand/{brand}/paged")
    public ResponseEntity<Page<Car>> getCarsByBrandPaged(
            @PathVariable String brand,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<Car> cars = carService.getCarsByBrandPaged(brand, pageable);
        return new ResponseEntity<>(cars, HttpStatus.OK);
    }

    @GetMapping("/model/{model}/paged")
    public ResponseEntity<Page<Car>> getCarsByModelPaged(
            @PathVariable String model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<Car> cars = carService.getCarsByModelPaged(model, pageable);
        return new ResponseEntity<>(cars, HttpStatus.OK);
    }

    @GetMapping("/rating/paged")
    public ResponseEntity<Page<Car>> getCarsByRatingRangePaged(
            @RequestParam Long minRating,
            @RequestParam Long maxRating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<Car> cars = carService.getCarsByRatingRangePaged(minRating, maxRating, pageable);
        return new ResponseEntity<>(cars, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable Long id) {
        Optional<Car> car = carService.getCarById(id);
        return car.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Car> createCar(@RequestBody Car car) {
        Car savedCar = carService.createCar(car);
        return new ResponseEntity<>(savedCar, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Car> updateCar(@PathVariable Long id, @RequestBody Car car) {
        try {
            Car updatedCar = carService.updateCar(id, car);
            return new ResponseEntity<>(updatedCar, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        try {
            carService.deleteCar(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
