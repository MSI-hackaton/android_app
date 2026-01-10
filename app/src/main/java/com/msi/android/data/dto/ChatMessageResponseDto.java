package com.msi.android.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponseDto {
    private String id;
    private String constructionId;
    private String senderId;
    private String senderName;
    private String message;
    private Boolean isRead;
    private String createdAt;
    private String timestamp;
}
