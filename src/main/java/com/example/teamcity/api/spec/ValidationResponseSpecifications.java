package com.example.teamcity.api.spec;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;

public class ValidationResponseSpecifications {

        public static ResponseSpecification checkProjectWithNameAlreadyExist(String projectName) {
            ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
            responseSpecBuilder.expectStatusCode(HttpStatus.SC_BAD_REQUEST);
            responseSpecBuilder.expectBody(Matchers.containsString("Project with this name already exists: %s".formatted(projectName)));
            return responseSpecBuilder.build();
    }
}
