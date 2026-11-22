
package com.rahim.crm.Security;

import io.jsonwebtoken.JwtException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService) {

        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        try {

            // 1. Check whether this token has been logged out
            if (jwtService.isTokenBlacklisted(jwt)) {
                rejectRequest(response, "You have been logged out");
                return;
            }

            // 2. Extract email from JWT
            String email = jwtService.extractUsername(jwt);

            if (email == null) {
                rejectRequest(response, "Invalid token");
                return;
            }

            // 3. Load authenticated user
            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                CustomUserPrincipal userPrincipal =
                        (CustomUserPrincipal)
                                userDetailsService.loadUserByUsername(email);

                // 4. Check account status and token validity
                if (!userPrincipal.isEnabled()) {
                    rejectRequest(response, "Account is disabled");
                    return;
                }

                if (!jwtService.isTokenValid(
                        jwt,
                        userPrincipal.getUsername())) {

                    rejectRequest(response, "Invalid or expired token");
                    return;
                }

                // 5. Set authentication in SecurityContext
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userPrincipal,
                                null,
                                userPrincipal.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
            }

        } catch (JwtException | IllegalArgumentException |
                 UsernameNotFoundException exception) {

            SecurityContextHolder.clearContext();
            rejectRequest(response, "Invalid or expired token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void rejectRequest(
            HttpServletResponse response,
            String message) throws IOException {

        SecurityContextHolder.clearContext();

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(
                "{\"status\":401,\"message\":\"" + message + "\"}"
        );
    }
}
