package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class HomePage extends BasePage {

    private By searchInput = By.cssSelector("input.search");
    private By searchButton = By.cssSelector("button.search-btn");
    private By homeButton = By.cssSelector(".logo, .home-link"); // דוגמה לסלקטור כפתור הבית
    private By categoryLinks = By.cssSelector(".category-link"); // דוגמה לסלקטור קטגוריות
    private By cartButton = By.cssSelector("a.cart-link"); // סלקטור לעגלת הקניות

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
        write(searchInput, text);
        click(searchButton);
    }

    // מעבר לדף הבית
    public void goToHome() {
        click(homeButton);
    }

    // פתיחת קטגוריה לפי שם
    public void openCategoryByName(String categoryName) {
        List<WebElement> categories = driver.findElements(categoryLinks);
        for (WebElement c : categories) {
            if (c.getText().equalsIgnoreCase(categoryName)) {
                click(By.xpath("//a[text()='" + categoryName + "']"));
                return;
            }
        }
        throw new RuntimeException("Category not found: " + categoryName);
    }

    // מעבר לעגלת הקניות
    public void goToCart() {
        click(cartButton);
    }
}
