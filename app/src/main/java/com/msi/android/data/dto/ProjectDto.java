package com.msi.android.data.dto;

import java.util.List;

import lombok.Data;

@Data
public class ProjectDto {
    private String id;
    private String title;
    private String description;
    private double area;
    private int floors;
    private int constructionTime;
    private double price;
    private String status;
    private List<String> photos;
}
