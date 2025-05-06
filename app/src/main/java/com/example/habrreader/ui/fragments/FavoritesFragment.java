package com.example.habrreader.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habrreader.R;
import com.example.habrreader.ui.adapters.ArticleAdapter;
import com.example.habrreader.viewmodel.FavoritesViewModel;

public class FavoritesFragment extends Fragment {

    private FavoritesViewModel viewModel;
    private RecyclerView recyclerView;
    private ArticleAdapter adapter;
    private ProgressBar progressBar;
    private TextView textError;
    private TextView textEmpty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Инициализация UI компонентов
        recyclerView = view.findViewById(R.id.recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);
        textError = view.findViewById(R.id.text_error);
        textEmpty = view.findViewById(R.id.text_empty);

        // Настройка RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ArticleAdapter(article -> {
            // Обработка нажатия на статью - переход к деталям
            FavoritesFragmentDirections.ActionFavoritesFragmentToArticleDetailsFragment action =
                    FavoritesFragmentDirections.actionFavoritesFragmentToArticleDetailsFragment(article.getId());
            Navigation.findNavController(view).navigate(action);
        }, article -> {
            // Обработка нажатия на кнопку избранного
            viewModel.toggleFavorite(article);
        });
        recyclerView.setAdapter(adapter);

        // Подключение ViewModel
        viewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);

        // Наблюдение за изменениями данных
        viewModel.getFavorites().observe(getViewLifecycleOwner(), articles -> {
            adapter.submitList(articles);

            // Показываем сообщение, если список избранных пуст
            boolean isEmpty = articles == null || articles.isEmpty();
            textEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            textError.setText(error);
            textError.setVisibility(error != null ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Обновляем список избранных при каждом возвращении к фрагменту
        viewModel.loadFavorites();
    }
}