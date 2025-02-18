package com.example.prototype.Api.Dto.points;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PointOfInterestListDto {
    private List<PointOfInterestDto> fitnessCentres;
    private List<PointOfInterestDto> sportsCentres;
    private List<PointOfInterestDto> fitnessStations;
    private List<PointOfInterestDto> tracks;
    private List<PointOfInterestDto> swimmings;
}
