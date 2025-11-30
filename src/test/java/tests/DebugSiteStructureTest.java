package tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;
import pages.HomePage;

import java.util.List;

public class DebugSiteStructureTest extends BaseTest {

    @Test
    public void debugSiteStructure() {
        try {
            Thread.sleep(5000); // Wait for page to fully load
            
            System.out.println("\n=== Debugging LastPrice Site Structure ===");
            System.out.println("Current URL: " + driver.getCurrentUrl());
            System.out.println("Page Title: " + driver.getTitle());
            
            HomePage homePage = new HomePage(driver);
            
            // Print all categories found
            homePage.printAllCategories();
            
            // Try to find navigation elements
            System.out.println("\n=== Looking for Navigation Elements ===");
            String[] navSelectors = {
                "nav", "nav a", ".navbar", ".navigation", ".menu", ".header",
                "a[href*='category']", "a[href*='cat']", ".nav-item", ".menu-item"
            };
            
            for (String selector : navSelectors) {
                try {
                    List<WebElement> elements = driver.findElements(By.cssSelector(selector));
                    if (!elements.isEmpty()) {
                        System.out.println("\nSelector '" + selector + "' found " + elements.size() + " elements:");
                        int count = 0;
                        for (WebElement el : elements) {
                            if (count++ >= 10) break; // Show max 10
                            try {
                                String text = el.getText();
                                if (text != null && !text.trim().isEmpty() && text.length() < 100) {
                                    System.out.println("  - " + text.substring(0, Math.min(text.length(), 50)));
                                }
                            } catch (Exception e) {
                                // skip
                            }
                        }
                    }
                } catch (Exception e) {
                    // skip
                }
            }
            
            // Try to find search elements
            System.out.println("\n=== Looking for Search Elements ===");
            String[] searchSelectors = {
                "input[type='search']", "input[placeholder*='חיפוש']", "input[name*='search']",
                ".search-input", "#search", "input.search", "[role='search']"
            };
            
            for (String selector : searchSelectors) {
                try {
                    List<WebElement> elements = driver.findElements(By.cssSelector(selector));
                    if (!elements.isEmpty()) {
                        System.out.println("Search selector '" + selector + "' found " + elements.size() + " elements");
                    }
                } catch (Exception e) {
                    // skip
                }
            }
            
            // Try to find cart elements
            System.out.println("\n=== Looking for Cart Elements ===");
            String[] cartSelectors = {
                "a[href*='cart']", "a[href*='basket']", ".cart", ".basket",
                "#cart", "[data-cart]", ".shopping-cart", "a[title*='עגלה']"
            };
            
            for (String selector : cartSelectors) {
                try {
                    List<WebElement> elements = driver.findElements(By.cssSelector(selector));
                    if (!elements.isEmpty()) {
                        System.out.println("Cart selector '" + selector + "' found " + elements.size() + " elements");
                        for (WebElement el : elements) {
                            try {
                                String text = el.getText();
                                String href = el.getAttribute("href");
                                if (text != null && !text.trim().isEmpty()) {
                                    System.out.println("  Text: " + text + " | Href: " + href);
                                }
                            } catch (Exception e) {
                                // skip
                            }
                        }
                    }
                } catch (Exception e) {
                    // skip
                }
            }
            
            System.out.println("\n=== Debug Complete ===");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
