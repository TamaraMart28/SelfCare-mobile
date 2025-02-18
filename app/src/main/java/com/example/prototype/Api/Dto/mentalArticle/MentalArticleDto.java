package com.example.prototype.Api.Dto.mentalArticle;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MentalArticleDto {
    private Long id;
    private String name;
    private String content;
    private String author;
}
