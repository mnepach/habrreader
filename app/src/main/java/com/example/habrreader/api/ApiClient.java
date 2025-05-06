package com.example.habrreader.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // В реальном приложении URL будет основан на реальном API Habr
    // Для демонстрации используем заглушку
    private static final String BASE_URL = "https://api.habr.com/v1/";
    private static Retrofit retrofit = null;
    private static HabrApiService apiService = null;

    public static HabrApiService getApiService() {
        if (apiService == null) {
            apiService = getRetrofitInstance().create(HabrApiService.class);
        }
        return apiService;
    }

    private static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            // Настраиваем логирование для отладки
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Настраиваем OkHttpClient
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);

            // Создаем Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }
}