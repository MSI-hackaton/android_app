package com.msi.android.ui.view;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.msi.android.data.dto.ProjectRequestBody;
import com.msi.android.data.dto.ResultDto;
import com.msi.android.data.repository.ProjectRepository;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class OrderProjectViewModel extends ViewModel {

    private final ProjectRepository repository;


    public LiveData<ResultDto<Void>> sendOrder(String projectId, ProjectRequestBody body) {
        return repository.sendProjectRequest(projectId, body);
    }
}

