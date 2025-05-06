package com.example.habrreader.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity(tableName = "favorite_articles")
public class Article implements Serializable {

    @PrimaryKey
    @NonNull
    @SerializedName("id")
    private String id;

    private String title;
    private String description;
    private String content;
    private String author;
    private String publishedAt;

    @SerializedName("url")
    private String imageUrl;

    private boolean isFavorite;

    public Article() {
        this.id = "";
        this.title = "Cat Image";
        this.description = "A cute cat image";
        this.content = "No additional content";
        this.author = "Unknown";
        this.publishedAt = "N/A";
        this.imageUrl = "";
    }

    @Ignore
    public Article(String id, String title, String description, String content, String author,
                   String publishedAt, String imageUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.content = content;
        this.author = author;
        this.publishedAt = publishedAt;
        this.imageUrl = imageUrl;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}