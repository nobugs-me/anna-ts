package com.example.teamcity.ui;

import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.enums.UiErrors;
import com.example.teamcity.api.generators.StepGenerator;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.models.Step;
import com.example.teamcity.api.models.Steps;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.spec.Specifications;
import com.example.teamcity.api.spec.ValidateElement;
import com.example.teamcity.ui.elements.BuildElement;
import com.example.teamcity.ui.pages.ProjectBuildsPage;
import com.example.teamcity.ui.pages.admin.CreateBuildConfigurationPage;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.teamcity.api.enums.Endpoint.BUILD_TYPES;

@Test(groups = {"Regression"})
public class CreateBuildTest extends BaseUiTest{
    private static final String REPO_URL = "https://github.com/AlexPshe/spring-core-for-qa";
    private static final String BUILD_STATE_SUCCESS = "Success";

    @Test(description = "User should be able to create build", groups = {"Positive"})
    public void userCreatesBuildConfiguration() {
        loginAs(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        var project = userCheckRequests.<Project>getRequest(Endpoint.PROJECTS).create(testData.getProject());

        CreateBuildConfigurationPage.open(project.getId()).createForm(REPO_URL).setBuildConfigurationName(testData.getBuildType().getName());
        var foundBuilds = ProjectBuildsPage.open(project.getId()).getBuilds().stream().map(builds -> builds.getName().text()).collect(Collectors.toList());
        soft.assertTrue(foundBuilds.contains(testData.getBuildType().getName()));
    }

    @Test(description = "User should not be able to create build with empty name", groups = {"Negative"})
    public void userCreatesBuildConfigurationWithEmptyBuildName() {
        loginAs(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        var project = userCheckRequests.<Project>getRequest(Endpoint.PROJECTS).create(testData.getProject());

        var inputWithError = CreateBuildConfigurationPage.open(project.getId()).createForm(REPO_URL).setBuildConfigurationName("").getBuildConfigurationNameInputError();
        ValidateElement.byText(inputWithError, UiErrors.BUILD_CONFIG_NAME_MUST_BE_NOT_NULL);

        var foundBuilds = ProjectBuildsPage.open(project.getId()).getBuilds().stream().map(builds -> builds.getName().text()).collect(Collectors.toList());
        soft.assertTrue(foundBuilds.isEmpty());
    }


    @Test(description = "User should be able to run the build", groups = {"Positive"})
    public void userRunsTheBuildSucessfully() {
        loginAs(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        var project = userCheckRequests.<Project>getRequest(Endpoint.PROJECTS).create(testData.getProject());

        var buildType = testData.getBuildType();
        Step simpleRunnerStep = Step.builder().type("simpleRunner").name("Print hello world").Properties(StepGenerator.generateSimpleRunner("Hello World!")).build();
        List<Step> buildStep = new ArrayList<>();
        buildStep.add(simpleRunnerStep);
        Steps steps = Steps.builder().steps(buildStep).build();
        buildType.setName("Print hello world");
        buildType.setSteps(steps);

        superUserCheckedRequests.getRequest(BUILD_TYPES).create(buildType);

        BuildElement buildElement = ProjectBuildsPage.open(project.getId()).getBuilds()
                .stream().filter(x -> x.getName().getText().equalsIgnoreCase(testData.getBuildType().getName())).findFirst().get();
        new ProjectBuildsPage().runBuildAndWaitBuildSuccess(buildElement);

        soft.assertEquals(buildElement.getBuildStatus().getText(), BUILD_STATE_SUCCESS);
    }
}
