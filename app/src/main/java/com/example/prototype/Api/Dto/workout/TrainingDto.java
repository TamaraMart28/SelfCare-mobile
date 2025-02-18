package com.example.prototype.Api.Dto.workout;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrainingDto implements Serializable {
    private Long id;

    private String name;
    private String description;
    private String image;
    private Integer[] duration;
    private String amount;
    private Long workoutId;

    @Override
    public String toString() {
        return name;
    }
}
