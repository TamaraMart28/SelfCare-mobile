package com.example.prototype.workout;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.prototype.ActivityMainMenu;
import com.example.prototype.Api.Dto.recipe.RecipeDto;
import com.example.prototype.Api.Dto.workout.WorkoutDto;
import com.example.prototype.Api.VkrService;
import com.example.prototype.R;
import com.example.prototype.recipe.ScrollingFragmentRecipe;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {
    private Context context;
    private List<WorkoutDto> workoutList;
    private String clockIcon = "\u23F3";
    private CompositeDisposable disposable = new CompositeDisposable();
    private final VkrService vkrService = new VkrService();

    public WorkoutAdapter(Context context, List<WorkoutDto> workoutList) {
        this.context = context;
        this.workoutList = workoutList;
    }

    public void addWorkouts(List<WorkoutDto> newWorkouts) {
        int startPosition = workoutList.size();
        workoutList.addAll(newWorkouts);
        notifyItemRangeInserted(startPosition, newWorkouts.size());
    }

    public void updateWorkout(int position, WorkoutDto updatedWorkout) {
        workoutList.set(position, updatedWorkout);
        notifyItemChanged(position);
    }

    public void clear() {
        int size = workoutList.size();
        workoutList.clear();
        notifyItemRangeRemoved(0, size);
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.workout_item_layout, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        WorkoutDto workout = workoutList.get(position);

        holder.textViewName.setText(workout.getName());

        String time = String.format("%02d", workout.getDuration()[0]) + ":" + String.format("%02d", workout.getDuration()[1]);
        int difficulty = workout.getDifficulty();
        holder.textViewInfo.setText("Сложность:" + " " + difficulty + "/5" + "\n" + clockIcon + " " + time);

        //TODO Image

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment selectedFragment = new ScrollingFragmentWorkout();
                Bundle bundle = new Bundle();
                bundle.putLong("workoutId", workout.getId());
                selectedFragment.setArguments(bundle);
                int currentMenuItem = ((ActivityMainMenu)context).getCurrentMenuItemId();
                ((ActivityMainMenu)context).pushFragmentToStack(currentMenuItem, selectedFragment);
                ((ActivityMainMenu)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).addToBackStack(null).commit();
            }
        });


    }

    @Override
    public int getItemCount() {
        return workoutList.size();
    }

    public static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName;
        public TextView textViewInfo;
        public ImageView imageView;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.workoutName);
            textViewInfo = itemView.findViewById(R.id.workoutInfo);
            imageView = itemView.findViewById(R.id.workoutImage);
        }
    }
}