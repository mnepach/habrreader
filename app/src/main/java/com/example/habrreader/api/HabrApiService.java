package com.example.habrreader.api;

import com.example.habrreader.data.model.Article;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface HabrApiService {

    // Получение списка изображений котов
    @GET("images/search")
    Single<List<Article>> getArticles(
            @Query("page") int page,
            @Query("limit") int limit
    );

    // Получение детальной информации об изображении
    @GET("images/{id}")
    Single<Article> getArticleDetails(@Path("id") String articleId);
}