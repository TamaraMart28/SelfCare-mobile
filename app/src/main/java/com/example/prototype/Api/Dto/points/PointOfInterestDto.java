package com.example.prototype.Api.Dto.points;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PointOfInterestDto {
    private Double latitude;
    private Double longtitude;
    private String name;
    private String description;
}
