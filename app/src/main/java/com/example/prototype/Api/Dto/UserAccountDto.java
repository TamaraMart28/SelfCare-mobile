package com.example.prototype.Api.Dto;

import java.io.Serializable;

import lombok.*;

@Getter
@Setter
public class UserAccountDto implements Serializable {
    private Long id;
    private String nickname;
    private String login;
    private String passwordHash;

    private Boolean gender;
    private Integer level;
    private Boolean isTrainer;
    private Boolean isModeration;
    private String image;

    public UserAccountDto() {
    }

    public UserAccountDto(String nickname, String login, String passwordHash, Boolean gender, Integer level, Boolean isTrainer, Boolean isModeration) {
        this.nickname = nickname;
        this.login = login;
        this.passwordHash = passwordHash;
        this.gender = gender;
        this.level = level;
        this.isTrainer = isTrainer;
        this.isModeration = isModeration;
    }
}
