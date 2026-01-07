package com.msi.android.data.api;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.Nullable;

import com.msi.android.data.dto.TokenResponseDto;

import org.json.JSONObject;

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
    private static final String KEY_USER_ID = "user_id";

    private final SharedPreferences prefs;
    private final AuthApiService authApiService;

    /** Получение access токена */
    @Nullable
    public synchronized String getAccessToken() {
        return prefs.getString(KEY_ACCESS_TOKEN, null);
    }

    /** Сохранение access токена и userId */
    public synchronized void saveAccessToken(String token) {
        prefs.edit().putString(KEY_ACCESS_TOKEN, token).apply();

        // Cохраняем userId из токена
        String userId = extractUserIdFromToken(token);
        if (userId != null) {
            saveUserId(userId);
        }
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

    /** Получение userId */
    @Nullable
    public synchronized String getUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }

    /** Сохранение userId */
    public synchronized void saveUserId(String userId) {
        prefs.edit().putString(KEY_USER_ID, userId).apply();
        Log.d("TokenManager", "Saved userId: " + userId);
    }

    /** Извлечение userId из JWT токена */
    @Nullable
    private String extractUserIdFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) return null;

            String payload = new String(android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE));

            Log.d("TokenManager", "JWT Payload: " + payload);

            JSONObject json = new JSONObject(payload);

            if (json.has("uid")) {
                String userId = json.getString("uid");
                Log.d("TokenManager", "Extracted userId: " + userId);
                return userId;
            } else if (json.has("sub")) {
                return json.getString("sub");
            } else if (json.has("userId")) {
                return json.getString("userId");
            }

            Log.e("TokenManager", "No userId field found in token");
            return null;
        } catch (Exception e) {
            Log.e("TokenManager", "Error extracting userId from token", e);
            return null;
        }
    }

    /** Очистка всех токенов (например при logout) */
    public synchronized void clearTokens() {
        prefs.edit()
                .remove(KEY_ACCESS_TOKEN)
                .remove(KEY_REFRESH_TOKEN)
                .remove(KEY_USER_ID)
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

    public boolean isAuthorized() {
        return getAccessToken() != null;
    }
}
