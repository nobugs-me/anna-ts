package com.example.teamcity.ui.elements;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

@Getter
public class SideBarProjectElement extends  BasePageElement {
    private SelenideElement name;
    private SelenideElement link;
    private SelenideElement button;

    public SideBarProjectElement(SelenideElement element) {
        super(element);
        this.name = find("span[class*='ProjectsTreeItem__name']");
        this.link = find("a");
        this.button = find("button");
    }
}
