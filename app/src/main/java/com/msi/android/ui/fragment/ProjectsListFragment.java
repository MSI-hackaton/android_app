package com.msi.android.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.msi.android.App;
import com.msi.android.R;
import com.msi.android.data.entity.ProjectEntity;
import com.msi.android.ui.adapter.ProjectAdapter;
import com.msi.android.ui.view.MainViewModel;

import java.util.List;

import javax.inject.Inject;

public class ProjectsListFragment extends Fragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private MainViewModel viewModel;
    private ProjectAdapter adapter;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((App) requireActivity().getApplication())
                .getAppComponent()
                .inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_projects_list, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        initUi(view);
        initViewModel();
        observeProjects();

        viewModel.loadProjects();
    }

    private void initUi(View view) {
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);

        adapter = new ProjectAdapter();
        adapter.setOnProjectClickListener(project -> {
            Bundle args = new Bundle();
            args.putString("projectId", project.getId());

            NavHostFragment
                    .findNavController(this)
                    .navigate(R.id.projectDetailsFragment, args);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this, viewModelFactory)
                .get(MainViewModel.class);
    }

    private void observeProjects() {
        viewModel.getProjects().observe(getViewLifecycleOwner(), result -> {
            switch (result.getStatus()) {
                case LOADING:
                    progressBar.setVisibility(View.VISIBLE);
                    break;

                case SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    updateList(result.getData());
                    break;

                case ERROR:
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void updateList(List<ProjectEntity> projects) {
        adapter.submitList(projects);
    }
}
