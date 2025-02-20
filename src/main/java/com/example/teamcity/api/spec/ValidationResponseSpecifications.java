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

    public static ResponseSpecification checkProjectIsInvalidWithNonLatinCharsProjectID(String projectName) {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        responseSpecBuilder.expectBody(Matchers.containsString("Project ID \"%s\" is invalid: contains non-latin letter 'ы'. ID should start with a latin letter and contain only latin letters, digits and underscores (at most 225 characters).\n".formatted(projectName) +
                "Error occurred while processing this request."));
        return responseSpecBuilder.build();
    }

    public static ResponseSpecification checkProjectIsInvalidStareWithNonLetter(String projectName) {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        responseSpecBuilder.expectBody(Matchers.containsString("Project ID \"%s\" is invalid: starts with non-letter character '1'. ID should start with a latin letter and contain only latin letters, digits and underscores (at most 225 characters).\n".formatted(projectName) +
                "Error occurred while processing this request."));
        return responseSpecBuilder.build();
    }

    public static ResponseSpecification checkProjectWithSameIdExists(String projectName) {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(HttpStatus.SC_BAD_REQUEST);
        responseSpecBuilder.expectBody(Matchers.containsString("Project ID \"%s\" is already used by another project".formatted(projectName)));
        return responseSpecBuilder.build();
    }

    public static ResponseSpecification checkProjectWithEmptyNameCannotBeCreated(String projectName) {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(HttpStatus.SC_BAD_REQUEST);
        responseSpecBuilder.expectBody(Matchers.containsString("Project name cannot be empty."));
        return responseSpecBuilder.build();
    }

    public static ResponseSpecification checkProjectWithEmptyIdCannotBeCreated(String projectName) {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        responseSpecBuilder.expectBody(Matchers.containsString("Project ID must not be empty."));
        return responseSpecBuilder.build();
    }

    public static ResponseSpecification checkCopyOfProjectWithNonExistingIdCannotBeCreated(String projectId) {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(HttpStatus.SC_NOT_FOUND);
        responseSpecBuilder.expectBody(Matchers.containsString("No project found by name or internal/external id '%s'.\n".formatted(projectId) +
                "Could not find the entity requested. Check the reference is correct and the user has permissions to access the entity."));
        return responseSpecBuilder.build();
    }

    public static ResponseSpecification checkUserWithoutPermissionsCannotCreateProject() {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(HttpStatus.SC_FORBIDDEN);
        responseSpecBuilder.expectBody(Matchers.containsString("You do not have \"Create subproject\" permission in project with internal id: _Root\n" +
            "Access denied. Check the user has enough permissions to perform the operation."));
        return responseSpecBuilder.build();
    }

    public static ResponseSpecification checkUserCannotCreateBuildTypeWithSameIdAsExisting(String buildTypeId) {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(HttpStatus.SC_BAD_REQUEST);
        responseSpecBuilder.expectBody(Matchers.containsString("The build configuration / template ID \"%s\" is already used by another configuration or template".formatted(buildTypeId)));
        return responseSpecBuilder.build();
    }

    public static ResponseSpecification checkUserCannotEditProjectWithInsufficientRights(String projectId) {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(HttpStatus.SC_FORBIDDEN);
        responseSpecBuilder.expectBody(Matchers.containsString("You do not have enough permissions to edit project with id: %s\n".formatted(projectId) +
            "Access denied. Check the user has enough permissions to perform the operation."));
        return responseSpecBuilder.build();
    }
}
