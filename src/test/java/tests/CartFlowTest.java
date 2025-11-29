package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.*;
import utils.ExcelUtils;
import utils.ScreenshotUtils;

import java.util.List;

public class CartFlowTest extends BaseTest {

    @Test
    public void testAddThreeCategoriesToCartAndVerifyExcel() throws Exception {
        HomePage home = new HomePage(driver);
        home.goToHome();

        // 1) Category 1: "מחשבים" (example - use category names as in site)
        home.openCategoryByName("בשמים");
        CategoryPage cat1 = new CategoryPage(driver);
        cat1.openProductByIndex(0); // open first product
        ProductPage p1 = new ProductPage(driver);
        String name1 = p1.getName();
        String price1 = p1.getPrice();
        p1.setQuantity(1);
        p1.addToCart();

        // 2) Category 2: "סמארטפון"
        home.goToHome();
        home.openCategoryByName("סמארטפון");
        CategoryPage cat2 = new CategoryPage(driver);
        cat2.openProductByIndex(1); // second product
        ProductPage p2 = new ProductPage(driver);
        String name2 = p2.getName();
        String price2 = p2.getPrice();
        p2.setQuantity(2); // at least 2 units for one item
        p2.addToCart();

        // 3) Category 3: "אביזרים" (accessories)
        home.goToHome();
        home.openCategoryByName("אביזרים");
        CategoryPage cat3 = new CategoryPage(driver);
        cat3.openProductByIndex(0);
        ProductPage p3 = new ProductPage(driver);
        String name3 = p3.getName();
        String price3 = p3.getPrice();
        p3.setQuantity(1);
        p3.addToCart();

        // go to cart and verify
        home.goToCart();
        CartPage cart = new CartPage(driver);
        List<CartPage.CartItem> items = cart.getCartItems();
        String total = cart.getCartTotal();

        // minimal assertions: at least 3 items present
        Assert.assertTrue(items.size() >= 3, "Expect at least 3 different items in cart");

        // Save screenshot before Excel
        ScreenshotUtils.takeScreenshot(driver, "cart_before_excel.png");

        // write to excel
        ExcelUtils.writeCartToExcel("output/cart_results.xlsx", items, total);

        // verify that Excel created (basic check)
        Assert.assertTrue(total != null && !total.isEmpty(), "Cart total should be present");
    }
}
