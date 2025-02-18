package com.example.prototype.Api.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAccountAnalyticDto {
    private Integer mentalArticles;
    private Integer recipes;
    private Integer workouts;
}
