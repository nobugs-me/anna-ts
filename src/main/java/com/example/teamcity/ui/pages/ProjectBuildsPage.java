package com.example.teamcity.ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.example.teamcity.ui.elements.BuildElement;

import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ProjectBuildsPage extends BasePage {
    private static final String PROJECT_BUILDS_URL = "/project/%s?mode=builds";
    private ElementsCollection buildsElements = $$("[class*='BuildTypes__item']");
    private SelenideElement content = $("[class*='ModeSwitch__header']");

    public ProjectBuildsPage() {
        content.shouldBe(Condition.visible, BASE_WAITING);
    }

    public static ProjectBuildsPage open(String projectId) {
        return Selenide.open(PROJECT_BUILDS_URL.formatted(projectId), ProjectBuildsPage.class);
    }

    public List<BuildElement> getBuilds() {
        return generatePageElements(buildsElements, BuildElement::new);
    }

    public void runBuildAndWaitBuildSuccess(BuildElement build) {
        build.getRunBuildButton().click();
        build.getBuildStatus().shouldBe(Condition.visible, BASE_WAITING);
        build.getBuildStatus().shouldNotBe(Condition.exactText("Waiting to start checking for changes"), BASE_WAITING);
        build.getBuildStatus().shouldNotBe(Condition.exactText("Running"), BASE_WAITING);
    }
}
