package com.example.selenium.tests;

import com.example.selenium.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test
    public void testSuccessfulLogin() {
        // Initialize page object
        LoginPage loginPage = new LoginPage(driver);

        // Navigate to the website
        driver.get("http://www.automationpractice.pl/index.php");

        // Perform login steps
        loginPage.clickSignIn();
        loginPage.enterEmail("bloggerdelhi123@gmail.com");
        loginPage.enterPassword("Password");
        loginPage.clickSubmitLogin();

        // Verify successful login
        Assert.assertTrue(loginPage.isLoggedIn(), "User should be logged in successfully");
    }
}