package com.rincentral.test.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CarInfo {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("segment")
    private String segment;
    @JsonProperty("brand")
    private String brand;
    @JsonProperty("model")
    private String model;
    @JsonProperty("country")
    private String country;
    @JsonProperty("generation")
    private String generation;
    @JsonProperty("modification")
    private String modification;
    @JsonIgnore
    private EngineCharacteristics engineCharacteristics;
    @JsonIgnore
    private BodyCharacteristics bodyCharacteristics;
}
