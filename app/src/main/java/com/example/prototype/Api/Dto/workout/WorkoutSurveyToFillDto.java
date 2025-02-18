package com.example.prototype.Api.Dto.workout;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutSurveyToFillDto {
    private String[] focusList;
    private String[] typeList;
    private String[] equipmentList;
    private String[] targetList;

    private WorkoutSurveyDto workoutSurveyDto;
}
