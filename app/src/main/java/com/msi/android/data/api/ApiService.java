package com.msi.android.data.api;

import com.msi.android.data.dto.LoginRequestDto;
import com.msi.android.data.dto.ProjectDto;
import com.msi.android.data.dto.TokenResponseDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Сервисный слой, предоставляющий операции
 * для доступа к API приложения.
 */
public interface ApiService {

    @POST("auth/refresh")
    Call<TokenResponseDto> refreshToken(@Body String refreshToken);

    @POST("auth/login")
    Call<TokenResponseDto> login(@Body LoginRequestDto request);
    @GET("projects")
    Call<List<ProjectDto>> getProjects();
}
