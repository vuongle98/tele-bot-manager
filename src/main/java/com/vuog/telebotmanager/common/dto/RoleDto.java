package com.vuog.telebotmanager.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto implements Serializable {

    private Long id;
    private String code;
    private String name;
}
