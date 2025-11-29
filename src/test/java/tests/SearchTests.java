package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;

public class SearchTests extends BaseTest {

    @Test
    public void testSearchWorks() {
        driver.get("https://www.lastprice.co.il/");
        HomePage home = new HomePage(driver);

        home.search("אוזניות");

        Assert.assertTrue(driver.getPageSource().contains("אוזניות"));
    }
}
