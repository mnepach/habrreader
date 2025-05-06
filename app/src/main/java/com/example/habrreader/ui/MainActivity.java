package com.example.habrreader.ui;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.habrreader.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Настройка нижней навигации
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Получение NavController через NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment == null) {
            Log.e("MainActivity", "NavHostFragment not found for ID R.id.nav_host_fragment");
            throw new IllegalStateException("NavHostFragment not found");
        }
        navController = navHostFragment.getNavController();
        Log.d("MainActivity", "NavController initialized successfully");

        // Определяем верхние уровни навигации (без стрелки "назад")
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.articleListFragment, R.id.favoritesFragment
        ).build();

        // Настройка ActionBar и BottomNavigationView с NavController
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}