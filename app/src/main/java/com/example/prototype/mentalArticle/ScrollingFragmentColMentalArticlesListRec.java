package com.example.prototype.mentalArticle;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.prototype.ActivityMainMenu;
import com.example.prototype.Api.Dto.mentalArticle.AllAboutMentalArticleDto;
import com.example.prototype.Api.Dto.mentalArticle.MentalArticleDto;
import com.example.prototype.Api.Dto.recipe.RecipeDto;
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

public class ScrollingFragmentColMentalArticlesListRec extends Fragment {
    private NestedScrollView nestedScrollView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private MentalArticleAdapter mentalArticleAdapter;
    private List<MentalArticleDto> mentalArticleList = new ArrayList<>();
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
        View view = inflater.inflate(R.layout.fragment_scrolling_col_mental_articles_list_rec, container, false);

        // Инициализация RecyclerView и адаптера
        nestedScrollView = view.findViewById(R.id.nestedMentalArtRec);
        recyclerView = view.findViewById(R.id.recyclerViewMentalArtsRec);
        progressBar = view.findViewById(R.id.progres);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mentalArticleAdapter = new MentalArticleAdapter(getContext(), mentalArticleList);
        recyclerView.setAdapter(mentalArticleAdapter);

        mentalArticleAdapter.clear();
        getMentalArticleRecomendations();

        Button buttonToFragRec = view.findViewById(R.id.button_mental_arts_rec_to_frag);
        buttonToFragRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment selectedFragment = new ScrollingFragmentMentalArticleSurvey();
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

    public void getMentalArticleRecomendations() {
        progressBar.setVisibility(View.VISIBLE);
        disposable.add((vkrService.getApi().GetMentalArticlesBySurvey(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<List<MentalArticleDto>, Throwable>() {
                    @Override
                    public void accept(List<MentalArticleDto> list, Throwable throwable) throws Exception {
                        if (throwable != null & list !=null) {
                            Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            if (list != null) mentalArticleAdapter.addMentalArticles(list);
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                })));
    }
}