package com.rincentral.test.services;

import com.rincentral.test.models.CarInfo;
import com.rincentral.test.models.params.CarRequestParameters;

import java.util.List;
import java.util.OptionalDouble;

public interface InternalCarsApiService {
    void addAll(final List<CarInfo> carInfoList);

    List<? extends CarInfo> getCars(final CarRequestParameters requestParameters);

    int size();

    List<String> getFuelTypes();

    List<String> getBodyStyles();

    List<String> getEngineTypes();

    List<String> getWheelDrives();

    List<String> getGearboxTypes();

    OptionalDouble getMaxSpeedByModel(final String model);

    OptionalDouble getMaxSpeedByBrand(final String brand);
}
