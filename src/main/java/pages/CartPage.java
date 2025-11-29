package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import java.util.ArrayList;
import java.util.List;

public class CartPage extends BasePage {

    // Selectors for cart items and their details
    private By cartRows = By.cssSelector(".cart-item, .cart-row, .basket-item"); // generic fallback
    private By nameInRow = By.cssSelector(".product-name, .item-title");
    private By qtyInRow = By.cssSelector("input.qty, .quantity input, .item-qty");
    private By priceInRow = By.cssSelector(".price, .item-price, .product-price");
    private By totalPrice = By.cssSelector(".cart-total, .order-total, .sum");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    // Inner class representing one item in the cart
    public static class CartItem {
        public String name;
        public int qty;
        public String price;
        public String rowTotal;
    }

    // Returns a list of items currently in the cart
    public List<CartItem> getCartItems() {
        waitForPresence(cartRows);
        List<WebElement> rows = driver.findElements(cartRows);
        List<CartItem> items = new ArrayList<>();

        for (WebElement r : rows) {
            CartItem it = new CartItem();
            try { 
                it.name = r.findElement(nameInRow).getText(); 
            } catch (Exception e) { 
                it.name = ""; 
            }

            try {
                WebElement qel = r.findElement(qtyInRow);
                String qval = qel.getAttribute("value");
                it.qty = Integer.parseInt(qval == null || qval.isEmpty() ? "1" : qval);
            } catch (Exception e) { 
                it.qty = 1; 
            }

            try { 
                it.price = r.findElement(priceInRow).getText(); 
            } catch (Exception e) { 
                it.price = ""; 
            }

            // Some sites may have a separate row total, fallback to price if missing
            it.rowTotal = it.price; 
            items.add(it);
        }

        return items;
    }

    // Returns the total price of the cart
    public String getCartTotal() {
        try { 
            return getText(totalPrice); 
        } catch (Exception e) { 
            return ""; 
        }
    }
}
