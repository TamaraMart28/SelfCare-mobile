package com.example.prototype;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prototype.Api.App;
import com.example.prototype.Api.Dto.AnalyticsDto;
import com.example.prototype.Api.Dto.UserAccountDto;
import com.example.prototype.Api.Dto.workout.WorkoutDto;
import com.example.prototype.Api.VkrService;
import com.example.prototype.signInOrUp.ActivityLoginData;
import com.example.prototype.signInOrUp.ActivitySignUpOrIn;
import com.example.prototype.workout.ScrollingFragmentCreateEditWorkout;
import com.example.prototype.workout.WorkoutAdapter;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;

public class ScrollingFragmentAccount extends Fragment {
    private static final String passwordRegex =  "^(?=.*[0-9])(?=.*[!@#$%^&*])(?=\\S+$).{6,}$";
    private static final String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    UserAccountDto userAccountDto;

    CompositeDisposable disposable = new CompositeDisposable();
    public final VkrService vkrService = new VkrService();
    private RecyclerView recyclerView;
    private MonthAdapter adapter;
    private List<AnalyticsDto> analyticsList;
    private int currentPosition = 0;
    private ImageButton imageBack;
    private ImageButton imageButtonNext;

    private Long userId;
    private Boolean isTrainer;
    private UserAccountDto user;

    private CardView cardView;
    private ImageButton imageButtonProfile;
    private LinearLayout layoutProfilePanel;

    private TextView textViewName;
    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPasswordOld;
    private EditText editTextPasswordNew;
    private Spinner spinnerGender;
    private EditText editTextLevel;
    private Button buttonSave;

    private Button buttonLogout;
    public PieChart pieChart;

