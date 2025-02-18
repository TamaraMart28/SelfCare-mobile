package com.example.prototype.Api.Dto.mentalArticle;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MentalArticleSurveyDto {
    private Long id;
    private Long userId;

    private String[] themesList;
    private String[] categoryList;
    private String[] typeList;
    private String[] durationList;
}
