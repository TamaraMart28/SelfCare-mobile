package com.example.prototype.signInOrUp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.content.Intent;
import android.view.View;

import com.example.prototype.Api.Dto.UserAccountDto;
import com.example.prototype.R;

public class ActivitySignUpOrIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_or_in);


        Button buttonSignUp = findViewById(R.id.button_sign_up);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivitySignUpOrIn.this, ActivityUserInfo.class);
                UserAccountDto userAccountDto = new UserAccountDto();
                intent.putExtra("userAccountDto", userAccountDto);
                startActivity(intent);
            }
        });

        Button buttonSignIn = findViewById(R.id.button_sign_in);
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivitySignUpOrIn.this, ActivitySignIn.class);
                startActivity(intent);
            }
        });
    }
}