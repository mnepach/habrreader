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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.habrreader.R;
import com.example.habrreader.data.model.Article;
import com.example.habrreader.ui.adapters.ArticleAdapter;
import com.example.habrreader.viewmodel.ArticleListViewModel;

public class ArticleListFragment extends Fragment {

    private ArticleListViewModel viewModel;
    private RecyclerView recyclerView;
    private ArticleAdapter adapter;
    private ProgressBar progressBar;
    private TextView textError;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_article_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);
        textError = view.findViewById(R.id.text_error);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ArticleAdapter(article -> {
            Bundle args = new Bundle();
            args.putString("articleId", article.getId());
            Navigation.findNavController(view).navigate(R.id.action_articleListFragment_to_articleDetailsFragment, args);
        }, article -> viewModel.toggleFavorite(article));
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(() -> viewModel.refreshArticles());

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        viewModel.loadNextPage();
                    }
                }
            }
        });

        viewModel = new ViewModelProvider(this).get(ArticleListViewModel.class);

        viewModel.getArticles().observe(getViewLifecycleOwner(), articles -> adapter.submitList(articles));
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            if (swipeRefreshLayout.isRefreshing() && !isLoading) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            textError.setText(error);
            textError.setVisibility(error != null ? View.VISIBLE : View.GONE);
        });
    }
}