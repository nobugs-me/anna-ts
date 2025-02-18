package com.example.teamcity.api;

import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.models.SourceProject;
import com.example.teamcity.api.models.User;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import com.example.teamcity.api.spec.Specifications;
import com.example.teamcity.api.spec.ValidationResponseSpecifications;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static com.example.teamcity.api.enums.Endpoint.*;
import static com.example.teamcity.api.generators.TestDataGenerator.generate;

@Test(groups = "Regression")
public class ProjectTest extends BaseApiTest {

    @Test(description = "User should be able to create new project in Root catalog", groups = {"Positive", "CRUD"})
    public void userCreatesProjectInRootTest() {

        superUserCheckedRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());
        var createdProject = userCheckRequests.<Project>getRequest(PROJECTS).read(testData.getProject().getId());

        soft.assertEquals(createdProject, testData.getProject());
    }

    @Test(description = "User should be able to create sub project of other project", groups = {"Positive", "CRUD"})
    public void userCreatesProjectNotInRootTest() {

        superUserCheckedRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());
        var createdProject = userCheckRequests.<Project>getRequest(PROJECTS).read(testData.getProject().getId());

        Project subProject = generate(Project.class);
        subProject.getParentProject().setId(createdProject.getId());
        subProject.getParentProject().setName(createdProject.getName());

        userCheckRequests.<Project>getRequest(PROJECTS).create(subProject);

        var createdSubProject = userCheckRequests.<Project>getRequest(PROJECTS).read(subProject.getId());

        soft.assertEquals(createdSubProject, subProject);
    }

    @Test(description = "User should not be able to create project with cyrillic symbols", groups = {"Negative", "CRUD"})
    public void userShouldNotBeAbleToCreateProjectWithCyrillicSymbolsTest() {
        Project projectWithCyrillicSymbols = generate(Project.class);
        projectWithCyrillicSymbols.setId(testData.getProject().getId()+"ылыов");

        superUserCheckedRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .create(projectWithCyrillicSymbols)
                .then().assertThat().statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .body(Matchers.containsString("Project ID \"%s\" is invalid: contains non-latin letter 'ы'. ID should start with a latin letter and contain only latin letters, digits and underscores (at most 225 characters).\n".formatted(projectWithCyrillicSymbols.getId()) +
                        "Error occurred while processing this request."));
    }

    @Test(description = "User should not be able to create project if id starts with number", groups = {"Negative", "CRUD"})
    public void userShouldNotBeAbleToCreateProjectIfIdStartsWithNumber() {
        Project projectStartsWithNumber = generate(Project.class);
        projectStartsWithNumber.setId("123" + testData.getProject().getId());

        superUserCheckedRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .create(projectStartsWithNumber)
                .then().assertThat().statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .body(Matchers.containsString("Project ID \"%s\" is invalid: starts with non-letter character '1'. ID should start with a latin letter and contain only latin letters, digits and underscores (at most 225 characters).\n".formatted(projectStartsWithNumber.getId()) +
                        "Error occurred while processing this request."));
    }

    @Test(description = "User should not be able to create project with the same name", groups = {"Negative", "CRUD"})
    public void userShouldNotBeAbleToCreateProjectWithTheSameNameTest() {
        Project projectWithSameName = generate(Project.class);
        projectWithSameName.setName(testData.getProject().getName());

        superUserCheckedRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .create(projectWithSameName)
                .then().spec(ValidationResponseSpecifications.checkProjectWithNameAlreadyExist(testData.getProject().getName()));
    }

    @Test(description = "User should not be able to create project with the same id", groups = {"Negative", "CRUD"})
    public void userShouldNotBeAbleToCreateProjectWithTheSameIdTest() {
        Project projectWithSameId = generate(Project.class);
        projectWithSameId.setId(testData.getProject().getId());

        superUserCheckedRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .create(projectWithSameId)
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString("Project ID \"%s\" is already used by another project".formatted(testData.getProject().getId())));
    }

    @Test(description = "User should not be able to create project with empty name", groups = {"Negative", "CRUD"})
    public void userCreatesTwoProjectsWithEmptyNameTest() {
        Project projectWithEmptyName = generate(Project.class);
        projectWithEmptyName.setName("");

        superUserCheckedRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .create(projectWithEmptyName)
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString("Project name cannot be empty."));
    }

    @Test(description = "User should not be able to create project with empty id", groups = {"Negative", "CRUD"})
    public void userCreatesTwoProjectsWithEmptyIdTest() {
        Project projectWithEmptyId = generate(Project.class);
        projectWithEmptyId.setId("");

        superUserCheckedRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .create(projectWithEmptyId)
                .then().assertThat().statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .body(Matchers.containsString("Project ID must not be empty."));
    }


    @Test(description = "User should be able to create project with 226 char name", groups = {"Positive", "CRUD"})
    public void userCreatesProjectsWith256StringNameTest() {
        Project projectWith256StringName = generate(Project.class);
        projectWith256StringName.setName(RandomStringUtils.randomAlphabetic(226));

        superUserCheckedRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(projectWith256StringName);
        var createdProject = userCheckRequests.<Project>getRequest(PROJECTS).read(projectWith256StringName.getId());

        soft.assertEquals(createdProject, projectWith256StringName);
        }

    @Test(description = "User should be able to copy the project", groups = {"Positive", "CRUD"})
    public void userCopiesProjectInRootTest() {
        superUserCheckedRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());
        var createdProject = userCheckRequests.<Project>getRequest(PROJECTS).read(testData.getProject().getId());

        Project projectCopy = generate(Project.class);
        SourceProject sourceProject = generate(SourceProject.class);
        projectCopy.setSourceProject(sourceProject);
        projectCopy.getSourceProject().setLocator(createdProject.getId());
        projectCopy.setDescription(createdProject.getDescription());

        userCheckRequests.<Project>getRequest(PROJECTS).create(projectCopy);

        projectCopy.setSourceProject(null);

        var copiedProject = userCheckRequests.<Project>getRequest(PROJECTS).read(projectCopy.getId());

        soft.assertEquals(copiedProject, projectCopy);
    }

    @Test(description = "User should not be able to create a copy of non existing source project", groups = {"Negative", "CRUD"})
    public void userShouldNotBeAbleToCreateACopyOfNonExistingSourceProject() {
        superUserCheckedRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());
        var createdProject = userCheckRequests.<Project>getRequest(PROJECTS).read(testData.getProject().getId());

        String nonExistingSourceId = RandomStringUtils.randomAlphabetic(10);
        Project projectCopy = generate(Project.class);
        SourceProject sourceProject = generate(SourceProject.class);
        projectCopy.setSourceProject(sourceProject);
        projectCopy.getSourceProject().setLocator(nonExistingSourceId);
        projectCopy.setDescription(createdProject.getDescription());

        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .create(projectCopy)
                .then().assertThat().statusCode(HttpStatus.SC_NOT_FOUND)
                .body(Matchers.containsString("No project found by name or internal/external id '%s'".formatted(nonExistingSourceId)));
    }

    @Test(description = "User should not be able to create a copy of empty source project", groups = {"Negative", "CRUD"})
    public void userShouldNotBeAbleToCreateACopyOfEmptySourceProject() {
        superUserCheckedRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());
        var createdProject = userCheckRequests.<Project>getRequest(PROJECTS).read(testData.getProject().getId());

        String nonExistingSourceId = RandomStringUtils.randomAlphabetic(10);
        Project projectCopy = generate(Project.class);
        SourceProject sourceProject = generate(SourceProject.class);
        projectCopy.setSourceProject(sourceProject);
        projectCopy.getSourceProject().setLocator(nonExistingSourceId);
        projectCopy.setDescription(createdProject.getDescription());

        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .create(projectCopy)
                .then().assertThat().statusCode(HttpStatus.SC_NOT_FOUND)
                .body(Matchers.containsString("No project found by name or internal/external id '%s'.\n".formatted(nonExistingSourceId) +
                        "Could not find the entity requested. Check the reference is correct and the user has permissions to access the entity."));
    }

    @Test(description = "User can search a project by name", groups = {"Positive", "CRUD"})
    public void userCanSearchProjectByName() {
        superUserCheckedRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());
        var createdProject = userCheckRequests.<Project>getRequest(PROJECTS).read(testData.getProject().getId());

        var searchedProject = userCheckRequests.getRequest(PROJECTS).search("name", createdProject.getName());

        soft.assertEquals(searchedProject, createdProject);
    }

    @Test(dataProvider = "projectCreationRolesAreNotAllowedData", description = "User should not be able to create project as Project Viewer", groups = {"Negative", "CRUD"})
    public void userShouldNotBeAbleToCreateProjectAsProjectViewer(String role) {
        User projectViewerUser = testData.getUser();
        projectViewerUser.getRoles().getRole().getFirst().setRoleId(role);
        superUserCheckedRequests.getRequest(USERS).create(projectViewerUser);

        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .create(testData.getProject())
                .then().assertThat().statusCode(HttpStatus.SC_FORBIDDEN)
                .body(Matchers.containsString("You do not have \"Create subproject\" permission in project with internal id: _Root\n" +
                        "Access denied. Check the user has enough permissions to perform the operation."));
    }

    @DataProvider(name = "projectCreationRolesAreNotAllowedData")
    public Object[][]  getRole() {
        return new Object[][]  { {"PROJECT_VIEWER"}, {"PROJECT_DEVELOPER"}, {"AGENT_MANAGER"}};
    }
}