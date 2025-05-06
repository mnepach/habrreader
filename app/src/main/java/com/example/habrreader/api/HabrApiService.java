package com.example.habrreader.api;

import com.example.habrreader.data.model.ApiResponse;
import com.example.habrreader.data.model.Article;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface HabrApiService {

    // Получение списка статей
    @GET("articles")
    Single<ApiResponse> getArticles(
            @Query("page") int page,
            @Query("per_page") int perPage
    );

    // Получение детальной информации о статье
    @GET("articles/{id}")
    Single<Article> getArticleDetails(@Path("id") int articleId);
}