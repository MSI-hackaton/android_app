package com.msi.android.ui.view;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.msi.android.data.dto.ResultDto;
import com.msi.android.data.entity.ProjectEntity;
import com.msi.android.data.repository.ProjectRepository;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ProjectDetailsViewModel extends ViewModel {

    private final ProjectRepository repository;
    private final MutableLiveData<ResultDto<ProjectEntity>> project = new MutableLiveData<>();

    public void loadProject(String id) {
        repository.getProjectById(id)
                .observeForever(project::setValue);
    }

    public LiveData<ResultDto<ProjectEntity>> getProject() {
        return project;
    }
}

