package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.CategoryPage;
import pages.ProductPage;
import pages.CartPage;
import utils.ExcelUtils;

import java.util.List;

public class MultiCategoryCartTest extends BaseTest {

    @Test
    public void testAddItemsFromThreeCategoriesAndValidateCart() {
        try {
            System.out.println("=== Starting Multi-Category Cart Test ===");

            HomePage homePage = new HomePage(driver);
            CategoryPage categoryPage = new CategoryPage(driver);
            ProductPage productPage = new ProductPage(driver);
            CartPage cartPage = new CartPage(driver);

            // רשימת קטגוריות לנסות - נבחר את הראשונות שעובדות
            // בהתבסס על המבנה האמיתי של האתר
            String[][] categories = {
                {"טלוויזיות", "כל הקטגוריות"},
                {"טלפונים סלולריים", "סלולר"},
                {"כביסה וייבוש", "מקררים ומקפיאים", "מיזוג ואוורור"}
            };

            // Category 1 - הוספת פריט אחד (טלוויזיות/אלקטרוניקה)
            System.out.println("\nStep 1: Adding product from Category 1 - TV/Electronics");
            boolean category1Success = false;
            for (String catName : categories[0]) {
                try {
                    homePage.openCategoryByName(catName);
                    Thread.sleep(3000);
                    category1Success = true;
                    System.out.println("✓ Opened category: " + catName);
                    break;
                } catch (Exception e) {
                    System.out.println("Category '" + catName + "' not accessible, trying next...");
                }
            }
            
            if (!category1Success) {
                throw new RuntimeException("Could not open any category from group 1");
            }
            
            categoryPage.openProductByIndex(0);
            Thread.sleep(3000);
            String product1Name = productPage.getName();
            String product1Price = productPage.getPrice();
            System.out.println("Product 1: " + product1Name + " - " + product1Price);
            productPage.addToCart();
            Thread.sleep(3000);

            // חזרה לדף הבית
            System.out.println("Returning to home page...");
            driver.get("https://www.lastprice.co.il");
            Thread.sleep(3000);

            // Category 2 - הוספת פריט בכמות 2 יחידות (טלפונים)
            System.out.println("\nStep 2: Adding product from Category 2 - Mobile Phones (Quantity: 2)");
            boolean category2Success = false;
            for (String catName : categories[1]) {
                try {
                    homePage.openCategoryByName(catName);
                    Thread.sleep(3000);
                    category2Success = true;
                    System.out.println("✓ Opened category: " + catName);
                    break;
                } catch (Exception e) {
                    System.out.println("Category '" + catName + "' not accessible, trying next...");
                }
            }
            
            if (!category2Success) {
                throw new RuntimeException("Could not open any category from group 2");
            }
            
            categoryPage.openProductByIndex(0);
            Thread.sleep(3000);
            String product2Name = productPage.getName();
            String product2Price = productPage.getPrice();
            System.out.println("Product 2: " + product2Name + " - " + product2Price);
            productPage.setQuantity(2);
            Thread.sleep(1000);
            productPage.addToCart();
            Thread.sleep(3000);

            // חזרה לדף הבית
            System.out.println("Returning to home page...");
            driver.get("https://www.lastprice.co.il");
            Thread.sleep(3000);

            // Category 3 - הוספת פריט אחד (כביסה/מוצרי חשמל)
            System.out.println("\nStep 3: Adding product from Category 3 - Home Appliances");
            boolean category3Success = false;
            for (String catName : categories[2]) {
                try {
                    homePage.openCategoryByName(catName);
                    Thread.sleep(3000);
                    category3Success = true;
                    System.out.println("✓ Opened category: " + catName);
                    break;
                } catch (Exception e) {
                    System.out.println("Category '" + catName + "' not accessible, trying next...");
                }
            }
            
            if (!category3Success) {
                throw new RuntimeException("Could not open any category from group 3");
            }
            
            categoryPage.openProductByIndex(0);
            Thread.sleep(3000);
            String product3Name = productPage.getName();
            String product3Price = productPage.getPrice();
            System.out.println("Product 3: " + product3Name + " - " + product3Price);
            productPage.addToCart();
            Thread.sleep(3000);

            // מעבר לעגלת הקניות
            System.out.println("Step 4: Navigating to Cart");
            homePage.goToCart();
            Thread.sleep(3000);

            // קריאת תוכן העגלה
            System.out.println("Step 5: Reading Cart Contents");
            List<CartPage.CartItem> cartItems = cartPage.getCartItems();
            String cartTotal = cartPage.getCartTotal();

            System.out.println("\n=== Cart Contents ===");
            int totalQuantity = 0;
            for (CartPage.CartItem item : cartItems) {
                System.out.println("Item: " + item.name + 
                                 ", Price: " + item.price + 
                                 ", Quantity: " + item.qty + 
                                 ", Row Total: " + item.rowTotal);
                totalQuantity += item.qty;
            }
            System.out.println("Cart Total: " + cartTotal);
            System.out.println("Total Items Count: " + totalQuantity);

            // Validations
            System.out.println("\n=== Validations ===");
            
            // בדיקה 1: וידוא שיש 3 פריטים שונים בעגלה
            Assert.assertEquals(cartItems.size(), 3, 
                "Expected 3 different products in cart");
            System.out.println("✓ Validation 1 Passed: Cart contains 3 different products");

            // בדיקה 2: וידוא שסך הכמויות הוא 4 (1+2+1)
            Assert.assertEquals(totalQuantity, 4, 
                "Expected total quantity of 4 items (1+2+1)");
            System.out.println("✓ Validation 2 Passed: Total quantity is correct (4 items)");

            // בדיקה 3: וידוא שלפחות פריט אחד בעגלה בכמות 2
            boolean hasQuantityOfTwo = false;
            for (CartPage.CartItem item : cartItems) {
                if (item.qty >= 2) {
                    hasQuantityOfTwo = true;
                    System.out.println("✓ Validation 3 Passed: Found item with quantity >= 2: " + 
                                     item.name + " (qty: " + item.qty + ")");
                    break;
                }
            }
            Assert.assertTrue(hasQuantityOfTwo, 
                "Expected at least one item with quantity >= 2");

            // בדיקה 4: וידוא שסכום העגלה לא ריק
            Assert.assertFalse(cartTotal.isEmpty(), 
                "Cart total should not be empty");
            System.out.println("✓ Validation 4 Passed: Cart total is calculated: " + cartTotal);

            // התוצאות יישמרו ב-all_test_results.xlsx דרך ConsolidatedTestResultsManager
            System.out.println("\nStep 6: Test completed");

            System.out.println("\n=== Test Completed Successfully ===");

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Test failed with exception: " + e.getMessage());
        }
    }
}
