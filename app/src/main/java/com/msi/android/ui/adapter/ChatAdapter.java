package com.msi.android.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.msi.android.R;
import com.msi.android.data.entity.ChatMessageEntity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_MESSAGE_SENT = 1;
    private static final int TYPE_MESSAGE_RECEIVED = 2;
    private static final int TYPE_DATE_DIVIDER = 3;

    private final List<Object> items = new ArrayList<>();
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        if (item instanceof String) {
            return TYPE_DATE_DIVIDER;
        }
        ChatMessageEntity message = (ChatMessageEntity) item;
        return message.isOwn() ? TYPE_MESSAGE_SENT : TYPE_MESSAGE_RECEIVED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_MESSAGE_SENT:
                return new SentMessageViewHolder(inflater.inflate(R.layout.item_message_sent, parent, false));
            case TYPE_MESSAGE_RECEIVED:
                return new ReceivedMessageViewHolder(inflater.inflate(R.layout.item_message_received, parent, false));
            case TYPE_DATE_DIVIDER:
                return new DateDividerViewHolder(inflater.inflate(R.layout.item_date_divider, parent, false));
            default:
                throw new IllegalArgumentException("Unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DateDividerViewHolder) {
            ((DateDividerViewHolder) holder).bind((String) items.get(position));
        } else if (holder instanceof SentMessageViewHolder) {
            ((SentMessageViewHolder) holder).bind((ChatMessageEntity) items.get(position));
        } else if (holder instanceof ReceivedMessageViewHolder) {
            ((ReceivedMessageViewHolder) holder).bind((ChatMessageEntity) items.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addMessage(ChatMessageEntity message) {
        String messageDate = dateFormat.format(new Date(message.getTimestamp()));

        boolean needDateDivider = true;
        for (int i = items.size() - 1; i >= 0; i--) {
            if (items.get(i) instanceof ChatMessageEntity) {
                ChatMessageEntity lastMsg = (ChatMessageEntity) items.get(i);
                String lastDate = dateFormat.format(new Date(lastMsg.getTimestamp()));
                needDateDivider = !lastDate.equals(messageDate);
                break;
            }
        }

        if (needDateDivider) {
            items.add(messageDate);
            notifyItemInserted(items.size() - 1);
        }

        items.add(message);
        notifyItemInserted(items.size() - 1);
    }

    class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime;

        SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
        }

        void bind(ChatMessageEntity message) {
            tvMessage.setText(message.getMessage());
            tvTime.setText(timeFormat.format(new Date(message.getTimestamp())));
        }
    }

    class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime;

        ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
        }

        void bind(ChatMessageEntity message) {
            tvMessage.setText(message.getMessage());
            tvTime.setText(timeFormat.format(new Date(message.getTimestamp())));
        }
    }

    static class DateDividerViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;

        DateDividerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
        }

        void bind(String date) {
            tvDate.setText(date);
        }
    }
}
