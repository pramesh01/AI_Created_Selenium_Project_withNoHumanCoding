package com.example.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ExamplePage extends BasePage {
    @FindBy(id = "example-element")
    private WebElement exampleElement;

    public ExamplePage(WebDriver driver) {
        super(driver);
    }

    public void performExampleAction() {
        exampleElement.click();
    }
}