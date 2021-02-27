package com.rincentral.test.controllers;

import com.rincentral.test.models.BodyCharacteristics;
import com.rincentral.test.models.CarInfo;
import com.rincentral.test.models.EngineCharacteristics;
import com.rincentral.test.models.external.ExternalBrand;
import com.rincentral.test.models.external.ExternalCar;
import com.rincentral.test.models.external.ExternalCarInfo;
import com.rincentral.test.models.params.CarRequestParameters;
import com.rincentral.test.models.params.MaxSpeedRequestParameters;
import com.rincentral.test.services.ExternalCarsApiService;
import com.rincentral.test.services.InternalCarsApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {
    private final ExternalCarsApiService externalCarsApiService;
    private final InternalCarsApiService internalCarsApiService;

    @GetMapping("/cars")
    public ResponseEntity<List<? extends CarInfo>> getCars(CarRequestParameters requestParameters) {
        return ResponseEntity.ok(internalCarsApiService.getCars(requestParameters));
    }

    @GetMapping("/fuel-types")
    public ResponseEntity<List<String>> getFuelTypes() {
        return ResponseEntity.ok(internalCarsApiService.getFuelTypes());
    }

    @GetMapping("/body-styles")
    public ResponseEntity<List<String>> getBodyStyles() {
        return ResponseEntity.ok(internalCarsApiService.getBodyStyles());
    }

    @GetMapping("/engine-types")
    public ResponseEntity<List<String>> getEngineTypes() {
        return ResponseEntity.ok(internalCarsApiService.getEngineTypes());
    }

    @GetMapping("/wheel-drives")
    public ResponseEntity<List<String>> getWheelDrives() {
        return ResponseEntity.ok(internalCarsApiService.getWheelDrives());
    }

    @GetMapping("/gearboxes")
    public ResponseEntity<List<String>> getGearboxTypes() {
        return ResponseEntity.ok(internalCarsApiService.getGearboxTypes());
    }

    @GetMapping("/max-speed")
    public ResponseEntity<Double> getMaxSpeed(MaxSpeedRequestParameters requestParameters) {
        final var model = requestParameters.getModel();
        final var brand = requestParameters.getBrand();
        if (model != null && brand != null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        final var result = model != null
                ? internalCarsApiService.getMaxSpeedByModel(model)
                : internalCarsApiService.getMaxSpeedByBrand(brand);
        if (result.isPresent()) {
            return ResponseEntity.ok(result.getAsDouble());
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostConstruct
    private void init() {
        log.info("Loading all brands...");
        final var brands = externalCarsApiService.loadAllBrands()
                .stream()
                .collect(Collectors.toMap(ExternalBrand::getId, Function.identity()));
        log.info(brands.size() + " brands are loaded!");

        log.info("Loading all cars...");
        final var cars = externalCarsApiService.loadAllCars()
                .stream()
                .collect(Collectors.toMap(ExternalCar::getId, Function.identity()));
        log.info(cars.size() + " cars are loaded!");

        log.info("Loading all car info...");
        final var carInfoMap = cars.keySet()
                .stream()
                .map(externalCarsApiService::loadCarInformationById)
                .collect(Collectors.toMap(ExternalCarInfo::getId, Function.identity()));
        log.info(carInfoMap.size() + " car info are loaded!");

        log.info("Processing car info...");
        final var carInfoList = cars.values()
                .stream()
                .map(externalCar -> {
                            final var id = externalCar.getId();
                            final var brandId = externalCar.getBrandId();
                            final var brand = brands.get(brandId);
                            final var externalCarInfo = carInfoMap.get(id);
                            return CarInfo.builder()
                                    .id(id)
                                    .segment(externalCar.getSegment())
                                    .brand(brand.getTitle())
                                    .model(externalCar.getModel())
                                    .country(brand.getCountry())
                                    .generation(externalCar.getGeneration())
                                    .modification(externalCar.getModification())
                                    .engineCharacteristics(EngineCharacteristics.builder()
                                            .engineType(externalCarInfo.getFuelType())
                                            .engineCylinders(externalCarInfo.getEngineType())
                                            .engineDisplacement(externalCarInfo.getEngineDisplacement())
                                            .engineHorsepower(externalCarInfo.getHp())
                                            .maxSpeed(externalCarInfo.getMaxSpeed())
                                            .gearboxType(externalCarInfo.getGearboxType())
                                            .build())
                                    .bodyCharacteristics(BodyCharacteristics.builder()
                                            .bodyLength(externalCarInfo.getBodyLength())
                                            .bodyWidth(externalCarInfo.getBodyWidth())
                                            .bodyHeight(externalCarInfo.getBodyHeight())
                                            .bodyStyle(externalCarInfo.getBodyStyle())
                                            .wheelDriveType(externalCarInfo.getWheelDriveType())
                                            .build())
                                    .build();
                        }
                ).collect(Collectors.toList());
        log.info(carInfoList.size() + " car info are processed!");

        log.info("Putting car info in internal cars api service...");
        this.internalCarsApiService.addAll(carInfoList);
        log.info(this.internalCarsApiService.size() + " car info now!");
    }
}
