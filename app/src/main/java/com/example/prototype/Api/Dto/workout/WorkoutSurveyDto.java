package com.example.prototype.Api.Dto.workout;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutSurveyDto {
    private Long id;
    private Long userAccountId;

    private String durationFrom;
    private String durationTo;

    private Integer amountTrainingFrom;
    private Integer amountTrainingTo;

    private String[] focusList;
    private String[] typeList;
    private String[] equipmentList;
    private String[] targetList;

}
