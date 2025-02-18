package com.example.prototype.Api;

import android.app.Application;

import com.example.prototype.Api.Dto.UserAccountDto;

import org.mindrot.jbcrypt.BCrypt;

import lombok.*;

@Getter
@Setter
public class App extends Application {
    private VkrService vkrService;
    public static String salt = "$2a$10$UhzRJljpnLMPfX7zyjDbFe";

    public VkrService getVkrService() {
        return vkrService;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        vkrService = new VkrService();
    }
}
