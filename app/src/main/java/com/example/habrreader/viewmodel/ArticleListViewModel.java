package com.example.habrreader.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.habrreader.data.model.Article;
import com.example.habrreader.data.repository.ArticleRepository;

import java.util.ArrayList;
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
    private final int perPage = 20;

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
        Disposable disposable = repository.getArticles(currentPage, perPage)
                .subscribe(
                        articles -> {
                            articlesLiveData.setValue(articles);
                            isLoadingLiveData.setValue(false);
                            if (articles.isEmpty()) {
                                errorLiveData.setValue("No data available or network error");
                            } else {
                                errorLiveData.setValue(null);
                            }
                        },
                        throwable -> {
                            errorLiveData.setValue("Error loading images: " + throwable.getMessage());
                            isLoadingLiveData.setValue(false);
                            articlesLiveData.setValue(new ArrayList<>());
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
                            loadArticles(); // Обновляем список после изменения избранного
                        },
                        throwable -> errorLiveData.setValue("Error toggling favorite: " + throwable.getMessage())
                );
        disposables.add(disposable);
    }

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
}