package com.rincentral.test.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rincentral.test.models.external.enums.WheelDriveType;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BodyCharacteristics {
    @JsonProperty("body_length")
    private Integer bodyLength;
    @JsonProperty("body_width")
    private Integer bodyWidth;
    @JsonProperty("body_height")
    private Integer bodyHeight;
    @JsonProperty("body_style")
    private String bodyStyle;
    @JsonIgnore
    private WheelDriveType wheelDriveType;
}
