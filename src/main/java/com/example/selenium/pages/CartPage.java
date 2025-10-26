package com.example.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class CartPage extends BasePage {
    @FindBy(css = ".cart_navigation a[title='Proceed to checkout']")
    private WebElement proceedToCheckoutButton;

    @FindBy(css = "#total_price")
    private WebElement totalPrice;

    @FindBy(css = ".standard-checkout")
    private WebElement standardCheckoutButton;

    @FindBy(name = "processAddress")
    private WebElement processAddressButton;

    @FindBy(name = "cgv")
    private WebElement termsCheckbox;

    @FindBy(name = "processCarrier")
    private WebElement processCarrierButton;

    @FindBy(css = ".bankwire")
    private WebElement payByBankWireButton;

    @FindBy(css = "#cart_navigation button")
    private WebElement confirmOrderButton;

    @FindBy(css = ".cheque-indent strong")
    private WebElement orderConfirmationMessage;

    private final WebDriverWait wait;

    public CartPage(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public String getTotalPrice() {
        return wait.until(ExpectedConditions.visibilityOf(totalPrice)).getText();
    }

    public void proceedToCheckout() {
        wait.until(ExpectedConditions.elementToBeClickable(proceedToCheckoutButton)).click();
    }

    public void proceedToAddressCheckout() {
        // Try multiple fallbacks for the checkout button on summary -> address transition
        try {
            wait.until(ExpectedConditions.elementToBeClickable(standardCheckoutButton)).click();
            return;
        } catch (Exception ignored) {}
        try {
            wait.until(ExpectedConditions.elementToBeClickable(proceedToCheckoutButton)).click();
            return;
        } catch (Exception ignored) {}
        try {
            org.openqa.selenium.WebElement btn = driver.findElement(org.openqa.selenium.By.xpath("//a[contains(., 'Proceed to checkout') or contains(., 'Proceed to checkout >') or contains(., 'Proceed to checkout »')]") );
            wait.until(ExpectedConditions.elementToBeClickable(btn)).click();
            return;
        } catch (Exception e) {
            throw new RuntimeException("Unable to find proceed-to-address checkout button", e);
        }
    }

    public void confirmAddress() {
        // processAddress button can be rendered differently across themes/steps.
        // Try multiple fallbacks for reliability.
        try {
            wait.until(ExpectedConditions.elementToBeClickable(processAddressButton)).click();
            return;
        } catch (Exception ignored) {
        }

        try {
            org.openqa.selenium.WebElement btn = driver.findElement(org.openqa.selenium.By.cssSelector("button[name='processAddress']"));
            wait.until(ExpectedConditions.elementToBeClickable(btn)).click();
            return;
        } catch (Exception ignored) {
        }

        try {
            // fallback: find any button with text 'Proceed to checkout' on the address step
            org.openqa.selenium.WebElement btn = driver.findElement(org.openqa.selenium.By.xpath("//button[contains(., 'Proceed to checkout') or contains(., 'Proceed to checkout >')]") );
            wait.until(ExpectedConditions.elementToBeClickable(btn)).click();
            return;
        } catch (Exception e) {
            throw new RuntimeException("Unable to find the address confirm button (processAddress)", e);
        }
    }

    public void acceptTermsAndProceed() {
        wait.until(ExpectedConditions.elementToBeClickable(termsCheckbox)).click();
        try {
            wait.until(ExpectedConditions.elementToBeClickable(processCarrierButton)).click();
            return;
        } catch (Exception ignored) {
        }

        try {
            org.openqa.selenium.WebElement btn = driver.findElement(org.openqa.selenium.By.cssSelector("button[name='processCarrier']"));
            wait.until(ExpectedConditions.elementToBeClickable(btn)).click();
            return;
        } catch (Exception e) {
            throw new RuntimeException("Unable to proceed on shipping/carrier step (processCarrier)", e);
        }
    }

    public void selectBankWirePayment() {
        wait.until(ExpectedConditions.elementToBeClickable(payByBankWireButton)).click();
    }

    public void confirmOrder() {
        wait.until(ExpectedConditions.elementToBeClickable(confirmOrderButton)).click();
    }

    public String getOrderConfirmationMessage() {
        return wait.until(ExpectedConditions.visibilityOf(orderConfirmationMessage)).getText();
    }
}