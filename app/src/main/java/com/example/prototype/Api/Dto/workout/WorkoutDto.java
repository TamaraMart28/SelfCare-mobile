package com.example.prototype.Api.Dto.workout;

import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkoutDto {
    private Long id;
    private Long userId;
    private String name;
    private String description;
    private Integer[] duration;
    private Integer difficulty;
    private Boolean gender;

}
