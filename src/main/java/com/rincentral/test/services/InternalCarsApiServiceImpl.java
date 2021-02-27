package com.rincentral.test.services;

import com.rincentral.test.models.CarInfo;
import com.rincentral.test.models.params.CarRequestParameters;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.OptionalDouble;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log
@Service
public class InternalCarsApiServiceImpl implements InternalCarsApiService {
    // CopyOnWriteArrayList is better than ArrayList when we will decide to add new CarFullInfo in future
    private final List<CarInfo> carInfoList = new CopyOnWriteArrayList<CarInfo>();

    @Override
    public void addAll(final List<CarInfo> carInfoList) {
        this.carInfoList.addAll(carInfoList);
    }

    @Override
    public List<? extends CarInfo> getCars(final CarRequestParameters requestParameters) {
        Stream<CarInfo> result = carInfoList.stream();

        {
            final var country = requestParameters.getCountry();
            if (country != null) {
                result = result.filter(carInfo -> country.equals(carInfo.getCountry()));
            }
        }

        {
            final var segment = requestParameters.getSegment();
            if (segment != null) {
                result = result.filter(carInfo -> segment.equals(carInfo.getSegment()));
            }
        }

        {
            final var minEngineDisplacement = requestParameters.getMinEngineDisplacement();
            if (minEngineDisplacement != null) {
                result = result.filter(carInfo -> carInfo.getEngineCharacteristics().getEngineDisplacement() >= minEngineDisplacement);
            }
        }

        {
            final var minEngineHorsepower = requestParameters.getMinEngineHorsepower();
            if (minEngineHorsepower != null) {
                result = result.filter(carInfo -> carInfo.getEngineCharacteristics().getEngineHorsepower() >= minEngineHorsepower);
            }
        }

        {
            final var minMaxSpeed = requestParameters.getMinMaxSpeed();
            if (minMaxSpeed != null) {
                result = result.filter(carInfo -> carInfo.getEngineCharacteristics().getMaxSpeed() >= minMaxSpeed);
            }
        }

        {
            final var search = requestParameters.getSearch();
            if (search != null) {
                result = result.filter(carInfo -> carInfo.getModel().contains(search)
                        || carInfo.getGeneration().contains(search)
                        || carInfo.getModification().contains(search));
            }
        }

        {
            final var isFull = requestParameters.getIsFull();  // Why not 'requestParameters.isFull()'?? O_o
            // TODO: No idea how to achieve that... >_<
        }

        {
            final var year = requestParameters.getYear();
            // TODO:
        }

        {
            final var bodyStyle = requestParameters.getBodyStyle();
            // TODO:
        }

        return result.collect(Collectors.toUnmodifiableList());
    }

    @Override
    public int size() {
        return carInfoList.size();
    }

    private List<String> toList(final Function<CarInfo, String> function) {
        return carInfoList.stream()
                .map(function)
                .distinct()
                .collect(Collectors.toUnmodifiableList());

    }

    @Override
    public List<String> getFuelTypes() {
        return toList(carInfo -> carInfo.getEngineCharacteristics().getEngineType().toString());
    }

    @Override
    public List<String> getBodyStyles() {
        return toList(carInfo -> carInfo.getBodyCharacteristics().getBodyStyle());
    }

    @Override
    public List<String> getEngineTypes() {
        return toList(carInfo -> carInfo.getEngineCharacteristics().getEngineCylinders().toString());
    }

    @Override
    public List<String> getWheelDrives() {
        return toList(carInfo -> carInfo.getBodyCharacteristics().getWheelDriveType().toString());
    }

    @Override
    public List<String> getGearboxTypes() {
        return toList(carInfo -> carInfo.getEngineCharacteristics().getGearboxType().toString());
    }

    private OptionalDouble getMaxSpeedBy(final Predicate<CarInfo> predicate) {
        return carInfoList.stream()
                .filter(predicate)
                .mapToInt(carInfo -> carInfo.getEngineCharacteristics().getMaxSpeed())
                .average();
    }

    @Override
    public OptionalDouble getMaxSpeedByModel(final String model) {
        return getMaxSpeedBy(carInfo -> carInfo.getModel().equals(model));
    }

    @Override
    public OptionalDouble getMaxSpeedByBrand(final String brand) {
        return getMaxSpeedBy(carInfo -> carInfo.getBrand().equals(brand));
    }
}
