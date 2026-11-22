
package com.rahim.crm.Security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rahim.crm.dto.ErrorResponse;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class FirstLoginPasswordChangeFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    public FirstLoginPasswordChangeFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof CustomUserPrincipal user) {

            boolean isPasswordChangeEndpoint =
                    request.getRequestURI().equals("/api/auth/change-password");

            if (user.isMustChangePassword() && !isPasswordChangeEndpoint) {

                ErrorResponse error = new ErrorResponse(
                        HttpStatus.FORBIDDEN.value(),
                        "Password Change Required",
                        "You must change your temporary password before accessing other resources.",
                        request.getRequestURI()
                );

                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");

                objectMapper.writeValue(response.getOutputStream(), error);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
