package com.msi.android.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.msi.android.data.api.ApiService;
import com.msi.android.data.dto.ProjectDto;
import com.msi.android.data.dto.ResultDto;
import com.msi.android.data.entity.ProjectEntity;
import com.msi.android.data.mapper.ProjectMapper;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.RequiredArgsConstructor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ProjectRepository {
    private final ApiService apiService;
    private Call<List<ProjectDto>> currentListCall;

    public LiveData<ResultDto<List<ProjectEntity>>> getProjects() {
        MutableLiveData<ResultDto<List<ProjectEntity>>> liveData = new MutableLiveData<>();
        liveData.setValue(ResultDto.loading());

        if (currentListCall != null && !currentListCall.isCanceled()) {
            currentListCall.cancel();
        }

        currentListCall = apiService.getProjects();

        currentListCall.enqueue(new Callback<List<ProjectDto>>() {
            @Override
            public void onResponse(Call<List<ProjectDto>> call, Response<List<ProjectDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ProjectEntity> mapped = response.body().stream()
                            .map(ProjectMapper.INSTANCE::dtoToEntity)
                            .collect(Collectors.toList());

                    liveData.setValue(ResultDto.success(mapped));
                } else {
                    String errorMsg = "Ошибка сервера: " + response.code();
                    liveData.setValue(ResultDto.error(errorMsg));
                }
            }

            @Override
            public void onFailure(Call<List<ProjectDto>> call, Throwable t) {
                if (call.isCanceled()) return;

                liveData.setValue(ResultDto.error("Проблема с сетью: " + t.getMessage()));
            }
        });

        return liveData;
    }

    public void cancelListCall() {
        if (currentListCall != null && !currentListCall.isCanceled()) {
            currentListCall.cancel();
        }
    }

    public LiveData<ResultDto<ProjectEntity>> getProjectById(String id) {
        MutableLiveData<ResultDto<ProjectEntity>> liveData = new MutableLiveData<>();
        liveData.setValue(ResultDto.loading());

        Call<ProjectDto> call = apiService.getProjectById(id);
        call.enqueue(new Callback<ProjectDto>() {
            @Override
            public void onResponse(Call<ProjectDto> call, Response<ProjectDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProjectEntity entity =
                            ProjectMapper.INSTANCE.dtoToEntity(response.body());
                    liveData.setValue(ResultDto.success(entity));
                } else {
                    liveData.setValue(ResultDto.error("Ошибка сервера: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ProjectDto> call, Throwable t) {
                liveData.setValue(ResultDto.error("Проблема с сетью: " + t.getMessage()));
            }
        });

        return liveData;
    }

}
