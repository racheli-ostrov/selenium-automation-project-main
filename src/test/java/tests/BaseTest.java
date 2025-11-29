package tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;

public class BaseTest {

    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {
        // התקנה והגדרת ChromeDriver באמצעות WebDriverManager
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();

        // הגדלת חלון והגדרת זמן המתנה גלובלי
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        // פתיחת האתר
        driver.get("https://www.lastprice.co.il/accessibility");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
