package com.msi.android.ui.fragment;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.msi.android.App;
import com.msi.android.R;
import com.msi.android.data.api.ApiService;
import com.msi.android.data.api.TokenManager;
import com.msi.android.data.dto.ChatMessageCreateDto;
import com.msi.android.data.dto.ChatMessageResponseDto;
import com.msi.android.data.entity.ChatMessageEntity;
import com.msi.android.ui.adapter.ChatAdapter;
import com.msi.android.websocket.WebSocketChatClient;
import java.util.List;
import javax.inject.Inject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {

    private String currentUserId;

    @Inject
    WebSocketChatClient webSocketClient;

    @Inject
    ApiService apiService;

    @Inject
    TokenManager tokenManager;

    private RecyclerView rvMessages;
    private EditText etMessage;
    private ImageButton btnSend;
    private ChatAdapter adapter;
    private String constructionId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App) requireActivity().getApplication())
                .getAppComponent()
                .inject(this);

        currentUserId = tokenManager.getUserId();

        Bundle args = getArguments();
        if (args != null) {
            constructionId = args.getString("constructionId");
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvMessages = view.findViewById(R.id.rvMessages);
        etMessage = view.findViewById(R.id.etMessage);
        btnSend = view.findViewById(R.id.btnSend);
        ImageButton btnBack = view.findViewById(R.id.btnBack);

        adapter = new ChatAdapter();
        rvMessages.setAdapter(adapter);
        rvMessages.setLayoutManager(new LinearLayoutManager(requireContext()));

        btnBack.setOnClickListener(v ->
                NavHostFragment.findNavController(this).popBackStack()
        );

        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean hasText = s.length() > 0;
                btnSend.setEnabled(hasText);
                btnSend.setBackgroundResource(hasText ?
                        R.drawable.bg_send_button : R.drawable.bg_send_button_disabled);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnSend.setOnClickListener(v -> sendMessage());

        loadChatHistory();
        connectWebSocket();
    }

    private void loadChatHistory() {
        apiService.getChatHistory(constructionId)
                .enqueue(new Callback<List<ChatMessageResponseDto>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<ChatMessageResponseDto>> call,
                                           @NonNull Response<List<ChatMessageResponseDto>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            for (ChatMessageResponseDto dto : response.body()) {
                                ChatMessageEntity msg = ChatMessageEntity.builder()
                                        .id(dto.getId())
                                        .senderId(dto.getSenderId())
                                        .senderName(dto.getSenderName())
                                        .message(dto.getMessage())
                                        .timestamp(System.currentTimeMillis())
                                        .isOwn(dto.getSenderId().equals(currentUserId))
                                        .build();
                                adapter.addMessage(msg);
                            }
                            rvMessages.scrollToPosition(adapter.getItemCount() - 1);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<ChatMessageResponseDto>> call, @NonNull Throwable t) {
                        Log.e("ChatFragment", "Error loading history", t);
                    }
                });
    }

    private void connectWebSocket() {
        webSocketClient.setMessageListener(new WebSocketChatClient.ChatMessageListener() {
            @Override
            public void onConnected() {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Подключено к чату", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onMessageReceived(ChatMessageResponseDto dto) {
                requireActivity().runOnUiThread(() -> {
                    ChatMessageEntity msg = ChatMessageEntity.builder()
                            .id(dto.getId())
                            .senderId(dto.getSenderId())
                            .senderName(dto.getSenderName())
                            .message(dto.getMessage())
                            .timestamp(System.currentTimeMillis())
                            .isOwn(dto.getSenderId().equals(currentUserId))
                            .build();
                    adapter.addMessage(msg);
                    rvMessages.smoothScrollToPosition(adapter.getItemCount() - 1);
                });
            }

            @Override
            public void onError(Throwable error) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Ошибка подключения", Toast.LENGTH_SHORT).show()
                );
            }
        });

        webSocketClient.connect(constructionId);
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        ChatMessageEntity message = ChatMessageEntity.builder()
                .message(text)
                .timestamp(System.currentTimeMillis())
                .isOwn(true)
                .build();

        adapter.addMessage(message);
        rvMessages.smoothScrollToPosition(adapter.getItemCount() - 1);

        // Отправка через REST API (сохранение в БД)
        ChatMessageCreateDto dto = new ChatMessageCreateDto(text);
        apiService.sendChatMessage(constructionId, dto).enqueue(new Callback<ChatMessageResponseDto>() {
            @Override
            public void onResponse(Call<ChatMessageResponseDto> call, Response<ChatMessageResponseDto> response) {
                // Успешно сохранено
            }

            @Override
            public void onFailure(Call<ChatMessageResponseDto> call, Throwable t) {
                Log.e(TAG, "Error sending", t);
            }
        });

        etMessage.setText("");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        webSocketClient.disconnect();
    }
}
