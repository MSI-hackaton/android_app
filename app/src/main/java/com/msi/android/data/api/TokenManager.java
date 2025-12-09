package com.msi.android.data.api;

import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.msi.android.data.dto.TokenResponseDto;

import java.io.IOException;

import javax.inject.Singleton;

import javax.inject.Inject;
import lombok.RequiredArgsConstructor;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Менеджер токенов: хранение access и refresh токенов,
 * обновление access токена при истечении.
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class TokenManager {

    private static final String PREFS_NAME = "auth_prefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";

    private final SharedPreferences prefs;
    private final AuthApiService authApiService;

    /** Получение access токена */
    @Nullable
    public synchronized String getAccessToken() {
        return prefs.getString(KEY_ACCESS_TOKEN, null);
    }

    /** Сохранение access токена */
    public synchronized void saveAccessToken(String token) {
        prefs.edit().putString(KEY_ACCESS_TOKEN, token).apply();
    }

    /** Получение refresh токена */
    @Nullable
    public synchronized String getRefreshToken() {
        return prefs.getString(KEY_REFRESH_TOKEN, null);
    }

    /** Сохранение refresh токена */
    public synchronized void saveRefreshToken(String token) {
        prefs.edit().putString(KEY_REFRESH_TOKEN, token).apply();
    }

    /** Очистка всех токенов (например при logout) */
    public synchronized void clearTokens() {
        prefs.edit().remove(KEY_ACCESS_TOKEN)
                .remove(KEY_REFRESH_TOKEN)
                .apply();
    }

    /**
     * Синхронный запрос на обновление токена.
     * Используется Authenticator в OkHttp.
     * Возвращает новый access токен, либо null, если не удалось обновить.
     */
    @Nullable
    public synchronized String refreshToken() {
        String refresh = getRefreshToken();
        if (refresh == null) return null;

        try {
            Call<TokenResponseDto> call = authApiService.refreshToken(refresh);
            retrofit2.Response<TokenResponseDto> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {
                TokenResponseDto body = response.body();
                saveAccessToken(body.getAccessToken());
                saveRefreshToken(body.getRefreshToken());
                return body.getAccessToken();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}