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

public class FavoritesViewModel extends AndroidViewModel {

    private final ArticleRepository repository;
    private final MutableLiveData<List<Article>> favoritesLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final CompositeDisposable disposables = new CompositeDisposable();

    public FavoritesViewModel(@NonNull Application application) {
        super(application);
        repository = new ArticleRepository(application);
        loadFavorites();
    }

    public LiveData<List<Article>> getFavorites() {
        return favoritesLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoadingLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public void loadFavorites() {
        isLoadingLiveData.setValue(true);
        Disposable disposable = repository.getFavoriteArticles()
                .subscribe(
                        articles -> {
                            favoritesLiveData.setValue(articles);
                            isLoadingLiveData.setValue(false);
                        },
                        error -> {
                            errorLiveData.setValue("Ошибка загрузки избранных статей: " + error.getMessage());
                            isLoadingLiveData.setValue(false);
                        }
                );
        disposables.add(disposable);
    }

    public void toggleFavorite(Article article) {
        Disposable disposable = repository.toggleFavorite(article)
                .subscribe(
                        this::loadFavorites,
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