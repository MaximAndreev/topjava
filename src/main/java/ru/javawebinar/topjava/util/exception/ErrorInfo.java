package ru.javawebinar.topjava.util.exception;

import java.util.List;

public class ErrorInfo {
    private String url;
    private ErrorType type;
    private List<String> detail;

    public ErrorInfo() {
    }

    public ErrorInfo(CharSequence url, ErrorType type, List<String> detail) {
        this.url = url.toString();
        this.type = type;
        this.detail = detail;
    }
}