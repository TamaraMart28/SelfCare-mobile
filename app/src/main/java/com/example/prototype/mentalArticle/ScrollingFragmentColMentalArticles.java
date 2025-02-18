package com.example.prototype.mentalArticle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.prototype.R;
import com.example.prototype.recipe.ScrollingFragmentColRecipesListAll;
import com.example.prototype.recipe.ScrollingFragmentColRecipesListFav;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class ScrollingFragmentColMentalArticles extends Fragment {
    private ViewPager2 viewPager2;
    private TabLayout tabs;
    private List<String> texts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scrolling_col_mental_articles, container, false);

        // Инициализация ViewPager2
        viewPager2 = view.findViewById(R.id.pager2);
        tabs = view.findViewById(R.id.tablayuout_mental_articles);

        // Создание списка текстов
        texts = new ArrayList<>();
        texts.add("Все");
        texts.add("Рекомендованные");
        texts.add("Избранные");

        // Создание адаптера
        FragmentStateAdapter adapter = new FragmentStateAdapter(this) {
            @Override
            public int getItemCount() {
                return texts.size();
            }

            @NonNull
            @Override
            public Fragment createFragment(int position) {
                Fragment fragment = null;
                switch (position) {
                    case 0:
                        fragment = new ScrollingFragmentColMentalArticlesListAll();
                        break;
                    case 1:
                        fragment = new ScrollingFragmentColMentalArticlesListRec();
                        break;
                    case 2:
                        fragment = new ScrollingFragmentColMentalArticlesListFav();
                        break;

                }
                Bundle bundle = new Bundle();
                bundle.putString("value", texts.get(position));
                fragment.setArguments(bundle);
                return fragment;
            }
        };

        // Установка адаптера
        viewPager2.setAdapter(adapter);

        TabLayoutMediator mediator = new TabLayoutMediator(tabs, viewPager2,
                (tab, position) -> tab.setText(texts.get(position)));
        mediator.attach();

        return view;
    }
}