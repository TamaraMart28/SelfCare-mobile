package com.example.prototype.recipe;

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
import com.example.prototype.Api.VkrService;
import com.example.prototype.R;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private Context context;
    private List<RecipeDto> recipeList;
    private String clockIcon = "\u23F3";
    private String portionIcon = "\uD83C\uDF7D";
    private CompositeDisposable disposable = new CompositeDisposable();
    private final VkrService vkrService = new VkrService();

    public RecipeAdapter(Context context, List<RecipeDto> recipeList) {
        this.context = context;
        this.recipeList = recipeList;
    }

    public void addRecipes(List<RecipeDto> newRecipes) {
        int startPosition = recipeList.size();
        recipeList.addAll(newRecipes);
        notifyItemRangeInserted(startPosition, newRecipes.size());
    }

    public void updateRecipe(int position, RecipeDto updatedRecipe) {
        recipeList.set(position, updatedRecipe);
        notifyItemChanged(position);
    }

    public void clear() {
        int size = recipeList.size();
        recipeList.clear();
        notifyItemRangeRemoved(0, size);
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recipe_item_layout, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        RecipeDto recipe = recipeList.get(position);

        holder.textViewName.setText(recipe.getName());

        String time = String.format("%02d", recipe.getTiming()[0]) + ":" + String.format("%02d", recipe.getTiming()[1]);
        int portion = recipe.getPortionAmount();
        holder.textViewTimingPortion.setText(clockIcon + " " + time + "     " + portionIcon + " " + portion + "     ");

        Glide.with(context).load(context.getString(R.string.server_url) + "images/" + recipe.getImagePath()).
                diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).
                into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment selectedFragment = new ScrollingFragmentRecipe();
                Bundle bundle = new Bundle();
                bundle.putLong("recipeId", recipe.getId());
                selectedFragment.setArguments(bundle);
                ((ActivityMainMenu)context).pushFragmentToStack(R.id.navigation_collections, selectedFragment);
                ((ActivityMainMenu)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).addToBackStack(null).commit();
            }
        });


    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName;
        public TextView textViewTimingPortion;
        public ImageView imageView;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.recipeNameTest);
            textViewTimingPortion = itemView.findViewById(R.id.recipeInfoTest);
            imageView = itemView.findViewById(R.id.recipeImageTest);
        }
    }
}
