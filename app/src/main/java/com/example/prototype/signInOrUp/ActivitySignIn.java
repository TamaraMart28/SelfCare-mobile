package com.example.prototype.signInOrUp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.prototype.ActivityMainMenu;
import com.example.prototype.Api.App;
import com.example.prototype.Api.Dto.UserAccountDto;
import com.example.prototype.Api.VkrService;
import com.example.prototype.R;

import org.mindrot.jbcrypt.BCrypt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;

public class ActivitySignIn extends AppCompatActivity {
    private static final String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final String passwordRegex =  "^(?=.*[0-9])(?=.*[!@#$%^&*])(?=\\S+$).{6,}$";

    CompositeDisposable disposable = new CompositeDisposable();
    public final VkrService vkrService = new VkrService();

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, App.salt);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        EditText editTextLogin = findViewById(R.id.edit_text_login);
        EditText editTextPassword = findViewById(R.id.edit_text_password);

        Button signIn = findViewById(R.id.button_sign_in);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean allFieldsFilled = !TextUtils.isEmpty(editTextLogin.getText())
                        && !TextUtils.isEmpty(editTextPassword.getText());
                if (!allFieldsFilled) {
                    Toast.makeText(ActivitySignIn.this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isValid(editTextLogin.getText().toString(), emailRegex)) {
                    Toast.makeText(ActivitySignIn.this, "В поле «Логин» не эл. почта", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isValid(editTextPassword.getText().toString(), passwordRegex)) {
                    Toast.makeText(ActivitySignIn.this, "Пароль должен быть не короче 6 символов и содержать цифры и специальные знаки", Toast.LENGTH_SHORT).show();
                    return;
                }

                login(editTextLogin.getText().toString(), hashPassword(editTextPassword.getText().toString()));
            }
        });
    }

    public static boolean isValid(String value, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    public void loginSuccess(UserAccountDto userAccountDto){
        SharedPreferences sharedPref = this.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong("user_id", userAccountDto.getId());
        editor.apply();

        Intent intent = new Intent(ActivitySignIn.this, ActivityMainMenu.class);
        startActivity(intent);
    }

    public void login(String login, String password) {
        disposable.add(vkrService.getApi().SignIn(login, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<UserAccountDto, Throwable>() {
                    @Override
                    public void accept(UserAccountDto userAccountDto, Throwable throwable) throws Exception {
                        if (throwable != null) {
                            if (userAccountDto == null) {
                                Toast.makeText(ActivitySignIn.this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ActivitySignIn.this, "Data loading error", Toast.LENGTH_SHORT).show();
                                System.out.println(throwable.getMessage());
                            }
                        } else {
                            loginSuccess(userAccountDto);
                        }
                    }
                }));
    }

    @Override
    public void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }
}