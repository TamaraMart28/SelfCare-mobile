package com.example.prototype.Api;

import com.example.prototype.Api.Dto.workout.AllAboutWorkoutDto;

public class DataHolder {
    private static AllAboutWorkoutDto allAboutWorkoutDto;

    public static void setData(AllAboutWorkoutDto data) {
        DataHolder.allAboutWorkoutDto = data;
    }

    public static AllAboutWorkoutDto getData() {
        return allAboutWorkoutDto;
    }
}
