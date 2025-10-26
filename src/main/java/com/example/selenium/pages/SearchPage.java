package com.example.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

/**
 * Page object that handles searching and product interactions.
 * It navigates to a product details page, selects an available size (if present),
 * adds the product to cart and proceeds to checkout.
 */
public class SearchPage extends BasePage {
    @FindBy(id = "search_query_top")
    private WebElement searchBox;

    @FindBy(name = "submit_search")
    private WebElement searchButton;

    @FindBy(xpath = "(//a[@class='product-name'])[1]")
    private WebElement firstProductLink;

    @FindBy(css = "#group_1")
    private WebElement sizeDropdown;
    
    @FindBy(css = "label[for='group_1']")
    private WebElement sizeLabel;
    
    @FindBy(css = "#uniform-group_1 span")
    private WebElement selectedSizeText;

    @FindBy(css = "#color_to_pick_list")
    private WebElement colorPickList;

    @FindBy(css = ".color_pick")
    private WebElement selectedColorText;

    @FindBy(xpath = "//p[@id='add_to_cart']//button")
    private WebElement addToCartButton;

    @FindBy(css = "div.layer_cart_product h2")
    private WebElement addToCartSuccessMessage;

    @FindBy(css = "a[title='Proceed to checkout'].button-medium")
    private WebElement proceedToCheckoutButton;

    private final WebDriverWait wait;
    private final JavascriptExecutor js;
    private String desiredSize;
    private String desiredColor;

    public SearchPage(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        this.js = (JavascriptExecutor) driver;
    }

    public void searchProduct(String productName) {
        wait.until(ExpectedConditions.visibilityOf(searchBox)).clear();
        searchBox.sendKeys(productName);
        wait.until(ExpectedConditions.elementToBeClickable(searchButton)).click();
    }

    public void selectSize(String size) {
        this.desiredSize = size;
    }

    public void selectColor(String color) {
        this.desiredColor = color;
    }

    private void selectColorAndSize() {
        wait.until(ExpectedConditions.visibilityOf(sizeLabel));
        js.executeScript("arguments[0].scrollIntoView(true);", sizeDropdown);

        // Handle color selection first if needed
        if (desiredColor != null && !desiredColor.isEmpty()) {
            try {
                // Wait for color picker list
                wait.until(ExpectedConditions.elementToBeClickable(colorPickList));
                
                // Find and click the desired color
                WebElement colorOption = driver.findElement(
                    By.xpath("//ul[@id='color_to_pick_list']//a[@title='" + desiredColor + "' or contains(@name, '" + desiredColor.toLowerCase() + "')]")
                );
                js.executeScript("arguments[0].scrollIntoView(true);", colorOption);
                Thread.sleep(500);
                js.executeScript("arguments[0].click();", colorOption);
                
                // Wait for the color change to take effect
                wait.until(ExpectedConditions.attributeContains(colorOption, "class", "selected"));
            } catch (Exception e) {
                System.out.println("Warning: Color '" + desiredColor + "' selection failed: " + e.getMessage());
            }
        }
        
        if (desiredSize != null && !desiredSize.isEmpty()) {
            try {
                // Strategy 1: Direct select with uniform span verification
                Select select = new Select(sizeDropdown);
                select.selectByVisibleText(desiredSize);
                wait.until(d -> selectedSizeText.getText().trim().equalsIgnoreCase(desiredSize));
                return;
            } catch (Exception e1) {
                // Try next strategy if this fails
            }

            try {
                // Strategy 2: JavaScript selection with uniform span update
                boolean selected = (Boolean) js.executeScript(
                    "var select = arguments[0];" +
                    "var targetSize = arguments[1].toUpperCase();" +
                    "for(var i = 0; i < select.options.length; i++) {" +
                    "  if(select.options[i].text.trim().toUpperCase() === targetSize) {" +
                    "    select.selectedIndex = i;" +
                    "    select.dispatchEvent(new Event('change'));" +
                    "    return true;" +
                    "  }" +
                    "}" +
                    "return false;",
                    sizeDropdown, desiredSize
                );
                
                if (selected) {
                    wait.until(d -> selectedSizeText.getText().trim().equalsIgnoreCase(desiredSize));
                    return;
                }
            } catch (Exception e2) {
                // Continue to next strategy
            }

            try {
                // Strategy 3: Direct option click with uniform update
                Select select = new Select(sizeDropdown);
                for (WebElement option : select.getOptions()) {
                    if (option.getText().trim().equalsIgnoreCase(desiredSize)) {
                        js.executeScript(
                            "var opt = arguments[0];" +
                            "opt.selected = true;" +
                            "opt.parentElement.selectedIndex = [].indexOf.call(opt.parentElement.options, opt);" +
                            "opt.parentElement.dispatchEvent(new Event('change'));",
                            option
                        );
                        wait.until(d -> selectedSizeText.getText().trim().equalsIgnoreCase(desiredSize));
                        return;
                    }
                }
            } catch (Exception e3) {
                System.out.println("Warning: Size '" + desiredSize + "' selection failed, falling back to first available");
            }
        }

        // Fallback: Select first available size
        selectFirstAvailableSize();
    }

