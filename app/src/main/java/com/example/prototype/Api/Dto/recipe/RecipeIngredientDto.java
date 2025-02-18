package com.example.prototype.Api.Dto.recipe;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeIngredientDto {
    private Long id;
//    private RecipeDto Recipe;
//    private IngredientDto Ingredient;
    private Long recipeId;
    private Long ingredientId;
    private String ingredient;
    private String amount;

}
