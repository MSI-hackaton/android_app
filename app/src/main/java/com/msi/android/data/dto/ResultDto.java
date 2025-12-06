package com.msi.android.data.dto;

import lombok.Value;

@Value
public class ResultDto<T> {

    public enum Status { SUCCESS, ERROR, LOADING }

    T data;
    String message;
    Status status;

    public static <T> ResultDto<T> success(T data) {
        return new ResultDto<>(data, null, Status.SUCCESS);
    }

    public static <T> ResultDto<T> error(String msg) {
        return new ResultDto<>(null, msg, Status.ERROR);
    }

    public static <T> ResultDto<T> loading() {
        return new ResultDto<>(null, null, Status.LOADING);
    }

}
