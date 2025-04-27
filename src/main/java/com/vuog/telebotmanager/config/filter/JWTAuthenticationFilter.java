package com.vuog.telebotmanager.config.filter;


import com.vuog.telebotmanager.common.dto.PermissionDto;
import com.vuog.telebotmanager.common.dto.RoleDto;
import com.vuog.telebotmanager.common.dto.UserResponseDto;
import com.vuog.telebotmanager.infrastructure.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final AuthService authService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);

            try {
                UserResponseDto user = authService.verifyToken(token);

                if (user != null) {
                    // Get roles and permissions from the user
                    List<String> roles = user.getRoles().stream().map(RoleDto::getCode).toList(); // Assuming getRoles() returns a List of role names
                    List<String> permissions = user.getPermissions().stream().map(PermissionDto::getCode).toList(); // Assuming getPermissions() returns a List of permissions

                    // Create an authentication token with the user details, roles, and permissions
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(user, null,
                                    AuthorityUtils.createAuthorityList(roles.toArray(new String[0])));

                    // Optionally, you can add permissions as custom authorities if needed:
//                 authentication.setDetails(new CustomUserDetails(user, permissions));

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // Set the authentication context
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Not found user info");
                    return;
                }
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
