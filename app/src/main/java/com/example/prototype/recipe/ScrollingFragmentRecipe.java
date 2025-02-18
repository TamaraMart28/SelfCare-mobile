package com.example.prototype.recipe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.prototype.Api.Dto.AnalyticsDto;
import com.example.prototype.Api.Dto.FavoriteDto;
import com.example.prototype.Api.Dto.recipe.*;
import com.example.prototype.Api.VkrService;
import com.example.prototype.R;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.internal.FlowLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;

public class ScrollingFragmentRecipe extends Fragment {
    private LinearLayout containerRecipe;
    private AllAboutRecipeDto allAboutRecipe;

    CompositeDisposable disposable = new CompositeDisposable();
    public final VkrService vkrService = new VkrService();

    public ScrollingFragmentRecipe() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scrolling_recipe, container, false);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getContext())
                .build();
        ImageLoader.getInstance().init(config);

        containerRecipe = view.findViewById(R.id.ll_recipe);
        getAllAboutRecipe();
        
        return view;
    }

    public void getAllAboutRecipe(){
        disposable.add(vkrService.getApi().GetAllAboutRecipe(requireArguments().getLong("recipeId"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<AllAboutRecipeDto, Throwable>() {
                    @Override
                    public void accept(AllAboutRecipeDto AllAboutRecipe, Throwable throwable) throws Exception {
                        if (throwable != null) {
                            Toast.makeText(getActivity(), "Data loading error", Toast.LENGTH_SHORT).show();
                            System.out.println(throwable.getMessage());
                        } else {
                            allAboutRecipe = AllAboutRecipe;
                            fullRecipe();
                        }
                    }
                }));
    }

    @SuppressLint("ResourceAsColor")
    private void fullRecipe() {
        //Название рецепта
        TextView textViewName = new TextView(getContext());
        LinearLayout.LayoutParams textParamsName = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        textViewName.setLayoutParams(textParamsName);
        textViewName.setText(allAboutRecipe.getName());
        textViewName.setPadding(15, 15, 15, 15);
        textViewName.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_brown));
        textViewName.setTextSize(23);
        Typeface typefaceRegular = ResourcesCompat.getFont(getContext(), R.font.gothicb);
        textViewName.setTypeface(typefaceRegular);
        textViewName.setGravity(Gravity.CENTER_HORIZONTAL);
        containerRecipe.addView(textViewName);


        RelativeLayout relativeLayout = new RelativeLayout(getContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        relativeLayout.setLayoutParams(layoutParams);

        //Картинка рецепта
        ImageView imageView = new ImageView(getContext());
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(imageParams);
        imageView.setPadding(30, 15, 30, 15);
        imageView.setScaleType(ImageView.ScaleType.FIT_START);
        imageView.setAdjustViewBounds(true);
        Glide.with(getContext()).load(getString(R.string.server_url) + "images/" + allAboutRecipe.getImagePath()).
                diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).
                into(imageView);
        relativeLayout.addView(imageView);
        //Кнопка избранное
        ImageButton imageButton = new ImageButton(getContext());
        RelativeLayout.LayoutParams buttonLayoutParams = new RelativeLayout.LayoutParams(100, 100);
        buttonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
        buttonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        buttonLayoutParams.setMargins(0, 20, 40, 0);
        imageButton.setLayoutParams(buttonLayoutParams);
        if (allAboutRecipe.getFavoriteId() != null) {
            imageButton.setBackgroundResource(R.drawable.ic_baseline_favorite_choosen_24);
        } else {
            imageButton.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24);
        }
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButton.setEnabled(false);
                if (allAboutRecipe.getFavoriteId() != null) {
                    imageButton.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24);
                    deleteFavorite(allAboutRecipe.getFavoriteId(), imageButton);
                }
                else {
                    FavoriteDto favoriteToCreate = new FavoriteDto();
                    favoriteToCreate.setType("Recipe");
                    favoriteToCreate.setMaterialId(allAboutRecipe.getRecipeId());
                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
                    favoriteToCreate.setUserAccountId(sharedPreferences.getLong("user_id", 0));

                    imageButton.setBackgroundResource(R.drawable.ic_baseline_favorite_choosen_24);
                    createFavorite(favoriteToCreate, imageButton);
                }
            }
        });
        relativeLayout.addView(imageButton);
        containerRecipe.addView(relativeLayout);


        //Доп информация
        TextView textViewInfo = new TextView(getContext());
        textViewInfo.setLayoutParams(textParamsName);
        String time = String.format("%02d", allAboutRecipe.getTiming()[0]) + ":" + String.format("%02d", allAboutRecipe.getTiming()[1]);
        int portion = allAboutRecipe.getPortionAmount();
        String calories = "";
        if (allAboutRecipe.getCalories() != -1) {
            calories =  "\n" + "Кол-во Ккалорий на 100 г \u2015 " + allAboutRecipe.getCalories();
        }
        textViewInfo.setText("Время приготовления \u2015 " + time + "\n"
                + "Кол-во порций \u2015 " + portion + calories);
        textViewInfo.setPadding(15, 15, 15, 15);
        textViewInfo.setTextColor(ContextCompat.getColor(getContext(), R.color.brown));
        textViewInfo.setTextSize(15);
        typefaceRegular = ResourcesCompat.getFont(getContext(), R.font.gothici);
        textViewInfo.setTypeface(typefaceRegular);
        textViewInfo.setGravity(Gravity.CENTER_HORIZONTAL);
        containerRecipe.addView(textViewInfo);

        //Описание
        TextView textViewDescription = new TextView(getContext());
        textViewDescription.setLayoutParams(textParamsName);
        textViewDescription.setText(allAboutRecipe.getDescription());
        textViewDescription.setPadding(15, 15, 15, 15);
        textViewDescription.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_brown));
        textViewDescription.setTextSize(15);
        typefaceRegular = ResourcesCompat.getFont(getContext(), R.font.gothic);
        textViewDescription.setTypeface(typefaceRegular);
        containerRecipe.addView(textViewDescription);

        //Ингредиенты
        TextView textViewIngrHead = new TextView(getContext());
        textViewIngrHead.setLayoutParams(textParamsName);
        textViewIngrHead.setText("Ингредиенты");
        textViewIngrHead.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_brown));
        textViewIngrHead.setTextSize(20);
        typefaceRegular = ResourcesCompat.getFont(getContext(), R.font.gothicb);
        textViewIngrHead.setTypeface(typefaceRegular);
        textViewIngrHead.setGravity(Gravity.CENTER_HORIZONTAL);
        containerRecipe.addView(textViewIngrHead);
        //
        TableLayout tableLayoutIngr = new TableLayout(getContext());
        LinearLayout.LayoutParams tableParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        tableLayoutIngr.setLayoutParams(tableParams);
        tableLayoutIngr.setPadding(30, 15, 30, 15);
        //for (int i = 0; i < allAboutRecipe.getRecipeIngredientList().size(); i++) {
        allAboutRecipe.getRecipeIngredientList().forEach((key, value) -> {
            TableRow tableRow = new TableRow(getContext());
            TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT);
            tableRow.setLayoutParams(tableRowParams);
            tableRow.setBackgroundResource(R.drawable.border);
            tableRow.setGravity(Gravity.CENTER_HORIZONTAL);

            TextView textViewIngr = new TextView(getContext());
            TableRow.LayoutParams params = new TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    0.5F);
            textViewIngr.setLayoutParams(params);
            textViewIngr.setPadding(15, 15, 15, 15);
            textViewIngr.setGravity(Gravity.CENTER);
            textViewIngr.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_brown));
            textViewIngr.setText(key);
            textViewIngr.setSingleLine(false);
            textViewIngr.setTextSize(15);
            Typeface typefaceRegularCur = ResourcesCompat.getFont(getContext(), R.font.gothic);
            textViewIngr.setTypeface(typefaceRegularCur);
            tableRow.addView(textViewIngr);


            TextView textView = new TextView(getContext());
            TableRow.LayoutParams params2 = new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.MATCH_PARENT);
            textView.setLayoutParams(params2);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
            textView.setText("|");
            tableRow.addView(textView);

            TextView textViewAmount = new TextView(getContext());
            textViewAmount.setLayoutParams(params);
            textViewAmount.setHeight(textViewIngr.getHeight());
            textViewAmount.setPadding(15, 15, 15, 15);
            textViewAmount.setGravity(Gravity.CENTER);
            textViewAmount.setTextColor(ContextCompat.getColor(getContext(), R.color.brown));
            textViewAmount.setTypeface(typefaceRegularCur);
            //textViewAmount.setText(ingredientList.get(i).getAmount());
            textViewAmount.setText(value);
            textViewAmount.setSingleLine(false);
            textViewAmount.setTextSize(15);
            tableRow.addView(textViewAmount);

            tableLayoutIngr.addView(tableRow);
        });
        containerRecipe.addView(tableLayoutIngr);

        //Шаги приготовления
        TextView textViewSCHead = new TextView(getContext());
        textViewSCHead.setLayoutParams(textParamsName);
        textViewSCHead.setText("Шаги приготовления");
        textViewSCHead.setTypeface(typefaceRegular);
        textViewSCHead.setPadding(0, 0, 0, 15);
        textViewSCHead.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_brown));
        textViewSCHead.setTextSize(20);
        textViewSCHead.setGravity(Gravity.CENTER_HORIZONTAL);
        containerRecipe.addView(textViewSCHead);
        //
        //for (int i = 0; i < stepCookingList.size(); i++) {
        allAboutRecipe.getStepCookingList().forEach((key, value) -> {
            CardView cardView = new CardView(getContext());
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardParams.setMargins(15, 0, 15, 5);
            cardView.setLayoutParams(cardParams);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke(2, ContextCompat.getColor(getContext(), R.color.green));
            cardView.setBackground(drawable);
            // Создание TextView для названия
            TextView textViewSC = new TextView(getContext());
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            textViewSC.setLayoutParams(textParams);
            textViewSC.setPadding(15, 15, 15, 15);
            String step = key + " \u2015 " + value;
            textViewSC.setText(step);
            textViewSC.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_brown));
            textViewSC.setTextSize(15);
            Typeface typefaceRegularCur = ResourcesCompat.getFont(getContext(), R.font.gothic);
            textViewSC.setTypeface(typefaceRegularCur);
            cardView.addView(textViewSC);
            containerRecipe.addView(cardView);
        });

        //Тэги
        FlexboxLayout flexboxLayout = new FlexboxLayout(getContext());
        flexboxLayout.setFlexDirection(FlexDirection.ROW);
        flexboxLayout.setFlexWrap(FlexWrap.WRAP);
        flexboxLayout.setJustifyContent(JustifyContent.CENTER);
        typefaceRegular = ResourcesCompat.getFont(getContext(), R.font.gothici);
        for (String tag : allAboutRecipe.getTagRecipesList()) {
            // Создать TextView для тега
            TextView tvTag = new TextView(getContext());
            tvTag.setText(tag);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke(2, ContextCompat.getColor(getContext(), R.color.green));

            tvTag.setBackground(drawable);
            tvTag.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
            tvTag.setPadding(10, 5, 10, 5); // Установить отступы
            tvTag.setTypeface(typefaceRegular);
            // Создать LayoutParams для тега
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(15, 15, 15, 15); // Установить отступы

            // Добавить тег к FlexboxLayout
            flexboxLayout.addView(tvTag, params);
        }
        // Добавить FlowLayout к FrameLayout
        containerRecipe.addView(flexboxLayout);

        Button buttonDone = new Button(getContext());
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        buttonParams.gravity = Gravity.CENTER;
        buttonParams.setMargins(0,20,0,0);
        buttonDone.setLayoutParams(buttonParams);
        buttonDone.setText("Пригодилось");
        buttonDone.setTypeface(typefaceRegular);
        buttonDone.setTextSize(15);
        buttonDone.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        buttonDone.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green));
        buttonDone.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_done_all_24, 0, 0, 0);
        buttonDone.setCompoundDrawablePadding(5);
        buttonDone.setPadding(25,0,25,0);
        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Получаем текущую дату
                Calendar calendar = Calendar.getInstance();
                int currentMonth = calendar.get(Calendar.MONTH) + 1;
                int currentYear = calendar.get(Calendar.YEAR);
                AnalyticsDto analyticsDto = new AnalyticsDto();
                analyticsDto.setMonth(currentMonth);
                analyticsDto.setYear(currentYear);
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
                analyticsDto.setUserAccountId(sharedPreferences.getLong("user_id", 0));
                analyticsDto.setRecipeCount(1);
                createUserAccountAnalytics(analyticsDto);
            }
        });
        containerRecipe.addView(buttonDone);
    }

    public void createUserAccountAnalytics(AnalyticsDto analyticsDto) {
        disposable.add((vkrService.getApi().CreateOrUpdateAnalytics(analyticsDto)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<AnalyticsDto, Throwable>() {
                    @Override
                    public void accept(AnalyticsDto AnalyticsDto, Throwable throwable) throws Exception {
                        if (throwable != null & AnalyticsDto !=null) {
                            Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Молодец!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })));
    }
//
//    public void createUserAccountRecipe(UserAccountRecipeDto userAccountRecipe) {
//        disposable.add((vkrService.getApi().CreateUserAccountRecipe(userAccountRecipe)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new BiConsumer<UserAccountRecipeDto, Throwable>() {
//                    @Override
//                    public void accept(UserAccountRecipeDto userAccountRecipeDto, Throwable throwable) throws Exception {
//                        if (throwable != null & userAccountRecipeDto !=null) {
//                            Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(getActivity(), "Молодец!", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                })));
//    }

    public void deleteFavorite(Long id, ImageButton imageButton) {
        disposable.add((vkrService.getApi().DeleteFavorite(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<FavoriteDto, Throwable>() {
                    @Override
                    public void accept(FavoriteDto favoriteDto, Throwable throwable) throws Exception {
                        if (throwable != null & favoriteDto != null) {
                            Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            allAboutRecipe.setFavoriteId(null);
                            imageButton.setEnabled(true);

                        }
                    }
                })));
    }

    public void createFavorite(FavoriteDto favoriteToCreate, ImageButton imageButton) {
        disposable.add((vkrService.getApi().CreateFavorite(favoriteToCreate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<FavoriteDto, Throwable>() {
                    @Override
                    public void accept(FavoriteDto favoriteDto, Throwable throwable) throws Exception {
                        if (throwable != null & favoriteDto !=null) {
                            Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            allAboutRecipe.setFavoriteId(favoriteDto.getId());
                            imageButton.setEnabled(true);
                        }
                    }
                })));
    }
}