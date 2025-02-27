package com.example.teamcity.ui.pages.admin;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.example.teamcity.ui.pages.CreateBasePage;

import static com.codeborne.selenide.Selenide.$;

public class CreateBuildConfigurationPage extends CreateBasePage {
    private static final String BUILD_TYPE_SHOW_MODE = "createBuildTypeMenu";

    private SelenideElement buildConfigurationNameInput = $("#buildTypeName");
    private SelenideElement buildConfigurationNameInputError = $("#error_buildTypeName");

    public static CreateBuildConfigurationPage open(String projectId) {
        return Selenide.open(CREATE_URL.formatted(projectId, BUILD_TYPE_SHOW_MODE), CreateBuildConfigurationPage.class);
    }

    public CreateBuildConfigurationPage createForm(String url) {
        baseCreateForm(url);
        return this;
    }

    public CreateBuildConfigurationPage setBuildConfigurationName(String buildName) {
        buildConfigurationNameInput.val(buildName);
        submitButton.click();
        return this;
    }

    public String getBuildConfigurationNameErrorMessage() {
        return buildConfigurationNameInputError.getText();
    }
}
