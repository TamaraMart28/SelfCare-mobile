package com.example.prototype.workout;

import android.app.TimePickerDialog;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.prototype.ActivityMainMenu;
import com.example.prototype.Api.Dto.UserAccountDto;
import com.example.prototype.Api.Dto.recipe.RecipeSurveyDto;
import com.example.prototype.Api.Dto.recipe.RecipeSurveyToFillDto;
import com.example.prototype.Api.Dto.workout.WorkoutSurveyDto;
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

public class ScrollingFragmentWorkoutSurvey extends Fragment {
    private Spinner spinnerFocus, spinnerType, spinnerEquipment, spinnerTarget;
    private ImageButton butAddFocus, butAddType, butAddEquipment, butAddTarget;
    private FlexboxLayout flFocus, flType, flEquipment, flTarget;
    private EditText editTextDurationFrom, editTextDurationTo, editTextAmountTrainingFrom, editTextAmountTrainingTo;
    private Button buttonPickW;
    private CardView cardViewFocus, cardViewType, cardViewEquipment, cardViewTarget, cardViewDuration, cardViewAmountTraining;
    private TimePickerDialog timePickerDialog;

    CompositeDisposable disposable = new CompositeDisposable();
    public final VkrService vkrService = new VkrService();
    private WorkoutSurveyDto workoutSurvey;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scrolling_workout_survey, container, false);

        {
            spinnerFocus = view.findViewById(R.id.spinner_focus);
            spinnerType = view.findViewById(R.id.spinner_type);
            spinnerEquipment = view.findViewById(R.id.spinner_equipment);
            spinnerTarget = view.findViewById(R.id.spinner_target);

            butAddFocus = view.findViewById(R.id.but_add_focus);
            butAddType = view.findViewById(R.id.but_add_type);
            butAddEquipment = view.findViewById(R.id.but_add_equipment);
            butAddTarget = view.findViewById(R.id.but_add_target);

            flFocus = view.findViewById(R.id.fl_focus);
            flType = view.findViewById(R.id.fl_type);
            flEquipment = view.findViewById(R.id.fl_equipment);
            flTarget = view.findViewById(R.id.fl_target);

            editTextDurationFrom = view.findViewById(R.id.edit_text_duration_from);
            editTextDurationTo = view.findViewById(R.id.edit_text_duration_to);
            editTextAmountTrainingFrom = view.findViewById(R.id.edit_text_amount_training_from);
            editTextAmountTrainingTo = view.findViewById(R.id.edit_text_amount_training_to);

            buttonPickW = view.findViewById(R.id.button_pick_w);

            cardViewFocus = view.findViewById(R.id.card_view_focus);
            cardViewType = view.findViewById(R.id.card_view_type);
            cardViewEquipment = view.findViewById(R.id.card_view_equipment);
            cardViewTarget = view.findViewById(R.id.card_view_target);
            cardViewDuration = view.findViewById(R.id.card_view_duration);
            cardViewAmountTraining = view.findViewById(R.id.card_view_amount_training);
        }

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        getWorkoutSurveyToFill(view, sharedPreferences.getLong("user_id", 0));

        buttonPickW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    int value = convertToMinutes(String.valueOf(editTextDurationFrom.getText()));
                    workoutSurvey.setDurationFrom(String.valueOf(editTextDurationFrom.getText()));
                }
                catch (Exception e ) {
                    workoutSurvey.setDurationFrom("");
                    if (!String.valueOf(editTextDurationFrom.getText()).equals("")) {
                        Toast.makeText(getActivity(), "Укажите корректное время", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                try {
                    int value = convertToMinutes(String.valueOf(editTextDurationTo.getText()));
                    workoutSurvey.setDurationTo(String.valueOf(editTextDurationTo.getText()));
                }
                catch (Exception e ) {
                    workoutSurvey.setDurationTo("");
                    if (!String.valueOf(editTextDurationTo.getText()).equals("")) {
                        Toast.makeText(getActivity(), "Укажите корректное время", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (!Objects.equals(workoutSurvey.getDurationFrom(), "") && !Objects.equals(workoutSurvey.getDurationTo(), "")) {
                    if (convertToMinutes(workoutSurvey.getDurationFrom()) > convertToMinutes(workoutSurvey.getDurationTo())) {
                        Toast.makeText(getActivity(), "'Время от' должно быть меньше 'Времени до'", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                try {workoutSurvey.setAmountTrainingFrom(Integer.valueOf(String.valueOf(editTextAmountTrainingFrom.getText())));}
                catch (Exception e ) {
                    if (!String.valueOf(editTextAmountTrainingFrom.getText()).equals("")) Toast.makeText(getActivity(), "Укажите корректное количество", Toast.LENGTH_SHORT).show();
                    workoutSurvey.setAmountTrainingFrom(null);
                }
                try {workoutSurvey.setAmountTrainingTo(Integer.valueOf(String.valueOf(editTextAmountTrainingTo.getText())));}
                catch (Exception e ) {
                    if (!String.valueOf(editTextAmountTrainingTo.getText()).equals("")) Toast.makeText(getActivity(), "Укажите корректное количество", Toast.LENGTH_SHORT).show();
                    workoutSurvey.setAmountTrainingTo(null);
                }
                if (workoutSurvey.getAmountTrainingFrom() != null && workoutSurvey.getAmountTrainingTo() != null &&
                        workoutSurvey.getAmountTrainingFrom() > workoutSurvey.getAmountTrainingTo()) {
                    Toast.makeText(getActivity(), "'Кол-во от' должно быть меньше 'Кол-во до'", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (workoutSurvey.getFocusList() != null && workoutSurvey.getFocusList().length == 0) workoutSurvey.setFocusList(null);
                if (workoutSurvey.getTypeList() != null && workoutSurvey.getTypeList().length == 0) workoutSurvey.setTypeList(null);
                if (workoutSurvey.getEquipmentList() != null && workoutSurvey.getEquipmentList().length == 0) workoutSurvey.setEquipmentList(null);
                if (workoutSurvey.getTargetList() != null && workoutSurvey.getTargetList().length == 0) workoutSurvey.setTargetList(null);

                workoutSurvey.setUserAccountId(sharedPreferences.getLong("user_id", 0));
                saveWorkoutRecomendations(workoutSurvey);
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
                case "focusList":
                    arrayList = new ArrayList<>(Arrays.asList(workoutSurvey.getFocusList()));
                    arrayList.add(value);
                    resultArray = arrayList.toArray(new String[0]);
                    workoutSurvey.setFocusList(resultArray);
                    break;
                case "typeList":
                    arrayList = new ArrayList<>(Arrays.asList(workoutSurvey.getTypeList()));
                    arrayList.add(value);
                    resultArray = arrayList.toArray(new String[0]);
                    workoutSurvey.setTypeList(resultArray);
                    break;
                case "equipmentList":
                    arrayList = new ArrayList<>(Arrays.asList(workoutSurvey.getEquipmentList()));
                    arrayList.add(value);
                    resultArray = arrayList.toArray(new String[0]);
                    workoutSurvey.setEquipmentList(resultArray);
                    break;
                case "targetList":
                    arrayList = new ArrayList<>(Arrays.asList(workoutSurvey.getTargetList()));
                    arrayList.add(value);
                    resultArray = arrayList.toArray(new String[0]);
                    workoutSurvey.setTargetList(resultArray);
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
                    case "focusList":
                        arrayList = new ArrayList<>(Arrays.asList(workoutSurvey.getFocusList()));
                        arrayList.remove(value);
                        resultArray = arrayList.toArray(new String[0]);
                        workoutSurvey.setFocusList(resultArray);
                        break;
                    case "typeList":
                        arrayList = new ArrayList<>(Arrays.asList(workoutSurvey.getTypeList()));
                        arrayList.remove(value);
                        resultArray = arrayList.toArray(new String[0]);
                        workoutSurvey.setTypeList(resultArray);
                        break;
                    case "equipmentList":
                        arrayList = new ArrayList<>(Arrays.asList(workoutSurvey.getEquipmentList()));
                        arrayList.remove(value);
                        resultArray = arrayList.toArray(new String[0]);
                        workoutSurvey.setEquipmentList(resultArray);
                        break;
                    case "targetList":
                        arrayList = new ArrayList<>(Arrays.asList(workoutSurvey.getTargetList()));
                        arrayList.remove(value);
                        resultArray = arrayList.toArray(new String[0]);
                        workoutSurvey.setTargetList(resultArray);
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

    public void fillQuestionsByUserSurvey(WorkoutSurveyDto workoutSurveyDto) {
        //Время от
        try {
            editTextDurationFrom.setText(workoutSurveyDto.getDurationFrom());
        }
        catch (Exception ex) { }
        //Время до
        try {
            editTextDurationTo.setText(workoutSurveyDto.getDurationTo());
        }
        catch (Exception ex) { }
        //Кол-во от
        try {
            if (workoutSurveyDto.getAmountTrainingFrom() != null) editTextAmountTrainingFrom.setText(String.valueOf(workoutSurveyDto.getAmountTrainingFrom()));
        }
        catch (Exception ex) { }
        //Кол-во до
        try {
            if (workoutSurveyDto.getAmountTrainingTo() != null) editTextAmountTrainingTo.setText(String.valueOf(workoutSurveyDto.getAmountTrainingTo()));
        }
        catch (Exception ex) { }
        //Фокус
        try {
            for (String el : workoutSurveyDto.getFocusList()){
                createEl(el, flFocus, "focusList", false);
            }
        }
        catch (Exception ex) { }
        //Тип
        try {
            for (String el : workoutSurveyDto.getTypeList()){
                createEl(el, flType, "typeList", false);
            }
        }
        catch (Exception ex) { }
        //Оборудование
        try {
            for (String el : workoutSurveyDto.getEquipmentList()){
                createEl(el, flEquipment, "equipmentList", false);
            }
        }
        catch (Exception ex) { }
        //Цель
        try {
            for (String el : workoutSurveyDto.getTargetList()){
                createEl(el, flTarget, "targetList", false);
            }
        }
        catch (Exception ex) { }
    }

    public void fillAllHelpData(WorkoutSurveyToFillDto workoutSurveyToFillDto, View view) {
        //Фокус
        ArrayAdapter<String> adapterFocus = new ArrayAdapter<>(getContext(), R.layout.spinner_dropdown_item, workoutSurveyToFillDto.getFocusList());
        spinnerFocus.setAdapter(adapterFocus);
        butAddFocus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEl(spinnerFocus.getSelectedItem().toString(), flFocus, "focusList", true);
            }
        });


        //Время приготовления
        editTextDurationFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timingDialog(editTextDurationFrom);
            }
        });
        editTextDurationTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timingDialog(editTextDurationTo);
            }
        });


        //Тип
        ArrayAdapter<String> adapterType = new ArrayAdapter<>(getContext(), R.layout.spinner_dropdown_item, workoutSurveyToFillDto.getTypeList());
        spinnerType.setAdapter(adapterType);
        butAddType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEl(spinnerType.getSelectedItem().toString(), flType, "typeList", true);
            }
        });


        //Оборудование
        ArrayAdapter<String> adapterEquipment = new ArrayAdapter<>(getContext(), R.layout.spinner_dropdown_item, workoutSurveyToFillDto.getEquipmentList());
        spinnerEquipment.setAdapter(adapterEquipment);
        butAddEquipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEl(spinnerEquipment.getSelectedItem().toString(), flEquipment, "equipmentList", true);
            }
        });

        //Цель
        ArrayAdapter<String> adapterTarget = new ArrayAdapter<>(getContext(), R.layout.spinner_dropdown_item, workoutSurveyToFillDto.getTargetList());
        spinnerTarget.setAdapter(adapterTarget);
        butAddTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEl(spinnerTarget.getSelectedItem().toString(), flTarget, "targetList", true);
            }
        });

        if (workoutSurveyToFillDto.getWorkoutSurveyDto() != null) {
            fillQuestionsByUserSurvey(workoutSurveyToFillDto.getWorkoutSurveyDto());
            workoutSurvey = workoutSurveyToFillDto.getWorkoutSurveyDto();
        }
        else {
            workoutSurvey = new WorkoutSurveyDto();
        }
        if (workoutSurvey.getFocusList() == null) workoutSurvey.setFocusList(new String[]{});
        if (workoutSurvey.getTypeList() == null) workoutSurvey.setTypeList(new String[]{});
        if (workoutSurvey.getEquipmentList() == null) workoutSurvey.setEquipmentList(new String[]{});
        if (workoutSurvey.getTargetList() == null) workoutSurvey.setTargetList(new String[]{});

    }

    public void getWorkoutSurveyToFill(View view, Long id) {
        disposable.add((vkrService.getApi().GetInfoToFillSurveyWorkout(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<WorkoutSurveyToFillDto, Throwable>() {
                    @Override
                    public void accept(WorkoutSurveyToFillDto workoutSurveyToFillDto, Throwable throwable) throws Exception {
                        if (throwable != null & workoutSurveyToFillDto !=null) {
                            Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            fillAllHelpData(workoutSurveyToFillDto, view);
                        }
                    }
                })));
    }

    public void saveWorkoutRecomendations(WorkoutSurveyDto workoutSurvey) {
        disposable.add((vkrService.getApi().CreateOrUpdateWorkoutSurvey(workoutSurvey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<WorkoutSurveyDto, Throwable>() {
                    @Override
                    public void accept(WorkoutSurveyDto el, Throwable throwable) throws Exception {
                        if (throwable != null & el !=null) {
                            Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            ((ActivityMainMenu)requireActivity()).onBackPressed();
                        }
                    }
                })));
    }
}