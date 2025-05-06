package com.example.habrreader.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.habrreader.data.model.Article;
import com.example.habrreader.data.repository.ArticleRepository;

import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public class ArticleListViewModel extends AndroidViewModel {

    private final ArticleRepository repository;
    private final MutableLiveData<List<Article>> articlesLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final CompositeDisposable disposables = new CompositeDisposable();

    private int currentPage = 1;
    private static final int PER_PAGE = 20;

    public ArticleListViewModel(@NonNull Application application) {
        super(application);
        repository = new ArticleRepository(application);
        loadArticles();
    }

    public LiveData<List<Article>> getArticles() {
        return articlesLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoadingLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public void loadArticles() {
        isLoadingLiveData.setValue(true);
        Disposable disposable = repository.getArticles(currentPage, PER_PAGE)
                .subscribe(
                        articles -> {
                            articlesLiveData.setValue(articles);
                            isLoadingLiveData.setValue(false);
                        },
                        error -> {
                            errorLiveData.setValue("Ошибка загрузки статей: " + error.getMessage());
                            isLoadingLiveData.setValue(false);
                        }
                );
        disposables.add(disposable);
    }

    public void loadNextPage() {
        currentPage++;
        loadArticles();
    }

    public void refreshArticles() {
        currentPage = 1;
        loadArticles();
    }

    public void toggleFavorite(Article article) {
        Disposable disposable = repository.toggleFavorite(article)
                .subscribe(
                        () -> {
                            // Обновляем состояние в UI
                            List<Article> currentArticles = articlesLiveData.getValue();
                            if (currentArticles != null) {
                                for (Article a : currentArticles) {
                                    if (a.getId() == article.getId()) {
                                        a.setFavorite(!a.isFavorite());
                                        break;
                                    }
                                }
                                articlesLiveData.setValue(currentArticles);
                            }
                        },
                        error -> errorLiveData.setValue("Ошибка при изменении статуса избранного: " + error.getMessage())
                );
        disposables.add(disposable);
    }

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
}