    private CardView cardMyWorkout;
    private ImageButton imageButtonMyWorkouts;
    private LinearLayout layoutMyWorkouts;
    private RecyclerView recyclerViewMyWorkouts;
    private WorkoutAdapter workoutAdapter;
    private List<WorkoutDto> workoutList = new ArrayList<>();
    private ImageButton buttonAddWorkout;

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, App.salt);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scrolling_account, container, false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getLong("user_id", 0);
        getUserAccount();

        // Инициализация элементов
        imageButtonProfile = view.findViewById(R.id.image_button_profile);
        layoutProfilePanel = view.findViewById(R.id.layout_profile_panel);
        cardView = view.findViewById(R.id.card_view_users_data);

        textViewName = view.findViewById(R.id.text_view_name);
        editTextName = view.findViewById(R.id.edit_text_name);
        editTextEmail = view.findViewById(R.id.edit_text_email);
        editTextPasswordOld = view.findViewById(R.id.edit_text_password_old);
        editTextPasswordNew = view.findViewById(R.id.edit_text_password_new);
        spinnerGender = view.findViewById(R.id.spinner_gender);
        editTextLevel = view.findViewById(R.id.edit_text_level);
        buttonSave = view.findViewById(R.id.button_save);
        buttonLogout = view.findViewById(R.id.button_logout);
        cardMyWorkout = view.findViewById(R.id.card_view_my_workout);
        imageButtonMyWorkouts = view.findViewById(R.id.image_button_my_workouts);
        layoutMyWorkouts = view.findViewById(R.id.linear_layout_my_workouts);
        buttonAddWorkout = view.findViewById(R.id.image_button_create_workout);
        recyclerViewMyWorkouts = view.findViewById(R.id.recyclerViewMyWorkouts);

        List<String> gender = new ArrayList<>();
        gender.add("Женский");
        gender.add("Мужской");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_dropdown_item, gender);
        spinnerGender.setAdapter(adapter);

        pieChart = view.findViewById(R.id.pie_chart);
        pieChart.getLegend().setTextSize(14f);
        pieChart.getLegend().setOrientation(Legend.LegendOrientation.VERTICAL);
        pieChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        pieChart.getLegend().setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
        pieChart.getLegend().setYOffset(7f);

        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawEntryLabels(false);


        // Обработчики нажатий
        imageButtonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProfilePanel(layoutProfilePanel);
            }
        });
        imageButtonMyWorkouts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProfilePanel(layoutMyWorkouts);
            }
        });
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Сохранить изменения профиля
                userAccountDto = new UserAccountDto();
                userAccountDto.setId(user.getId());
                if (!TextUtils.isEmpty(editTextName.getText())) userAccountDto.setNickname(String.valueOf(editTextName.getText()));

                if (!TextUtils.isEmpty(editTextEmail.getText())) {
                    if (!isValid(editTextEmail.getText().toString(), emailRegex)) {
                        Toast.makeText(getActivity(), "В поле «Логин» не эл. почта", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else
                        userAccountDto.setLogin(String.valueOf(editTextEmail.getText()));
                }

                if (!TextUtils.isEmpty(editTextPasswordOld.getText()) && !TextUtils.isEmpty(editTextPasswordNew.getText())) {
                    if (Objects.equals(hashPassword(String.valueOf(editTextPasswordOld.getText())), user.getPasswordHash())) {
                        if (!isValid(editTextPasswordNew.getText().toString(), passwordRegex)) {
                            Toast.makeText(getActivity(), "Пароль должен быть не короче 6 символов и содержать цифры и специальные знаки", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else
                            userAccountDto.setPasswordHash(hashPassword(String.valueOf(editTextPasswordNew.getText())));
                    }
                    else {
                        Toast.makeText(getActivity(), "Введите верный старый пароль", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if ((TextUtils.isEmpty(editTextPasswordOld.getText()) && !TextUtils.isEmpty(editTextPasswordNew.getText())) ||
                        (!TextUtils.isEmpty(editTextPasswordOld.getText()) && TextUtils.isEmpty(editTextPasswordNew.getText()))) {
                    Toast.makeText(getActivity(), "Введите старый и новый пароль", Toast.LENGTH_SHORT).show();
                    return;
                }

                userAccountDto.setGender(spinnerGender.getSelectedItemPosition() != 0);

                if (!TextUtils.isEmpty(editTextLevel.getText())) {
                    int value = -1;
                    try {
                        value = Integer.parseInt(String.valueOf(editTextLevel.getText()));
                    }
                    catch (Exception e) {
                        Toast.makeText(getActivity(), "Уровень должен быть числом от 1 до 5", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (value >= 1 && value <= 5) userAccountDto.setLevel(value);
                    else {
                        Toast.makeText(getActivity(), "Уровень должен быть числом от 1 до 5", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                checkUserExistance(editTextEmail.getText().toString());

            }
        });
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = getActivity().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.remove("user_id");
                editor.apply();

                Intent intent = new Intent(getActivity(), ActivitySignUpOrIn.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        buttonAddWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment selectedFragment = new ScrollingFragmentCreateEditWorkout();
                Bundle bundle = new Bundle();
                selectedFragment.setArguments(bundle);
                int currentMenuItem = ((ActivityMainMenu)getContext()).getCurrentMenuItemId();
                ((ActivityMainMenu)getContext()).pushFragmentToStack(currentMenuItem, selectedFragment);
                ((ActivityMainMenu)getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).addToBackStack(null).commit();

            }
        });


        // Инициализация RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewMonths);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        imageBack = view.findViewById(R.id.btn_previous);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPreviousClick();
            }
        });
        imageButtonNext = view.findViewById(R.id.btn_next);
        imageButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNextClick();
            }
        });
        getAnalytics(userId);

        return view;
    }

    public void checkUserExistance(String login) {
        disposable.add((vkrService.getApi().GetUserAccountByLogin(login)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<UserAccountDto, Throwable>() {
                    @Override
                    public void accept(UserAccountDto UserAccountDto, Throwable throwable) throws Exception {
                        if (throwable != null && UserAccountDto == null) {
                            updateUserAccount(userAccountDto);
                        } else {
                            if (!Objects.equals(UserAccountDto.getId(), userId)) {
                                Toast.makeText(getActivity(), "Пользователь с таким логином уже существует", Toast.LENGTH_SHORT).show();
                            }
                            else updateUserAccount(userAccountDto);

                        }
                    }
                })));
    }

    public static boolean isValid(String value, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    // Обработчик нажатия на кнопку "Назад"
    public void onPreviousClick() {
        if (currentPosition > 0) {
            currentPosition--;
            recyclerView.smoothScrollToPosition(currentPosition);
            chartInit(adapter.getItem(currentPosition).getMentalArticleCount(), adapter.getItem(currentPosition).getRecipeCount(),adapter.getItem(currentPosition).getWorkoutCount());
        }
    }

    // Обработчик нажатия на кнопку "Вперед"
    public void onNextClick() {
        if (currentPosition < adapter.getItemCount() - 1) {
            currentPosition++;
            recyclerView.smoothScrollToPosition(currentPosition);
            chartInit(adapter.getItem(currentPosition).getMentalArticleCount(), adapter.getItem(currentPosition).getRecipeCount(), adapter.getItem(currentPosition).getWorkoutCount());
        }
    }

    public void getUserAccount(){
        disposable.add(vkrService.getApi().GetUserAccount(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<UserAccountDto, Throwable>() {
                    @Override
                    public void accept(UserAccountDto userAccountDto, Throwable throwable) throws Exception {
                        if (throwable != null) {
                            Toast.makeText(getActivity(), "Data loading error", Toast.LENGTH_SHORT).show();
                            System.out.println(throwable.getMessage());
                        } else {
                            user = userAccountDto;
                            textViewName.setText(user.getNickname());
                            editTextName.setText(user.getNickname());
                            editTextEmail.setText(user.getLogin());
                            editTextPasswordOld.setText("");
                            editTextPasswordNew.setText("");
                            isTrainer = user.getIsTrainer();
                            if (isTrainer) {
                                cardMyWorkout.setVisibility(View.VISIBLE);
                                recyclerViewMyWorkouts.setLayoutManager(new LinearLayoutManager(getContext()));
                                workoutAdapter = new WorkoutAdapter(getContext(), workoutList);
                                recyclerViewMyWorkouts.setAdapter(workoutAdapter);

                                workoutAdapter.clear();
                                getMyWorkouts();
                            }
                            if (user.getGender()) {
                                spinnerGender.setSelection(1);
                            } else {
                                spinnerGender.setSelection(0);
                            }
                            editTextLevel.setText(String.valueOf(user.getLevel()));
                        }
                    }
                }));
    }

    public void updateUserAccount(UserAccountDto userAccountDto) {
        disposable.add(vkrService.getApi().UpdateUserAccount(userAccountDto)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<UserAccountDto, Throwable>() {
                    @Override
                    public void accept(UserAccountDto user, Throwable throwable) throws Exception {
                        if (throwable != null) {
                            Toast.makeText(getActivity(), "Data loading error", Toast.LENGTH_SHORT).show();
                            System.out.println(throwable.getMessage());
                        } else {
                            Toast.makeText(getActivity(), "Успешно", Toast.LENGTH_SHORT).show();
                            getUserAccount();
                        }
                    }
                }));
    }

    public void getAnalytics(Long userId) {
        disposable.add(vkrService.getApi().GetAnalyticsByUserAccount(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<List<AnalyticsDto>, Throwable>() {
                    @Override
                    public void accept(List<AnalyticsDto> analyticsDtoList, Throwable throwable) throws Exception {
                        if (throwable != null) {
                            Toast.makeText(getActivity(), "Data loading error", Toast.LENGTH_SHORT).show();
                            System.out.println(throwable.getMessage());
                        } else {
//                          // Создание и установка адаптера
                            analyticsList = analyticsDtoList;
                            adapter = new MonthAdapter(getContext(), analyticsList);
                            recyclerView.setAdapter(adapter);
                            setupChar();
                        }
                    }
                }));
    }

    public void getMyWorkouts() {
        disposable.add((vkrService.getApi().GetWorkoutsByUserAccount(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<List<WorkoutDto>, Throwable>() {
                    @Override
                    public void accept(List<WorkoutDto> list, Throwable throwable) throws Exception {
                        if (throwable != null & list !=null) {
                            Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            if (list != null) workoutAdapter.addWorkouts(list);
                        }
                    }
                })));
    }

    public void setupChar() {
        // Получаем текущую дату
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentYear = calendar.get(Calendar.YEAR);
        int position = adapter.getPositionByMonthAndYear(currentMonth, currentYear);
        if (position == -1) {
            //TODO создание записи analytics с текущей датой и по нулям
            AnalyticsDto analyticsDto = new AnalyticsDto();
            analyticsDto.setMonth(currentMonth);
            analyticsDto.setYear(currentYear);
            analyticsDto.setUserAccountId(userId);
            createUserAccountAnalytics(analyticsDto);
        }
        else {
            currentPosition = position;
            recyclerView.scrollToPosition(currentPosition);
            chartInit(adapter.getItem(currentPosition).getMentalArticleCount(), adapter.getItem(currentPosition).getRecipeCount(),adapter.getItem(currentPosition).getWorkoutCount());
        }
    }


    public void createUserAccountAnalytics(AnalyticsDto analyticsDto) {
        disposable.add((vkrService.getApi().CreateOrUpdateAnalytics(analyticsDto)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<AnalyticsDto, Throwable>() {
                    @Override
                    public void accept(AnalyticsDto AnalyticsDto, Throwable throwable) throws Exception {
                        if (throwable != null & AnalyticsDto !=null) {
                            Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            adapter.addCurrentMonthAndYearItem(AnalyticsDto);
                            currentPosition = adapter.getItemCount() - 1;
                            recyclerView.scrollToPosition(currentPosition);
                            chartInit(adapter.getItem(currentPosition).getMentalArticleCount(), adapter.getItem(currentPosition).getRecipeCount(),adapter.getItem(currentPosition).getWorkoutCount());
                        }
                    }
                })));
    }

    public void chartInit(int mentalArticles, int recipes, int workouts) {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry((float)mentalArticles,"Ментальное здоровье"));
        pieEntries.add(new PieEntry((float)recipes,"Рецепты"));
        pieEntries.add(new PieEntry((float)workouts,"Физические тренировки"));

        if (mentalArticles == 0 && recipes == 0 && workouts == 0) {
            pieChart.setData(null);
            pieChart.setNoDataText("Самое время ознакомиться с материалами!");
            pieChart.setNoDataTextColor(Color.BLACK);
        } else {
            PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
            pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            pieDataSet.setValueTextSize(15f);

            PieData pieData = new PieData(pieDataSet);
            pieChart.setData(pieData);

            pieChart.animateXY(1500,1500);
        }
        pieChart.invalidate();
    }

    private void showProfilePanel(LinearLayout layout) {
        if (layout.getVisibility() == View.VISIBLE) {
            layout.setVisibility(View.GONE);
        } else {
            layout.setVisibility(View.VISIBLE);
        }
    }
}


