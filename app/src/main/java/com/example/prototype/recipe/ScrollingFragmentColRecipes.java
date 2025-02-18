package com.example.prototype.recipe;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.prototype.ActivityMainMenu;
import com.example.prototype.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class ScrollingFragmentColRecipes extends Fragment {
    private ViewPager2 viewPager2;
    private TabLayout tabs;
    private List<String> texts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scrolling_col_recipes, container, false);

        // Инициализация ViewPager2
        viewPager2 = view.findViewById(R.id.pager);
        tabs = view.findViewById(R.id.tablayuout_recipes);

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
                        fragment = new ScrollingFragmentColRecipesListAll();
                        break;
                    case 1:
                        fragment = new ScrollingFragmentColRecipesListRec();
                        break;
                    case 2:
                        fragment = new ScrollingFragmentColRecipesListFav();
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

    public ScrollingFragmentColRecipes newInstance() {
        ScrollingFragmentColRecipes frag = new ScrollingFragmentColRecipes();
        return frag;
    }
}
