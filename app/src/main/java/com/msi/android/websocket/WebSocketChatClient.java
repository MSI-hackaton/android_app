package com.msi.android.websocket;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.gson.Gson;
import com.msi.android.data.dto.ChatMessageCreateDto;
import com.msi.android.data.dto.ChatMessageResponseDto;
import lombok.Setter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WebSocketChatClient {
    private static final String TAG = "WebSocketChat";
    private static final String WS_URL = "ws://10.0.2.2:8080/ws-chat";

    private final OkHttpClient client;
    private final Gson gson;
    private WebSocket webSocket;
    @Setter
    private ChatMessageListener messageListener;
    private String constructionId;

    @Inject
    public WebSocketChatClient(OkHttpClient client, Gson gson) {
        this.client = client;
        this.gson = gson;
    }

    public void connect(String constructionId) {
        this.constructionId = constructionId;

        Request request = new Request.Builder()
                .url(WS_URL)
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                Log.d(TAG, "WebSocket connected");
                sendStompConnect();
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                Log.d(TAG, "Message received: " + text);
                handleStompMessage(text);
            }

            @Override
            public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                Log.d(TAG, "WebSocket closing: " + reason);
                webSocket.close(1000, null);
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
                Log.e(TAG, "WebSocket error", t);
                if (messageListener != null) {
                    messageListener.onError(t);
                }
            }
        });
    }

    private void sendStompConnect() {
        String connectFrame = "CONNECT\n" +
                "accept-version:1.1,1.0\n" +
                "heart-beat:10000,10000\n\n\u0000";
        webSocket.send(connectFrame);
    }

    private void sendStompSubscribe() {
        String subscribeFrame = "SUBSCRIBE\n" +
                "id:sub-0\n" +
                "destination:/topic/chat/" + constructionId + "\n\n\u0000";
        webSocket.send(subscribeFrame);

        if (messageListener != null) {
            messageListener.onConnected();
        }
    }

    private void handleStompMessage(String message) {
        if (message.startsWith("CONNECTED")) {
            Log.d(TAG, "STOMP connected");
            sendStompSubscribe();
        } else if (message.startsWith("MESSAGE")) {
            try {
                String[] lines = message.split("\n");
                String body = lines[lines.length - 1].replace("\u0000", "");

                ChatMessageResponseDto msg = gson.fromJson(body, ChatMessageResponseDto.class);
                if (messageListener != null) {
                    messageListener.onMessageReceived(msg);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing message", e);
            }
        }
    }

    public void sendMessage(String messageText) {
        if (webSocket != null && constructionId != null) {
            try {
                ChatMessageCreateDto dto = new ChatMessageCreateDto(messageText);
                String json = gson.toJson(dto);

                String sendFrame = "SEND\n" +
                        "destination:/app/chat.send\n" +
                        "content-type:application/json\n\n" +
                        json + "\u0000";

                webSocket.send(sendFrame);
            } catch (Exception e) {
                Log.e(TAG, "Error sending message", e);
            }
        }
    }

    public void disconnect() {
        if (webSocket != null) {
            String disconnectFrame = "DISCONNECT\n\n\u0000";
            webSocket.send(disconnectFrame);
            webSocket.close(1000, "Client disconnect");
            webSocket = null;
        }
    }

    public interface ChatMessageListener {
        void onConnected();
        void onMessageReceived(ChatMessageResponseDto message);
        void onError(Throwable error);
    }
}
