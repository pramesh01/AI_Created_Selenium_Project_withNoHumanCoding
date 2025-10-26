package com.example.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class LoginPage extends BasePage {
    @FindBy(css = "a.login")
    private WebElement signInLink;

    @FindBy(id = "email")
    private WebElement emailInput;

    @FindBy(id = "passwd")
    private WebElement passwordInput;

    @FindBy(id = "SubmitLogin")
    private WebElement signInButton;

    @FindBy(css = "a.account")
    private WebElement accountLink;

    private final WebDriverWait wait;

    public LoginPage(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void clickSignIn() {
        wait.until(ExpectedConditions.elementToBeClickable(signInLink)).click();
    }

    public void enterEmail(String email) {
        wait.until(ExpectedConditions.visibilityOf(emailInput)).sendKeys(email);
    }

    public void enterPassword(String password) {
        wait.until(ExpectedConditions.visibilityOf(passwordInput)).sendKeys(password);
    }

    public void clickSubmitLogin() {
        wait.until(ExpectedConditions.elementToBeClickable(signInButton)).click();
    }

    public boolean isLoggedIn() {
        return wait.until(ExpectedConditions.visibilityOf(accountLink)).isDisplayed();
    }

    public String getLoggedInUsername() {
        return wait.until(ExpectedConditions.visibilityOf(accountLink)).getText().trim();
    }
}