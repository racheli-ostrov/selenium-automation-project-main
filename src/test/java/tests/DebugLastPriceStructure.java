package tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;
import pages.HomePage;

import java.util.List;

public class DebugLastPriceStructure extends BaseTest {

    @Test
    public void debugSearchResultsAndCart() {
        try {
            System.out.println("\n=== DEBUGGING LASTPRICE STRUCTURE ===\n");
            
            HomePage homePage = new HomePage(driver);
            
            // Step 1: Check home page structure
            System.out.println("=== Step 1: Home Page Structure ===");
            Thread.sleep(3000);
            
            // Print page source snippet
            String pageSource = driver.getPageSource();
            System.out.println("Page title: " + driver.getTitle());
            System.out.println("Current URL: " + driver.getCurrentUrl());
            
            // Step 2: Try to search
            System.out.println("\n=== Step 2: Attempting Search for 'טלוויזיה' ===");
            try {
                homePage.search("טלוויזיה");
                Thread.sleep(5000); // Wait for results
                
                System.out.println("After search - URL: " + driver.getCurrentUrl());
                System.out.println("After search - Title: " + driver.getTitle());
                
            } catch (Exception e) {
                System.out.println("⚠ Search failed: " + e.getMessage());
            }
            
            // Step 3: Analyze search results structure
            System.out.println("\n=== Step 3: Analyzing Search Results Page ===");
            
            // Try various link selectors
            String[] linkSelectors = {
                "a[href*='product']",
                "a[href*='item']",
                "a[href*='p/']",
                "a.product",
                "div.product a",
                "div[class*='product'] a",
                "div[class*='item'] a",
                ".result a",
                ".results a",
                "a[class*='product']",
                "a[class*='item']"
            };
            
            for (String selector : linkSelectors) {
                try {
                    List<WebElement> links = driver.findElements(By.cssSelector(selector));
                    if (!links.isEmpty()) {
                        System.out.println("\n✓ Found " + links.size() + " links with selector: " + selector);
                        
                        // Show first 3 links
                        for (int i = 0; i < Math.min(3, links.size()); i++) {
                            WebElement link = links.get(i);
                            try {
                                String text = link.getText().trim();
                                String href = link.getAttribute("href");
                                String classes = link.getAttribute("class");
                                
                                if (!text.isEmpty() || href != null) {
                                    System.out.println("  Link " + (i+1) + ":");
                                    System.out.println("    Text: " + (text.isEmpty() ? "(empty)" : text));
                                    System.out.println("    Href: " + href);
                                    System.out.println("    Classes: " + classes);
                                }
                            } catch (Exception e) {
                                // Skip stale elements
                            }
                        }
                    }
                } catch (Exception e) {
                    // Selector didn't work
                }
            }
            
            // Check for specific product card structures
            System.out.println("\n=== Checking for Product Card Structures ===");
            String[] cardSelectors = {
                "div[class*='product']",
                "div[class*='item']",
                "div[class*='card']",
                "article",
                "li[class*='product']",
                "li[class*='item']"
            };
            
            for (String selector : cardSelectors) {
                try {
                    List<WebElement> cards = driver.findElements(By.cssSelector(selector));
                    if (cards.size() > 0) {
                        System.out.println("\n✓ Found " + cards.size() + " elements with: " + selector);
                        
                        // Analyze first card structure
                        if (!cards.isEmpty()) {
                            WebElement firstCard = cards.get(0);
                            String classes = firstCard.getAttribute("class");
                            System.out.println("  First element classes: " + classes);
                            
                            // Look for nested links
                            List<WebElement> nestedLinks = firstCard.findElements(By.tagName("a"));
                            if (!nestedLinks.isEmpty()) {
                                System.out.println("  Contains " + nestedLinks.size() + " <a> tags");
                                WebElement firstLink = nestedLinks.get(0);
                                System.out.println("    First link href: " + firstLink.getAttribute("href"));
                                System.out.println("    First link text: " + firstLink.getText().trim());
                            }
                        }
                    }
                } catch (Exception e) {
                    // Skip
                }
            }
            
            // Step 4: Try to navigate to cart
            System.out.println("\n=== Step 4: Attempting Cart Navigation ===");
            
            String[] cartSelectors = {
                "a[href*='cart']",
                "a[href*='Cart']",
                "a[href*='basket']",
                "a[href*='Basket']",
                ".cart",
                "#cart",
                ".basket",
                "button[class*='cart']",
                "a[class*='cart']"
            };
            
            boolean foundCart = false;
            for (String selector : cartSelectors) {
                try {
                    WebElement cartLink = driver.findElement(By.cssSelector(selector));
                    if (cartLink.isDisplayed()) {
                        System.out.println("✓ Found cart element with: " + selector);
                        System.out.println("  Text: " + cartLink.getText());
                        System.out.println("  Href: " + cartLink.getAttribute("href"));
                        System.out.println("  Classes: " + cartLink.getAttribute("class"));
                        foundCart = true;
                        break;
                    }
                } catch (Exception e) {
                    // Not found
                }
            }
            
            if (!foundCart) {
                System.out.println("⚠ Could not find cart element");
            }
            
            // Step 5: Save page source for manual inspection
            System.out.println("\n=== Step 5: Saving Page Source ===");
            String htmlPath = "output/lastprice_search_page.html";
            java.nio.file.Files.write(
                java.nio.file.Paths.get(htmlPath),
                driver.getPageSource().getBytes(java.nio.charset.StandardCharsets.UTF_8)
            );
            System.out.println("✓ Saved page source to: " + htmlPath);
            System.out.println("  You can open this file to inspect the actual HTML structure");
            
            System.out.println("\n=== DEBUG COMPLETE ===\n");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
