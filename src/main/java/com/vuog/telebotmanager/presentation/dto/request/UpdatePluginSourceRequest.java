package com.vuog.telebotmanager.presentation.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UpdatePluginSourceRequest implements Serializable {
    private String sourceCode;
}
