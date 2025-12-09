package com.msi.android.di;

import com.msi.android.data.api.ApiService;
import com.msi.android.data.api.AuthApiService;
import com.msi.android.data.api.TokenManager;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

import okhttp3.Authenticator;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetworkModule {

    private static final String BASE_URL = "http://10.0.2.2:8080/";

    @Provides
    @Singleton
    public Retrofit provideRetrofit(TokenManager tokenManager) {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        Interceptor authInterceptor = chain -> {
            Request original = chain.request();
            Request.Builder builder = original.newBuilder();
            String token = tokenManager.getAccessToken();
            if (token != null) {
                builder.header("Authorization", "Bearer " + token);
            }
            return chain.proceed(builder.build());
        };

        Authenticator tokenAuthenticator = (route, response) -> {
            synchronized (this) {
                String newToken = tokenManager.refreshToken();
                if (newToken != null) {
                    tokenManager.saveAccessToken(newToken);
                    return response.request().newBuilder()
                            .header("Authorization", "Bearer " + newToken)
                            .build();
                }
                return null;
            }
        };

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(authInterceptor)
                .authenticator(tokenAuthenticator)
                .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    @Provides
    @Singleton
    public ApiService provideApiService(Retrofit retrofit) {
        return retrofit.create(ApiService.class);
    }

    @Provides
    @Singleton
    public AuthApiService provideAuthApiService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(AuthApiService.class);
    }
}