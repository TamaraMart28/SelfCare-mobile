package com.example.prototype.Api.Dto.mentalArticle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MentalArticleSurveyToFillDto {
    private String[] themesList;
    private String[] categoryList;
    private String[] typeList;
    private String[] durationList;
    private MentalArticleSurveyDto mentalArticleSurveyDto;
}
