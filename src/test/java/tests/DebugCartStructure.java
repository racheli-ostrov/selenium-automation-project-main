package tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import java.util.List;

public class DebugCartStructure extends BaseTest {

    @Test
    public void debugCartStructure() {
        try {
            System.out.println("\n=== DEBUGGING CART STRUCTURE ===\n");
            
            // Navigate directly to cart
            System.out.println("=== Navigating to Cart ===");
            driver.get("https://www.lastprice.co.il/shopping-cart.asp");
            Thread.sleep(5000);
            
            System.out.println("Cart URL: " + driver.getCurrentUrl());
            System.out.println("Cart Title: " + driver.getTitle());
            
            // Check for various cart item selectors
            System.out.println("\n=== Checking Cart Item Selectors ===");
            String[] itemSelectors = {
                ".cart-item",
                ".cart-row",
                ".basket-item",
                "tr.cart-item",
                "div[class*='cart']",
                "div[class*='item']",
                "tr[class*='item']",
                "tbody tr",
                ".shopping-cart-item",
                ".cartItem"
            };
            
            for (String selector : itemSelectors) {
                try {
                    List<WebElement> items = driver.findElements(By.cssSelector(selector));
                    if (items.size() > 0) {
                        System.out.println("✓ Found " + items.size() + " elements with: " + selector);
                        
                        // Analyze first item if exists
                        if (!items.isEmpty()) {
                            WebElement firstItem = items.get(0);
                            String classes = firstItem.getAttribute("class");
                            String tag = firstItem.getTagName();
                            System.out.println("  Tag: " + tag + ", Classes: " + classes);
                            System.out.println("  Text preview: " + firstItem.getText().substring(0, Math.min(100, firstItem.getText().length())));
                        }
                    }
                } catch (Exception e) {
                    // Skip
                }
            }
            
            // Check for total selectors
            System.out.println("\n=== Checking Total Price Selectors ===");
            String[] totalSelectors = {
                ".cart-total",
                ".order-total",
                ".total",
                ".sum",
                "span[class*='total']",
                "div[class*='total']",
                ".grandTotal",
                "#total"
            };
            
            for (String selector : totalSelectors) {
                try {
                    WebElement total = driver.findElement(By.cssSelector(selector));
                    if (total.isDisplayed()) {
                        System.out.println("✓ Found total with: " + selector);
                        System.out.println("  Text: " + total.getText());
                        System.out.println("  Classes: " + total.getAttribute("class"));
                    }
                } catch (Exception e) {
                    // Skip
                }
            }
            
            // Save cart page source
            System.out.println("\n=== Saving Cart Page Source ===");
            String htmlPath = "output/lastprice_cart_page.html";
            java.nio.file.Files.write(
                java.nio.file.Paths.get(htmlPath),
                driver.getPageSource().getBytes(java.nio.charset.StandardCharsets.UTF_8)
            );
            System.out.println("✓ Saved cart page source to: " + htmlPath);
            
            System.out.println("\n=== CART DEBUG COMPLETE ===\n");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
