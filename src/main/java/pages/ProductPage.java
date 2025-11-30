package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;


public class ProductPage extends BasePage {

    // Primary grouped selectors
    private By addToCartPrimary = By.cssSelector("button.add-to-cart, button#add-to-cart-button, button[data-role='add-to-cart']");
    private By quantityInput = By.cssSelector("input.qty, input[name*='qty'], input[id*='qty']");
    private By productName = By.cssSelector("h1, .product-name, .productTitle");
    private By productPrice = By.cssSelector(".price, .product-price, .price-current, span[class*='price'], div[class*='price']");

    public ProductPage(WebDriver driver) {
        super(driver);
    }

    public String getName() {
        try { return getText(productName); } catch (Exception e) { return driver.getTitle(); }
    }

    public String getPrice() {
        try { return getText(productPrice); } catch (Exception e) { return ""; }
    }

    public void setQuantity(int qty) {
        try {
            WebElement q = driver.findElement(quantityInput);
            q.clear();
            q.sendKeys(String.valueOf(qty));
        } catch (Exception e) {
            // If no quantity input exists, rely on multiple add button clicks later.
        }
    }

    public void addToCart() {
        WebElement btn = findAddToCartButton();
        if (btn != null) {
            try {
                btn.click();
                return;
            } catch (Exception e) {
                try {
                    jsClick(btn);
                    return;
                } catch (Exception ignored) {}
            }
        }
        // Final attempt using grouped primary selector if everything else failed.
        try { click(addToCartPrimary); } catch (Exception ignored) {}
    }

    private WebElement findAddToCartButton() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        
        // Priority 1: Look for EXACT Hebrew "הוסף לסל" text (most reliable)
        try {
            System.out.println("  🔍 Searching for add-to-cart button...");
            List<WebElement> allButtons = driver.findElements(By.cssSelector("button, a.button, a[role='button'], input[type='submit'], input[type='button']"));
            
            for (WebElement btn : allButtons) {
                try {
                    String text = btn.getText().trim();
                    String value = btn.getAttribute("value");
                    String ariaLabel = btn.getAttribute("aria-label");
                    String classes = btn.getAttribute("class");
                    
                    // Log what we found
                    if (!text.isEmpty() || value != null || ariaLabel != null) {
                        System.out.println("    Candidate: text='" + text + "' value='" + value + "' aria='" + ariaLabel + "' class='" + classes + "'");
                    }
                    
                    // EXPANDED MATCH: Try to match various cart-related buttons
                    boolean isAddToCart = 
                        (text.contains("הוסף לסל") || text.contains("הוספה לסל") || 
                         text.contains("Add to Cart") || text.contains("ADD TO CART") ||
                         text.contains("Add to Basket") || text.contains("הוסף")) ||
                        (value != null && (value.contains("הוסף לסל") || value.contains("Add to Cart"))) ||
                        (ariaLabel != null && (ariaLabel.contains("הוסף לסל") || ariaLabel.contains("Add to Cart"))) ||
                        (classes != null && classes.contains("addItemToCart"));
                    
                    // EXCLUDE dangerous buttons that navigate away or are comparisons
                    boolean isDangerous = 
                        text.contains("קנה עכשיו") || text.contains("Buy Now") || 
                        text.contains("המשך לתשלום") || text.contains("Checkout") ||
                        text.contains("השווה") || text.contains("Compare") ||
                        text.contains("הצג למעלה");
                    
                    if (isAddToCart && !isDangerous && btn.isDisplayed() && btn.isEnabled()) {
                        System.out.println("  ✓ Found add-to-cart button: '" + text + "'");
                        return btn;
                    }
                } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            System.out.println("  ⚠ Error scanning buttons: " + e.getMessage());
        }

        // Priority 2: Try standard CSS selectors
        String[] safeSelectors = new String[] {
            "button[class*='addItemToCart']",
            "a[class*='addItemToCart']",
            "button[class*='add-to-cart']",
            "button[id*='add-to-cart']",
            "button.add-to-cart",
            "button[data-role='add-to-cart']"
        };
        
        for (String css : safeSelectors) {
            try {
                WebElement candidate = driver.findElement(By.cssSelector(css));
                if (candidate.isDisplayed() && candidate.isEnabled()) {
                    System.out.println("  ✓ Found via selector: " + css);
                    return candidate;
                }
            } catch (Exception ignored) {}
        }

        System.out.println("  ⚠ No add-to-cart button found!");
        System.out.println("  ℹ Note: LastPrice may require login or use a different cart system");
        return null;
    }
}
