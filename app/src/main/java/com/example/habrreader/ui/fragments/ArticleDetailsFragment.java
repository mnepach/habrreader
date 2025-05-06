package com.example.habrreader.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.habrreader.R;
import com.example.habrreader.data.model.Article;
import com.example.habrreader.viewmodel.ArticleDetailsViewModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ArticleDetailsFragment extends Fragment {

    private ArticleDetailsViewModel viewModel;
    private ImageView imageHeader;
    private TextView textTitle, textAuthor, textDate, textContent, textError;
    private ProgressBar progressBar;
    private FloatingActionButton fabFavorite;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private int articleId;

    // Форматы даты
    private final SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
    private final SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_article_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Инициализация UI компонентов
        imageHeader = view.findViewById(R.id.image_header);
        textTitle = view.findViewById(R.id.text_title);
        textAuthor = view.findViewById(R.id.text_author);
        textDate = view.findViewById(R.id.text_date);
        textContent = view.findViewById(R.id.text_content);
        textError = view.findViewById(R.id.text_error);
        progressBar = view.findViewById(R.id.progress_bar);
        fabFavorite = view.findViewById(R.id.fab_favorite);
        collapsingToolbarLayout = view.findViewById(R.id.collapsing_toolbar);
        toolbar = view.findViewById(R.id.toolbar);

        // Настройка Toolbar
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Получение ID статьи из аргументов (исправлено)
        if (getArguments() != null) {
            articleId = getArguments().getInt("articleId", 0);
        }

        // Подключение ViewModel
        viewModel = new ViewModelProvider(this).get(ArticleDetailsViewModel.class);

        // Наблюдение за изменениями данных
        viewModel.getArticle().observe(getViewLifecycleOwner(), this::updateUI);

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            fabFavorite.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                textError.setText(error);
                textError.setVisibility(View.VISIBLE);
            } else {
                textError.setVisibility(View.GONE);
            }
        });

        // Обработка нажатия на кнопку избранного
        fabFavorite.setOnClickListener(v -> viewModel.toggleFavorite());

        // Загрузка деталей статьи
        viewModel.loadArticleDetails(articleId);
    }

    private void updateUI(Article article) {
        if (article == null) return;

        // Заголовок в CollapsingToolbarLayout
        collapsingToolbarLayout.setTitle(article.getTitle());

        // Основная информация
        textTitle.setText(article.getTitle());
        textAuthor.setText(article.getAuthor());
        textContent.setText(article.getContent());

        // Форматирование даты
        try {
            Date date = apiDateFormat.parse(article.getPublishedAt());
            if (date != null) {
                textDate.setText(displayDateFormat.format(date));
            } else {
                textDate.setText(article.getPublishedAt());
            }
        } catch (ParseException e) {
            textDate.setText(article.getPublishedAt());
        }

        // Загрузка изображения
        if (article.getImageUrl() != null && !article.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(article.getImageUrl())
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(imageHeader);
        }

        // Обновление иконки избранного
        updateFavoriteIcon(article.isFavorite());
    }

    private void updateFavoriteIcon(boolean isFavorite) {
        if (isFavorite) {
            fabFavorite.setImageResource(R.drawable.ic_favorites);
            fabFavorite.setContentDescription(getString(R.string.remove_from_favorites));
        } else {
            fabFavorite.setImageResource(R.drawable.ic_favorite_border);
            fabFavorite.setContentDescription(getString(R.string.add_to_favorites));
        }
    }
}