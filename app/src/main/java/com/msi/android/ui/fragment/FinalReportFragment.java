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
import com.msi.android.App;
import com.msi.android.R;
import com.msi.android.data.api.ApiService;
import com.msi.android.data.api.TokenManager;
import com.msi.android.data.dto.ConstructionStageResponseDto;
import com.msi.android.ui.helper.ProjectInfoHelper;
import com.msi.android.ui.view.ProjectDetailsViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FinalReportFragment extends Fragment {

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
        return inflater.inflate(R.layout.fragment_final_report, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Toolbar
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigateUp();
        });

        // Загрузить данные проекта
        loadProjectData(view);

        // Обработчики документов
        view.findViewById(R.id.doc_acceptance_act).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Скачать акт приема-передачи", Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.doc_closing_documents).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Скачать закрывающие документы", Toast.LENGTH_SHORT).show();
        });

        // Кнопка подписания
        view.findViewById(R.id.btn_sign_documents).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Переход к подписанию документов", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadProjectData(View view) {
        String userId = tokenManager.getUserId();
        if (userId == null) {
            return;
        }

        TextView tvProjectName = view.findViewById(R.id.tv_project_name);
        TextView tvProjectArea = view.findViewById(R.id.tv_project_area);
        TextView tvStartDate = view.findViewById(R.id.tv_start_date);
        TextView tvEndDate = view.findViewById(R.id.tv_end_date);
        TextView tvConstructionPeriod = view.findViewById(R.id.tv_construction_period);

        apiService.getConstructionStagesByCustomer(userId)
                .enqueue(new Callback<List<ConstructionStageResponseDto>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<ConstructionStageResponseDto>> call,
                                           @NonNull Response<List<ConstructionStageResponseDto>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            ConstructionStageResponseDto stage = response.body().get(0);

                            // Загрузить информацию о проекте через Helper
                            loadProjectInfoViaHelper(view, stage);

                            // Заполнить специфичные для этого экрана поля
                            tvStartDate.setText(ProjectInfoHelper.formatDate(stage.getStartDate()));
                            tvEndDate.setText(ProjectInfoHelper.formatDate(stage.getEndDate()));

                            long days = calculateDaysBetween(stage.getStartDate(), stage.getEndDate());
                            tvConstructionPeriod.setText(days + " дней (по плану)");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<ConstructionStageResponseDto>> call, @NonNull Throwable t) {
                        Log.e("FinalReportFragment", "Error loading project data", t);
                    }
                });
    }

    private void loadProjectInfoViaHelper(View view, ConstructionStageResponseDto stage) {
        if (stage.getProjectId() == null) return;

        ProjectDetailsViewModel viewModel = new ViewModelProvider(this, viewModelFactory)
                .get(ProjectDetailsViewModel.class);

        viewModel.getProject().observe(getViewLifecycleOwner(), result -> {
            switch (result.getStatus()) {
                case SUCCESS:
                    if (result.getData() != null) {
                        TextView tvProjectName = view.findViewById(R.id.tv_project_name);
                        TextView tvProjectArea = view.findViewById(R.id.tv_project_area);

                        if (tvProjectName != null) {
                            tvProjectName.setText(result.getData().getTitle());
                        }

                        if (tvProjectArea != null) {
                            tvProjectArea.setText(result.getData().getArea() + " м²");
                        }
                    }
                    break;
                case ERROR:
                    Log.e("FinalReportFragment", "Error loading project: " + result.getMessage());
                    break;
                case LOADING:
                    break;
            }
        });

        viewModel.loadProject(stage.getProjectId());
    }


    private long calculateDaysBetween(String startDate, String endDate) {
        if (startDate == null || endDate == null) return 0;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        format.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));

        try {
            Date start = format.parse(startDate);
            Date end = format.parse(endDate);
            if (start != null && end != null) {
                long diff = end.getTime() - start.getTime();
                return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            }
        } catch (ParseException e) {
            Log.e("FinalReportFragment", "Error calculating days", e);
        }

        return 0;
    }
}
