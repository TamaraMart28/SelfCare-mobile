package com.example.prototype.signInOrUp;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.prototype.Api.Dto.UserAccountDto;
import com.example.prototype.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ActivityUserInfo extends AppCompatActivity {
    private boolean gender;
    ImageView imageView;
    String base64Image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        RadioGroup radioGroupGender = findViewById(R.id.radio_group_gender);
        radioGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_button_female) {
                    gender = false;
                } else if (checkedId == R.id.radio_button_male) {
                    gender = true;
                }
            }
        });
        CheckBox buttonIsTrainer = findViewById(R.id.isTrainer);

        setupSeekBar(R.id.seekBarDifficulty, R.id.difficulty);
        TextView textViewDifficulty = findViewById(R.id.difficulty);
        textViewDifficulty.setText(String.valueOf(((SeekBar)findViewById(R.id.seekBarDifficulty)).getProgress()));

        Button buttonContinue = findViewById(R.id.button_continue_to_LP);
        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityUserInfo.this, ActivityLoginData.class);
                UserAccountDto userAccountDto = (UserAccountDto) getIntent().getSerializableExtra("userAccountDto");
                userAccountDto.setGender(gender);
                userAccountDto.setLevel(Integer.valueOf(textViewDifficulty.getText().toString()));
                userAccountDto.setIsTrainer(false);
                userAccountDto.setIsModeration(buttonIsTrainer.isChecked());
                userAccountDto.setImage(base64Image);
                intent.putExtra("userAccountDto", userAccountDto);
                startActivity(intent);
            }
        });

        imageView  = findViewById(R.id.imageView);

        Button buttonSelectImage = findViewById(R.id.button_select_image);
        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    private void setupSeekBar(int seekBarId, final int textViewId) {
        SeekBar seekBar = findViewById(seekBarId);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TextView textView = findViewById(textViewId);
                textView.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    //Выбор фото из галереи
    private static final int PICK_IMAGE_REQUEST = 1;
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap);

                // Преобразование изображения в строку Base64
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();
                base64Image = java.util.Base64.getEncoder().encodeToString(imageBytes);

                // Восстановление изображения из строки Base64

                setImageFromString(base64Image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    // Восстановление изображения из байтов
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setImageFromString(String base64Image) {
        byte[] imageByte = java.util.Base64.getDecoder().decode(base64Image);
        Bitmap restoredBitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
        imageView.setImageBitmap(restoredBitmap);
    }
}