package com.vuog.telebotmanager.infrastructure.restclient;

import com.vuog.telebotmanager.common.dto.ApiResponse;
import com.vuog.telebotmanager.common.dto.UserResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthRestClient extends BaseRestClient {

    @Value("${app.core.service.url}")
    private String authBaseUrl;

    public AuthRestClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public UserResponseDto verifyToken(String token) {
        ResponseEntity<ApiResponse<UserResponseDto>> response = this.get(
                authBaseUrl + "/api/auth/verify",
                defaultHeaders(token),
                new ParameterizedTypeReference<>() {}
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            ApiResponse<UserResponseDto> apiResponse = response.getBody();
            return apiResponse.getData(); // Assuming 'getData()' returns the User object
        }

        throw new RuntimeException("Failed to verify token");
    }

}
