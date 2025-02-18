package com.example.prototype.mentalArticle;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.prototype.ActivityMainMenu;
import com.example.prototype.Api.Dto.mentalArticle.MentalArticleSurveyDto;
import com.example.prototype.Api.Dto.mentalArticle.MentalArticleSurveyToFillDto;
import com.example.prototype.Api.Dto.recipe.RecipeSurveyDto;
import com.example.prototype.Api.Dto.recipe.RecipeSurveyToFillDto;
import com.example.prototype.Api.Dto.workout.WorkoutSurveyToFillDto;
import com.example.prototype.Api.VkrService;
import com.example.prototype.R;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;

public class ScrollingFragmentMentalArticleSurvey extends Fragment {
    private Spinner spinnerThemes;
    private ImageButton buttonThemes;
    private FlexboxLayout flexboxLayoutThemes;

    private Spinner spinnerType;
    private ImageButton buttonType;
    private FlexboxLayout flexboxLayoutType;

    private Spinner spinnerDuration;
    private ImageButton buttonDuration;
    private FlexboxLayout flexboxLayoutDuration;

    private Spinner spinnerMCategory;
    private ImageButton buttonMCategory;
    private FlexboxLayout flexboxLayoutMCategory;

    private Button buttonDoSurvey;

    CompositeDisposable disposable = new CompositeDisposable();
    public final VkrService vkrService = new VkrService();
    private MentalArticleSurveyDto mentalArticleSurvey;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scrolling_mental_article_survey, container, false);

        spinnerThemes = view.findViewById(R.id.spinner_themes);
        buttonThemes = view.findViewById(R.id.but_add_themes);
        flexboxLayoutThemes = view.findViewById(R.id.fl_themes);

        spinnerType = view.findViewById(R.id.spinner_type);
        buttonType = view.findViewById(R.id.but_add_type);
        flexboxLayoutType = view.findViewById(R.id.fl_type);

        spinnerDuration = view.findViewById(R.id.spinner_duration);
        buttonDuration = view.findViewById(R.id.but_add_duration);
        flexboxLayoutDuration = view.findViewById(R.id.fl_duration);

        spinnerMCategory = view.findViewById(R.id.spinner_m_category);
        buttonMCategory = view.findViewById(R.id.but_add_m_category);
        flexboxLayoutMCategory = view.findViewById(R.id.fl_m_category);


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        getMentalArticleSurveyToFill(view, sharedPreferences.getLong("user_id", 0));
        buttonDoSurvey = view.findViewById(R.id.button_pick_ma);
        buttonDoSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mentalArticleSurvey.getThemesList() != null && mentalArticleSurvey.getThemesList().length == 0) mentalArticleSurvey.setThemesList(null);
                if (mentalArticleSurvey.getCategoryList() != null && mentalArticleSurvey.getCategoryList().length == 0) mentalArticleSurvey.setCategoryList(null);
                if (mentalArticleSurvey.getTypeList() != null && mentalArticleSurvey.getTypeList().length == 0) mentalArticleSurvey.setTypeList(null);
                if (mentalArticleSurvey.getDurationList() != null && mentalArticleSurvey.getDurationList().length == 0) mentalArticleSurvey.setDurationList(null);

                mentalArticleSurvey.setUserId(sharedPreferences.getLong("user_id", 0));
                saveMentalArticleRecomendations(mentalArticleSurvey);
            }
        });


        return view;
    }

    public void createEl(String value, FlexboxLayout flexboxLayout, String question, Boolean isFromDto) {
        LinearLayout linearLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(15,5,15,5);
        linearLayout.setLayoutParams(params);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green));

        TextView textView = new TextView(getContext());
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0.8f
        ));
        textView.setText(value);
        textView.setTextColor(Color.WHITE);
        textView.setPadding(5, 5, 5, 5);
        Typeface typefaceRegular = ResourcesCompat.getFont(getContext(), R.font.gothic);
        textView.setTypeface(typefaceRegular);

        ImageButton imageButton = new ImageButton(getContext());
        imageButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        imageButton.setBackgroundResource(R.drawable.ic_baseline_cancel_24);
        imageButton.setPadding(20,20,20,20);

        linearLayout.addView(textView);
        linearLayout.addView(imageButton);
        flexboxLayout.addView(linearLayout);

        if (isFromDto) {
            List<String> arrayList;
            String[] resultArray;
            switch (question) {
                case "themesList":
                    arrayList = new ArrayList<>(Arrays.asList(mentalArticleSurvey.getThemesList()));
                    arrayList.add(value);
                    resultArray = arrayList.toArray(new String[0]);
                    mentalArticleSurvey.setThemesList(resultArray);
                    break;
                case "categoryList":
                    arrayList = new ArrayList<>(Arrays.asList(mentalArticleSurvey.getCategoryList()));
                    arrayList.add(value);
                    resultArray = arrayList.toArray(new String[0]);
                    mentalArticleSurvey.setCategoryList(resultArray);
                    break;
                case "typeList":
                    arrayList = new ArrayList<>(Arrays.asList(mentalArticleSurvey.getTypeList()));
                    arrayList.add(value);
                    resultArray = arrayList.toArray(new String[0]);
                    mentalArticleSurvey.setTypeList(resultArray);
                    break;
                case "durationList":
                    arrayList = new ArrayList<>(Arrays.asList(mentalArticleSurvey.getDurationList()));
                    arrayList.add(value);
                    resultArray = arrayList.toArray(new String[0]);
                    mentalArticleSurvey.setDurationList(resultArray);
                    break;
            }
        }
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flexboxLayout.removeView(linearLayout);
                List<String> arrayList;
                String[] resultArray;
                switch (question) {
                    case "themesList":
                        arrayList = new ArrayList<>(Arrays.asList(mentalArticleSurvey.getThemesList()));
                        arrayList.remove(value);
                        resultArray = arrayList.toArray(new String[0]);
                        mentalArticleSurvey.setThemesList(resultArray);
                        break;
                    case "categoryList":
                        arrayList = new ArrayList<>(Arrays.asList(mentalArticleSurvey.getCategoryList()));
                        arrayList.remove(value);
                        resultArray = arrayList.toArray(new String[0]);
                        mentalArticleSurvey.setCategoryList(resultArray);
                        break;
                    case "typeList":
                        arrayList = new ArrayList<>(Arrays.asList(mentalArticleSurvey.getTypeList()));
                        arrayList.remove(value);
                        resultArray = arrayList.toArray(new String[0]);
                        mentalArticleSurvey.setTypeList(resultArray);
                        break;
                    case "durationList":
                        arrayList = new ArrayList<>(Arrays.asList(mentalArticleSurvey.getDurationList()));
                        arrayList.remove(value);
                        resultArray = arrayList.toArray(new String[0]);
                        mentalArticleSurvey.setDurationList(resultArray);
                        break;
                }
            }
        });

    }

    public void fillQuestionsByUserSurvey(MentalArticleSurveyDto mentalArticleSurveyDto) {
        //Темы
        try {
            for (String el : mentalArticleSurveyDto.getThemesList()){
                createEl(el, flexboxLayoutThemes, "themesList", false);
            }
        }
        catch (Exception ex) { }
        //Категории
        try {
            for (String el : mentalArticleSurveyDto.getCategoryList()){
                createEl(el, flexboxLayoutMCategory, "categoryList", false);
            }
        }
        catch (Exception ex) { }
        //Типы
        try {
            for (String el : mentalArticleSurveyDto.getTypeList()){
                createEl(el, flexboxLayoutType, "typeList", false);
            }
        }
        catch (Exception ex) { }
        //Продолжительность
        try {
            for (String el : mentalArticleSurveyDto.getDurationList()){
                createEl(el, flexboxLayoutDuration, "durationList", false);
            }
        }
        catch (Exception ex) { }
    }

    public void fillAllHelpData(MentalArticleSurveyToFillDto mentalArticleSurveyToFillDto, View view) {
        //Темы
        ArrayAdapter<String> adapterThemes = new ArrayAdapter<>(getContext(), R.layout.spinner_dropdown_item, mentalArticleSurveyToFillDto.getThemesList());
        spinnerThemes.setAdapter(adapterThemes);
        buttonThemes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEl(spinnerThemes.getSelectedItem().toString(), flexboxLayoutThemes, "themesList", true);
            }
        });

        //Категория
        ArrayAdapter<String> adapterMCategory = new ArrayAdapter<>(getContext(), R.layout.spinner_dropdown_item, mentalArticleSurveyToFillDto.getCategoryList());
        spinnerMCategory.setAdapter(adapterMCategory);
        buttonMCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEl(spinnerMCategory.getSelectedItem().toString(), flexboxLayoutMCategory, "categoryList", true);
            }
        });


        //Тип
        ArrayAdapter<String> adapterTypes = new ArrayAdapter<>(getContext(), R.layout.spinner_dropdown_item, mentalArticleSurveyToFillDto.getTypeList());
        spinnerType.setAdapter(adapterTypes);
        buttonType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEl(spinnerType.getSelectedItem().toString(), flexboxLayoutType, "typeList", true);
            }
        });


        //Продолжительность
        ArrayAdapter<String> adapterDuration = new ArrayAdapter<>(getContext(), R.layout.spinner_dropdown_item, mentalArticleSurveyToFillDto.getDurationList());
        spinnerDuration.setAdapter(adapterDuration);
        buttonDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEl(spinnerDuration.getSelectedItem().toString(), flexboxLayoutDuration, "durationList", true);
            }
        });


        if (mentalArticleSurveyToFillDto.getMentalArticleSurveyDto() != null) {
            fillQuestionsByUserSurvey(mentalArticleSurveyToFillDto.getMentalArticleSurveyDto());
            mentalArticleSurvey = mentalArticleSurveyToFillDto.getMentalArticleSurveyDto();
        }
        else {
            mentalArticleSurvey = new MentalArticleSurveyDto();
        }
        if (mentalArticleSurvey.getThemesList() == null) mentalArticleSurvey.setThemesList(new String[]{});
        if (mentalArticleSurvey.getCategoryList() == null) mentalArticleSurvey.setCategoryList(new String[]{});
        if (mentalArticleSurvey.getTypeList() == null) mentalArticleSurvey.setTypeList(new String[]{});
        if (mentalArticleSurvey.getDurationList() == null) mentalArticleSurvey.setDurationList(new String[]{});

    }

    public void getMentalArticleSurveyToFill(View view, Long id) {
        disposable.add((vkrService.getApi().GetInfoToFillSurveyMA(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<MentalArticleSurveyToFillDto, Throwable>() {
                    @Override
                    public void accept(MentalArticleSurveyToFillDto mentalArticleSurveyToFillDto, Throwable throwable) throws Exception {
                        if (throwable != null & mentalArticleSurveyToFillDto !=null) {
                            Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            fillAllHelpData(mentalArticleSurveyToFillDto, view);
                        }
                    }
                })));
    }

    public void saveMentalArticleRecomendations(MentalArticleSurveyDto mentalArticleSurvey) {
        disposable.add((vkrService.getApi().CreateOrUpdateMentalArticleSurvey(mentalArticleSurvey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<MentalArticleSurveyDto, Throwable>() {
                    @Override
                    public void accept(MentalArticleSurveyDto el, Throwable throwable) throws Exception {
                        if (throwable != null & el !=null) {
                            Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            ((ActivityMainMenu)requireActivity()).onBackPressed();
                        }
                    }
                })));
    }

}