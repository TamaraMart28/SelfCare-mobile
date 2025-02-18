package com.example.prototype.Api.Dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteDto {
    private Long id;
    private String type;
    private Long materialId;
    private Long userAccountId;

}
