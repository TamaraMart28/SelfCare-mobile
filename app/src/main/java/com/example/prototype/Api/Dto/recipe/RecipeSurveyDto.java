package com.example.prototype.Api.Dto.recipe;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeSurveyDto {
    private Long id;
    private Long userId;

    private String[] dishList;
    private String[] purposeList;

    private String timingFrom;
    private String timingTo;

    private Integer amountIngrFrom;
    private Integer amountIngrTo;

    private String[] dietList;
    private String[] kitchenList;

    private String[] ingrList;
    private String[] antiIngrList;

    private Integer calories;

}
