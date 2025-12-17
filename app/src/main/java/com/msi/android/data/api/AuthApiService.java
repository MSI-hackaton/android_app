package com.msi.android.data.api;

import com.msi.android.data.dto.CodeRequestDto;
import com.msi.android.data.dto.LoginRequestDto;
import com.msi.android.data.dto.TokenResponseDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {

    @POST("api/auth/code")
    Call<Void> code(@Body CodeRequestDto request);

    @POST("api/auth/sign-in")
    Call<TokenResponseDto> login(@Body LoginRequestDto request);
    @POST("api/auth/refresh")
    Call<TokenResponseDto> refreshToken(@Body String refreshToken);
}