    private void selectFirstAvailableSize() {
        Select select = new Select(sizeDropdown);
        WebElement firstOption = select.getOptions().stream()
            .filter(opt -> opt.isEnabled() && !opt.getText().trim().isEmpty() && !opt.getText().trim().equals("-"))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No enabled size options found"));
        
        String expectedSize = firstOption.getText().trim();
        js.executeScript(
            "var opt = arguments[0];" +
            "opt.selected = true;" +
            "opt.parentElement.selectedIndex = [].indexOf.call(opt.parentElement.options, opt);" +
            "opt.parentElement.dispatchEvent(new Event('change'));",
            firstOption
        );
        
        wait.until(d -> selectedSizeText.getText().trim().equalsIgnoreCase(expectedSize));
    }

    public void selectProductByName(String productName) {
        try {
            // Wait for product to be visible and get its element
            By productLocator = By.xpath("//a[@class='product-name' and normalize-space(text())='" + productName + "']");
            WebElement prod = wait.until(ExpectedConditions.elementToBeClickable(productLocator));
            
            // Scroll into view and pause briefly
            js.executeScript("arguments[0].scrollIntoView({block: 'center', behavior: 'instant'});", prod);
            Thread.sleep(500);
            
            String href = prod.getAttribute("href");
            if (href != null && !href.isEmpty()) {
                driver.get(href);
            } else {
                try {
                    prod.click();
                } catch (Exception e) {
                    js.executeScript("arguments[0].click();", prod);
                }
            }
            
            // Wait for product page elements with increased timeout
            WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(30));
            longWait.until(ExpectedConditions.and(
                ExpectedConditions.or(
                    ExpectedConditions.elementToBeClickable(sizeDropdown),
                    ExpectedConditions.elementToBeClickable(addToCartButton)
                ),
                ExpectedConditions.invisibilityOfElementLocated(By.id("ajax_loader"))
            ));

            // Handle size and color selection
            if ((desiredSize != null && !desiredSize.isEmpty()) || 
                (desiredColor != null && !desiredColor.isEmpty())) {
                selectColorAndSize();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to select product '" + productName + "': " + e.getMessage(), e);
        }
    }

    public void addToCart() {
        try {
            // Extended wait for product page stability
            WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(30));
            
            // Wait for initial page load completion
            longWait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("ajax_loader")));
            
            // Ensure add to cart button is ready
            longWait.until(ExpectedConditions.and(
                ExpectedConditions.elementToBeClickable(addToCartButton),
                ExpectedConditions.visibilityOf(addToCartButton)
            ));
            
            // Scroll to button and ensure it's centered
            js.executeScript("arguments[0].scrollIntoView({block: 'center', behavior: 'instant'});", addToCartButton);
            Thread.sleep(500);

            // Try clicking with multiple strategies
            try {
                addToCartButton.click();
            } catch (Exception e1) {
                try {
                    js.executeScript("arguments[0].click();", addToCartButton);
                } catch (Exception e2) {
                    // Final attempt: Try to force the click
                    js.executeScript(
                        "arguments[0].dispatchEvent(new MouseEvent('click', {" +
                        "   bubbles: true," +
                        "   cancelable: true," +
                        "   view: window" +
                        "}));", addToCartButton
                    );
                }
            }

            // Wait for success message and proceed button
            longWait.until(ExpectedConditions.and(
                ExpectedConditions.visibilityOf(addToCartSuccessMessage),
                ExpectedConditions.elementToBeClickable(proceedToCheckoutButton)
            ));
        } catch (Exception e) {
            throw new RuntimeException("Failed to add product to cart: " + e.getMessage(), e);
        }
    }

    public void proceedToCheckout() {
        wait.until(ExpectedConditions.elementToBeClickable(proceedToCheckoutButton)).click();
    }

    public boolean isProductAdded() {
        return wait.until(ExpectedConditions.visibilityOf(addToCartSuccessMessage))
                .getText().toLowerCase().contains("success");
    }
}