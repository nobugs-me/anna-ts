package com.example.teamcity.api;

import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.models.SourceProject;
import com.example.teamcity.api.models.User;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import com.example.teamcity.api.spec.Specifications;
import com.example.teamcity.api.spec.ValidationResponseSpecifications;
import org.apache.commons.lang3.RandomStringUtils;
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
                .then().spec(ValidationResponseSpecifications.checkProjectIsInvalidWithNonLatinCharsProjectID(projectWithCyrillicSymbols.getId()));
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
                .then().spec(ValidationResponseSpecifications.checkProjectIsInvalidStareWithNonLetter(projectStartsWithNumber.getId()));
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
                .then().spec(ValidationResponseSpecifications.checkProjectWithSameIdExists(projectWithSameId.getId()));}

    @Test(description = "User should not be able to create project with empty name", groups = {"Negative", "CRUD"})
    public void userCreatesTwoProjectsWithEmptyNameTest() {
        Project projectWithEmptyName = generate(Project.class);
        projectWithEmptyName.setName("");

        superUserCheckedRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());

        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .create(projectWithEmptyName)
                .then().spec(ValidationResponseSpecifications.checkProjectWithEmptyNameCannotBeCreated(projectWithEmptyName.getId()));
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
                .then().spec(ValidationResponseSpecifications.checkProjectWithEmptyIdCannotBeCreated(projectWithEmptyId.getId()));
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
                .then().spec(ValidationResponseSpecifications.checkCopyOfProjectWithNonExistingIdCannotBeCreated(nonExistingSourceId));}


    @Test(description = "User can search a project by name", groups = {"Positive", "CRUD"})
    public void userCanSearchProjectByName() {
        superUserCheckedRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());
        var createdProject = userCheckRequests.<Project>getRequest(PROJECTS).read(testData.getProject().getId());

        var searchedProject = userCheckRequests.getRequest(PROJECTS).search("name", createdProject.getName());

        soft.assertEquals(searchedProject, createdProject);
    }

//подскажи пжта а можно с датапровайдера передать роль в название теста?
    @Test(dataProvider = "projectCreationRolesAreNotAllowedData", description = "User should not be able to create project as Project Viewer, Develop, Agent Manager", groups = {"Negative", "CRUD"})
    public void userShouldNotBeAbleToCreateProjectAsProjectViewer(String role) {
        User projectViewerUser = testData.getUser();
        projectViewerUser.getRoles().getRole().get(0).setRoleId(role);
        superUserCheckedRequests.getRequest(USERS).create(projectViewerUser);

        new UncheckedBase(Specifications.authSpec(testData.getUser()), PROJECTS)
                .create(testData.getProject())
                .then().spec(ValidationResponseSpecifications.checkUserWithoutPermissionsCannotCreateProject());}

    @DataProvider(name = "projectCreationRolesAreNotAllowedData")
    public Object[][]  getRole() {
        return new Object[][]  { {"PROJECT_VIEWER"}, {"PROJECT_DEVELOPER"}, {"AGENT_MANAGER"}};
    }
}