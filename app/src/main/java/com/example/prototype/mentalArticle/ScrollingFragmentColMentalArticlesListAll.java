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

public class ScrollingFragmentColMentalArticlesListAll extends Fragment {
    private NestedScrollView nestedScrollView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    public MentalArticleAdapter mentalArticleAdapter;
    private List<MentalArticleDto> mentalArticleList = new ArrayList<>();
    public int currentPage = 0; // Номер текущей страницы
    public int pageSize = 10;
    private LinearLayoutManager layoutManager;
    private boolean isLoading = false;
    CompositeDisposable disposable = new CompositeDisposable();
    public final VkrService vkrService = new VkrService();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMentalArticlesAll(currentPage, pageSize);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scrolling_col_mental_articles_list_all, container, false);

        // Инициализация RecyclerView и адаптера
        nestedScrollView = view.findViewById(R.id.nestedMentalArticle);
        recyclerView = view.findViewById(R.id.recyclerViewMentalArticles);
        progressBar = view.findViewById(R.id.progres);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        mentalArticleAdapter = new MentalArticleAdapter(getContext(), mentalArticleList);
        recyclerView.setAdapter(mentalArticleAdapter);

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
                    getMentalArticlesAll(currentPage, pageSize);

                }
            }
        });


        return view;
    }

    public void getMentalArticlesAll(int pageNumber, int pageSize){
        disposable.add(vkrService.getApi().GetAllMentalArticlesPagination(pageNumber, pageSize)
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
                            mentalArticleAdapter.addMentalArticles(MentalArticles); // Уведомляем адаптер о изменениях

                            if (MentalArticles == null || MentalArticles.size() < pageSize) {
                                Toast.makeText(getActivity(), "Это все ментальные материалы", Toast.LENGTH_SHORT).show();
                            }
                            currentPage++;
                            isLoading = false;
                        }
                    }
                }));
    }
}
