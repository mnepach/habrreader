package com.example.habrreader.data.repository;

import android.content.Context;

import com.example.habrreader.api.ApiClient;
import com.example.habrreader.api.HabrApiService;
import com.example.habrreader.data.model.ApiResponse;
import com.example.habrreader.data.model.Article;
import com.example.habrreader.db.AppDatabase;
import com.example.habrreader.db.ArticleDao;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ArticleRepository {

    private final HabrApiService apiService;
    private final ArticleDao articleDao;

    public ArticleRepository(Context context) {
        apiService = ApiClient.getApiService();
        articleDao = AppDatabase.getInstance(context).articleDao();
    }

    // Получение списка статей из API
    public Single<List<Article>> getArticles(int page, int perPage) {
        return apiService.getArticles(page, perPage)
                .map(ApiResponse::getArticles)
                .flatMap(articles -> {
                    // Проверяем, является ли каждая статья избранной
                    return Single.just(articles)
                            .flatMap(articleList -> {
                                for (Article article : articleList) {
                                    checkIfFavorite(article);
                                }
                                return Single.just(articleList);
                            });
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // Получение детальной информации о статье
    public Single<Article> getArticleDetails(int articleId) {
        return apiService.getArticleDetails(articleId)
                .flatMap(article -> {
                    return articleDao.isArticleFavorite(articleId)
                            .map(isFavorite -> {
                                article.setFavorite(isFavorite);
                                return article;
                            });
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // Проверка, является ли статья избранной
    private void checkIfFavorite(Article article) {
        articleDao.isArticleFavorite(article.getId())
                .subscribeOn(Schedulers.io())
                .subscribe(article::setFavorite, throwable -> {});
    }

    // Получение избранных статей из локальной базы данных
    public Single<List<Article>> getFavoriteArticles() {
        return articleDao.getFavoriteArticles()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // Добавление статьи в избранное
    public Completable addToFavorites(Article article) {
        article.setFavorite(true);
        return articleDao.insertArticle(article)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // Удаление статьи из избранного
    public Completable removeFromFavorites(Article article) {
        article.setFavorite(false);
        return articleDao.deleteArticle(article)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // Переключение состояния избранного для статьи
    public Completable toggleFavorite(Article article) {
        if (article.isFavorite()) {
            return removeFromFavorites(article);
        } else {
            return addToFavorites(article);
        }
    }
}