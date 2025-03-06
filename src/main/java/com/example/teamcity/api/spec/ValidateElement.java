package com.example.teamcity.api.spec;

import com.codeborne.selenide.SelenideElement;
import com.example.teamcity.api.enums.UiErrors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ValidateElement {
    public static void byText(SelenideElement element, UiErrors error) {
        assertThat(element.getText(), equalTo(error.getText()));
    }
}
