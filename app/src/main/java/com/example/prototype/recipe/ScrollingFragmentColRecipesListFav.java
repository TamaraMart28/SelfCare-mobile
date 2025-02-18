package com.example.prototype.recipe;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class ScrollingFragmentColRecipesListFav extends Fragment {
    private NestedScrollView nestedScrollView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    public RecipeAdapter recipeAdapter;
    private ViewPager2 viewPager2;
    private List<RecipeDto> recipeList = new ArrayList<>();
    public int currentPage = 0; // Номер текущей страницы
    public int previousPage = 0;
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
        View view = inflater.inflate(R.layout.fragment_scrolling_col_recipes_list_fav, container, false);

        // Инициализация RecyclerView и адаптера
        nestedScrollView = view.findViewById(R.id.nestedRecipeFav);
        recyclerView = view.findViewById(R.id.recyclerViewRecipesFav);
        progressBar = view.findViewById(R.id.progres);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recipeAdapter = new RecipeAdapter(getContext(), recipeList);
        recyclerView.setAdapter(recipeAdapter);

        scrollInitialized = false;
        if (scrollPosition != 0 ) {
            currentPage = 0;
            recipeAdapter.clear();
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
            getFavoriteRecipes(sharedPreferences.getLong("user_id", 0), "Recipe", currentPage, scrollPosition + (pageSize - (scrollPosition % pageSize)) + pageSize);
            currentPage = (scrollPosition + (pageSize - (scrollPosition % pageSize)) + pageSize)/pageSize - 1;
            Toast.makeText(getActivity(), " " + (scrollPosition + (pageSize - (scrollPosition % pageSize))) + " " + currentPage, Toast.LENGTH_SHORT).show();
        }
        else {
            currentPage = 0;
            recipeAdapter.clear();
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
            getFavoriteRecipes(sharedPreferences.getLong("user_id", 0), "Recipe", currentPage, pageSize);
        }

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
                    getFavoriteRecipes(sharedPreferences.getLong("user_id", 0), "Recipe", currentPage, pageSize);
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


    public void getFavoriteRecipes(Long userAccountId, String type, int pageNumber, int pageSize){
        disposable.add(vkrService.getApi().GetRecipesByFavoritePagination(userAccountId, type, pageNumber, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<List<RecipeDto>, Throwable>() {
                    @Override
                    public void accept(List<RecipeDto> Recipes, Throwable throwable) throws Exception {
                        if (throwable != null) {
                            Toast.makeText(getActivity(), "Data loading error", Toast.LENGTH_SHORT).show();
                            System.out.println(throwable.getMessage());
                        } else {
                            progressBar.setVisibility(View.GONE);
                            recipeAdapter.addRecipes(Recipes);
                            currentPage++;

                            isLoading = false;
                        }
                    }
                }));
    }

}
