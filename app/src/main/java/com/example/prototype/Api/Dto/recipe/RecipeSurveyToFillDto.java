package com.example.prototype.Api.Dto.recipe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeSurveyToFillDto {
    private String[] ingrList;
    private String[] dishList;
    private String[] purposeList;
    private String[] dietList;
    private String[] kitchenList;
    private RecipeSurveyDto recipeSurveyDto;
}
