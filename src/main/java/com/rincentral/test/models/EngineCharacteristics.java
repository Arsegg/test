package com.rincentral.test.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rincentral.test.models.external.enums.EngineType;
import com.rincentral.test.models.external.enums.FuelType;
import com.rincentral.test.models.external.enums.GearboxType;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EngineCharacteristics {
    @JsonProperty("engine_type")
    private FuelType engineType;
    @JsonProperty("engine_cylinders")
    private EngineType engineCylinders;
    @JsonProperty("engine_displacement")
    private Integer engineDisplacement;
    @JsonProperty("engine_horsepower")
    private Integer engineHorsepower;
    @JsonIgnore
    private Integer maxSpeed;
    @JsonIgnore
    private GearboxType gearboxType;
}
