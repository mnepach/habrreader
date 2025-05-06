package com.example.habrreader.data.repository;

import android.content.Context;

import com.example.habrreader.api.ApiClient;
import com.example.habrreader.api.HabrApiService;
import com.example.habrreader.data.model.Article;
import com.example.habrreader.db.AppDatabase;
import com.example.habrreader.db.ArticleDao;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ArticleRepository {

    private final HabrApiService apiService;
    private final ArticleDao articleDao;

    public ArticleRepository(Context context) {
        apiService = ApiClient.getApiService();
        articleDao = AppDatabase.getInstance(context).articleDao();
    }

    public Single<List<Article>> getArticles(int page, int perPage) {
        return apiService.getArticles(page, perPage)
                .onErrorReturn(throwable -> {
                    // Возвращаем пустой список при ошибке сети
                    return new ArrayList<>();
                })
                .flatMap(articles -> Single.just(articles)
                        .flatMap(articleList -> {
                            for (Article article : articleList) {
                                checkIfFavorite(article);
                            }
                            return Single.just(articleList);
                        }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Article> getArticleDetails(String articleId) {
        return apiService.getArticleDetails(articleId)
                .onErrorReturn(throwable -> {
                    // Возвращаем пустой объект при ошибке
                    return new Article();
                })
                .flatMap(article -> articleDao.isArticleFavorite(articleId)
                        .map(isFavorite -> {
                            article.setFavorite(isFavorite);
                            return article;
                        }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void checkIfFavorite(Article article) {
        articleDao.isArticleFavorite(article.getId())
                .subscribeOn(Schedulers.io())
                .subscribe(article::setFavorite, throwable -> {});
    }

    public Single<List<Article>> getFavoriteArticles() {
        return articleDao.getFavoriteArticles()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable addToFavorites(Article article) {
        article.setFavorite(true);
        return articleDao.insertArticle(article)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable removeFromFavorites(Article article) {
        article.setFavorite(false);
        return articleDao.deleteArticle(article)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable toggleFavorite(Article article) {
        if (article.isFavorite()) {
            return removeFromFavorites(article);
        } else {
            return addToFavorites(article);
        }
    }
}