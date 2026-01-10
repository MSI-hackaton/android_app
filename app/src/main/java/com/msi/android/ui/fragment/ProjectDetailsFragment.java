package com.msi.android.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.button.MaterialButton;
import com.msi.android.App;
import com.msi.android.R;
import com.msi.android.data.entity.ProjectEntity;
import com.msi.android.ui.view.ProjectDetailsViewModel;

import javax.inject.Inject;

public class ProjectDetailsFragment extends Fragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private ProjectDetailsViewModel viewModel;
    private ProgressBar progressBar;
    private TextView title, description, price, area, floors, constructionTime;
    private ImageView projectImage;

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
        return inflater.inflate(R.layout.fragment_project_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args == null || !args.containsKey("projectId")) {
            Toast.makeText(getContext(), "projectId is null", Toast.LENGTH_SHORT).show();
            return;
        }

        String projectId = args.getString("projectId");

        progressBar = view.findViewById(R.id.progressBar);
        title = view.findViewById(R.id.title);
        description = view.findViewById(R.id.description);
        price = view.findViewById(R.id.price);
        area = view.findViewById(R.id.area);
        floors = view.findViewById(R.id.floors);
        constructionTime = view.findViewById(R.id.constructionTime);
        projectImage = view.findViewById(R.id.projectImage);

        viewModel = new ViewModelProvider(this, viewModelFactory)
                .get(ProjectDetailsViewModel.class);

        viewModel.getProject().observe(getViewLifecycleOwner(), result -> {
            switch (result.getStatus()) {
                case LOADING:
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    bindData(result.getData(), view);
                    break;
                case ERROR:
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        viewModel.loadProject(projectId);

    }

    private void bindData(ProjectEntity project, View view) {
        if (project == null) return;

        title.setText(project.getTitle());
        description.setText(project.getDescription());
        price.setText("Цена: " + project.getPrice());
        area.setText("Площадь: " + project.getArea() + " м²");
        floors.setText("Этажей: " + project.getFloors());
        constructionTime.setText("Срок: " + project.getConstructionTime() + " мес");

        // Если есть фото (URL), загружаем вручную

        MaterialButton orderButton = view.findViewById(R.id.orderButton);

        orderButton.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("projectId", project.getId());

            NavHostFragment.findNavController(ProjectDetailsFragment.this)
                    .navigate(R.id.orderProjectFragment, args);
        });
    }


}
