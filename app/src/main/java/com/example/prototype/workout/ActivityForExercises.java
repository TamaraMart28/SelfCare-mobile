package com.example.prototype.workout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.prototype.ActivityMainMenu;
import com.example.prototype.Api.DataHolder;
import com.example.prototype.Api.Dto.AnalyticsDto;
import com.example.prototype.Api.Dto.workout.AllAboutWorkoutDto;
import com.example.prototype.Api.Dto.workout.TrainingDto;
import com.example.prototype.Api.VkrService;
import com.example.prototype.R;

import java.util.Calendar;
import java.util.List;
import java.util.Stack;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;

public class ActivityForExercises extends AppCompatActivity {
    private int currentTrainingIndex = 0;
    private TrainingDto currentTraining;
    private List<TrainingDto> trainingList;
    private Fragment currentFragment;
    private FrameLayout container;

    CompositeDisposable disposable = new CompositeDisposable();
    public final VkrService vkrService = new VkrService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_exercises);
        container = findViewById(R.id.fragmentContainer);

        AllAboutWorkoutDto allAboutWorkoutDto = DataHolder.getData();
        trainingList = allAboutWorkoutDto.getTrainingList();
        showTrainingExplanationFragment();
    }

    private void showTrainingExplanationFragment() {
        currentTraining = trainingList.get(currentTrainingIndex);
        ScrollingFragmentTraining fragment = ScrollingFragmentTraining.newInstance(currentTraining);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
        currentFragment = fragment;
    }

    public void showNextFragment() {
        if (currentTrainingIndex < trainingList.size() - 1) {
            currentTrainingIndex++;
            showTrainingExplanationFragment();
        } else {
            container.removeAllViews();


            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            ));
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setPadding(15, 15, 15, 15);

            TextView textView = new TextView(this);
            LinearLayout.LayoutParams textParamsName = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(textParamsName);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setTextColor(ContextCompat.getColor(this, R.color.dark_brown));
            Typeface typefaceRegular = ResourcesCompat.getFont(this, R.font.gothicb);
            textView.setTypeface(typefaceRegular);
            textView.setTextSize(20);
            textView.setPadding(15, 35, 15, 15);
            textView.setText("Тренировка выполнена!");
            linearLayout.addView(textView);

            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            imageView.setLayoutParams(imageParams);
            imageView.setPadding(30, 15, 30, 15);
            Glide.with(this)
                    .asGif()
                    .load(R.raw.workout_complete)
                    .into(imageView);
            linearLayout.addView(imageView);

            container.addView(linearLayout);

            createUserAccountAnalytics();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    DataHolder.setData(null);
                    finish();
                }
            }, 3000);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this); // Используем контекст активити
        builder.setTitle("Выход");
        builder.setMessage("Вы уверены, что хотите прекратить тренировку?");

        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DataHolder.setData(null);
                finish();
            }
        });

        builder.setNegativeButton("Отмена", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void createUserAccountAnalytics() {
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentYear = calendar.get(Calendar.YEAR);
        AnalyticsDto analyticsDto = new AnalyticsDto();
        analyticsDto.setMonth(currentMonth);
        analyticsDto.setYear(currentYear);
        SharedPreferences sharedPreferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        analyticsDto.setUserAccountId(sharedPreferences.getLong("user_id", 0));
        analyticsDto.setWorkoutCount(1);

        disposable.add((vkrService.getApi().CreateOrUpdateAnalytics(analyticsDto)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<AnalyticsDto, Throwable>() {
                    @Override
                    public void accept(AnalyticsDto AnalyticsDto, Throwable throwable) throws Exception {
                        if (throwable != null & AnalyticsDto !=null) {
                            Toast.makeText(ActivityForExercises.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })));
    }
}