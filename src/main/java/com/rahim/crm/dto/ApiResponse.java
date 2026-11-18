package com.rahim.crm.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * Unified API response wrapper for all endpoints.
 *
 * @param <T> payload type
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    // ── constructors ──────────────────────────────────────────────────────────

    private ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    // ── static factories ──────────────────────────────────────────────────────

    public static <T> ApiResponse<T> success(String message, T data) {
        ApiResponse<T> res = new ApiResponse<>();
        res.success = true;
        res.message = message;
        res.data    = data;
        return res;
    }

    public static <T> ApiResponse<T> success(String message) {
        return success(message, null);
    }

    public static <T> ApiResponse<T> failure(String message) {
        ApiResponse<T> res = new ApiResponse<>();
        res.success = false;
        res.message = message;
        return res;
    }


    // ── getters ───────────────────────────────────────────────────────────────

    public boolean isSuccess()         { return success; }
    public String getMessage()         { return message; }
    public T getData()                 { return data; }
    public LocalDateTime getTimestamp(){ return timestamp; }
}
