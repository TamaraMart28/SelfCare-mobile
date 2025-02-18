package com.example.prototype.Api.Dto.workout;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutHelpInfoDto {
    private List<String> focusList;
    private List<String> typeList;
    private List<String> equipmentList;
    private List<String> targetList;
}
