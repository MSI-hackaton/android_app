package com.msi.android.data.api;

import com.msi.android.data.dto.LoginRequestDto;
import com.msi.android.data.dto.TokenResponseDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {

    @POST("auth/login")
    Call<TokenResponseDto> login(@Body LoginRequestDto request);
    @POST("auth/refresh")
    Call<TokenResponseDto> refreshToken(@Body String refreshToken);
}
