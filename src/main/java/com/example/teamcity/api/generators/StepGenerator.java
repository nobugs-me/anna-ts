package com.example.teamcity.api.generators;

import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.Property;
import com.example.teamcity.api.models.Step;
import com.example.teamcity.api.models.Steps;

import java.util.ArrayList;
import java.util.List;

public class StepGenerator {
    public static List<Property> generateSimpleRunner(String content) {
        List<Property> buildProperties = new ArrayList<>();
        buildProperties.add(Property.builder().name("teamcity.step.mode").value("default").build());
        buildProperties.add(Property.builder().name("use.custom.script").value("true").build());
        buildProperties.add(Property.builder().name("script.content").value(content).build());
        return buildProperties;
    }
}
