package com.example.teamcity;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.asserts.SoftAssert;

public class BaseTest {

    protected SoftAssert soft;

    @BeforeMethod(alwaysRun = true)
    public void beforeTest() {
        soft = new SoftAssert();
    }

    @AfterMethod(alwaysRun = true)
    public void  afterTest() {
        soft.assertAll();
    }
}
