package com.example.prototype.recipe;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.prototype.ActivityMainMenu;
import com.example.prototype.Api.Dto.FavoriteDto;
import com.example.prototype.Api.Dto.recipe.RecipeDto;
import com.example.prototype.Api.Dto.recipe.RecipeSurveyDto;
import com.example.prototype.Api.VkrService;
import com.example.prototype.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;

public class ScrollingFragmentColRecipesListRec extends Fragment {
    private NestedScrollView nestedScrollView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private RecipeAdapter recipeAdapter;
    private List<RecipeDto> recipeList = new ArrayList<>();
    private int currentPage = 0;
    private int pageSize = 10;
    private int scrollPosition;
    private boolean isLoading = false;
    Long userId;
    String clockIcon = "\u23F3";
    String portionIcon = "\uD83C\uDF7D";

    CompositeDisposable disposable = new CompositeDisposable();
    public final VkrService vkrService = new VkrService();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getLong("user_id", 0);
        //getRecipeRecomendations();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scrolling_col_recipes_list_rec, container, false);

        // Инициализация RecyclerView и адаптера
        nestedScrollView = view.findViewById(R.id.nestedRecipeRec);
        recyclerView = view.findViewById(R.id.recyclerViewRecipesRec);
        progressBar = view.findViewById(R.id.progres);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recipeAdapter = new RecipeAdapter(getContext(), recipeList);
        recyclerView.setAdapter(recipeAdapter);

        recipeAdapter.clear();
        getRecipeRecomendations();

        Button buttonToFragRec = view.findViewById(R.id.button_recipes_rec_to_frag);
        buttonToFragRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment selectedFragment = new ScrollingFragmentRecipeSurvey();
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

    // Метод для проверки видимости представления внутри RecyclerView
    private boolean isViewVisible(View view) {
        Rect scrollBounds = new Rect();
        nestedScrollView.getDrawingRect(scrollBounds);
        float top = view.getY();
        float bottom = top + view.getHeight();
        return scrollBounds.top < bottom && scrollBounds.bottom > top;
    }

    public void getRecipeRecomendations() {
        progressBar.setVisibility(View.VISIBLE);
        disposable.add((vkrService.getApi().GetRecipesBySurvey(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<List<RecipeDto>, Throwable>() {
                    @Override
                    public void accept(List<RecipeDto> list, Throwable throwable) throws Exception {
                        if (throwable != null & list !=null) {
                            Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            //progressBar.setVisibility(View.GONE);
                            if (list != null) recipeAdapter.addRecipes(list); // Уведомляем адаптер о изменениях
                            progressBar.setVisibility(View.GONE);
                            //currentPage++;
                        }
                    }
                })));
    }

}
