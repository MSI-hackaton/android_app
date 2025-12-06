package com.msi.android.data.api;

import androidx.annotation.NonNull;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import okhttp3.Authenticator;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class NetworkService {
    private static final String BASE_URL = "http://10.0.2.2:8095/";
    private final TokenManager tokenManager;
    @Provides
    @Singleton
    public Retrofit provideRetrofit() {

        // Логирование
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        Interceptor authInterceptor = getAuthInterceptor();
        Authenticator tokenAuthenticator = getTokenAuthenticator();

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

    /**
     * Возвращает аутентификатор для обновления токена.
     * @return Authenticator
     */
    @NonNull
    private Authenticator getTokenAuthenticator() {
        Authenticator tokenAuthenticator = (Route route, Response response) -> {
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
        return tokenAuthenticator;
    }

    /**
     * Возвращает модификатор запроса, добавляющий токен в запрос.
     * @return Interceptor
     */
    @NonNull
    private Interceptor getAuthInterceptor() {
        Interceptor authInterceptor = chain -> {
            Request original = chain.request();
            Request.Builder builder = original.newBuilder();

            String token = tokenManager.getAccessToken();
            if (token != null) {
                builder.header("Authorization", "Bearer " + token);
            }

            return chain.proceed(builder.build());
        };
        return authInterceptor;
    }

    @Provides
    @Singleton
    public ApiService provideApiClient(Retrofit retrofit) {
        return retrofit.create(ApiService.class);
    }

}
