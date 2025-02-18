package com.example.prototype.workout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.prototype.ActivityMainMenu;
import com.example.prototype.Api.Dto.workout.AllAboutWorkoutDto;
import com.example.prototype.Api.Dto.workout.TrainingDto;
import com.example.prototype.Api.Dto.workout.WorkoutHelpInfoDto;
import com.example.prototype.Api.VkrService;
import com.example.prototype.R;
import com.google.android.flexbox.FlexboxLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.util.Base64;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;

public class ScrollingFragmentCreateEditWorkout extends Fragment {
    private EditText editTextName;
    private EditText editTextDuration;
    private RadioGroup radioGroupDif;
    private RadioGroup radioGroupGender;
    private EditText editTextDescription;
    private AutoCompleteTextView autoCompleteTextFocus;
    private ImageButton butAddFocus;
    private FlexboxLayout flFocus;
    private AutoCompleteTextView autoCompleteTextType;
    private ImageButton butAddType;
    private FlexboxLayout flType;
    private AutoCompleteTextView autoCompleteTextEquipment;
    private ImageButton butAddEquipment;
    private FlexboxLayout flEquipment;
    private AutoCompleteTextView autoCompleteTextTarget;
    private ImageButton butAddTarget;
    private FlexboxLayout flTarget;
    private Spinner spinner;
    private EditText editTextNameTraining;
    private EditText editTextDescriptionTraining;
    private EditText editTextDurationTraining;
    private EditText editTextAmountTraining;
    private Button buttonSelectImage;
    private ImageView imageView;
    private ImageButton butAddTraining;
    private ImageButton butDeleteTraining;
    private Button buttonDone;

    private Long userId;
    private AllAboutWorkoutDto allAboutWorkoutDto;
    List<TrainingDto> trainingList;
    TrainingDto emptyTrainingDto;
    ArrayAdapter<TrainingDto> adapterTraining;
    TrainingDto itemSelected;
    int itemSelectedPosition;
    String base64Image;

