package com.msi.android.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.msi.android.App;
import com.msi.android.R;
import com.msi.android.data.api.ApiService;
import com.msi.android.data.api.TokenManager;
import com.msi.android.data.dto.ConstructionStageResponseDto;
import com.msi.android.ui.view.ProjectDetailsViewModel;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignActFragment extends Fragment {

    @Inject
    ApiService apiService;

    @Inject
    TokenManager tokenManager;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App) requireActivity().getApplication())
                .getAppComponent()
                .inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_act, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Toolbar
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigateUp();
        });

        // Загрузить данные документа
        loadDocumentData(view);

        // Чекбокс и кнопка
        MaterialCheckBox checkboxConfirmation = view.findViewById(R.id.checkbox_confirmation);
        com.google.android.material.button.MaterialButton btnSignAct = view.findViewById(R.id.btn_sign_act);

        // Изначально кнопка неактивна
        btnSignAct.setEnabled(false);

        // Активировать кнопку при отметке чекбокса
        checkboxConfirmation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            btnSignAct.setEnabled(isChecked);
        });

        // Кнопка подписания
        btnSignAct.setOnClickListener(v -> {
            signDocument();
        });
    }

    private void loadDocumentData(View view) {
        String userId = tokenManager.getUserId();
        if (userId == null) {
            return;
        }

        TextView tvDocumentObject = view.findViewById(R.id.tv_document_object);

        apiService.getConstructionStagesByCustomer(userId)
                .enqueue(new Callback<List<ConstructionStageResponseDto>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<ConstructionStageResponseDto>> call,
                                           @NonNull Response<List<ConstructionStageResponseDto>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            ConstructionStageResponseDto stage = response.body().get(0);
                            loadProjectInfo(view, stage);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<ConstructionStageResponseDto>> call, @NonNull Throwable t) {
                        Log.e("SignActFragment", "Error loading document data", t);
                    }
                });
    }

    private void loadProjectInfo(View view, ConstructionStageResponseDto stage) {
        if (stage.getProjectId() == null) return;

        ProjectDetailsViewModel viewModel = new ViewModelProvider(this, viewModelFactory)
                .get(ProjectDetailsViewModel.class);

        viewModel.getProject().observe(getViewLifecycleOwner(), result -> {
            switch (result.getStatus()) {
                case SUCCESS:
                    if (result.getData() != null) {
                        TextView tvDocumentObject = view.findViewById(R.id.tv_document_object);
                        if (tvDocumentObject != null) {
                            tvDocumentObject.setText("Проект \"" + result.getData().getTitle() + "\"");
                        }
                    }
                    break;
                case ERROR:
                    Log.e("SignActFragment", "Error loading project: " + result.getMessage());
                    break;
                case LOADING:
                    break;
            }
        });

        viewModel.loadProject(stage.getProjectId());
    }

    private void signDocument() {
        Toast.makeText(getContext(), "Акт успешно подписан", Toast.LENGTH_SHORT).show();
        NavHostFragment.findNavController(this).navigateUp();
    }
}
