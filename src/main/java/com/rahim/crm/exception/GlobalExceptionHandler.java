package com.rahim.crm.exception;

import com.rahim.crm.dto.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Centralised exception handler — catches every exception thrown from
 * controllers/services and returns a consistent {@link ErrorResponse} body.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── 400 Bad Request ───────────────────────────────────────────────────────

    /**
     * Bean-validation failures (@Valid / @Validated).
     * Groups all field violations into a map: field → [messages].
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, List<String>> fieldErrors = new LinkedHashMap<>();

        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fieldErrors
                    .computeIfAbsent(fe.getField(), k -> new ArrayList<>())
                    .add(fe.getDefaultMessage());
        }

        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                "One or more fields have invalid values.",
                request.getRequestURI()
        );
        body.setFieldErrors(fieldErrors);

        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Business-rule violations (e.g. duplicate e-mail / username).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request",
                ex.getMessage(), request);
    }

    /**
     * Path-variable / request-param type mismatch (e.g. string where int expected).
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        String message = String.format(
                "Parameter '%s' has an invalid value: '%s'",
                ex.getName(), ex.getValue());

        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request",
                message, request);
    }

    // ── 401 Unauthorized ──────────────────────────────────────────────────────

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request) {

        return buildResponse(HttpStatus.UNAUTHORIZED, "Unauthorized",
                "Invalid email or password.", request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(
            AuthenticationException ex,
            HttpServletRequest request) {

        return buildResponse(HttpStatus.UNAUTHORIZED, "Unauthorized",
                ex.getMessage(), request);
    }

    // ── 403 Forbidden ─────────────────────────────────────────────────────────

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {

        return buildResponse(HttpStatus.FORBIDDEN, "Forbidden",
                "You do not have permission to access this resource.", request);
    }

    // ── 403 Account disabled / locked ────────────────────────────────────────

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handleDisabled(
            DisabledException ex,
            HttpServletRequest request) {

        return buildResponse(HttpStatus.FORBIDDEN, "Account Disabled",
                "Your account has been disabled. Please contact support.", request);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ErrorResponse> handleLocked(
            LockedException ex,
            HttpServletRequest request) {

        return buildResponse(HttpStatus.FORBIDDEN, "Account Locked",
                "Your account is locked. Please contact support.", request);
    }

    // ── 404 Not Found ─────────────────────────────────────────────────────────

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandler(
            NoHandlerFoundException ex,
            HttpServletRequest request) {

        return buildResponse(HttpStatus.NOT_FOUND, "Not Found",
                "The requested resource was not found: " + ex.getRequestURL(),
                request);
    }

    // ── 500 Internal Server Error ─────────────────────────────────────────────

    /**
     * Catch-all — prevents stack traces leaking to clients.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex,
            HttpServletRequest request) {

        // Log the real cause here if you have a logger:
        // log.error("Unhandled exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "An unexpected error occurred. Please try again later.",
                request);
    }

    // ── helper ────────────────────────────────────────────────────────────────

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status, String error, String message,
            HttpServletRequest request) {

        ErrorResponse body = new ErrorResponse(
                status.value(), error, message, request.getRequestURI());

        return ResponseEntity.status(status).body(body);
    }
}
