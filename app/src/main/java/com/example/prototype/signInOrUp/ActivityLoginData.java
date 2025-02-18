package com.example.prototype.signInOrUp;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.prototype.ActivityMainMenu;
import com.example.prototype.Api.App;
import com.example.prototype.Api.Dto.UserAccountDto;
import com.example.prototype.Api.VkrService;
import com.example.prototype.R;

import org.mindrot.jbcrypt.BCrypt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ActivityLoginData extends AppCompatActivity {
    private static final String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final String passwordRegex =  "^(?=.*[0-9])(?=.*[!@#$%^&*])(?=\\S+$).{6,}$";

    CompositeDisposable disposable = new CompositeDisposable();
    public final VkrService vkrService = new VkrService();
    EditText editTextLogin;
    EditText editTextPassword;
    EditText editTextNickname;

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, App.salt);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_data);

        editTextLogin = findViewById(R.id.edit_text_login);
        editTextPassword = findViewById(R.id.edit_text_password);
        editTextNickname = findViewById(R.id.edit_text_nickname);

        Button buttonDone = findViewById(R.id.button_done_registration);
        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean allFieldsFilled = !TextUtils.isEmpty(editTextLogin.getText())
                        && !TextUtils.isEmpty(editTextPassword.getText())
                        && !TextUtils.isEmpty(editTextNickname.getText());
                if (!allFieldsFilled) {
                    Toast.makeText(ActivityLoginData.this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isValid(editTextLogin.getText().toString(), emailRegex)) {
                    Toast.makeText(ActivityLoginData.this, "В поле «Логин» не эл. почта", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isValid(editTextPassword.getText().toString(), passwordRegex)) {
                    Toast.makeText(ActivityLoginData.this, "Пароль должен быть не короче 6 символов и содержать цифры и специальные знаки", Toast.LENGTH_SHORT).show();
                    return;
                }

                checkUserExistance(editTextLogin.getText().toString());

            }
        });
    }

    public static boolean isValid(String value, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    public void checkUserExistance(String login) {
        disposable.add((vkrService.getApi().GetUserAccountByLogin(login)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<UserAccountDto, Throwable>() {
                    @Override
                    public void accept(UserAccountDto UserAccountDto, Throwable throwable) throws Exception {
                        if (throwable != null && UserAccountDto == null) {
                            UserAccountDto userAccountDto = (UserAccountDto) getIntent().getSerializableExtra("userAccountDto");
                            userAccountDto.setNickname(editTextNickname.getText().toString());
                            userAccountDto.setLogin(editTextLogin.getText().toString());
                            userAccountDto.setPasswordHash(hashPassword(editTextPassword.getText().toString()));
                            createUserAccount(userAccountDto);
                        } else {
                            Toast.makeText(ActivityLoginData.this, "Пользователь с таким логином уже существует", Toast.LENGTH_SHORT).show();
                        }
                    }
                })));
    }

    public void createUserAccount(UserAccountDto userAccountDto) {
        disposable.add((vkrService.getApi().CreateUserAccount(userAccountDto)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<UserAccountDto, Throwable>() {
                    @Override
                    public void accept(UserAccountDto UserAccountDto, Throwable throwable) throws Exception {
                        if (throwable != null & UserAccountDto != null) {
                            Toast.makeText(ActivityLoginData.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            System.out.println(throwable.getMessage());
                        } else {
                            Toast.makeText(ActivityLoginData.this, "Регистрация прошла успешно!", Toast.LENGTH_SHORT).show();
                            SharedPreferences sharedPref = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putLong("user_id", UserAccountDto.getId());
                            editor.apply();
                            Intent intent = new Intent(ActivityLoginData.this, ActivityMainMenu.class);
                            startActivity(intent);
                        }
                    }
                })));
    }

    @Override
    public void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }


}