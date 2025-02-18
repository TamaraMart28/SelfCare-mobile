package com.example.prototype.recipe;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.CollapsibleActionView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.prototype.ActivityMainMenu;
import com.example.prototype.Api.Dto.FavoriteDto;
import com.example.prototype.Api.Dto.recipe.RecipeDto;
import com.example.prototype.Api.Dto.recipe.RecipeSurveyDto;
import com.example.prototype.Api.Dto.recipe.RecipeSurveyToFillDto;
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

public class ScrollingFragmentRecipeSurvey extends Fragment {
    private AutoCompleteTextView autoCompleteTextIngr;
    private ImageButton buttonIngr;
    private FlexboxLayout flexboxLayoutIngr;

    private Spinner spinnerDish;
    private ImageButton buttonDish;
    private FlexboxLayout flexboxLayoutDish;

    private EditText editTextCalories;

    private EditText editTextTimingFrom;
    private EditText editTextTimingTo;
    private TimePickerDialog timePickerDialog;

    private EditText editTextAmountIngrFrom;
    private EditText editTextAmountIngrTo;

    private Spinner spinnerDiet;
    private ImageButton buttonDiet;
    private FlexboxLayout flexboxLayoutDiet;

    private Spinner spinnerPurpose;
    private ImageButton buttonPurpose;
    private FlexboxLayout flexboxLayoutPurpose;

    private Spinner spinnerKitchen;
    private ImageButton buttonKitchen;
    private FlexboxLayout flexboxLayoutKitchen;

    private AutoCompleteTextView autoCompleteTextAntiIngr;
    private ImageButton buttonAntiIngr;
    private FlexboxLayout flexboxLayoutAntiIngr;

    private Button buttonDoSurvey;

    CompositeDisposable disposable = new CompositeDisposable();
    public final VkrService vkrService = new VkrService();
    private RecipeSurveyDto recipeSurvey;

