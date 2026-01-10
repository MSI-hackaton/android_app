package com.msi.android.data.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageEntity {
    private String id;
    private String senderId;
    private String senderName;
    private String message;
    private boolean isOwn;
    private long timestamp;
}