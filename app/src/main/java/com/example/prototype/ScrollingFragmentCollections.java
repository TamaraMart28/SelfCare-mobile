package com.example.prototype;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.prototype.Api.VkrService;
import com.example.prototype.mentalArticle.ScrollingFragmentColMentalArticles;
import com.example.prototype.recipe.ScrollingFragmentColRecipes;
import com.example.prototype.recipe.ScrollingFragmentRecipeSurvey;
import com.example.prototype.workout.ScrollingFragmentColWorkouts;

import io.reactivex.disposables.CompositeDisposable;

public class ScrollingFragmentCollections extends Fragment {
    CompositeDisposable disposable = new CompositeDisposable();
    public final VkrService vkrService = new VkrService();

    public ScrollingFragmentCollections() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scrolling_collections, container, false);

        CardView card_workouts = view.findViewById(R.id.card_workouts);
        CardView card_recipes = view.findViewById(R.id.card_recipes);
        CardView card_mental_articles = view.findViewById(R.id.card_mental_material);
        card_workouts.setOnClickListener(this::onClick);
        card_recipes.setOnClickListener(this::onClick);
        card_mental_articles.setOnClickListener(this::onClick);

        return view;
    }

    public ScrollingFragmentCollections newInstance() {
        ScrollingFragmentCollections frag = new ScrollingFragmentCollections();
        return frag;
    }


    @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        Fragment selectedFragment;
        switch (view.getId()) {
            case R.id.card_workouts:
                selectedFragment = new ScrollingFragmentColWorkouts();
                ((ActivityMainMenu)requireActivity()).pushFragmentToStack(R.id.navigation_collections, selectedFragment);
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).addToBackStack(null).commit();

                break;
            case R.id.card_recipes:
                selectedFragment = new ScrollingFragmentColRecipes();
//                ((ActivityMainMenu)requireActivity()).pushFragmentToStack(R.id.navigation_collections, selectedFragment);
                /*getFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment)./*addToBackStack(null).commit();*/
                ((ActivityMainMenu)requireActivity()).pushFragmentToStack(R.id.navigation_collections, selectedFragment);
//                getFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).addToBackStack(null).commit();

                break;
            case R.id.card_mental_material:
                selectedFragment = new ScrollingFragmentColMentalArticles();
                ((ActivityMainMenu)requireActivity()).pushFragmentToStack(R.id.navigation_collections, selectedFragment);
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).addToBackStack(null).commit();

                break;
            default:
                break;
        }
    }
}