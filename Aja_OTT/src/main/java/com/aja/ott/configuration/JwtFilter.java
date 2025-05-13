package com.aja.ott.configuration;

import com.aja.ott.service.JwtService;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.io.IOException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Arrays;
import java.util.stream.Stream;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final String[] userEndPoint = {"/user/change-password" };
    private static final String[] hrEndPoint = {"/hr"};
    private static final String[] financeEndPoint = {"/finance"};
    private static final String[] adminEndPoint = {"/admin"};
    @Autowired
    private JwtService jwtService;
    @Autowired
    private ApplicationContext context;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                username = jwtService.extractUserName(token);
            } catch (ExpiredJwtException e) {
                sendForbiddenResponse(response, "Your token was expired. Please login again");
                return;
            } catch (SignatureException e) {
                sendForbiddenResponse(response, "Token is mismatched please provide correct token");
                return;
            } catch (Exception e) {
                sendForbiddenResponse(response, e.getMessage());
                return;
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Claims claims = jwtService.getClaims(token);
            String role = (String) claims.get("role");
            String normalizedendPoint = normalizeEndpoint(request.getRequestURI());

            if (Stream.of(userEndPoint, hrEndPoint)
                    .noneMatch(endpoints -> Arrays.asList(endpoints).contains(normalizedendPoint))) {
                sendForbiddenResponse(response, "End point not specified");
                return;
            }

            if ("HR".equalsIgnoreCase(role)) {
                if (Arrays.asList(hrEndPoint).contains(normalizedendPoint)) {
                    authenticateUser(username, request, token);
                } else {
                    sendForbiddenResponse(response, "You do not have permission to access this resource");
                    return;
                }
            }
            else if ("FINANCE".equalsIgnoreCase(role)) {
                if (Arrays.asList(financeEndPoint).contains(normalizedendPoint)) {
                    authenticateUser(username, request, token);
                } else {
                    sendForbiddenResponse(response, "You do not have permission to access this resource");
                    return;
                }
            } else if ("ADMIN".equalsIgnoreCase(role)) {
                if (Arrays.asList(adminEndPoint).contains(normalizedendPoint)) {
                    authenticateUser(username, request, token);
                } else {
                    sendForbiddenResponse(response, "You do not have permission to access this resource");
                    return;
                }
            }

            else {
                if (Arrays.asList(userEndPoint).contains(normalizedendPoint)) {
                    authenticateUser(username, request, token);
                } else {
                    sendForbiddenResponse(response, "You do not have permission to access this resource");
                    return;
                }
            }
        }

        try {
            filterChain.doFilter(request, response);
        } catch (IOException | ServletException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String normalizeEndpoint(String requestURI) {
        String[] segments = requestURI.split("/");
        StringBuilder normalizedEndpoint = new StringBuilder();
        for (String segment : segments) {
            if (!segment.isEmpty()) {
                if (segment.matches("\\d+")) {
                    normalizedEndpoint.append("/{id}");
                } else {
                    normalizedEndpoint.append("/").append(segment);
                }
            }
        }
        return normalizedEndpoint.toString();
    }

    private void authenticateUser(String username, HttpServletRequest request, String token) {
        UserDetails userDetails = context.getBean(CustomUserDetailsService.class).loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, username,
                userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private void sendForbiddenResponse(HttpServletResponse response, String message) {
        try {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");

            Map<String, String> responseData = new HashMap<>();
            responseData.put("error", "Forbidden");
            responseData.put("message", message);

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(responseData);

            response.getWriter().write(jsonResponse);
            response.getWriter().flush();
        } catch (IOException | JsonProcessingException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getCurrentUserRole() {
        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
        return extractRole(token);
    }

    public String extractRole(String token) {
        Claims claims = jwtService.getClaims(token);
        return (String) claims.get("role");
    }

    public String extractUsername(String token) {
        Claims claims = jwtService.getClaims(token);
        return claims.getSubject();
    }

    public String getCurrentUsername() {
        String token = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
        return extractUsername(token);
    }
}

