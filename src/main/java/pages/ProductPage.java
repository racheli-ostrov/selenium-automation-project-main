package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;


public class ProductPage extends BasePage {

    private By addToCartBtn = By.cssSelector("button.add-to-cart, button#add-to-cart-button, button[data-role='add-to-cart']");
    private By quantityInput = By.cssSelector("input.qty, input[name*='qty'], input[id*='qty']");
    private By productName = By.cssSelector("h1, .product-name, .productTitle");
    private By productPrice = By.cssSelector(".price, .product-price, .price-current");

    public ProductPage(WebDriver driver) {
        super(driver);
    }

    public String getName() {
        try { return getText(productName); } catch (Exception e) { return driver.getTitle(); }
    }

    public String getPrice() {
        return getText(productPrice);
    }

    public void setQuantity(int qty) {
        try {
            WebElement q = driver.findElement(quantityInput);
            q.clear();
            q.sendKeys(String.valueOf(qty));
        } catch (Exception e) {
            // if no quantity input, add multiple times
            for (int i = 1; i < qty; i++) {
                click(addToCartBtn);
                try { Thread.sleep(500); } catch (InterruptedException ex) {}
            }
            return;
        }
    }

    public void addToCart() {
        click(addToCartBtn);
    }
}
