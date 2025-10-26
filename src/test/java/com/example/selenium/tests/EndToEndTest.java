package com.example.selenium.tests;

import com.example.selenium.pages.LoginPage;
import com.example.selenium.pages.SearchPage;
import com.example.selenium.pages.CartPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EndToEndTest extends BaseTest {

    @Test
    public void testEndToEndPurchaseFlow() {
        // Initialize page objects
        LoginPage loginPage = new LoginPage(driver);
        SearchPage searchPage = new SearchPage(driver);
        CartPage cartPage = new CartPage(driver);

        // Step 1: Login
        driver.get("http://www.automationpractice.pl/index.php");
        loginPage.clickSignIn();
        loginPage.enterEmail("bloggerdelhi123@gmail.com");
        loginPage.enterPassword("Password");
        loginPage.clickSubmitLogin();
        Assert.assertTrue(loginPage.isLoggedIn(), "User should be logged in successfully");

        // Step 2: Search and select product (Printed Summer Dress, size S, color White)
        searchPage.searchProduct("Printed Summer Dress");
        searchPage.selectProductByName("Printed Summer Dress");
        searchPage.selectSize("S");
        searchPage.selectColor("White");
        searchPage.addToCart();
        Assert.assertTrue(searchPage.isProductAdded(), "Product should be added to cart successfully");
        searchPage.proceedToCheckout();

        // Step 3: Cart and Checkout process
        String totalPrice = cartPage.getTotalPrice();
        Assert.assertFalse(totalPrice.isEmpty(), "Total price should not be empty");
        
        // Proceed through checkout steps
        cartPage.proceedToCheckout();
        cartPage.proceedToAddressCheckout();
        cartPage.confirmAddress();
        cartPage.acceptTermsAndProceed();
        cartPage.selectBankWirePayment();
        cartPage.confirmOrder();

        // Verify order confirmation
        String confirmationMessage = cartPage.getOrderConfirmationMessage();
        Assert.assertTrue(confirmationMessage.contains("Your order on My Store is complete"),
                "Order should be completed successfully");
    }
}