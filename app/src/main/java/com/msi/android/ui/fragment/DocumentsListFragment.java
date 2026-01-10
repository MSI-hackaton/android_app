package com.msi.android.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.msi.android.R;
import com.msi.android.data.entity.DocumentEntity;
import com.msi.android.ui.adapter.DocumentAdapter;

import java.util.Arrays;
import java.util.List;

public class DocumentsListFragment extends Fragment {

    private RecyclerView recyclerView;
    private DocumentAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_documents_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.documentsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new DocumentAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnClickListener(doc -> {

        });

        loadMock();
    }

    /** Заглушка: тестовые данные */
    private void loadMock() {
        List<DocumentEntity> list = Arrays.asList(
                new DocumentEntity("1","Договор подряда №123","01.12.2024","Ожидает подписания"),
                new DocumentEntity("2","Смета","01.12.2024","Ожидает подписания"),
                new DocumentEntity("3","График работ","01.12.2024","Ожидает подписания")
        );

        adapter.submitList(list);
    }
}
