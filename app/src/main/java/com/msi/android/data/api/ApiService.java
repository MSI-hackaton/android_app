package com.msi.android.data.api;

import com.msi.android.data.dto.ChatMessageCreateDto;
import com.msi.android.data.dto.ChatMessageResponseDto;
import com.msi.android.data.dto.ConstructionStageResponseDto;
import com.msi.android.data.dto.ProjectDto;
import com.msi.android.data.dto.VideoStreamResponseDto;
import com.msi.android.data.dto.ProjectRequestBody;
import com.msi.android.data.dto.TokenResponseDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Сервисный слой, предоставляющий операции
 * для доступа к API приложения.
 */
public interface ApiService {

    @GET("/api/projects")
    Call<List<ProjectDto>> getProjects();

    @GET("/api/projects/{id}")
    Call<ProjectDto> getProjectById(@Path("id") String id);

    @GET("/api/construction-stages/customer/{customerId}")
    Call<List<ConstructionStageResponseDto>> getConstructionStagesByCustomer(@Path("customerId") String customerId);

    @GET("/api/video-streams/constructions/{constructionId}")
    Call<List<VideoStreamResponseDto>> getVideoStreams(@Path("constructionId") String constructionId);

    @POST("api/chat/constructions/{constructionId}/messages")
    Call<ChatMessageResponseDto> sendChatMessage(
            @Path("constructionId") String constructionId,
            @Body ChatMessageCreateDto message
    );

    @GET("api/chat/constructions/{constructionId}/messages")
    Call<List<ChatMessageResponseDto>> getChatHistory(
            @Path("constructionId") String constructionId
    @POST("/api/requests/projects/{id}")
    Call<Void> sendProjectRequest(
            @Path("id") String projectId,
            @Body ProjectRequestBody body
    );
}
