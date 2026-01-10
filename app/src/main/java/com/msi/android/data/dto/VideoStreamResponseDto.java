package com.msi.android.data.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class VideoStreamResponseDto {
    private UUID id;
    private UUID constructionId;
    private String name;
    private String description;
    private String streamUrl;
    private String lastUpdate;
    private String createdAt;
    private String updatedAt;
}
