package com.example.teamcity.ui.elements;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

@Getter
public class BuildElement extends  BasePageElement {
    private SelenideElement name;
    private SelenideElement link;
    private SelenideElement runBuildButton;
    private SelenideElement customBuildRunButton;
    private SelenideElement buildActionsButton;
    private SelenideElement buildExpandButton;
    private SelenideElement buildStatus;

    public BuildElement(SelenideElement element) {
        super(element);
        this.name = find("span[class*='MiddleEllipsis']");
        this.link = find("a");
        this.runBuildButton = find("[data-test='run-build']");
        this.customBuildRunButton = find("#custom-run");
        this.buildActionsButton = find("button[aria-label='Actions']");
        this.buildExpandButton = find ("span[class*='ring-icon-icon']");
        this.buildStatus = find("div[class*='Build__status']");
    }
}
