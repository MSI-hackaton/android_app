package com.msi.android.data.dto;

import lombok.Data;

@Data
public class ConstructionStageResponseDto {
    private String id;
    private String requestId;
    private String projectId;
    private String customerId;
    private String specialistId;
    private String name;
    private String description;
    private String startDate;
    private String endDate;
    private String status;
    private String createdAt;
    private String updatedAt;
}
