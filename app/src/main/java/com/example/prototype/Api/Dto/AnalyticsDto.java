package com.example.prototype.Api.Dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnalyticsDto {
    private Long id;

    private Integer month;
    private Integer year;

    private Integer recipeCount;
    private Integer mentalArticleCount;
    private Integer workoutCount;

    private Long userAccountId;


}
