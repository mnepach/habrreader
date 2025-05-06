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

public class ArticleDetailsFragment extends Fragment {

    private ArticleDetailsViewModel viewModel;
    private ImageView imageHeader;
    private TextView textTitle, textAuthor, textDate, textContent, textError;
    private ProgressBar progressBar;
    private FloatingActionButton fabFavorite;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private String articleId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_article_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getArguments() != null) {
            articleId = getArguments().getString("articleId");
        }

        viewModel = new ViewModelProvider(this).get(ArticleDetailsViewModel.class);

        viewModel.getArticle().observe(getViewLifecycleOwner(), this::updateUI);
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            fabFavorite.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        });
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            textError.setText(error);
            textError.setVisibility(error != null ? View.VISIBLE : View.GONE);
        });

        fabFavorite.setOnClickListener(v -> viewModel.toggleFavorite());

        viewModel.loadArticleDetails(articleId);
    }

    private void updateUI(Article article) {
        if (article == null) return;

        collapsingToolbarLayout.setTitle(article.getTitle());
        textTitle.setText(article.getTitle());
        textAuthor.setText(article.getAuthor());
        textContent.setText(article.getContent());
        textDate.setText(article.getPublishedAt());

        if (article.getImageUrl() != null && !article.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(article.getImageUrl())
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(imageHeader);
        }

        updateFavoriteIcon(article.isFavorite());
    }

    private void updateFavoriteIcon(boolean isFavorite) {
        fabFavorite.setImageResource(isFavorite ? R.drawable.ic_favorites : R.drawable.ic_favorite_border);
        fabFavorite.setContentDescription(getString(isFavorite ? R.string.remove_from_favorites : R.string.add_to_favorites));
    }
}