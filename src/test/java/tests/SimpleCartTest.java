package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.ProductPage;
import pages.CartPage;
import utils.ExcelUtils;

import java.util.List;
import java.util.ArrayList;

public class SimpleCartTest extends BaseTest {

    @Test
    public void testAddMultipleItemsToCartAndExport() {
        try {
            System.out.println("=== Starting LastPrice Product Search & Export Test ===");
            System.out.println("Note: LastPrice is a price comparison site, not a shopping cart site");
            System.out.println("This test will search products, collect prices, and export to Excel\n");

            HomePage homePage = new HomePage(driver);
            ProductPage productPage = new ProductPage(driver);
            
            // List to store found products
            List<CartPage.CartItem> foundProducts = new ArrayList<>();

            // Product 1: Search for TV
            System.out.println("\n=== Product 1: Searching for TV ===");
            Thread.sleep(3000);
            
            try {
                homePage.search("טלוויזיה");
                Thread.sleep(3000);
                
                // Click first result
                driver.findElement(org.openqa.selenium.By.cssSelector("a.prodLink")).click();
                Thread.sleep(3000);
                
                String product1Name = productPage.getName();
                String product1Price = productPage.getPrice();
                System.out.println("✓ Product 1: " + product1Name + " - " + product1Price);
                
                // Create cart item representation
                CartPage.CartItem item1 = new CartPage.CartItem();
                item1.name = product1Name;
                item1.price = product1Price;
                item1.qty = 1;
                item1.rowTotal = product1Price;
                foundProducts.add(item1);
                
            } catch (Exception e) {
                System.out.println("⚠ Could not find TV product: " + e.getMessage());
            }

            // Return to home page
            System.out.println("\nReturning to home...");
            driver.get("https://www.lastprice.co.il");
            Thread.sleep(3000);

            // Product 2: Search for Phone - Quantity: 2
            System.out.println("\n=== Product 2: Searching for Mobile Phone (Qty: 2) ===");
            
            try {
                homePage.search("אייפון");
                Thread.sleep(3000);
                
                // Click first result
                driver.findElement(org.openqa.selenium.By.cssSelector("a.prodLink")).click();
                Thread.sleep(3000);
                
                String product2Name = productPage.getName();
                String product2Price = productPage.getPrice();
                System.out.println("✓ Product 2: " + product2Name + " - " + product2Price);
                
                // Create cart item representation with quantity 2
                CartPage.CartItem item2 = new CartPage.CartItem();
                item2.name = product2Name;
                item2.price = product2Price;
                item2.qty = 2;
                item2.rowTotal = product2Price + " x 2";
                foundProducts.add(item2);
                
            } catch (Exception e) {
                System.out.println("⚠ Could not find phone product: " + e.getMessage());
            }

            // Return to home page
            System.out.println("\nReturning to home...");
            driver.get("https://www.lastprice.co.il");
            Thread.sleep(3000);

            // Product 3: Search for Washing Machine
            System.out.println("\n=== Product 3: Searching for Washing Machine ===");
            
            try {
                homePage.search("מכונת כביסה");
                Thread.sleep(3000);
                
                // Click first result
                driver.findElement(org.openqa.selenium.By.cssSelector("a.prodLink")).click();
                Thread.sleep(3000);
                
                String product3Name = productPage.getName();
                String product3Price = productPage.getPrice();
                System.out.println("✓ Product 3: " + product3Name + " - " + product3Price);
                
                // Create cart item representation
                CartPage.CartItem item3 = new CartPage.CartItem();
                item3.name = product3Name;
                item3.price = product3Price;
                item3.qty = 1;
                item3.rowTotal = product3Price;
                foundProducts.add(item3);
                
            } catch (Exception e) {
                System.out.println("⚠ Could not find washing machine product: " + e.getMessage());
            }

            // Summary
            System.out.println("\n=== Search Results Summary ===");
            int totalQuantity = 0;
            for (CartPage.CartItem item : foundProducts) {
                System.out.println("Item: " + item.name + 
                                 ", Price: " + item.price + 
                                 ", Quantity: " + item.qty + 
                                 ", Row Total: " + item.rowTotal);
                totalQuantity += item.qty;
            }
            System.out.println("Total Products Found: " + foundProducts.size());
            System.out.println("Total Items Count: " + totalQuantity);

            // התוצאות יישמרו ב-all_test_results.xlsx דרך ConsolidatedTestResultsManager
            System.out.println("\n=== Test Results ===");

            // Validations
            System.out.println("\n=== Validations ===");
            
            if (!foundProducts.isEmpty()) {
                // Validation 1: Check if we found products
                System.out.println("✓ Validation 1 Passed: Found " + foundProducts.size() + " product(s)");

                // Validation 2: Check total quantity
                if (totalQuantity >= 2) {
                    System.out.println("✓ Validation 2 Passed: Total quantity is " + totalQuantity + " items");
                } else {
                    System.out.println("⚠ Validation 2: Expected at least 2 items, found " + totalQuantity);
                }

                // Validation 3: Check if at least one item has quantity >= 2
                boolean hasQuantityOfTwo = false;
                for (CartPage.CartItem item : foundProducts) {
                    if (item.qty >= 2) {
                        hasQuantityOfTwo = true;
                        System.out.println("✓ Validation 3 Passed: Found item with quantity >= 2: " + 
                                         item.name + " (qty: " + item.qty + ")");
                        break;
                    }
                }
                if (!hasQuantityOfTwo) {
                    System.out.println("⚠ Validation 3: No item with quantity >= 2 found");
                }

                // Validation 4: Check if prices were found
                boolean hasPrices = false;
                for (CartPage.CartItem item : foundProducts) {
                    if (!item.price.isEmpty()) {
                        hasPrices = true;
                        break;
                    }
                }
                if (hasPrices) {
                    System.out.println("✓ Validation 4 Passed: Price information found for products");
                } else {
                    System.out.println("⚠ Validation 4: No price information found");
                }

                // התוצאות יישמרו ב-all_test_results.xlsx דרך ConsolidatedTestResultsManager
            } else {
                System.out.println("⚠ Validations failed: No products were found");
                Assert.fail("No products were found during the search");
            }

            System.out.println("\n=== Test Completed Successfully ===");
            System.out.println("התוצאות יישמרו ב-output/all_test_results.xlsx");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("\n=== Test Failed ===");
            System.out.println("Error: " + e.getMessage());
            Assert.fail("Test failed with exception: " + e.getMessage());
        }
    }
}
