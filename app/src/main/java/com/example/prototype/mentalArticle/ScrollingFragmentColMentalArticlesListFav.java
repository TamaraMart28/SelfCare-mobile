package com.example.prototype.mentalArticle;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.example.prototype.Api.Dto.mentalArticle.AllAboutMentalArticleDto;
import com.example.prototype.Api.Dto.mentalArticle.MentalArticleDto;
import com.example.prototype.Api.Dto.recipe.RecipeDto;
import com.example.prototype.Api.VkrService;
import com.example.prototype.R;
import com.example.prototype.recipe.RecipeAdapter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;

public class ScrollingFragmentColMentalArticlesListFav extends Fragment {
    private NestedScrollView nestedScrollView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    public MentalArticleAdapter mentalArticleAdapter;
    public List<MentalArticleDto> mentalArticleList = new ArrayList<>();
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
        View view = inflater.inflate(R.layout.fragment_scrolling_col_mental_articles_list_fav, container, false);

        // Инициализация RecyclerView и адаптера
        nestedScrollView = view.findViewById(R.id.nestedMentalArticleFav);
        recyclerView = view.findViewById(R.id.recyclerViewMentalArticlesFav);
        progressBar = view.findViewById(R.id.progres);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mentalArticleAdapter = new MentalArticleAdapter(getContext(), mentalArticleList);
        recyclerView.setAdapter(mentalArticleAdapter);

        scrollInitialized = false;
        currentPage = 0;
        mentalArticleAdapter.clear();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        getFavoriteMentalArticles(sharedPreferences.getLong("user_id", 0), "MentalArticle", currentPage, pageSize);

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
                    getFavoriteMentalArticles(sharedPreferences.getLong("user_id", 0), "MentalArticle", currentPage, pageSize);
                }
            }
        });

        return view;
    }

    public void getFavoriteMentalArticles(Long userAccountId, String type, int pageNumber, int pageSize){
        disposable.add(vkrService.getApi().GetMentalArticlesByFavoritePagination(userAccountId, type, pageNumber, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<List<MentalArticleDto>, Throwable>() {
                    @Override
                    public void accept(List<MentalArticleDto> MentalArticles, Throwable throwable) throws Exception {
                        if (throwable != null) {
                            Toast.makeText(getActivity(), "Data loading error", Toast.LENGTH_SHORT).show();
                            System.out.println(throwable.getMessage());
                        } else {
                            progressBar.setVisibility(View.GONE);
                            mentalArticleAdapter.addMentalArticles(MentalArticles);
                            currentPage++;

                            isLoading = false;
                        }
                    }
                }));
    }
}
