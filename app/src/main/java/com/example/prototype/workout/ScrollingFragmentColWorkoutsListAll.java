package com.example.prototype.workout;

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

public class ScrollingFragmentColWorkoutsListAll extends Fragment {
    private NestedScrollView nestedScrollView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    public WorkoutAdapter workoutAdapter;
    private List<WorkoutDto> workoutList = new ArrayList<>();
    public int currentPage = 0;
    public int pageSize = 10;
    private CompositeDisposable disposable = new CompositeDisposable();
    private final VkrService vkrService = new VkrService();
    private LinearLayoutManager layoutManager;
    private boolean isLoading = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWorkouts(currentPage, pageSize);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scrolling_col_workouts_list_all, container, false);

        nestedScrollView = view.findViewById(R.id.nestedWorkout);
        recyclerView = view.findViewById(R.id.recyclerViewWorkouts);
        progressBar = view.findViewById(R.id.progres);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        workoutAdapter = new WorkoutAdapter(getContext(), workoutList);
        recyclerView.setAdapter(workoutAdapter);

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            boolean initialized = false;

            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (!initialized) {
                    initialized = true;
                    return;
                }

                //Подгрузка данных
                if (!isLoading && scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    progressBar.setVisibility(View.VISIBLE);
                    isLoading = true;
                    getWorkouts(currentPage, pageSize);

                }
            }
        });

        return view;
    }

    public void getWorkouts(int pageNumber, int pageSize) {
        disposable.add(vkrService.getApi().GetWorkoutsPagination(pageNumber, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<List<WorkoutDto>, Throwable>() {
                    @Override
                    public void accept(List<WorkoutDto> workouts, Throwable throwable) throws Exception {
                        if (throwable != null) {
                            Toast.makeText(getActivity(), "Data loading error", Toast.LENGTH_SHORT).show();
                            System.out.println(throwable.getMessage());
                        } else {
                            progressBar.setVisibility(View.GONE);
                            workoutAdapter.addWorkouts(workouts);

                            if (workouts == null || workouts.size() < pageSize) {
                                Toast.makeText(getActivity(), "Это все тренировки", Toast.LENGTH_SHORT).show();
                            }
                            currentPage++;
                            isLoading = false;
                        }
                    }
                }));
    }
}