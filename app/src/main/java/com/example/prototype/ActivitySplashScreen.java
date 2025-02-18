package com.example.prototype;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.example.prototype.signInOrUp.ActivitySignUpOrIn;
import com.yandex.mapkit.MapKitFactory;

public class ActivitySplashScreen extends AppCompatActivity {
    private static final int SPLASH_DURATION = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        MapKitFactory.setApiKey(getString(R.string.api_key));
        ImageView imageView = findViewById(R.id.imageView);
        Glide.with(this)
                .asGif()
                .load(R.raw.herbal_tea_says_relax)
                .into(imageView);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Проверяем условие
                boolean condition = checkAccountExistance();

                // Если условие выполняется, запускаем одну активити, иначе другую
                if (condition) {
                    startActivity(new Intent(ActivitySplashScreen.this, ActivityMainMenu.class));
                } else {
                    startActivity(new Intent(ActivitySplashScreen.this, ActivitySignUpOrIn.class));
                }

                finish();
            }
        }, SPLASH_DURATION);
    }

    private boolean checkAccountExistance() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        return sharedPreferences.contains("user_id");
    }
}