package com.example.teamcity.ui.pages.admin;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.example.teamcity.ui.pages.CreateBasePage;
import lombok.Getter;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.page;


public class CreateProjectPage extends CreateBasePage {
    private static final String PROJECT_SHOW_MODE = "createProjectMenu";

    private SelenideElement projectNameInput = $("#projectName");
    private SelenideElement nameInput = $("#name");
    protected SelenideElement projectIdInput = $("#externalId");
    protected SelenideElement manuallyOption = $("[href='#createManually']");
    protected SelenideElement createButton = $(Selectors.byAttribute("value", "Create"));

    @Getter
    public SelenideElement projectNameInputError = $("#errorName");

    public static CreateProjectPage open(String projectId) {
        return Selenide.open(CREATE_URL.formatted(projectId, PROJECT_SHOW_MODE), CreateProjectPage.class);
    }

    public CreateProjectPage() {
        manuallyOption.shouldBe(Condition.clickable, BASE_WAITING);
    }

    public CreateProjectPage createForm(String url) {
        baseCreateForm(url);
        return this;
    }

    public void setupProject(String projectName, String buildTypeName) {
        projectNameInput.val(projectName);
        buildTypeNameInput.val(buildTypeName);
        submitButton.click();
    }

    public CreateProjectPage setupProjectManually(String projectName, String buildTypeName) {
        manuallyOption.click();
        nameInput.val(projectName);
        projectIdInput.val(buildTypeName);
        createButton.click();
        return this;
    }
}
