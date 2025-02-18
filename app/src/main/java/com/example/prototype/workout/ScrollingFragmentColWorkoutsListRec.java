package com.example.prototype.workout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prototype.ActivityMainMenu;
import com.example.prototype.Api.Dto.recipe.RecipeDto;
import com.example.prototype.Api.Dto.workout.WorkoutDto;
import com.example.prototype.Api.VkrService;
import com.example.prototype.R;
import com.example.prototype.recipe.RecipeAdapter;
import com.example.prototype.recipe.ScrollingFragmentRecipeSurvey;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;

public class ScrollingFragmentColWorkoutsListRec extends Fragment {
    private NestedScrollView nestedScrollView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private WorkoutAdapter workoutAdapter;
    private List<WorkoutDto> workoutList = new ArrayList<>();
    Long userId;

    CompositeDisposable disposable = new CompositeDisposable();
    public final VkrService vkrService = new VkrService();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getLong("user_id", 0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scrolling_col_workouts_list_rec, container, false);

        // Инициализация RecyclerView и адаптера
        nestedScrollView = view.findViewById(R.id.nestedWorkoutRec);
        recyclerView = view.findViewById(R.id.recyclerViewWorkoutsRec);
        progressBar = view.findViewById(R.id.progres);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        workoutAdapter = new WorkoutAdapter(getContext(), workoutList);
        recyclerView.setAdapter(workoutAdapter);

        workoutAdapter.clear();
        getWorkoutRecomendations();

        Button buttonToFragRec = view.findViewById(R.id.button_workouts_rec_to_frag);
        buttonToFragRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment selectedFragment = new ScrollingFragmentWorkoutSurvey();
                ((ActivityMainMenu)requireActivity()).pushFragmentToStack(R.id.navigation_collections, selectedFragment);
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).addToBackStack(null).commit();
            }
        });

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            boolean initialized = false;

            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (!initialized) {
                    initialized = true;
                    return;
                }
            }
        });

        return view;
    }

    public void getWorkoutRecomendations() {
        progressBar.setVisibility(View.VISIBLE);
        disposable.add((vkrService.getApi().GetWorkoutsBySurvey(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<List<WorkoutDto>, Throwable>() {
                    @Override
                    public void accept(List<WorkoutDto> list, Throwable throwable) throws Exception {
                        if (throwable != null & list !=null) {
                            Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            if (list != null) workoutAdapter.addWorkouts(list);
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                })));
    }
}