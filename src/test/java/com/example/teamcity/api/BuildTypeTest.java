package com.example.teamcity.api;

import com.example.teamcity.api.generators.StepGenerator;
import com.example.teamcity.api.models.*;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.UncheckedRequests;
import com.example.teamcity.api.requests.unchecked.UncheckedBase;
import com.example.teamcity.api.spec.Specifications;
import com.example.teamcity.api.spec.ValidationResponseSpecifications;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.teamcity.api.enums.Endpoint.*;
import static com.example.teamcity.api.generators.TestDataGenerator.generate;
import static io.qameta.allure.Allure.step;

@Test(groups = "Regression")
public class BuildTypeTest extends BaseApiTest {

    @Test(description = "User should be able to create build type", groups = {"Positive", "CRUD"})
    public void userCreatesBuildTypeTest() {

        superUserCheckedRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());
        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        var createdBuildType = userCheckRequests.<BuildType>getRequest(BUILD_TYPES).read(testData.getBuildType().getId());

        soft.assertEquals(testData.getBuildType().getName(), createdBuildType.getName());
    }

    @Test(description = "User should not be able to create two build types with the same id", groups = {"Negative", "CRUD"})
    public void userCreatesTwoBuildTypesWithTheSameIdTest() {
        var buildTypeWithSameId = generate(Arrays.asList(testData.getProject()), BuildType.class, testData.getBuildType().getId());

        superUserCheckedRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());
        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        new UncheckedBase(Specifications.authSpec(testData.getUser()), BUILD_TYPES)
                .create(buildTypeWithSameId)
                .then().spec(ValidationResponseSpecifications.checkUserCannotCreateBuildTypeWithSameIdAsExisting(buildTypeWithSameId.getId()));
    }

    @Test(description = "Project admin should be able to create build type for their project", groups = {"Positive", "CRUD"})
    public void projectAdminCreatesBuildTypeTest() {
        User developerRoleUser = testData.getUser();
        developerRoleUser.getRoles().getRole().getFirst().setRoleId("PROJECT_ADMIN");
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(developerRoleUser));

        step("Create user with PROJECT_ADMIN role in project", () -> {
        superUserCheckedRequests.getRequest(USERS).create(developerRoleUser);});

        step("Create project by user", () -> {
        userCheckRequests.<Project>getRequest(PROJECTS).create(testData.getProject());});

        step("Create build type for project by user (PROJECT_ADMIN)", () -> {
        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());
        });

        step("Check build type was created successfully", () -> {
        var createdBuildType = userCheckRequests.<BuildType>getRequest(BUILD_TYPES).read(testData.getBuildType().getId());
        soft.assertEquals(testData.getBuildType().getName(), createdBuildType.getName());});
    }

    @Test(description = "Project admin should not be able to create build type for another user's project", groups = {"Negative", "CRUD"})
    public void projectAdminCreatesBuildTypeForAnotherUserProjectTest() {
        User developerRoleUser1 = testData.getUser();
        User developerRoleUser2 = generate(User.class);

        step("Create user with PROJECT_ADMIN role in project", () -> {
        Project user1Project = (Project) superUserCheckedRequests.getRequest(PROJECTS).create(testData.getProject());
        developerRoleUser1.getRoles().getRole().getFirst().setRoleId("PROJECT_ADMIN");
        developerRoleUser1.getRoles().getRole().getFirst().setScope("p:"+user1Project.getId());
        superUserCheckedRequests.getRequest(USERS).create(developerRoleUser1);});

        step("Create user with PROJECT_ADMIN role in project", () -> {
        Project user2Project = (Project) superUserCheckedRequests.getRequest(PROJECTS).create(generate(Project.class));
        developerRoleUser2.getRoles().getRole().getFirst().setRoleId("PROJECT_ADMIN");
        developerRoleUser2.getRoles().getRole().getFirst().setScope("p:"+user2Project.getId());
        superUserCheckedRequests.getRequest(USERS).create(developerRoleUser2);});

        step("Create buildType for project1 by user2 and check buildType was not created with forbidden code", () -> {
        BuildType buildType = testData.getBuildType();

        new UncheckedRequests(Specifications.authSpec(developerRoleUser2)).getRequest(BUILD_TYPES).create(buildType)
                    .then().spec(ValidationResponseSpecifications.checkUserCannotEditProjectWithInsufficientRights(buildType.getId()));});
    //тут не переноситься в месседже почему-то - подскажи плиз что не так
    }

    @Test(description = "User should be able to run build type and check its status", groups = {"Positive", "CRUD"})
    public void userShouldBeAbleToRunBuildTypeAndCheckItsStatus() {
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

        var createdBuildType = superUserCheckedRequests.<BuildType>getRequest(BUILD_TYPES).read(buildType.getId());

        BuildType buildTypeToRun = BuildType.builder().id(createdBuildType.getId()).build();
        Build buildToRun = Build.builder().buildType(buildTypeToRun).build();
        BuildType buildQueued = (BuildType) superUserCheckedRequests.getRequest(BUILD_QUEUE).create(buildToRun);

        Build buildState = (Build) superUserCheckedRequests.getRequest(BUILD).read(buildQueued.getId());

        soft.assertTrue(buildState.getState().equals("queued")|buildState.getState().equals("running"));
    }
}
