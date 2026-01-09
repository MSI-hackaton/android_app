package com.msi.android.data.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DocumentEntity {

    private String id;
    private String title;
    private String date;
    private String status;

}

