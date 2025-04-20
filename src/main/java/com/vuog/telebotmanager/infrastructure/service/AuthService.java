package com.vuog.telebotmanager.infrastructure.service;

import com.vuog.telebotmanager.common.dto.UserResponseDto;

public interface AuthService {

    UserResponseDto verifyToken(String token);
}
