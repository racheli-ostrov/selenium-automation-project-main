package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class HomePage extends BasePage {

    // Base selectors for search – will attempt multiple fallbacks
    private By searchInputPrimary = By.cssSelector("input.search");
    private By searchButtonPrimary = By.cssSelector("button.search-btn");
    private By searchIcon = By.cssSelector(".search-icon, button.search-toggle, a.search-toggle, .header-search, [class*='search'] svg, [class*='search'] i");
    private By anySearchInput = By.cssSelector("input[type='search'], input[name*='search'], input[id*='search'], input[placeholder*='חיפוש'], input[placeholder*='Search'], input.search, .search input");
    private By homeButton = By.cssSelector(".logo, .home-link, a[href='/']");
    // Try multiple category selectors
    private By categoryLinks = By.cssSelector("a[href*='category'], a[href*='cat'], nav a, .menu a, .category a, .nav-item a, .prodLink");
    private By cartButton = By.cssSelector("a.cart-link, a[href*='cart'], .cart, .basket, #cart");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    // לחיצה על אלמנט לפי By
    public void click(By locator) {
        waitForPresence(locator).click();
    }

    // כתיבה לתוך input
    public void write(By locator, String text) {
        waitForPresence(locator).sendKeys(text);
    }

    // קבלת טקסט מאלמנט
    public String getText(By locator) {
        return waitForPresence(locator).getText();
    }

    // מחכה לנוכחות אלמנט ומחזיר אותו
    public WebElement waitForPresence(By locator) {
        return new WebDriverWait(driver, Duration.ofSeconds(10))
               .until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    // חיפוש באתר
    public void search(String text) {
        // Try to reveal search box if hidden behind icon
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {}

        WebElement input = null;
        // Try direct primary locator
        try { input = waitForPresence(searchInputPrimary); } catch (Exception e) { /* ignore */ }

        if (input == null) {
            // Try clicking search icon candidates to reveal input
            List<WebElement> icons = driver.findElements(searchIcon);
            for (WebElement ic : icons) {
                try {
                    if (ic.isDisplayed()) {
                        ic.click();
                        Thread.sleep(500);
                        break;
                    }
                } catch (Exception ignored) {}
            }
            // Attempt any generic search input
            try { input = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.presenceOfElementLocated(anySearchInput)); } catch (Exception e) { /* ignore */ }
        }

        if (input == null) {
            throw new RuntimeException("Could not locate search input on home page");
        }

        try {
            input.clear();
            input.sendKeys(text);
        } catch (Exception e) {
            throw new RuntimeException("Unable to type into search input: " + e.getMessage());
        }

        // Try pressing ENTER
        try { input.sendKeys(org.openqa.selenium.Keys.ENTER); return; } catch (Exception ignored) {}

        // Fallback: click a dedicated search button if exists
        try { click(searchButtonPrimary); } catch (Exception ignored) {}
    }

    // מעבר לדף הבית
    public void goToHome() {
        click(homeButton);
    }

    // Print all available categories - for debugging
    public void printAllCategories() {
        try {
            List<WebElement> categories = driver.findElements(categoryLinks);
            System.out.println("=== Found " + categories.size() + " potential category links ===");
            int count = 0;
            for (WebElement c : categories) {
                try {
                    String text = c.getText();
                    String href = c.getAttribute("href");
                    if (text != null && !text.trim().isEmpty()) {
                        System.out.println((++count) + ". Text: '" + text + "' | Href: " + href);
                    }
                } catch (Exception e) {
                    // skip
                }
            }
        } catch (Exception e) {
            System.out.println("Could not find category links: " + e.getMessage());
        }
    }

    // פתיחת קטגוריה לפי שם - גרסה משופרת
    public void openCategoryByName(String categoryName) {
        try {
            Thread.sleep(2000); // wait for page to fully load
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        List<WebElement> categories = driver.findElements(categoryLinks);
        System.out.println("Searching for category: '" + categoryName + "' among " + categories.size() + " links");
        
        for (WebElement c : categories) {
            try {
                String text = c.getText();
                if (text != null && !text.trim().isEmpty()) {
                    System.out.println("  Checking: '" + text + "'");
                    if (text.trim().contains(categoryName) || categoryName.contains(text.trim())) {
                        System.out.println("  ✓ Match found! Clicking...");
                        c.click();
                        return;
                    }
                }
            } catch (Exception e) {
                // Continue to next element
            }
        }
        throw new RuntimeException("Category not found: " + categoryName);
    }

    // מעבר לעגלת הקניות
    public void goToCart() {
        click(cartButton);
    }
}
