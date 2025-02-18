package com.example.prototype.mentalArticle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.prototype.Api.Dto.AnalyticsDto;
import com.example.prototype.Api.Dto.FavoriteDto;
import com.example.prototype.Api.Dto.mentalArticle.AllAboutMentalArticleDto;
import com.example.prototype.Api.VkrService;
import com.example.prototype.R;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.JustifyContent;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;

public class ScrollingFragmentMentalArticle extends Fragment {
    private LinearLayout containerMentalArticle;
    private AllAboutMentalArticleDto allAboutMentalArticle;

    CompositeDisposable disposable = new CompositeDisposable();
    public final VkrService vkrService = new VkrService();

    public ScrollingFragmentMentalArticle() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scrolling_mental_article, container, false);

        containerMentalArticle = view.findViewById(R.id.ll_mental_article);
        getAllAboutMentalArticle();
        return view;
    }

    public void getAllAboutMentalArticle(){
        disposable.add(vkrService.getApi().GetAllAboutMentalArticle(requireArguments().getLong("mentalArticleId"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<AllAboutMentalArticleDto, Throwable>() {
                    @Override
                    public void accept(AllAboutMentalArticleDto AllAboutMentalArticle, Throwable throwable) throws Exception {
                        if (throwable != null) {
                            Toast.makeText(getActivity(), "Data loading error", Toast.LENGTH_SHORT).show();
                            System.out.println(throwable.getMessage());
                        } else {
                            allAboutMentalArticle = AllAboutMentalArticle;
                            fullMentalArticle();
                        }
                    }
                }));
    }

    @SuppressLint("ResourceAsColor")
    private void fullMentalArticle() {
        LinearLayout linearLayoutName = new LinearLayout(getContext());
        linearLayoutName.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
        linearLayoutName.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutName.setPadding(15, 15, 15, 15);

        //Название
        TextView textViewName = new TextView(getContext());
        LinearLayout.LayoutParams textParamsName = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f);
        textViewName.setLayoutParams(textParamsName);
        textViewName.setText(allAboutMentalArticle.getName());
        textViewName.setPadding(15, 15, 15, 15);
        textViewName.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_brown));
        textViewName.setTextSize(23);
        textViewName.setGravity(Gravity.CENTER_HORIZONTAL);
        Typeface typefaceRegular = ResourcesCompat.getFont(getContext(), R.font.gothicb);
        textViewName.setTypeface(typefaceRegular);
        linearLayoutName.addView(textViewName);
        //Кнопка избранное
        ImageButton imageButton = new ImageButton(getContext());
        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonLayoutParams.width = 100; // Ширина кнопки
        buttonLayoutParams.height = 100;
        buttonLayoutParams.setMargins(0, 20, 40, 0);
        imageButton.setLayoutParams(buttonLayoutParams);
        if (allAboutMentalArticle.getFavoriteId() != null) {
            imageButton.setBackgroundResource(R.drawable.ic_baseline_favorite_choosen_24);
        } else {
            imageButton.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24);
        }
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButton.setEnabled(false);
                if (allAboutMentalArticle.getFavoriteId() != null) {
                    imageButton.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24);
                    deleteFavorite(allAboutMentalArticle.getFavoriteId(), imageButton);
                }
                else {
                    FavoriteDto favoriteToCreate = new FavoriteDto();
                    favoriteToCreate.setType("MentalArticle");
                    favoriteToCreate.setMaterialId(allAboutMentalArticle.getMentalArticleId());
                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
                    favoriteToCreate.setUserAccountId(sharedPreferences.getLong("user_id", 0));
                    imageButton.setBackgroundResource(R.drawable.ic_baseline_favorite_choosen_24);
                    createFavorite(favoriteToCreate, imageButton);
                }
            }
        });
        linearLayoutName.addView(imageButton);
        containerMentalArticle.addView(linearLayoutName);


        //Доп информация
        LinearLayout.LayoutParams textParamsInfo = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView textViewInfo = new TextView(getContext());
        textViewInfo.setLayoutParams(textParamsInfo);
        String category = "TODO";
        String author = allAboutMentalArticle.getAuthor();
        textViewInfo.setText("Категория \u2015 " + category + "\n"
                + "Автор \u2015 " + author);
        textViewInfo.setPadding(15, 15, 15, 15);
        textViewInfo.setTextColor(ContextCompat.getColor(getContext(), R.color.brown));
        textViewInfo.setTextSize(15);
        typefaceRegular = ResourcesCompat.getFont(getContext(), R.font.gothici);
        textViewInfo.setTypeface(typefaceRegular);
        textViewInfo.setGravity(Gravity.CENTER_HORIZONTAL);
        containerMentalArticle.addView(textViewInfo);

        //Описание
        textParamsInfo.setMargins(10, 0, 10, 0);
        TextView textViewDescription = new TextView(getContext());
        textViewDescription.setLayoutParams(textParamsInfo);
        textViewDescription.setText(HtmlCompat.fromHtml(allAboutMentalArticle.getContent(), HtmlCompat.FROM_HTML_MODE_COMPACT));
        textViewDescription.setPadding(15, 15, 15, 15);
        textViewDescription.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_brown));
        textViewDescription.setTextSize(18);
        typefaceRegular = ResourcesCompat.getFont(getContext(), R.font.gothic);
        textViewDescription.setTypeface(typefaceRegular);
        containerMentalArticle.addView(textViewDescription);

        //Тэги
        FlexboxLayout flexboxLayout = new FlexboxLayout(getContext());
        flexboxLayout.setFlexDirection(FlexDirection.ROW);
        flexboxLayout.setFlexWrap(FlexWrap.WRAP);
        flexboxLayout.setJustifyContent(JustifyContent.CENTER);
        typefaceRegular = ResourcesCompat.getFont(getContext(), R.font.gothici);
        for (Map.Entry<String, List<String>> entry : allAboutMentalArticle.getTagsMap().entrySet()) {
            for (String tag : entry.getValue()) {
                TextView tvTag = new TextView(getContext());
                tvTag.setText(tag);

                GradientDrawable drawable = new GradientDrawable();
                drawable.setShape(GradientDrawable.RECTANGLE);
                drawable.setStroke(2, ContextCompat.getColor(getContext(), R.color.green));

                tvTag.setBackground(drawable);
                tvTag.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                tvTag.setPadding(10, 5, 10, 5);
                tvTag.setTypeface(typefaceRegular);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(15, 15, 15, 15);

                flexboxLayout.addView(tvTag, params);
            }
        }

        containerMentalArticle.addView(flexboxLayout);

        Button buttonDone = new Button(getContext());
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        buttonParams.gravity = Gravity.CENTER;
        buttonParams.setMargins(0,20,0,0);
        buttonDone.setLayoutParams(buttonParams);
        buttonDone.setText("Выполнено");
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
                analyticsDto.setMentalArticleCount(1);
                createUserAccountAnalytics(analyticsDto);
            }
        });
        containerMentalArticle.addView(buttonDone);
    }

    public void deleteFavorite(Long id, ImageButton imageButton) {
        disposable.add((vkrService.getApi().DeleteFavorite(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<FavoriteDto, Throwable>() {
                    @Override
                    public void accept(FavoriteDto favoriteDto, Throwable throwable) throws Exception {
                        if (throwable != null & favoriteDto !=null) {
                            System.out.println(throwable.getMessage());
                        } else {
                            allAboutMentalArticle.setFavoriteId(null);
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
                            Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            allAboutMentalArticle.setFavoriteId(favoriteDto.getId());
                            imageButton.setEnabled(true);
                        }
                    }
                })));
    }

//    public void createUserAccountMentalArticle(UserAccountMentalArticleDto userAccountMentalArticle) {
//        disposable.add((vkrService.getApi().CreateUserAccountMentalArticle(userAccountMentalArticle)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new BiConsumer<UserAccountMentalArticleDto, Throwable>() {
//                    @Override
//                    public void accept(UserAccountMentalArticleDto userAccountMentalArticleDto, Throwable throwable) throws Exception {
//                        if (throwable != null & userAccountMentalArticleDto !=null) {
//                            Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(getActivity(), "Молодец!", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                })));
//    }

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
}