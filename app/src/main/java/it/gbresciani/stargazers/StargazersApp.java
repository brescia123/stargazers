package it.gbresciani.stargazers;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import it.gbresciani.stargazers.network.StargazersService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StargazersApp extends Application {

    private static final String GITHUB_HOST = "https://api.github.com";
    private static StargazersService stargazersService;

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }

    public static StargazersService getStargazersService() {
        if (stargazersService == null) {
            // Add a call logger
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(GITHUB_HOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(httpClient.build())
                    .build();

            stargazersService = retrofit.create(StargazersService.class);
        }
        return stargazersService;
    }
}
