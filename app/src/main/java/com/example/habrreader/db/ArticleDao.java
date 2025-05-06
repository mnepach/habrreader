package com.example.habrreader.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.habrreader.data.model.Article;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertArticle(Article article);

    @Delete
    Completable deleteArticle(Article article);

    @Query("SELECT * FROM favorite_articles")
    Single<List<Article>> getFavoriteArticles();

    @Query("SELECT * FROM favorite_articles WHERE id = :articleId")
    Single<Article> getArticleById(int articleId);

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_articles WHERE id = :articleId LIMIT 1)")
    Single<Boolean> isArticleFavorite(int articleId);
}