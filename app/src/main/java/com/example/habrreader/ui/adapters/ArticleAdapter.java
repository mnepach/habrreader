package com.example.habrreader.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.habrreader.R;
import com.example.habrreader.data.model.Article;

public class ArticleAdapter extends ListAdapter<Article, ArticleAdapter.ArticleViewHolder> {

    private final OnArticleClickListener onArticleClickListener;
    private final OnFavoriteClickListener onFavoriteClickListener;

    public interface OnArticleClickListener {
        void onArticleClick(Article article);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Article article);
    }

    public ArticleAdapter(OnArticleClickListener onArticleClickListener,
                          OnFavoriteClickListener onFavoriteClickListener) {
        super(new ArticleDiffCallback());
        this.onArticleClickListener = onArticleClickListener;
        this.onFavoriteClickListener = onFavoriteClickListener;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_article, parent, false);
        return new ArticleViewHolder(view, onArticleClickListener, onFavoriteClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class ArticleViewHolder extends RecyclerView.ViewHolder {
        private final TextView textTitle;
        private final TextView textDescription;
        private final TextView textAuthor;
        private final ImageView imageArticle;
        private final ImageButton buttonFavorite;
        private Article article;

        ArticleViewHolder(@NonNull View itemView,
                          OnArticleClickListener onArticleClickListener,
                          OnFavoriteClickListener onFavoriteClickListener) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.text_title);
            textDescription = itemView.findViewById(R.id.text_description);
            textAuthor = itemView.findViewById(R.id.text_author);
            imageArticle = itemView.findViewById(R.id.image_article);
            buttonFavorite = itemView.findViewById(R.id.button_favorite);

            itemView.setOnClickListener(v -> {
                if (article != null && onArticleClickListener != null) {
                    onArticleClickListener.onArticleClick(article);
                }
            });

            buttonFavorite.setOnClickListener(v -> {
                if (article != null && onFavoriteClickListener != null) {
                    onFavoriteClickListener.onFavoriteClick(article);
                }
            });
        }

        void bind(Article article) {
            this.article = article;
            textTitle.setText(article.getTitle() != null ? article.getTitle() : "");
            textDescription.setText(article.getDescription() != null ? article.getDescription() : "");
            textAuthor.setText(article.getAuthor() != null ? article.getAuthor() : "");

            if (article.getImageUrl() != null && !article.getImageUrl().isEmpty()) {
                Glide.with(itemView)
                        .load(article.getImageUrl())
                        .centerCrop()
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .into(imageArticle);
            } else {
                imageArticle.setImageResource(R.drawable.placeholder_image);
            }

            updateFavoriteIcon(article.isFavorite());
        }

        private void updateFavoriteIcon(boolean isFavorite) {
            buttonFavorite.setImageResource(isFavorite ? R.drawable.ic_favorites : R.drawable.ic_favorite_border);
            buttonFavorite.setContentDescription(
                    itemView.getContext().getString(isFavorite ? R.string.remove_from_favorites : R.string.add_to_favorites));
        }
    }

    static class ArticleDiffCallback extends DiffUtil.ItemCallback<Article> {
        @Override
        public boolean areItemsTheSame(@NonNull Article oldItem, @NonNull Article newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Article oldItem, @NonNull Article newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getDescription().equals(newItem.getDescription()) &&
                    oldItem.isFavorite() == newItem.isFavorite();
        }
    }
}