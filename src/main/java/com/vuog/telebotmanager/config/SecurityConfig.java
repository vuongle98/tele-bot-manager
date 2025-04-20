package com.vuog.telebotmanager.config;
import com.vuog.telebotmanager.config.filter.JWTAuthenticationFilter;
import com.vuog.telebotmanager.infrastructure.service.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthService authService;

    public SecurityConfig(AuthService authService) {
        this.authService = authService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                            AntPathRequestMatcher.antMatcher("/api/auth/**"),
                            AntPathRequestMatcher.antMatcher("/v3/api-docs/**"),
                            AntPathRequestMatcher.antMatcher("/swagger-ui.html"),
                            AntPathRequestMatcher.antMatcher("/swagger-ui/**"),
                            AntPathRequestMatcher.antMatcher("/actuator/**"),
                            AntPathRequestMatcher.antMatcher("/favicon.ico")
                ).permitAll()
                .anyRequest().authenticated());

        http.addFilterBefore(new JWTAuthenticationFilter(authService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
