package com.example.prototype.Api;

import com.example.prototype.Api.Dto.*;
import com.example.prototype.Api.Dto.mentalArticle.*;
import com.example.prototype.Api.Dto.points.PointOfInterestListDto;
import com.example.prototype.Api.Dto.points.PointsSurveyDto;
import com.example.prototype.Api.Dto.recipe.*;
import com.example.prototype.Api.Dto.workout.AllAboutWorkoutDto;
import com.example.prototype.Api.Dto.workout.WorkoutDto;
import com.example.prototype.Api.Dto.workout.WorkoutHelpInfoDto;
import com.example.prototype.Api.Dto.workout.WorkoutSurveyDto;
import com.example.prototype.Api.Dto.workout.WorkoutSurveyToFillDto;

import java.io.InputStream;
import java.text.ParseException;
import java.util.List;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.*;

public interface VkrApi {
    //USER_ACCOUNT
    @POST("account/create")
    Single<UserAccountDto> CreateUserAccount(@Body UserAccountDto userAccountDto);

    @POST("account/update")
    Single<UserAccountDto> UpdateUserAccount(@Body UserAccountDto userAccountDto);

    @GET("account/login")
    Single<UserAccountDto> SignIn(@Query("login") String login, @Query("password") String password);

    @GET("account/get/{id}")
    Single<UserAccountDto> GetUserAccount(@Path("id") Long id);

    @GET("account/getbylogin/{login}")
    Single<UserAccountDto> GetUserAccountByLogin(@Path("login") String login);


    //ANALYTICS
    @GET("analytics/getbyuseraccount/{id}")
    Single<List<AnalyticsDto>> GetAnalyticsByUserAccount(@Path("id") Long id);

    @POST("analytics/createorupdate")
    Single<AnalyticsDto> CreateOrUpdateAnalytics(@Body AnalyticsDto analyticsDto);


    //POINTS_SURVEY
    @GET("pointsSurvey/getbyuser/{id}")
    Single<PointsSurveyDto> GetPointsSurveyByUserAccount(@Path("id") Long id);

    @POST("pointsSurvey/createorupdate")
    Single<PointsSurveyDto> CreateOrUpdatePointsSurvey(@Body PointsSurveyDto pointsSurveyDto);

    @POST("pointsSurvey/points")
    Single<PointOfInterestListDto> SearchPointsNearby(@Body PointsSurveyDto pointsSurveyDto);


    //FAVORITE
    @POST("favorite/create")
    Single<FavoriteDto> CreateFavorite(@Body FavoriteDto favoriteDto);

    @DELETE("favorite/delete/{id}")
    Single<FavoriteDto> DeleteFavorite(@Path("id") Long id);



    //RECIPES

    @GET("recipe/getallpagination")
    Single<List<RecipeDto>> GetRecipesPagination(@Query("page") int page, @Query("pageSize") int pageSize);

    @GET("recipe/getbyfavoritepagination")
    Single<List<RecipeDto>> GetRecipesByFavoritePagination(@Query("userAccountId") Long userAccountId, @Query("type") String type, @Query("page") int page, @Query("pageSize") int pageSize);

    @GET("recipe/getallabout/{id}")
    Single<AllAboutRecipeDto> GetAllAboutRecipe(@Path("id") Long id);

    @GET("recipe/getinfotofillsurvey/{id}")
    Single<RecipeSurveyToFillDto> GetInfoToFillSurveyRecipe(@Path("id") Long id);

    @GET("recipe/getbysurvey/{id}")
    Single<List<RecipeDto>> GetRecipesBySurvey(@Path("id") Long id);

    //RECIPE_SURVEY
    @POST("recipeSurvey/createorupdate")
    Single<RecipeSurveyDto> CreateOrUpdateRecipeSurvey(@Body RecipeSurveyDto recipeSurveyDto);



    //MENTAL_ARTICLES
    @GET("mentalArticle/getallabout/{id}")
    Single<AllAboutMentalArticleDto> GetAllAboutMentalArticle(@Path("id") Long id);

    @GET("mentalArticle/getbyfavoritepagination")
    Single<List<MentalArticleDto>> GetMentalArticlesByFavoritePagination(@Query("userAccountId") Long userAccountId, @Query("type") String type, @Query("page") int page, @Query("pageSize") int pageSize);

    @GET("mentalArticle/getallpagination")
    Single<List<MentalArticleDto>> GetAllMentalArticlesPagination(@Query("page") int page, @Query("pageSize") int pageSize);

    @GET("mentalArticle/getinfotofillsurvey/{id}")
    Single<MentalArticleSurveyToFillDto> GetInfoToFillSurveyMA(@Path("id") Long id);

    @GET("mentalArticle/getbysurvey/{id}")
    Single<List<MentalArticleDto>> GetMentalArticlesBySurvey(@Path("id") Long id);


    //MENTAL_ARTICLES_SURVEY
    @POST("mentalArticleSurvey/createorupdate")
    Single<MentalArticleSurveyDto> CreateOrUpdateMentalArticleSurvey(@Body MentalArticleSurveyDto mentalArticleSurveyDto);



    //WORKOUTS
    @GET("workout/getallpagination")
    Single<List<WorkoutDto>> GetWorkoutsPagination(@Query("page") int page, @Query("pageSize") int pageSize);

    @GET("workout/getbyfavoritepagination")
    Single<List<WorkoutDto>> GetWorkoutsByFavoritePagination(@Query("userAccountId") Long userAccountId, @Query("type") String type, @Query("page") int page, @Query("pageSize") int pageSize);

    @GET("workout/getbyuser/{id}")
    Single<List<WorkoutDto>> GetWorkoutsByUserAccount(@Path("id") Long id);

    @GET("workout/getallabout/{id}")
    Single<AllAboutWorkoutDto> GetAllAboutWorkout(@Path("id") Long id);

    @POST("workout/createallabout")
    Single<Response<Void>> CreateAllAboutWorkout(@Body AllAboutWorkoutDto allAboutWorkoutDto);

    @POST("workout/updateallabout")
    Single<Response<Void>> UpdateAllAboutWorkout(@Body AllAboutWorkoutDto allAboutWorkoutDto);

    @DELETE("workout/deleteallabout/{id}")
    Single<Response<Void>> DeleteAllAboutWorkout(@Path("id") Long id);

    @GET("workout/gethelpinfoworkout")
    Single<WorkoutHelpInfoDto> GetHelpInfoWorkout();

    @GET("workout/getinfotofillsurvey/{id}")
    Single<WorkoutSurveyToFillDto> GetInfoToFillSurveyWorkout(@Path("id") Long id);

    @GET("workout/getbysurvey/{id}")
    Single<List<WorkoutDto>> GetWorkoutsBySurvey(@Path("id") Long id);


    //WORKOUTS_SURVEY
    @POST("workoutSurvey/createorupdate")
    Single<WorkoutSurveyDto> CreateOrUpdateWorkoutSurvey(@Body WorkoutSurveyDto workoutSurveyDto);



}
