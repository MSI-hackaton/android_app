package com.msi.android.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.msi.android.R;
import com.msi.android.data.entity.DocumentEntity;

import java.util.ArrayList;
import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocViewHolder> {

    public interface OnDocumentClickListener {
        void onClick(DocumentEntity doc);
    }

    private List<DocumentEntity> items = new ArrayList<>();
    private OnDocumentClickListener listener;

    public void setOnClickListener(OnDocumentClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<DocumentEntity> list) {
        this.items = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DocViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_document, parent, false);
        return new DocViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DocViewHolder h, int pos) {
        DocumentEntity d = items.get(pos);

        h.title.setText(d.getTitle());
        h.date.setText("от " + d.getDate());
        h.status.setText(d.getStatus());

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(d);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class DocViewHolder extends RecyclerView.ViewHolder {
        TextView title, date, status;
        public DocViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            date = itemView.findViewById(R.id.date);
            status = itemView.findViewById(R.id.status);
        }
    }
}
