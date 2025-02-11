package com.example.teamcity;

import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.spec.Specifications;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.asserts.SoftAssert;

public class BaseTest {

    protected SoftAssert soft;

    protected CheckedRequests superUserCheckedRequests = new CheckedRequests(Specifications.superUserAuthSpec());

    @BeforeMethod(alwaysRun = true)
    public void beforeTest() {
        soft = new SoftAssert();
    }

    @AfterMethod(alwaysRun = true)
    public void  afterTest() {
        soft.assertAll();
    }
}