    CompositeDisposable disposable = new CompositeDisposable();
    public final VkrService vkrService = new VkrService();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scrolling_create_edit_workout, container, false);

        // Инициализация всех элементов по их идентификаторам
        {
            editTextName = rootView.findViewById(R.id.edit_text_name);
            editTextDuration = rootView.findViewById(R.id.edit_text_duration);
            radioGroupDif = rootView.findViewById(R.id.radioGroupDif);
            radioGroupGender = rootView.findViewById(R.id.radioGroupGender);
            editTextDescription = rootView.findViewById(R.id.edit_text_description);
            autoCompleteTextFocus = rootView.findViewById(R.id.auto_complete_text_focus);
            butAddFocus = rootView.findViewById(R.id.but_add_focus);
            flFocus = rootView.findViewById(R.id.fl_focus);
            autoCompleteTextType = rootView.findViewById(R.id.auto_complete_text_type);
            butAddType = rootView.findViewById(R.id.but_add_type);
            flType = rootView.findViewById(R.id.fl_type);
            autoCompleteTextEquipment = rootView.findViewById(R.id.auto_complete_text_equipment);
            butAddEquipment = rootView.findViewById(R.id.but_add_equipment);
            flEquipment = rootView.findViewById(R.id.fl_equipment);
            autoCompleteTextTarget = rootView.findViewById(R.id.auto_complete_text_target);
            butAddTarget = rootView.findViewById(R.id.but_add_target);
            flTarget = rootView.findViewById(R.id.fl_target);
            spinner = rootView.findViewById(R.id.spinner_dish);
            editTextNameTraining = rootView.findViewById(R.id.edit_text_name_training);
            editTextDescriptionTraining = rootView.findViewById(R.id.edit_text_description_training);
            editTextDurationTraining = rootView.findViewById(R.id.edit_text_duration_training);
            editTextAmountTraining = rootView.findViewById(R.id.edit_text_amount_training);
            buttonSelectImage = rootView.findViewById(R.id.button_select_image);
            imageView = rootView.findViewById(R.id.imageView);
            butAddTraining = rootView.findViewById(R.id.but_add_training);
            butDeleteTraining = rootView.findViewById(R.id.but_delete_training);
            buttonDone = rootView.findViewById(R.id.button_done);
        }

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getLong("user_id", 0);

        // Создаем пустой объект TrainingDto
        emptyTrainingDto = new TrainingDto();
        emptyTrainingDto.setName("Создать тренировку");
        trainingList = new ArrayList<>();
        trainingList.add(emptyTrainingDto);
        // Создаем адаптер для спиннера, предоставляющий данные
        adapterTraining = new ArrayAdapter<>(requireContext(), R.layout.spinner_dropdown_item, trainingList);
        adapterTraining.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapterTraining);
        //
        allAboutWorkoutDto = new AllAboutWorkoutDto();
        Map<String, List<String>> tagsMap = new HashMap<String, List<String>>();
        tagsMap.put("Тренировка - Фокус", new ArrayList<>());
        tagsMap.put("Тренировка - Тип", new ArrayList<>());
        tagsMap.put("Тренировка - Оборудование", new ArrayList<>());
        tagsMap.put("Тренировка - Цель", new ArrayList<>());
        allAboutWorkoutDto.setTagsMap(tagsMap);

        getHelpInfoWorkout();

        //Обработчики
        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(editTextName.getText())
                        && !TextUtils.isEmpty(editTextDuration.getText())
                        && !TextUtils.isEmpty(editTextDescription.getText())
                        && adapterTraining.getCount() > 1) {

                    if (requireArguments().containsKey("workoutId")) {
                        fullDtoToCreateOrUpdate();
                        updateAllAboutWorkout(allAboutWorkoutDto);
                    }
                    else {
                        fullDtoToCreateOrUpdate();
                        createAllAboutWorkout(allAboutWorkoutDto);
                    }
                }
                else {
                    Toast.makeText(getActivity(), "Заполните обязательные поля", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Устанавливаем слушатель для выбора элемента
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Получаем ID выбранного элемента
                itemSelected = adapterTraining.getItem(position);
                itemSelectedPosition = position;

                if (position != 0) {
                    editTextNameTraining.setText(itemSelected.getName());
                    editTextDescriptionTraining.setText(itemSelected.getDescription());
                    setImageFromString(itemSelected.getImage());
                    base64Image = itemSelected.getImage();
                    if (itemSelected.getDuration() != null) editTextDurationTraining.setText(String.format("%02d", itemSelected.getDuration()[0]) + ":" + String.format("%02d", itemSelected.getDuration()[1]));
                    if (itemSelected.getAmount() != null) editTextAmountTraining.setText(itemSelected.getAmount());
                }
                else {
                    editTextNameTraining.setText("");
                    editTextDescriptionTraining.setText("");
                    imageView.setImageDrawable(null);
                    base64Image = null;
                    editTextDurationTraining.setText("");
                    editTextAmountTraining.setText("");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) { }
        });

        return rootView;
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
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();
                base64Image = java.util.Base64.getEncoder().encodeToString(imageBytes);
                setImageFromString(base64Image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setImageFromString(String base64Image) {
        byte[] imageByte = java.util.Base64.getDecoder().decode(base64Image);
        Bitmap restoredBitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
        imageView.setImageBitmap(restoredBitmap);
    }

    public void fillHelpInfo(WorkoutHelpInfoDto workoutHelpInfoDto) {


        editTextDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timingDialog(editTextDuration, "Часы : Минуты", 24);
            }
        });
        editTextDurationTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timingDialog(editTextDurationTraining, "Минуты : Секунды", 59);
            }
        });

        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        //Tags
        ArrayAdapter<String> adapterFocus = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, workoutHelpInfoDto.getFocusList());
        autoCompleteTextFocus.setAdapter(adapterFocus);
        butAddFocus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(autoCompleteTextFocus.getText())) createEl(String.valueOf(autoCompleteTextFocus.getText()), flFocus, "Тренировка - Фокус", true);
            }
        });
        ArrayAdapter<String> adapterType = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, workoutHelpInfoDto.getTypeList());
        autoCompleteTextType.setAdapter(adapterType);
        butAddType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(autoCompleteTextType.getText())) createEl(String.valueOf(autoCompleteTextType.getText()), flType, "Тренировка - Тип", true);


            }
        });
        ArrayAdapter<String> adapterEquipment = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, workoutHelpInfoDto.getEquipmentList());
        autoCompleteTextEquipment.setAdapter(adapterEquipment);
        butAddEquipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(autoCompleteTextEquipment.getText())) createEl(String.valueOf(autoCompleteTextEquipment.getText()), flEquipment, "Тренировка - Оборудование", true);
            }
        });
        ArrayAdapter<String> adapterTarget = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, workoutHelpInfoDto.getTargetList());
        autoCompleteTextTarget.setAdapter(adapterTarget);
        butAddTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(autoCompleteTextTarget.getText())) createEl(String.valueOf(autoCompleteTextTarget.getText()), flTarget, "Тренировка - Цель", true);
            }
        });


        butAddTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(editTextNameTraining.getText())
                        && (!TextUtils.isEmpty(editTextDurationTraining.getText()) || !TextUtils.isEmpty(editTextAmountTraining.getText()))
                        && !TextUtils.isEmpty(editTextDescriptionTraining.getText())
                        && base64Image != null) {

                    TrainingDto trainingDto = new TrainingDto();
                    trainingDto.setName(String.valueOf(editTextNameTraining.getText()));
                    trainingDto.setDescription(String.valueOf(editTextDescriptionTraining.getText()));
                    trainingDto.setImage(base64Image);
                    if (!String.valueOf(editTextDurationTraining.getText()).equals("")) {
                        trainingDto.setDuration(parseTime(String.valueOf(editTextDurationTraining.getText())));
                    }
                    if (!String.valueOf(editTextAmountTraining.getText()).equals("")) {
                        trainingDto.setAmount(String.valueOf(editTextAmountTraining.getText()) );
                    }

                    if (itemSelected != emptyTrainingDto) {
                        trainingList.set(itemSelectedPosition, trainingDto);
                    }
                    else {
                        trainingList.add(trainingDto);
                    }

                    adapterTraining.notifyDataSetChanged();
                    spinner.setSelection(0);

                    editTextNameTraining.setText("");
                    editTextDescriptionTraining.setText("");
                    imageView.setImageDrawable(null);
                    base64Image = null;
                    editTextDurationTraining.setText("");
                    editTextAmountTraining.setText("");
                }
                else {
                    Toast.makeText(getActivity(), "Заполните обязательные поля", Toast.LENGTH_SHORT).show();
                }
            }
        });
        butDeleteTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemSelected != emptyTrainingDto) {
                    trainingList.remove(itemSelected);
                    adapterTraining.notifyDataSetChanged();

                    editTextNameTraining.setText("");
                    editTextDescriptionTraining.setText("");
                    imageView.setImageDrawable(null);
                    base64Image = null;
                    editTextDurationTraining.setText("");
                    editTextAmountTraining.setText("");
                }
            }
        });
    }

    public void fullDtoToCreateOrUpdate() {
        allAboutWorkoutDto.setUserId(userId);
        allAboutWorkoutDto.setName(String.valueOf(editTextName.getText()));
        allAboutWorkoutDto.setDescription(String.valueOf(editTextDescription.getText()));
        allAboutWorkoutDto.setDuration(parseTime(String.valueOf(editTextDuration.getText())));
        int selectedRadioButtonDId = radioGroupDif.getCheckedRadioButtonId();
        if (selectedRadioButtonDId != -1) {
            RadioButton selectedRadioButton = getView().findViewById(selectedRadioButtonDId);
            Integer difficulty = Integer.parseInt(selectedRadioButton.getText().toString());
            allAboutWorkoutDto.setDifficulty(difficulty);
        }
        int selectedRadioButtonGId = radioGroupGender.getCheckedRadioButtonId();
        if (selectedRadioButtonGId != -1) {
            RadioButton selectedRadioButton = getView().findViewById(selectedRadioButtonGId);
            if (selectedRadioButton.getText().toString().equals("Для женщин")) {
                allAboutWorkoutDto.setGender(false);
            }
            else if (selectedRadioButton.getText().toString().equals("Для мужчин")) {
                allAboutWorkoutDto.setGender(true);
            }
        }
        trainingList.remove(emptyTrainingDto);
        allAboutWorkoutDto.setTrainingList(trainingList);
        if (allAboutWorkoutDto.getTagsMap().get("Тренировка - Оборудование").size() == 0) {
            List<String> list = new ArrayList<>();
            list.add("Без оборудования");
            allAboutWorkoutDto.getTagsMap().put("Тренировка - Оборудование", list);
        }
    }

    public void createEl(String value, FlexboxLayout flexboxLayout, String tagType, Boolean isFromDto) {
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
            allAboutWorkoutDto.getTagsMap().get(tagType).add(value);
        }
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flexboxLayout.removeView(linearLayout);
                allAboutWorkoutDto.getTagsMap().get(tagType).remove(value);
            }
        });

    }

    private void fullWorkout() {
        editTextName.setText(allAboutWorkoutDto.getName());
        editTextDuration.setText(String.format("%02d", allAboutWorkoutDto.getDuration()[0]) + ":" + String.format("%02d", allAboutWorkoutDto.getDuration()[1]));
        int radioButtonCount = radioGroupDif.getChildCount();
        RadioButton radioButton = null;
        for (int i = 0; i < radioButtonCount; i++) {
            View view = radioGroupDif.getChildAt(i);
            radioButton = (RadioButton) view;
            if (radioButton.getText().toString().equals(String.valueOf(allAboutWorkoutDto.getDifficulty()))) {
                radioButton.setChecked(true);
                break;
            }
        }
        radioButtonCount = radioGroupGender.getChildCount();
        for (int i = 0; i < radioButtonCount; i++) {
            View view = radioGroupGender.getChildAt(i);
            radioButton = (RadioButton) view;
            if (radioButton.getText().toString().equals("Для женщин") && allAboutWorkoutDto.getGender() != null && !allAboutWorkoutDto.getGender()) {
                radioButton.setChecked(true);
                break;
            }
            if (radioButton.getText().toString().equals("Для мужчин") && allAboutWorkoutDto.getGender() != null && allAboutWorkoutDto.getGender()) {
                radioButton.setChecked(true);
                break;
            }
            if (radioButton.getText().toString().equals("Для всех") && allAboutWorkoutDto.getGender() == null) {
                radioButton.setChecked(true);
                break;
            }
        }
        editTextDescription.setText(allAboutWorkoutDto.getDescription());
        for (String el : allAboutWorkoutDto.getTagsMap().get("Тренировка - Фокус")){
            createEl(el, flFocus, "Тренировка - Фокус", false);
        }
        for (String el : allAboutWorkoutDto.getTagsMap().get("Тренировка - Тип")){
            createEl(el, flType, "Тренировка - Тип", false);
        }
        for (String el : allAboutWorkoutDto.getTagsMap().get("Тренировка - Оборудование")){
            createEl(el, flEquipment, "Тренировка - Оборудование", false);
        }
        for (String el : allAboutWorkoutDto.getTagsMap().get("Тренировка - Цель")){
            createEl(el, flTarget, "Тренировка - Цель", false);
        }
    }

    public void getHelpInfoWorkout(){
        disposable.add(vkrService.getApi().GetHelpInfoWorkout()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<WorkoutHelpInfoDto, Throwable>() {
                    @Override
                    public void accept(WorkoutHelpInfoDto workoutHelpInfoDto, Throwable throwable) throws Exception {
                        if (throwable != null) {
                            Toast.makeText(getActivity(), "Data loading error", Toast.LENGTH_SHORT).show();
                            System.out.println(throwable.getMessage());
                        } else {
                           fillHelpInfo(workoutHelpInfoDto);
                            if (requireArguments().containsKey("workoutId")) {
                                getAllAboutWorkout();
                            }
                        }
                    }
                }));
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
                        }
                        else {
                            allAboutWorkoutDto = AllAboutWorkoutDto;
                            for (TrainingDto dto : allAboutWorkoutDto.getTrainingList()) {
                                trainingList.add(dto);
                            }
                            adapterTraining.notifyDataSetChanged();
                            fullWorkout();
                        }
                    }
                }));
    }

    public void createAllAboutWorkout(AllAboutWorkoutDto allAboutWorkoutDto) {
        disposable.add(vkrService.getApi().CreateAllAboutWorkout(allAboutWorkoutDto)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.isSuccessful()) {
                        // Обработка успешного запроса
                        Toast.makeText(getActivity(), "Успешно создано", Toast.LENGTH_SHORT).show();
                        ((ActivityMainMenu)requireActivity()).onBackPressed();
                    } else {
                        // Обработка неуспешного запроса
                        Toast.makeText(getActivity(), "Error creating workout: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }, throwable -> {
                    // Обработка ошибки
                    Toast.makeText(getActivity(), "Error creating workout", Toast.LENGTH_SHORT).show();
                    throwable.printStackTrace();
                }));
    }

    public void updateAllAboutWorkout(AllAboutWorkoutDto allAboutWorkoutDto) {
        disposable.add(vkrService.getApi().UpdateAllAboutWorkout(allAboutWorkoutDto)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.isSuccessful()) {
                        // Обработка успешного запроса
                        Toast.makeText(getActivity(), "Успешно изменено", Toast.LENGTH_SHORT).show();
                        ((ActivityMainMenu)requireActivity()).onBackPressed();
                    } else {
                        // Обработка неуспешного запроса
                        Toast.makeText(getActivity(), "Error creating workout: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }, throwable -> {
                    // Обработка ошибки
                    Toast.makeText(getActivity(), "Error creating workout", Toast.LENGTH_SHORT).show();
                    throwable.printStackTrace();
                }));
    }


    public void timingDialog(EditText editText, String title, int maxOne) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);

        Context context = getContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER);

        NumberPicker onePicker = new NumberPicker(context);
        onePicker.setMaxValue(maxOne);
        onePicker.setMinValue(0);

        NumberPicker twoPicker = new NumberPicker(context);
        twoPicker.setMaxValue(59);
        twoPicker.setMinValue(0);

        layout.addView(onePicker);
        layout.addView(twoPicker);

        builder.setView(layout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int one = onePicker.getValue();
                int two = twoPicker.getValue();
                editText.setText(String.format("%02d:%02d", one, two));
            }
        });

        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    public Integer[] parseTime(String timeString) {
        String[] parts = timeString.split(":");
        Integer[] timeValues = new Integer[parts.length];
        for (int i = 0; i < parts.length; i++) {
            timeValues[i] = Integer.parseInt(parts[i]);
        }
        return timeValues;
    }

}
