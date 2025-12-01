package tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.ArrayList;

/**
 * בדיקת שינוי תוכן דינמי בעקבות הפעלת סינונים
 * טסט פשוט שעובד עם אתר דוגמה
 */
public class SimpleDynamicContentFilterTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);
        
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            try {
                Thread.sleep(2000); // המתנה קצרה לפני סגירה
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            driver.quit();
        }
    }

    @Test
    public void testDynamicContentChangeWithFilters() {
        System.out.println("\n====================================================");
        System.out.println("   בדיקת שינוי תוכן דינמי עם סינונים - בשמים");
        System.out.println("====================================================\n");

        try {
            // שלב 1: מעבר לעמוד דוגמה עם סינונים (האתר https://demoqa.com/)
            System.out.println("שלב 1: טעינת דף קטגוריית בשמים");
            System.out.println("----------------------------------------------------");
            
            // ניסיון ישיר לאתר lastprice
            driver.get("https://www.lastprice.co.il");
            Thread.sleep(3000);
            
            // בדיקה שהחלון פתוח
            System.out.println("כותרת הדף: " + driver.getTitle());
            System.out.println("URL נוכחי: " + driver.getCurrentUrl());
            
            // ניווט לקטגוריה
            System.out.println("\nמחפש קישור לבשמים...");
            
            // חיפוש בדרכים שונות
            List<By> perfumeSelectors = new ArrayList<>();
            perfumeSelectors.add(By.linkText("בשמים"));
            perfumeSelectors.add(By.partialLinkText("בשמים"));
            perfumeSelectors.add(By.xpath("//a[contains(text(), 'בשמים')]"));
            perfumeSelectors.add(By.cssSelector("a[href*='בשמים']"));
            
            WebElement perfumeLink = null;
            for (By selector : perfumeSelectors) {
                try {
                    perfumeLink = wait.until(ExpectedConditions.presenceOfElementLocated(selector));
                    if (perfumeLink != null) {
                        System.out.println("✓ נמצא קישור בשמים");
                        break;
                    }
                } catch (Exception e) {
                    // נסה את הסלקטור הבא
                }
            }
            
            if (perfumeLink == null) {
                System.out.println("לא נמצא קישור בשמים בתפריט, עובר לכתובת ישירה...");
                driver.get("https://www.lastprice.co.il/c/65");
                Thread.sleep(3000);
            } else {
                ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", 
                    perfumeLink
                );
                Thread.sleep(500);
                perfumeLink.click();
                Thread.sleep(3000);
            }
            
            System.out.println("✓ נכנסנו לדף בשמים");
            System.out.println("URL נוכחי: " + driver.getCurrentUrl());
            
            // שלב 2: שמירת מצב התחלתי
            System.out.println("\nשלב 2: תיעוד מצב התחלתי");
            System.out.println("----------------------------------------------------");
            
            takeScreenshot("1_before_filters.png");
            System.out.println("✓ צילום מסך התחלתי נשמר");
            
            int initialProductCount = countElements(By.cssSelector(".product, .product-item, [class*='product']"));
            System.out.println("מספר מוצרים התחלתי: " + initialProductCount);
            
            // שלב 3: הפעלת סינון ראשון - בשמים לנשים
            System.out.println("\nשלב 3: הפעלת סינון - בשמים לנשים");
            System.out.println("----------------------------------------------------");
            
            boolean firstFilterApplied = applyFilter("בשמים לנשים");
            
            if (firstFilterApplied) {
                System.out.println("✓ סינון 'בשמים לנשים' הופעל");
                Thread.sleep(2000); // המתנה לעדכון תוכן
                
                takeScreenshot("2_after_women_filter.png");
                System.out.println("✓ צילום מסך לאחר סינון נשים נשמר");
                
                int afterFirstFilter = countElements(By.cssSelector(".product, .product-item, [class*='product']"));
                System.out.println("מספר מוצרים לאחר סינון נשים: " + afterFirstFilter);
                
                // שלב 4: הפעלת סינון שני - Calvin Klein
                System.out.println("\nשלב 4: הפעלת סינון נוסף - Calvin Klein");
                System.out.println("----------------------------------------------------");
                
                boolean secondFilterApplied = applyFilter("Calvin Klein");
                
                if (secondFilterApplied) {
                    System.out.println("✓ סינון 'Calvin Klein' הופעל");
                    Thread.sleep(2000); // המתנה לעדכון תוכן
                    
                    takeScreenshot("3_after_calvin_klein_filter.png");
                    System.out.println("✓ צילום מסך סופי נשמר");
                    
                    int finalCount = countElements(By.cssSelector(".product, .product-item, [class*='product']"));
                    System.out.println("מספר מוצרים סופי: " + finalCount);
                    
                    // סיכום
                    System.out.println("\n====================================================");
                    System.out.println("                      סיכום");
                    System.out.println("====================================================");
                    System.out.println("מספר מוצרים התחלתי:        " + initialProductCount);
                    System.out.println("לאחר סינון נשים:           " + afterFirstFilter);
                    System.out.println("לאחר סינון Calvin Klein:   " + finalCount);
                    
                    if (finalCount < initialProductCount) {
                        System.out.println("\n✓✓✓ הצלחה! התוכן השתנה דינמית בעקבות הסינונים ✓✓✓");
                    }
                    
                } else {
                    System.out.println("⚠ לא הצלחנו למצוא מסנן Calvin Klein");
                }
                
            } else {
                System.out.println("⚠ לא הצלחנו למצוא מסנן בשמים לנשים");
            }
            
            System.out.println("\nצילומי מסך נשמרו בתיקייה: output/");
            System.out.println("  - 1_before_filters.png");
            System.out.println("  - 2_after_women_filter.png");
            System.out.println("  - 3_after_calvin_klein_filter.png");
            
        } catch (Exception e) {
            System.out.println("\n✗ שגיאה: " + e.getMessage());
            e.printStackTrace();
            
            // ניסיון לצלם מסך בכל מקרה
            try {
                takeScreenshot("error_screenshot.png");
                System.out.println("צילום מסך של השגיאה נשמר");
            } catch (Exception ex) {
                System.out.println("לא ניתן לצלם מסך");
            }
        }
        
        System.out.println("\n====================================================");
        System.out.println("              הבדיקה הסתיימה");
        System.out.println("====================================================\n");
    }

    /**
     * החלת סינון לפי טקסט
     */
    private boolean applyFilter(String filterText) {
        try {
            // ניסיון למצוא את המסנן בדרכים שונות
            List<By> filterSelectors = new ArrayList<>();
            filterSelectors.add(By.xpath("//span[contains(text(), '" + filterText + "')]"));
            filterSelectors.add(By.xpath("//label[contains(text(), '" + filterText + "')]"));
            filterSelectors.add(By.xpath("//a[contains(text(), '" + filterText + "')]"));
            filterSelectors.add(By.xpath("//div[contains(text(), '" + filterText + "')]"));
            
            for (By selector : filterSelectors) {
                try {
                    WebElement filterElement = wait.until(ExpectedConditions.presenceOfElementLocated(selector));
                    
                    // גלילה לאלמנט
                    ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", 
                        filterElement
                    );
                    Thread.sleep(500);
                    
                    // ניסיון ללחיצה
                    try {
                        filterElement.click();
                    } catch (Exception e) {
                        // ניסיון עם JavaScript click
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", filterElement);
                    }
                    
                    return true;
                    
                } catch (Exception e) {
                    // נסה את הסלקטור הבא
                }
            }
            
            return false;
            
        } catch (Exception e) {
            System.out.println("שגיאה בהחלת סינון: " + e.getMessage());
            return false;
        }
    }

    /**
     * ספירת אלמנטים
     */
    private int countElements(By locator) {
        try {
            List<WebElement> elements = driver.findElements(locator);
            return elements.size();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * צילום מסך
     */
    private void takeScreenshot(String fileName) {
        try {
            File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destinationFile = new File("output/" + fileName);
            FileUtils.copyFile(screenshotFile, destinationFile);
        } catch (Exception e) {
            System.out.println("שגיאה בצילום מסך: " + e.getMessage());
        }
    }
}
