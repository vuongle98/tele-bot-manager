package com.vuog.telebotmanager.infrastructure.service.impl;


import com.vuog.telebotmanager.common.dto.UserResponseDto;
import com.vuog.telebotmanager.infrastructure.restclient.AuthRestClient;
import com.vuog.telebotmanager.infrastructure.service.AuthService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AuthRestClient authRestClient;

    public AuthServiceImpl(AuthRestClient authRestClient) {
        this.authRestClient = authRestClient;
    }


    // Method to verify token with core service
    public UserResponseDto verifyToken(String token) {
        return authRestClient.verifyToken(token);
    }
}
