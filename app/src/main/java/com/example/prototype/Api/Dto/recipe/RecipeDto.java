package com.example.prototype.Api.Dto.recipe;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeDto {
    private Long id;
    private String name;
    private String description;
    private String imagePath;
    private Integer[] timing;
    private int portionAmount;
    private int calories;
}
