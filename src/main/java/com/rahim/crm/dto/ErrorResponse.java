package com.rahim.crm.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Structured error body returned by {@link com.rahim.crm.exception.GlobalExceptionHandler}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private int status;
    private String error;
    private String message;
    private String path;
    private LocalDateTime timestamp;

    /** Field-level validation errors: field → list of violation messages. */
    private Map<String, List<String>> fieldErrors;

    // ── constructors ──────────────────────────────────────────────────────────

    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(int status, String error, String message, String path) {
        this();
        this.status  = status;
        this.error   = error;
        this.message = message;
        this.path    = path;
    }

    // ── getters / setters ─────────────────────────────────────────────────────

    public int getStatus()                             { return status; }
    public void setStatus(int status)                  { this.status = status; }

    public String getError()                           { return error; }
    public void setError(String error)                 { this.error = error; }

    public String getMessage()                         { return message; }
    public void setMessage(String message)             { this.message = message; }

    public String getPath()                            { return path; }
    public void setPath(String path)                   { this.path = path; }

    public LocalDateTime getTimestamp()                { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp)  { this.timestamp = timestamp; }

    public Map<String, List<String>> getFieldErrors()               { return fieldErrors; }
    public void setFieldErrors(Map<String, List<String>> fieldErrors){ this.fieldErrors = fieldErrors; }
}
