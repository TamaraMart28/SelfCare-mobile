package com.example.prototype;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yandex.mapkit.MapKitFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;


public class ActivityMainMenu extends AppCompatActivity {
    private Map<Integer, Stack<Fragment>> fragmentStackMap = new HashMap<>();
    private int currentMenuItemId;
    BottomNavigationView bottomNav;

    public int getCurrentMenuItemId() {
        return currentMenuItemId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // Инициализируем стеки для каждого раздела меню
        fragmentStackMap.put(R.id.navigation_selections, new Stack<>());
        fragmentStackMap.put(R.id.navigation_collections, new Stack<>());
        fragmentStackMap.put(R.id.navigation_account, new Stack<>());

        // Показываем фрагменты по умолчанию
        bottomNav.setSelectedItemId(R.id.navigation_collections);

        //Инициализация карты
        MapKitFactory.initialize(this);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if (item.getItemId() == currentMenuItemId) {
                        clearFragmentStackForMenuItemId(currentMenuItemId);
                    }

                    currentMenuItemId = item.getItemId();
                    Fragment fragment = null;
                    switch (item.getItemId()) {
                        case R.id.navigation_selections:
                            fragment = getOrCreateFragment(R.id.navigation_selections, ScrollingFragmentSelections.class);
                            break;
                        case R.id.navigation_collections:
                            fragment = getOrCreateFragment(R.id.navigation_collections, ScrollingFragmentCollections.class);
                            break;
                        case R.id.navigation_account:
                            fragment = getOrCreateFragment(R.id.navigation_account, ScrollingFragmentAccount.class);
                            break;
                    }
                    if (fragment != null) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, fragment)
                                .addToBackStack(null)  // Добавляем в стек возврата
                                .commit();
                        return true;
                    }
                    return false;
                }
            };

    private Fragment getOrCreateFragment(int menuItemId, Class<? extends Fragment> fragmentClass) {
        Stack<Fragment> fragmentStack = fragmentStackMap.get(menuItemId);
        Fragment fragment = null;
        if (fragmentStack.isEmpty() /*|| fragmentStack.peek().getClass() != fragmentClass*/) {
            try {
                fragment = fragmentClass.newInstance();
                fragmentStack.push(fragment);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return fragmentStack.peek();
    }

    public void pushFragmentToStack(Integer key, Fragment fragment) {
        Stack<Fragment> stack = fragmentStackMap.get(key);
        stack.push(fragment);
    }

    @Override
    public void onBackPressed() {
        Stack<Fragment> stack = fragmentStackMap.get(currentMenuItemId);
        if (stack != null && !stack.isEmpty() && stack.size() != 1) {
            getSupportFragmentManager().beginTransaction().remove(stack.pop()).commit();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, stack.peek()).commit();
        }
    }

    private void clearFragmentStackForMenuItemId(int menuItemId) {
        Stack<Fragment> fragmentStack = fragmentStackMap.get(menuItemId);
        if (fragmentStack != null) {
            fragmentStack.clear();
        }
    }


    public void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
