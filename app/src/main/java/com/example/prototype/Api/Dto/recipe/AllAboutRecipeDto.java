package com.example.prototype.Api.Dto.recipe;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AllAboutRecipeDto {
    //recipe
    private Long recipeId;
    private String name;
    private String description;
    private String imagePath;
    private Integer[] timing;
    private int portionAmount;
    private int calories;
    private Long favoriteId;

    //recipeIngredient
    private Map<String, String> recipeIngredientList;

    //recipeIngredient
    private Map<Integer, String> stepCookingList;

    private List<String> tagRecipesList;
}
