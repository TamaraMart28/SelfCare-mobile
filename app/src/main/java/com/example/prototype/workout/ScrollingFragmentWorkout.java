package com.example.prototype.workout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.prototype.ActivityMainMenu;
import com.example.prototype.Api.DataHolder;
import com.example.prototype.Api.Dto.FavoriteDto;
import com.example.prototype.Api.Dto.workout.AllAboutWorkoutDto;
import com.example.prototype.Api.VkrService;
import com.example.prototype.R;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.JustifyContent;

import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;

public class ScrollingFragmentWorkout extends Fragment {
    private LinearLayout containerWorkout;
    private AllAboutWorkoutDto allAboutWorkout;
    private String clockIcon = "\u23F3";

    private Long userId;

    CompositeDisposable disposable = new CompositeDisposable();
    public final VkrService vkrService = new VkrService();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scrolling_workout, container, false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getLong("user_id", 0);

        containerWorkout = view.findViewById(R.id.ll_workout);
        getAllAboutWorkout();

        return view;
    }

    public void getAllAboutWorkout(){
        disposable.add(vkrService.getApi().GetAllAboutWorkout(requireArguments().getLong("workoutId"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<AllAboutWorkoutDto, Throwable>() {
                    @Override
                    public void accept(AllAboutWorkoutDto AllAboutWorkoutDto, Throwable throwable) throws Exception {
                        if (throwable != null) {
                            Toast.makeText(getActivity(), "Data loading error", Toast.LENGTH_SHORT).show();
                            System.out.println(throwable.getMessage());
                        } else {
                            allAboutWorkout = AllAboutWorkoutDto;
                            fullWorkout();
                        }
                    }
                }));
    }

    @SuppressLint("ResourceAsColor")
    private void fullWorkout() {
        LinearLayout linearLayoutName = new LinearLayout(getContext());
        linearLayoutName.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        linearLayoutName.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutName.setPadding(15, 15, 15, 15);

        //Название
        TextView textViewName = new TextView(getContext());
        LinearLayout.LayoutParams textParamsName = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f);
        textViewName.setLayoutParams(textParamsName);
        textViewName.setText(allAboutWorkout.getName());
        textViewName.setPadding(15, 15, 15, 15);
        textViewName.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_brown));
        textViewName.setTextSize(23);
        textViewName.setGravity(Gravity.CENTER_HORIZONTAL);
        Typeface typefaceRegular = ResourcesCompat.getFont(getContext(), R.font.gothicb);
        textViewName.setTypeface(typefaceRegular);
        linearLayoutName.addView(textViewName);
        //Кнопка избранное
        ImageButton imageButton = new ImageButton(getContext());
        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonLayoutParams.width = 100;
        buttonLayoutParams.height = 100;
        buttonLayoutParams.setMargins(0, 20, 40, 0);
        imageButton.setLayoutParams(buttonLayoutParams);
        if (allAboutWorkout.getFavoriteId() != null) {
            imageButton.setBackgroundResource(R.drawable.ic_baseline_favorite_choosen_24);
        } else {
            imageButton.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24);
        }
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButton.setEnabled(false);
                if (allAboutWorkout.getFavoriteId() != null) {
                    imageButton.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24);
                    deleteFavorite(allAboutWorkout.getFavoriteId(), imageButton);
                }
                else {
                    FavoriteDto favoriteToCreate = new FavoriteDto();
                    favoriteToCreate.setType("Workout");
                    favoriteToCreate.setMaterialId(allAboutWorkout.getWorkoutId());
                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
                    favoriteToCreate.setUserAccountId(sharedPreferences.getLong("user_id", 0));
                    imageButton.setBackgroundResource(R.drawable.ic_baseline_favorite_choosen_24);
                    createFavorite(favoriteToCreate, imageButton);
                }
            }
        });
        linearLayoutName.addView(imageButton);

        //
        if (userId == allAboutWorkout.getUserId()) {
            ImageButton imageButtonEdit = new ImageButton(getContext());
            imageButtonEdit.setLayoutParams(buttonLayoutParams);
            imageButtonEdit.setBackgroundResource(R.drawable.ic_baseline_edit_24);
            imageButtonEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment selectedFragment = new ScrollingFragmentCreateEditWorkout();
                    Bundle bundle = new Bundle();
                    bundle.putLong("workoutId", allAboutWorkout.getWorkoutId());
                    selectedFragment.setArguments(bundle);
                    int currentMenuItem = ((ActivityMainMenu)getContext()).getCurrentMenuItemId();
                    ((ActivityMainMenu)getContext()).pushFragmentToStack(currentMenuItem, selectedFragment);
                    ((ActivityMainMenu)getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).addToBackStack(null).commit();

                }
            });
            linearLayoutName.addView(imageButtonEdit);

            ImageButton imageButtonDelete = new ImageButton(getContext());
            imageButtonDelete.setLayoutParams(buttonLayoutParams);
            imageButtonDelete.setBackgroundResource(R.drawable.ic_baseline_cancel_w_24);
            imageButtonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Подтверждение удаления");
                    builder.setMessage("Вы уверены, что хотите удалить эту тренировку?");

                    builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteWorkout(allAboutWorkout.getWorkoutId());
                        }
                    });

                    builder.setNegativeButton("Отмена", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
            linearLayoutName.addView(imageButtonDelete);
        }

        containerWorkout.addView(linearLayoutName);

        //Доп информация
        LinearLayout.LayoutParams textParamsInfo = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView textViewInfo = new TextView(getContext());
        textViewInfo.setLayoutParams(textParamsInfo);
        String author = allAboutWorkout.getAuthor();
        String time = String.format("%02d", allAboutWorkout.getDuration()[0]) + ":" + String.format("%02d", allAboutWorkout.getDuration()[1]);
        int difficulty = allAboutWorkout.getDifficulty();
        String gender = "";
        if (allAboutWorkout.getGender() != null) {
            if (allAboutWorkout.getGender()) {
                gender += "\n Для мужчин";
            }
            else {
                gender += "\n Для женщин";
            }
        }
        textViewInfo.setText("Автор \u2015 " + author + "\n" + "Сложность:" + " " + difficulty + "/5" + "\n" + clockIcon + " " + time + gender);
        textViewInfo.setPadding(15, 15, 15, 15);
        textViewInfo.setTextColor(ContextCompat.getColor(getContext(), R.color.brown));
        textViewInfo.setTextSize(15);
        textViewInfo.setGravity(Gravity.CENTER_HORIZONTAL);
        typefaceRegular = ResourcesCompat.getFont(getContext(), R.font.gothici);
        textViewInfo.setTypeface(typefaceRegular);
        containerWorkout.addView(textViewInfo);

        //Описание
        textParamsInfo.setMargins(10, 0, 10, 0);
        TextView textViewDescription = new TextView(getContext());
        textViewDescription.setLayoutParams(textParamsInfo);
        textViewDescription.setText(HtmlCompat.fromHtml(allAboutWorkout.getDescription(), HtmlCompat.FROM_HTML_MODE_COMPACT));
        textViewDescription.setPadding(15, 15, 15, 15);
        textViewDescription.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_brown));
        textViewDescription.setTextSize(18);
        typefaceRegular = ResourcesCompat.getFont(getContext(), R.font.gothic);
        textViewDescription.setTypeface(typefaceRegular);
        containerWorkout.addView(textViewDescription);

        Button buttonDo = new Button(getContext());
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        buttonParams.gravity = Gravity.CENTER;
        buttonParams.setMargins(0,20,0,0);
        buttonParams.setMargins(0,0,0,20);
        buttonDo.setLayoutParams(buttonParams);
        buttonDo.setText("Приступить");
        typefaceRegular = ResourcesCompat.getFont(getContext(), R.font.gothicbi);
        buttonDo.setTypeface(typefaceRegular);
        buttonDo.setTextSize(15);
        buttonDo.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        buttonDo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_fitness_center_24, 0, 0, 0);
        buttonDo.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green));
        buttonDo.setCompoundDrawablePadding(5);
        buttonDo.setPadding(25,0,25,0);
        buttonDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ActivityForExercises.class);
                DataHolder.setData(allAboutWorkout);
                startActivity(intent);
            }
        });
        containerWorkout.addView(buttonDo);

        //Тэги
        FlexboxLayout flexboxLayout = new FlexboxLayout(getContext());
        flexboxLayout.setFlexDirection(FlexDirection.ROW);
        flexboxLayout.setFlexWrap(FlexWrap.WRAP);
        flexboxLayout.setJustifyContent(JustifyContent.CENTER);
        typefaceRegular = ResourcesCompat.getFont(getContext(), R.font.gothici);

        for (Map.Entry<String, List<String>> entry : allAboutWorkout.getTagsMap().entrySet()) {
            for (String tag : entry.getValue()) {
                TextView tvTag = new TextView(getContext());
                tvTag.setText(tag);
                GradientDrawable drawable = new GradientDrawable();
                drawable.setShape(GradientDrawable.RECTANGLE);
                drawable.setStroke(2, ContextCompat.getColor(getContext(), R.color.green));

                tvTag.setBackground(drawable);
                tvTag.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                tvTag.setPadding(10, 5, 10, 5);
                tvTag.setTypeface(typefaceRegular);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(15, 15, 15, 15);

                flexboxLayout.addView(tvTag, params);
            }
        }

        containerWorkout.addView(flexboxLayout);
    }

    public void deleteWorkout (Long id) {
        disposable.add((vkrService.getApi().DeleteAllAboutWorkout(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.isSuccessful()) {
                        // Обработка успешного запроса
                        Toast.makeText(getActivity(), "Тренировка удалена", Toast.LENGTH_SHORT).show();
                        ((ActivityMainMenu)requireActivity()).onBackPressed();
                    } else {
                        // Обработка неуспешного запроса
                        Toast.makeText(getActivity(), "Тренировка не удалена", Toast.LENGTH_SHORT).show();
                    }
                }, throwable -> {
                    // Обработка ошибки
                    Toast.makeText(getActivity(), "Ошибка", Toast.LENGTH_SHORT).show();
                    throwable.printStackTrace();
                })));
    }

    public void deleteFavorite(Long id, ImageButton imageButton) {
        disposable.add((vkrService.getApi().DeleteFavorite(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<FavoriteDto, Throwable>() {
                    @Override
                    public void accept(FavoriteDto favoriteDto, Throwable throwable) throws Exception {
                        if (throwable != null & favoriteDto !=null) {
                            System.out.println(throwable.getMessage());
                        } else {
                            allAboutWorkout.setFavoriteId(null);
                            imageButton.setEnabled(true);
                        }
                    }
                })));
    }

    public void createFavorite(FavoriteDto favoriteToCreate, ImageButton imageButton) {
        disposable.add((vkrService.getApi().CreateFavorite(favoriteToCreate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<FavoriteDto, Throwable>() {
                    @Override
                    public void accept(FavoriteDto favoriteDto, Throwable throwable) throws Exception {
                        if (throwable != null & favoriteDto !=null) {
                            Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            allAboutWorkout.setFavoriteId(favoriteDto.getId());
                            imageButton.setEnabled(true);
                        }
                    }
                })));
    }
}