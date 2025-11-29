package tests;

import org.openqa.selenium.By;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.DynamicContentPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class DynamicContentChangeTest {

    private WebDriver driver;
    private DynamicContentPage dynamicPage;

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        dynamicPage = new DynamicContentPage(driver);
    }

    @Test
    public void testDynamicContentChanges() {
        driver.get("https://www.lastprice.co.il/"); 
        String beforeRefresh = dynamicPage.getDynamicText();

        dynamicPage.refreshPage();
        String afterRefresh = dynamicPage.getDynamicText();

        if (beforeRefresh.equals(afterRefresh)) {
            System.out.println("התוכן לא השתנה");
        } else {
            System.out.println("התוכן השתנה בהצלחה!");
        }
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
