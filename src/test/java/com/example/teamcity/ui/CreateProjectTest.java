package com.example.teamcity.ui;

import com.codeborne.selenide.Condition;
import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.UncheckedRequests;
import com.example.teamcity.api.spec.Specifications;
import com.example.teamcity.ui.pages.ProjectPage;
import com.example.teamcity.ui.pages.ProjectsPage;
import com.example.teamcity.ui.pages.admin.CreateProjectPage;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Test(groups = {"Regression"})
public class CreateProjectTest extends BaseUiTest {
    private static final String REPO_URL = "https://github.com/AlexPshe/spring-core-for-qa";

    @Test(description = "User should be able to create project", groups = {"Positive"})
    public void userCreatesProject() {
        /*step("Open `Create Project Page` (http://localhost:8111/admin/createObjectMenu.html)");
        step("Send all project parameters (repository URL)");
        step("Click `Proceed`");
        step("Fix Project Name and Build Type name values");
        step("Click `Proceed`");
        step("Check that all entities (project, build type) was successfully created with correct data on API level");
        step("Check that project is visible on Projects Page (http://localhost:8111/favorite/projects)");*/
        // подготовка окружения
        loginAs(testData.getUser());

        // взаимодействие с UI
        CreateProjectPage.open("_Root")
                .createForm(REPO_URL)
                .setupProject(testData.getProject().getName(), testData.getBuildType().getName());

        // проверка состояния API
        var createdProject = superUserCheckedRequests.<Project>getRequest(Endpoint.PROJECTS).search("name", testData.getProject().getName());
        soft.assertNotNull(createdProject);
        // (корректность отправки данных с UI на API)

        // проверка состояния UI
        // (корректность считывания данных и отображение данных на UI)

        ProjectPage.open(createdProject.getId())
                .title.shouldHave(Condition.exactText(testData.getProject().getName()));

        var foundProjects = ProjectsPage.open()
                .getProjects().stream()
                .anyMatch(project -> project.getName().text().equals(testData.getProject().getName()));

        soft.assertTrue(foundProjects);
    }

    @Test(description = "User should not be able to crete project without name", groups = {"Negative"})
    public void userCreatesProjectWithoutName() {
       /* // подготовка окружения
        step("Login as user");
        step("Check number of projects");

        // взаимодействие с UI
        step("Open `Create Project Page` (http://localhost:8111/admin/createObjectMenu.html)");
        step("Send all project parameters (repository URL)");
        step("Click `Proceed`");
        step("Set Project Name");
        step("Click `Proceed`");

        // проверка состояния API
        // (корректность отправки данных с UI на API)
        step("Check that number of projects did not change");

        // проверка состояния UI
        // (корректность считывания данных и отображение данных на UI)
        step("Check that error appears `Project name must not be empty`");*/

        loginAs(testData.getUser());

        // взаимодействие с UI

        String errorMessage =  CreateProjectPage.open("_Root").setupProjectManually("", "").getProjectNameInputError().text();

        // проверка состояния API
        var userUncheckRequests = new UncheckedRequests(Specifications.authSpec(testData.getUser()));

        Response createdProject = userUncheckRequests.getRequest(Endpoint.PROJECTS).search("name", testData.getProject().getName());
        createdProject.then().statusCode(HttpStatus.SC_NOT_FOUND);

        var foundProjects = ProjectsPage.open()
                .getProjects().stream()
                .noneMatch(project -> project.getName().text().equals(testData.getProject().getName()));

        soft.assertEquals(errorMessage, "Project name is empty");
        soft.assertTrue(foundProjects);
    }

    @Test(description = "User should be able to search for project by its full name", groups = {"Positive"})
    public void userSearchesProjectByItsName() throws InterruptedException {
        loginAs(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        var project = userCheckRequests.<Project>getRequest(Endpoint.PROJECTS).create(testData.getProject());

        List<String> searchedProjects = ProjectsPage.open().searchForProject(project.getName()).getSideBarProjects().stream().map(proj -> proj.getName().text()).collect(Collectors.toList());

        soft.assertEquals(searchedProjects, Arrays.asList(testData.getProject().getName()));
    }
}