package com.example.teamcity.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParentProject extends BaseModel {
    @Builder.Default
    private String id = "_Root";
    @Builder.Default
    private String name = "<Root project>";
    @Builder.Default
    private String description = "Contains all other projects";
}
