package com.msi.android.ui.view;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.msi.android.data.dto.ResultDto;
import com.msi.android.data.entity.ProjectEntity;
import com.msi.android.data.repository.ProjectRepository;

import java.util.List;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class MainViewModel extends ViewModel {

    private final ProjectRepository repository;
    private final MutableLiveData<ResultDto<List<ProjectEntity>>> projects = new MutableLiveData<>();

    public void loadProjects() {
        repository.getProjects().observeForever(projects::setValue);
    }

    public LiveData<ResultDto<List<ProjectEntity>>> getProjects() {

        return projects;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.cancelListCall();
    }
}
