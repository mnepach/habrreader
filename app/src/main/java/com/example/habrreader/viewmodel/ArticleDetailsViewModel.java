package com.example.habrreader.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.habrreader.data.model.Article;
import com.example.habrreader.data.repository.ArticleRepository;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public class ArticleDetailsViewModel extends AndroidViewModel {

    private final ArticleRepository repository;
    private final MutableLiveData<Article> articleLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final CompositeDisposable disposables = new CompositeDisposable();

    public ArticleDetailsViewModel(@NonNull Application application) {
        super(application);
        repository = new ArticleRepository(application);
    }

    public LiveData<Article> getArticle() {
        return articleLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoadingLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public void loadArticleDetails(String articleId) {
        isLoadingLiveData.setValue(true);
        Disposable disposable = repository.getArticleDetails(articleId)
                .subscribe(
                        article -> {
                            articleLiveData.setValue(article);
                            isLoadingLiveData.setValue(false);
                        },
                        error -> {
                            errorLiveData.setValue("Ошибка загрузки изображения: " + error.getMessage());
                            isLoadingLiveData.setValue(false);
                        }
                );
        disposables.add(disposable);
    }

    public void toggleFavorite() {
        Article article = articleLiveData.getValue();
        if (article != null) {
            Disposable disposable = repository.toggleFavorite(article)
                    .subscribe(
                            () -> {
                                article.setFavorite(!article.isFavorite());
                                articleLiveData.setValue(article);
                            },
                            error -> errorLiveData.setValue("Ошибка при изменении статуса избранного: " + error.getMessage())
                    );
            disposables.add(disposable);
        }
    }

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
}