package com.example.prototype.Api.Dto.mentalArticle;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TagMentalArticleDto {
    private Long id;
    //private MentalArticle mentalArticle;
    //private Tag tag;
    private Long mentalArticleId;
    private Long tagId;
}
