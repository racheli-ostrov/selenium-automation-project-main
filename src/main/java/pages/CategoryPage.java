package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class CategoryPage extends BasePage {
    // product links list
    private By productLinks = By.cssSelector("a.product-item-link, div.product-card a");

    public CategoryPage(WebDriver driver) {
        super(driver);
    }

    public void openProductByIndex(int index) {
        waitForPresence(productLinks);
        var list = driver.findElements(productLinks);
        if (index < 0 || index >= list.size()) {
            throw new RuntimeException("Product index out of bounds");
        }
        list.get(index).click();
    }
}
