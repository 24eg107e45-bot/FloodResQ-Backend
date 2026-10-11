
package com.floodresq.floodresq_backend;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class SessionAuthFilter extends OncePerRequestFilter {

    private static final String REPORTS_PATH = "/api/emergency-reports";

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        // Match both "/api/emergency-reports" and "/api/emergency-reports/{id}".
        // (The previous check only matched the trailing-slash form, so the list
        // and create endpoints were never given an authenticated user.)
        boolean isReportsPath = path.equals(REPORTS_PATH)
                || path.startsWith(REPORTS_PATH + "/");

        return !isReportsPath
                || "OPTIONS".equalsIgnoreCase(request.getMethod());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(
                "{\"message\":\"Please sign in to access emergency reports.\"}"
            );
            return;
        }

        // The session login (AuthController) stores userId/userRole/userEmail.
        // Spring Security's authorization rules only see the SecurityContext,
        // so expose the logged-in user and role there for this request.
        Object email = session.getAttribute("userEmail");
        Object role = session.getAttribute("userRole");

        UsernamePasswordAuthenticationToken authentication =
                UsernamePasswordAuthenticationToken.authenticated(
                    email == null ? "user" : email.toString(),
                    null,
                    List.of(new SimpleGrantedAuthority(
                        "ROLE_" + (role == null
                            ? "VICTIM"
                            : role.toString().trim().toUpperCase())))
                );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        filterChain.doFilter(request, response);
    }
}
