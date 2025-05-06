package com.example.habrreader.db;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.content.Context;

import com.example.habrreader.data.model.Article;

@Database(entities = {Article.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ArticleDao articleDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "habr_reader_db")
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE favorite_articles_temp (" +
                    "id TEXT PRIMARY KEY NOT NULL, " +
                    "title TEXT, " +
                    "description TEXT, " +
                    "content TEXT, " +
                    "author TEXT, " +
                    "publishedAt TEXT, " +
                    "imageUrl TEXT, " +
                    "url TEXT, " +
                    "isFavorite INTEGER NOT NULL)");
            database.execSQL("INSERT INTO favorite_articles_temp (id, title, description, content, author, publishedAt, imageUrl, url, isFavorite) " +
                    "SELECT CAST(id AS TEXT), title, description, content, author, publishedAt, imageUrl, url, isFavorite " +
                    "FROM favorite_articles");
            database.execSQL("DROP TABLE favorite_articles");
            database.execSQL("ALTER TABLE favorite_articles_temp RENAME TO favorite_articles");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE favorite_articles_temp (" +
                    "id TEXT PRIMARY KEY NOT NULL, " +
                    "title TEXT, " +
                    "description TEXT, " +
                    "content TEXT, " +
                    "author TEXT, " +
                    "publishedAt TEXT, " +
                    "imageUrl TEXT, " +
                    "isFavorite INTEGER NOT NULL)");
            database.execSQL("INSERT INTO favorite_articles_temp (id, title, description, content, author, publishedAt, imageUrl, isFavorite) " +
                    "SELECT id, title, description, content, author, publishedAt, imageUrl, isFavorite " +
                    "FROM favorite_articles");
            database.execSQL("DROP TABLE favorite_articles");
            database.execSQL("ALTER TABLE favorite_articles_temp RENAME TO favorite_articles");
        }
    };
}