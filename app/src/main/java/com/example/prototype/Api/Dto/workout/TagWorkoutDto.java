package com.example.prototype.Api.Dto.workout;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TagWorkoutDto {
    private Long id;
    private Long workoutId;
    private Long tagId;

    public TagWorkoutDto(Long workoutId, Long tagId) {
        this.workoutId = workoutId;
        this.tagId = tagId;
    }
}
