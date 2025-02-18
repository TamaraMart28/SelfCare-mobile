package com.example.prototype.Api.Dto.mentalArticle;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AllAboutMentalArticleDto {
    private Long mentalArticleId;
    private String name;
    private String content;
    private String author;
    private Long favoriteId;

    private Map<String, List<String>> tagsMap;
}
