package com.example.prototype.Api.Dto.recipe;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StepCookingDto {
    private Long id;
    private int numberOrder;
    private String description;
//    private RecipeDto Recipe;
    private Long recipeId;

}
