package com.example.prototype.Api.Dto.points;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PointsSurveyDto {
    private Long id;
    private Long userId;
    private Double latitude;
    private Double longtitude;
    private Integer radius;

    private Boolean fitnessCentres;
    private Boolean sportsCentres;
    private Boolean fitnessStations;
    private Boolean tracks;
    private Boolean swimmings;
}
