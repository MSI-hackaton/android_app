package com.msi.android.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.msi.android.R;
import com.msi.android.data.entity.ProjectEntity;

public class ProjectAdapter extends ListAdapter<ProjectEntity, ProjectAdapter.ProjectViewHolder> {

    private OnProjectClickListener clickListener;
    public ProjectAdapter() {
        super(DIFF_CALLBACK);
    }

    public interface OnProjectClickListener {
        void onProjectClick(ProjectEntity project);
    }

    private static final DiffUtil.ItemCallback<ProjectEntity> DIFF_CALLBACK = new DiffUtil.ItemCallback<ProjectEntity>() {
        @Override
        public boolean areItemsTheSame(@NonNull ProjectEntity oldItem, @NonNull ProjectEntity newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ProjectEntity oldItem, @NonNull ProjectEntity newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProjectViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project, parent, false)
        );
    }

    public void setOnProjectClickListener(OnProjectClickListener listener) {
        this.clickListener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        holder.bind(getItem(position), clickListener);
    }
    static class ProjectViewHolder extends RecyclerView.ViewHolder {

        TextView title, description, price;
        ImageView image;

        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.projectTitle);
            description = itemView.findViewById(R.id.projectDescription);
            price = itemView.findViewById(R.id.projectPrice);
            image = itemView.findViewById(R.id.projectImage);
        }

        public void bind(ProjectEntity project, OnProjectClickListener listener) {
            title.setText(project.getTitle());
            description.setText(project.getDescription());
            price.setText(String.valueOf(project.getPrice()));
            image.setImageResource(android.R.drawable.ic_menu_gallery);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProjectClick(project);
                }
            });
        }
    }


}
