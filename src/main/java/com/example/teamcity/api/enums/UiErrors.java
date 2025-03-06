package com.example.teamcity.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UiErrors {
    BUILD_CONFIG_NAME_MUST_BE_NOT_NULL("Build configuration name must not be empty");

    private final String text;
}