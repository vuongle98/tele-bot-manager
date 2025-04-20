package com.vuog.telebotmanager.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto implements Serializable {

    private Long id;
    private String username;
    private String email;
    private List<String> roles;
    private List<String> permissions;
    private Boolean locked;

    public UserResponseDto(String username, String email) {
        this.username = username;
        this.email = email;
    }
}
