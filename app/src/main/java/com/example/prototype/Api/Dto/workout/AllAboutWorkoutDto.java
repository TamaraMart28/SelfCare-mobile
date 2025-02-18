package com.example.prototype.Api.Dto.workout;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AllAboutWorkoutDto implements Serializable {
    //workout
    private Long workoutId;
    private Long userId;
    private String name;
    private String description;
    private Integer[] duration;
    private Integer difficulty;
    private String author;
    private Boolean gender;
    private Long favoriteId;

    //trainings
    private List<TrainingDto> trainingList;

    //tags
    private Map<String, List<String>> tagsMap;
}
