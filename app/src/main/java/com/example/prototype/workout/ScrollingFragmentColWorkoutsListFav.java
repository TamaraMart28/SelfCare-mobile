package com.example.prototype.workout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.prototype.Api.Dto.recipe.RecipeDto;
import com.example.prototype.Api.Dto.workout.WorkoutDto;
import com.example.prototype.Api.VkrService;
import com.example.prototype.R;
import com.example.prototype.recipe.RecipeAdapter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;

public class ScrollingFragmentColWorkoutsListFav extends Fragment {
    private NestedScrollView nestedScrollView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    public WorkoutAdapter workoutAdapter;
    private List<WorkoutDto> workoutList = new ArrayList<>();
    public int currentPage = 0;
    private boolean isLoading = false;
    public int pageSize = 5;
    private int scrollPosition;
    boolean scrollInitialized = false;

    CompositeDisposable disposable = new CompositeDisposable();
    public final VkrService vkrService = new VkrService();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scrolling_col_workouts_list_fav, container, false);

        // Инициализация RecyclerView и адаптера
        nestedScrollView = view.findViewById(R.id.nestedWorkoutFav);
        recyclerView = view.findViewById(R.id.recyclerViewWorkoutsFav);
        progressBar = view.findViewById(R.id.progres);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        workoutAdapter = new WorkoutAdapter(getContext(), workoutList);
        recyclerView.setAdapter(workoutAdapter);

        scrollInitialized = false;
        currentPage = 0;
        workoutAdapter.clear();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        getFavoriteWorkouts(sharedPreferences.getLong("user_id", 0), "Workout", currentPage, pageSize);
        
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            boolean initialized = false;

            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (!initialized) {
                    initialized = true;
                    return;
                }

                if (!isLoading && scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    progressBar.setVisibility(View.VISIBLE);
                    isLoading = true;
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
                    getFavoriteWorkouts(sharedPreferences.getLong("user_id", 0), "Workout", currentPage, pageSize);
                }
            }
        });

        return view;
    }

    public void getFavoriteWorkouts(Long userAccountId, String type, int pageNumber, int pageSize){
        disposable.add(vkrService.getApi().GetWorkoutsByFavoritePagination(userAccountId, type, pageNumber, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<List<WorkoutDto>, Throwable>() {
                    @Override
                    public void accept(List<WorkoutDto> Workouts, Throwable throwable) throws Exception {
                        if (throwable != null) {
                            Toast.makeText(getActivity(), "Data loading error", Toast.LENGTH_SHORT).show();
                            System.out.println(throwable.getMessage());
                        } else {
                            progressBar.setVisibility(View.GONE);
                            workoutAdapter.addWorkouts(Workouts);
                            currentPage++;

                            isLoading = false;
                        }
                    }
                }));
    }
}