    public ScrollingFragmentRecipeSurvey() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scrolling_recipe_survey, container, false);

        //Ингредиенты
        autoCompleteTextIngr = view.findViewById(R.id.auto_complete_text_ingr);
        buttonIngr = view.findViewById(R.id.but_add_ingr);
        flexboxLayoutIngr = view.findViewById(R.id.fl_ingr);
        //Категории
        spinnerDish = view.findViewById(R.id.spinner_dish);
        buttonDish = view.findViewById(R.id.but_add_dish);
        flexboxLayoutDish = view.findViewById(R.id.fl_dish);
        //Калории
        editTextCalories = view.findViewById(R.id.edit_text_calories);
        //Время приготовления
        editTextTimingFrom = view.findViewById(R.id.edit_text_timing_from);
        editTextTimingTo = view.findViewById(R.id.edit_text_timing_to);
        //Кол-во продуктов
        editTextAmountIngrFrom = view.findViewById(R.id.edit_text_amount_ingr_from);
        editTextAmountIngrTo = view.findViewById(R.id.edit_text_amount_ingr_to);
        //Диет. предпочтения
        spinnerDiet = view.findViewById(R.id.spinner_diet);
        buttonDiet = view.findViewById(R.id.but_add_diet);
        flexboxLayoutDiet = view.findViewById(R.id.fl_diet);
        //Назначение
        spinnerPurpose = view.findViewById(R.id.spinner_purpose);
        buttonPurpose = view.findViewById(R.id.but_add_purpose);
        flexboxLayoutPurpose = view.findViewById(R.id.fl_purpose);
        //Кухня
        spinnerKitchen = view.findViewById(R.id.spinner_kitchen);
        buttonKitchen = view.findViewById(R.id.but_add_kitchen);
        flexboxLayoutKitchen = view.findViewById(R.id.fl_kitchen);
        //Анти-Ингредиенты
        autoCompleteTextAntiIngr = view.findViewById(R.id.auto_complete_text_anti_ingr);
        buttonAntiIngr = view.findViewById(R.id.but_add_anti_ingr);
        flexboxLayoutAntiIngr = view.findViewById(R.id.fl_anti_ingr);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        getRecipeSurveyToFill(view, sharedPreferences.getLong("user_id", 0));
        buttonDoSurvey = view.findViewById(R.id.button_pick_r);
        buttonDoSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {recipeSurvey.setCalories(Integer.valueOf(String.valueOf(editTextCalories.getText())));}
                catch (Exception e ) {
                    if (!String.valueOf(editTextCalories.getText()).equals("")) Toast.makeText(getActivity(), "Укажите корректное количество", Toast.LENGTH_SHORT).show();
                    recipeSurvey.setCalories(null);
                }

                try {
                    int value = convertToMinutes(String.valueOf(editTextTimingFrom.getText()));
                    recipeSurvey.setTimingFrom(String.valueOf(editTextTimingFrom.getText()));
                }
                catch (Exception e ) {
                    recipeSurvey.setTimingFrom("");
                    if (!String.valueOf(editTextTimingFrom.getText()).equals("")) {
                        Toast.makeText(getActivity(), "Укажите корректное время", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                try {
                    int value = convertToMinutes(String.valueOf(editTextTimingTo.getText()));
                    recipeSurvey.setTimingTo(String.valueOf(editTextTimingTo.getText()));
                }
                catch (Exception e ) {
                    recipeSurvey.setTimingTo("");
                    if (!String.valueOf(editTextTimingTo.getText()).equals("")) {
                        Toast.makeText(getActivity(), "Укажите корректное время", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (!Objects.equals(recipeSurvey.getTimingFrom(), "") && !Objects.equals(recipeSurvey.getTimingTo(), "")) {
                    if (convertToMinutes(recipeSurvey.getTimingFrom()) > convertToMinutes(recipeSurvey.getTimingTo())) {
                        Toast.makeText(getActivity(), "'Время от' должно быть меньше 'Времени до'", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                try {recipeSurvey.setAmountIngrFrom(Integer.valueOf(String.valueOf(editTextAmountIngrFrom.getText())));}
                catch (Exception e ) {
                    if (!String.valueOf(editTextAmountIngrFrom.getText()).equals("")) Toast.makeText(getActivity(), "Укажите корректное количество", Toast.LENGTH_SHORT).show();
                    recipeSurvey.setAmountIngrFrom(null);
                }
                try {recipeSurvey.setAmountIngrTo(Integer.valueOf(String.valueOf(editTextAmountIngrTo.getText())));}
                catch (Exception e ) {
                    if (!String.valueOf(editTextAmountIngrTo.getText()).equals("")) Toast.makeText(getActivity(), "Укажите корректное количество", Toast.LENGTH_SHORT).show();
                    recipeSurvey.setAmountIngrTo(null);
                }
                if (recipeSurvey.getAmountIngrFrom() != null && recipeSurvey.getAmountIngrTo() != null &&
                        recipeSurvey.getAmountIngrFrom() > recipeSurvey.getAmountIngrTo()) {
                    Toast.makeText(getActivity(), "'Кол-во от' должно быть меньше 'Кол-во до'", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (recipeSurvey.getIngrList() != null && recipeSurvey.getIngrList().length == 0) recipeSurvey.setIngrList(null);
                if (recipeSurvey.getDishList() != null && recipeSurvey.getDishList().length == 0) recipeSurvey.setDishList(null);
                if (recipeSurvey.getDietList() != null && recipeSurvey.getDietList().length == 0) recipeSurvey.setDietList(null);
                if (recipeSurvey.getPurposeList() != null && recipeSurvey.getPurposeList().length == 0) recipeSurvey.setPurposeList(null);
                if (recipeSurvey.getKitchenList() != null && recipeSurvey.getKitchenList().length == 0) recipeSurvey.setKitchenList(null);
                if (recipeSurvey.getAntiIngrList() != null && recipeSurvey.getAntiIngrList().length == 0) recipeSurvey.setAntiIngrList(null);

                recipeSurvey.setUserId(sharedPreferences.getLong("user_id", 0));
                saveRecipeRecomendations(recipeSurvey);
            }
        });

        return view;
    }

    private int convertToMinutes(String timeString) {
        String[] parts = timeString.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return hours * 60 + minutes;
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
                case "ingrList":
                    arrayList = new ArrayList<>(Arrays.asList(recipeSurvey.getIngrList()));
                    arrayList.add(value);
                    resultArray = arrayList.toArray(new String[0]);
                    recipeSurvey.setIngrList(resultArray);
                    break;
                case "dishList":
                    arrayList = new ArrayList<>(Arrays.asList(recipeSurvey.getDishList()));
                    arrayList.add(value);
                    resultArray = arrayList.toArray(new String[0]);
                    recipeSurvey.setDishList(resultArray);
                    break;
                case "dietList":
                    arrayList = new ArrayList<>(Arrays.asList(recipeSurvey.getDietList()));
                    arrayList.add(value);
                    resultArray = arrayList.toArray(new String[0]);
                    recipeSurvey.setDietList(resultArray);
                    break;
                case "purposeList":
                    arrayList = new ArrayList<>(Arrays.asList(recipeSurvey.getPurposeList()));
                    arrayList.add(value);
                    resultArray = arrayList.toArray(new String[0]);
                    recipeSurvey.setPurposeList(resultArray);
                    break;
                case "kitchenList":
                    arrayList = new ArrayList<>(Arrays.asList(recipeSurvey.getKitchenList()));
                    arrayList.add(value);
                    resultArray = arrayList.toArray(new String[0]);
                    recipeSurvey.setKitchenList(resultArray);
                    break;
                case "antiIngrList":
                    arrayList = new ArrayList<>(Arrays.asList(recipeSurvey.getAntiIngrList()));
                    arrayList.add(value);
                    resultArray = arrayList.toArray(new String[0]);
                    recipeSurvey.setAntiIngrList(resultArray);
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
                    case "ingrList":
                        arrayList = new ArrayList<>(Arrays.asList(recipeSurvey.getIngrList()));
                        arrayList.remove(value);
                        resultArray = arrayList.toArray(new String[0]);
                        recipeSurvey.setIngrList(resultArray);
                        break;
                    case "dishList":
                        arrayList = new ArrayList<>(Arrays.asList(recipeSurvey.getDishList()));
                        arrayList.remove(value);
                        resultArray = arrayList.toArray(new String[0]);
                        recipeSurvey.setDishList(resultArray);
                        break;
                    case "dietList":
                        arrayList = new ArrayList<>(Arrays.asList(recipeSurvey.getDietList()));
                        arrayList.remove(value);
                        resultArray = arrayList.toArray(new String[0]);
                        recipeSurvey.setDietList(resultArray);
                        break;
                    case "purposeList":
                        arrayList = new ArrayList<>(Arrays.asList(recipeSurvey.getPurposeList()));
                        arrayList.remove(value);
                        resultArray = arrayList.toArray(new String[0]);
                        recipeSurvey.setPurposeList(resultArray);
                        break;
                    case "kitchenList":
                        arrayList = new ArrayList<>(Arrays.asList(recipeSurvey.getKitchenList()));
                        arrayList.remove(value);
                        resultArray = arrayList.toArray(new String[0]);
                        recipeSurvey.setKitchenList(resultArray);
                        break;
                    case "antiIngrList":
                        arrayList = new ArrayList<>(Arrays.asList(recipeSurvey.getAntiIngrList()));
                        arrayList.remove(value);
                        resultArray = arrayList.toArray(new String[0]);
                        recipeSurvey.setAntiIngrList(resultArray);
                        break;
                }
            }
        });

    }

    public void timingDialog(EditText editText){
        timePickerDialog = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                editText.setText(String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute));
            }
        }, 0,0,true);
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
        timePickerDialog.setTitle("Время");
        timePickerDialog.show();
    }

    public void fillQuestionsByUserSurvey(RecipeSurveyDto recipeSurveyDto) {
        //Ингредиенты
        try {
            for (String el : recipeSurveyDto.getIngrList()){
                createEl(el, flexboxLayoutIngr, "ingrList", false);
            }
        }
        catch (Exception ex) { }
        //Категории
        try {
            for (String el : recipeSurveyDto.getDishList()){
                createEl(el, flexboxLayoutDish, "dishList", false);
            }
        }
        catch (Exception ex) { }
        //Время от
        try {
            editTextTimingFrom.setText(recipeSurveyDto.getTimingFrom());
        }
        catch (Exception ex) { }
        //Время до
        try {
            editTextTimingTo.setText(recipeSurveyDto.getTimingTo());
        }
        catch (Exception ex) { }
        //Ккал
        try {
            if (recipeSurveyDto.getCalories() != null) editTextCalories.setText(String.valueOf(recipeSurveyDto.getCalories()));
        }
        catch (Exception ex) { }
        //Кол-во от
        try {
            if (recipeSurveyDto.getAmountIngrFrom() != null) editTextAmountIngrFrom.setText(String.valueOf(recipeSurveyDto.getAmountIngrFrom()));
        }
        catch (Exception ex) { }
        //Кол-во до
        try {
            if (recipeSurveyDto.getAmountIngrTo() != null) editTextAmountIngrTo.setText(String.valueOf(recipeSurveyDto.getAmountIngrTo()));
        }
        catch (Exception ex) { }
        //Предпочтения
        try {
            for (String el : recipeSurveyDto.getDietList()){
                createEl(el, flexboxLayoutDiet, "dietList", false);
            }
        }
        catch (Exception ex) { }
        //Назначение
        try {
            for (String el : recipeSurveyDto.getPurposeList()){
                createEl(el, flexboxLayoutPurpose, "purposeList", false);
            }
        }
        catch (Exception ex) { }
        //Кухня
        try {
            for (String el : recipeSurveyDto.getKitchenList()){
                createEl(el, flexboxLayoutKitchen, "kitchenList", false);
            }
        }
        catch (Exception ex) { }
        //Ингредиенты
        try {
            for (String el : recipeSurveyDto.getAntiIngrList()){
                createEl(el, flexboxLayoutAntiIngr, "antiIngrList", false);
            }
        }
        catch (Exception ex) { }
    }

    public void fillAllHelpData(RecipeSurveyToFillDto recipeSurveyToFillDto, View view) {
        //Ингредиенты
        ArrayAdapter<String> adapterIngr = new ArrayAdapter<>(getContext(), R.layout.spinner_dropdown_item, recipeSurveyToFillDto.getIngrList());
        autoCompleteTextIngr.setAdapter(adapterIngr);
        buttonIngr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(autoCompleteTextIngr.getText())) createEl(String.valueOf(autoCompleteTextIngr.getText()), flexboxLayoutIngr, "ingrList", true);
            }
        });


        //Категории
        ArrayAdapter<String> adapterDish = new ArrayAdapter<>(getContext(), R.layout.spinner_dropdown_item, recipeSurveyToFillDto.getDishList());
        spinnerDish.setAdapter(adapterDish);
        buttonDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEl(spinnerDish.getSelectedItem().toString(), flexboxLayoutDish, "dishList", true);
            }
        });


        //Время приготовления
        editTextTimingFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timingDialog(editTextTimingFrom);
            }
        });
        editTextTimingTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timingDialog(editTextTimingTo);
            }
        });


        //Предпочтения
        ArrayAdapter<String> adapterDiet = new ArrayAdapter<>(getContext(), R.layout.spinner_dropdown_item, recipeSurveyToFillDto.getDietList());
        spinnerDiet.setAdapter(adapterDiet);
        buttonDiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEl(spinnerDiet.getSelectedItem().toString(), flexboxLayoutDiet, "dietList", true);
            }
        });


        //Назначение
        ArrayAdapter<String> adapterPurpose = new ArrayAdapter<>(getContext(), R.layout.spinner_dropdown_item, recipeSurveyToFillDto.getPurposeList());
        spinnerPurpose.setAdapter(adapterPurpose);
        buttonPurpose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEl(spinnerPurpose.getSelectedItem().toString(), flexboxLayoutPurpose, "purposeList", true);
            }
        });

        //Кухня
        ArrayAdapter<String> adapterKitchen = new ArrayAdapter<>(getContext(), R.layout.spinner_dropdown_item, recipeSurveyToFillDto.getKitchenList());
        spinnerKitchen.setAdapter(adapterKitchen);
        buttonKitchen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEl(spinnerKitchen.getSelectedItem().toString(), flexboxLayoutKitchen, "kitchenList", true);
            }
        });


        //Анти-Ингредиенты
        ArrayAdapter<String> adapterAntiIngr = new ArrayAdapter<>(getContext(), R.layout.spinner_dropdown_item, recipeSurveyToFillDto.getIngrList());
        autoCompleteTextAntiIngr.setAdapter(adapterAntiIngr);
        buttonAntiIngr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(autoCompleteTextAntiIngr.getText())) createEl(String.valueOf(autoCompleteTextAntiIngr.getText()), flexboxLayoutAntiIngr, "antiIngrList", true);
            }
        });

        if (recipeSurveyToFillDto.getRecipeSurveyDto() != null) {
            fillQuestionsByUserSurvey(recipeSurveyToFillDto.getRecipeSurveyDto());
            recipeSurvey = recipeSurveyToFillDto.getRecipeSurveyDto();
        }
        else {
            recipeSurvey = new RecipeSurveyDto();
        }
        if (recipeSurvey.getIngrList() == null) recipeSurvey.setIngrList(new String[]{});
        if (recipeSurvey.getDishList() == null) recipeSurvey.setDishList(new String[]{});
        if (recipeSurvey.getDietList() == null) recipeSurvey.setDietList(new String[]{});
        if (recipeSurvey.getPurposeList() == null) recipeSurvey.setPurposeList(new String[]{});
        if (recipeSurvey.getKitchenList() == null) recipeSurvey.setKitchenList(new String[]{});
        if (recipeSurvey.getAntiIngrList() == null) recipeSurvey.setAntiIngrList(new String[]{});

    }

    public void getRecipeSurveyToFill(View view, Long id) {
        disposable.add((vkrService.getApi().GetInfoToFillSurveyRecipe(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<RecipeSurveyToFillDto, Throwable>() {
                    @Override
                    public void accept(RecipeSurveyToFillDto recipeSurveyToFillDto, Throwable throwable) throws Exception {
                        if (throwable != null & recipeSurveyToFillDto !=null) {
                            Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            fillAllHelpData(recipeSurveyToFillDto, view);
                        }
                    }
                })));
    }

    public void saveRecipeRecomendations(RecipeSurveyDto recipeSurvey) {
        disposable.add((vkrService.getApi().CreateOrUpdateRecipeSurvey(recipeSurvey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<RecipeSurveyDto, Throwable>() {
                    @Override
                    public void accept(RecipeSurveyDto el, Throwable throwable) throws Exception {
                        if (throwable != null & el !=null) {
                            Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {

                            ((ActivityMainMenu)requireActivity()).onBackPressed();
                        }
                    }
                })));
    }
}