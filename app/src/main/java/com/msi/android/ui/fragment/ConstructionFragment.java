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

import com.msi.android.App;
import com.msi.android.R;
import com.msi.android.data.entity.ProjectEntity;
import com.msi.android.ui.view.ProjectDetailsViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

public class ConstructionFragment extends Fragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private TextView tvProjectName;
    private TextView tvProjectArea;

    private String constructionStageId;

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
        return inflater.inflate(R.layout.fragment_construction_stage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args == null) {
            Log.e("ConstructionFragment", "Arguments are null");
            return;
        }

        String projectId = args.getString("projectId");
        constructionStageId = args.getString("constructionStageId");
        String startDate = args.getString("startDate");

        tvProjectName = view.findViewById(R.id.tv_info_project_name);
        tvProjectArea = view.findViewById(R.id.tv_info_project_area);
        TextView tvStartDate = view.findViewById(R.id.tv_info_start_date);

        tvStartDate.setText(formatDate(startDate));

        ProjectDetailsViewModel viewModel = new ViewModelProvider(this, viewModelFactory)
                .get(ProjectDetailsViewModel.class);

        viewModel.getProject().observe(getViewLifecycleOwner(), result -> {
            switch (result.getStatus()) {
                case LOADING:
                    Log.d("ConstructionFragment", "Loading project...");
                    break;
                case SUCCESS:
                    bindProjectData(result.getData());
                    break;
                case ERROR:
                    Log.e("ConstructionFragment", "Error: " + result.getMessage());
                    Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        if (projectId != null) {
            viewModel.loadProject(projectId);
        }
    }

    private void bindProjectData(ProjectEntity project) {
        if (project == null) return;

        tvProjectName.setText(project.getTitle());
        tvProjectArea.setText(getString(R.string.project_area_format, project.getArea()));
    }

    private String formatDate(String date) {
        if (date == null) return "";

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        inputFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.forLanguageTag("ru"));

        try {
            Date parsedDate = inputFormat.parse(date);
            if (parsedDate != null) {
                return outputFormat.format(parsedDate);
            }
        } catch (Exception e) {
            Log.e("ConstructionFragment", "Error parsing date", e);
            return date;
        }

        return date;
    }
}
