package com.msi.android.di;

import android.content.Context;
import android.content.SharedPreferences;

import com.msi.android.data.api.AuthApiService;
import com.msi.android.data.api.TokenManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class TokenModule {

    private static final String PREFS_NAME = "auth_prefs";

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    TokenManager provideTokenManager(SharedPreferences prefs, AuthApiService authApiService) {
        return new TokenManager(prefs, authApiService);
    }
}