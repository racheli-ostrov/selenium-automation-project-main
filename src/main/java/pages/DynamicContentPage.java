package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class DynamicContentPage extends BasePage {

    private By dynamicBlock = By.cssSelector(".dynamic, .content, #content, .article"); // generic

    public DynamicContentPage(WebDriver driver) {
        super(driver);
    }

    public String getDynamicText() {
        try {
            return getText(dynamicBlock);
        } catch (Exception e) {
            return driver.getPageSource().substring(0, Math.min(300, driver.getPageSource().length()));
        }
    }

    public void refreshPage() {
        driver.navigate().refresh();
    }
}
