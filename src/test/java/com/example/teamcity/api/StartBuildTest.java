package com.example.teamcity.api;

import com.example.teamcity.api.generators.StepGenerator;
import com.example.teamcity.api.models.*;
import com.example.teamcity.api.requests.checked.CheckedBase;
import com.example.teamcity.api.spec.Specifications;
import com.example.teamcity.common.WireMock;
import io.qameta.allure.Feature;
import org.apache.http.HttpStatus;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static com.example.teamcity.api.enums.Endpoint.*;
import static com.github.tomakehurst.wiremock.client.WireMock.post;

@Feature("Start build")
public class StartBuildTest extends BaseApiTest {

    @BeforeMethod
    public void setupWireMockServer() {
        var fakeBuild = Build.builder()
                .state("finished")
                .status("SUCCESS")
                .build();

        WireMock.setupServer(post(BUILD_QUEUE.getUrl()), HttpStatus.SC_OK, fakeBuild);
    }

    @Test(description = "User should be able to start build (with WireMock)", groups = {"Regression"})
    public void userStartsBuildWithWireMockTest() {
        var checkedBuildQueueRequest = new CheckedBase<Build>(Specifications
                .mockSpec(), BUILD_QUEUE);

        var build = checkedBuildQueueRequest.create(Build.builder()
                .buildType(testData.getBuildType())
                .build());

        soft.assertEquals(build.getState(), "finished");
        soft.assertEquals(build.getStatus(), "SUCCESS");
    }

    @Test(description = "User should be able to run build type and check its mocked status", groups = {"Positive", "CRUD"})
    public void userShouldBeAbleToRunBuildTypeAndCheckItsMockedStatus() {
        superUserCheckedRequests.getRequest(USERS).create(testData.getUser());

        superUserCheckedRequests.<Project>getRequest(PROJECTS).create(testData.getProject());
        var buildType = testData.getBuildType();

        Step simpleRunnerStep = Step.builder().type("simpleRunner").name("Print hello world").Properties(StepGenerator.generateSimpleRunner("Hello World!")).build();
        List<Step> buildStep = new ArrayList<>();
        buildStep.add(simpleRunnerStep);
        Steps steps = Steps.builder().steps(buildStep).build();
        buildType.setName("Print hello world");
        buildType.setSteps(steps);

        superUserCheckedRequests.getRequest(BUILD_TYPES).create(buildType);

        //var createdBuildType = superUserCheckedRequests.<BuildType>getRequest(BUILD_TYPES).read(buildType.getId());

        //BuildType buildTypeToRun = BuildType.builder().id(createdBuildType.getId()).build();
        //Build buildToRun = Build.builder().buildType(buildTypeToRun).build();

        //Build buildQueued = (Build) superUserCheckedRequests.getRequest(BUILD_QUEUE).create(buildToRun);

        //Build buildState = (Build) superUserCheckedRequests.getRequest(BUILD).read(fakeBuild.getId());
//закоментила как я понимаю ту часть которая мокируется

        var checkedBuildQueueRequest = new CheckedBase<Build>(Specifications
                .mockSpec(), BUILD_QUEUE);

        var build = checkedBuildQueueRequest.create(Build.builder()
                .buildType(testData.getBuildType())
                .build());

        soft.assertTrue(build.getState().equals("finished")|build.getStatus().equals("SUCCESS"));
    }

    @AfterMethod(alwaysRun = true)
    public void stopWireMockServer() {
        WireMock.stopServer();
    }
}
