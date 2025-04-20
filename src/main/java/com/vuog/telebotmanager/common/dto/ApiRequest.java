package com.vuog.telebotmanager.common.dto;

import lombok.Data;
import org.springframework.data.domain.Pageable;

import java.util.Map;

@Data
public class ApiRequest<T> {
    private T data;
    private String projection;
    private Map<String, String> filters;
    private Pageable pageable;
} 