package tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;

public class BaseTest {

    protected WebDriver driver;

    @BeforeClass
    public void setUp() {
        // התקנה והגדרת ChromeDriver באמצעות WebDriverManager
        WebDriverManager.chromedriver().setup();
        
        // הגדרות Chrome - דפדפן גלוי עם חלון מקסימלי
        ChromeOptions options = new ChromeOptions();
        // אפשר להוסיף אופציות נוספות כאן אם צריך
        // options.addArguments("--start-maximized");
        
        driver = new ChromeDriver(options);

        // הגדלת חלון והגדרת זמן המתנה גלובלי
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        // פתיחת האתר
        driver.get("https://www.lastprice.co.il");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
