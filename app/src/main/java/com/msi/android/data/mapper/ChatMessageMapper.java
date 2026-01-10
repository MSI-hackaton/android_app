package com.msi.android.data.mapper;

import com.msi.android.data.dto.ChatMessageResponseDto;
import com.msi.android.data.entity.ChatMessageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "default")
public interface ChatMessageMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "senderId", source = "senderId")
    @Mapping(target = "senderName", source = "senderName")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "timestamp", expression = "java(parseTimestamp(dto.getCreatedAt()))")
    @Mapping(target = "isOwn", ignore = true)
    ChatMessageEntity toEntity(ChatMessageResponseDto dto);

    default long parseTimestamp(String createdAt) {
        try {
            return java.time.Instant.parse(createdAt).toEpochMilli();
        } catch (Exception e) {
            return System.currentTimeMillis();
        }
    }
}
