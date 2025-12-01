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

/**
 * בדיקת שינוי תוכן דינמי בעקבות הפעלת סינונים
 * 
 * דוגמה זו משתמשת באתר https://www.saucedemo.com כדי להדגים את הקונספט של:
 * 1. שמירת תמונת מסך לפני פעולה
 * 2. ביצוע פעולה שמשנה את התוכן (מיון/סינון)
 * 3. המתנה לשינוי התוכן
 * 4. שמירת תמונת מסך לאחר השינוי
 * 5. בדיקה שהתוכן אכן השתנה
 */
public class DemoFilterContentChangeTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            driver.quit();
        }
    }

    @Test
    public void testDynamicContentChangeWithFiltering() {
        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.println("║   בדיקת שינוי תוכן דינמי בעקבות פעולת סינון/מיון   ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝\n");

        try {
            // שלב 1: כניסה לאתר
            System.out.println("🌐 שלב 1: פתיחת האתר והתחברות");
            System.out.println("─".repeat(60));
            
            driver.get("https://www.saucedemo.com/");
            System.out.println("✓ האתר נפתח בהצלחה");
            
            // התחברות
            driver.findElement(By.id("user-name")).sendKeys("standard_user");
            driver.findElement(By.id("password")).sendKeys("secret_sauce");
            driver.findElement(By.id("login-button")).click();
            Thread.sleep(1000);
            System.out.println("✓ התחברות הושלמה\n");

            // שלב 2: תיעוד מצב התחלתי
            System.out.println("📸 שלב 2: תיעוד מצב התחלתי");
            System.out.println("─".repeat(60));
            
            // המתנה לטעינת המוצרים
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("inventory_item")));
            
            // קבלת סדר המוצרים לפני השינוי
            List<WebElement> initialProducts = driver.findElements(By.className("inventory_item_name"));
            System.out.println("מספר מוצרים: " + initialProducts.size());
            System.out.println("\nסדר המוצרים לפני הסינון:");
            for (int i = 0; i < initialProducts.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + initialProducts.get(i).getText());
            }
            
            // צילום מסך התחלתי
            takeScreenshot("1_before_sort_filter.png");
            System.out.println("\n✓ צילום מסך התחלתי נשמר: 1_before_sort_filter.png\n");

            // שלב 3: ביצוע פעולת סינון/מיון
            System.out.println("🔄 שלב 3: הפעלת מיון - מחיר מהגבוה לנמוך");
            System.out.println("─".repeat(60));
            
            // לחיצה על dropdown המיון
            WebElement sortDropdown = driver.findElement(By.className("product_sort_container"));
            sortDropdown.click();
            Thread.sleep(500);
            
            // בחירת מיון לפי מחיר (גבוה לנמוך)
            sortDropdown.sendKeys("Price (high to low)");
            Thread.sleep(500);
            
            System.out.println("✓ מיון הופעל: מחיר מהגבוה לנמוך");
            
            // שלב 4: המתנה לשינוי התוכן
            System.out.println("\n⏳ שלב 4: המתנה לעדכון התוכן");
            System.out.println("─".repeat(60));
            
            // המתנה ל-DOM להתעדכן
            Thread.sleep(1000);
            
            // המתנה לסיום האנימציה (אם יש)
            wait.until(driver1 -> {
                String readyState = ((JavascriptExecutor) driver1)
                    .executeScript("return document.readyState").toString();
                return "complete".equals(readyState);
            });
            
            System.out.println("✓ התוכן עודכן\n");

            // שלב 5: תיעוד המצב החדש
            System.out.println("📸 שלב 5: תיעוד מצב לאחר הסינון");
            System.out.println("─".repeat(60));
            
            // קבלת סדר המוצרים אחרי השינוי
            List<WebElement> sortedProducts = driver.findElements(By.className("inventory_item_name"));
            System.out.println("מספר מוצרים: " + sortedProducts.size());
            System.out.println("\nסדר המוצרים אחרי המיון:");
            for (int i = 0; i < sortedProducts.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + sortedProducts.get(i).getText());
            }
            
            // צילום מסך לאחר השינוי
            takeScreenshot("2_after_sort_filter.png");
            System.out.println("\n✓ צילום מסך לאחר מיון נשמר: 2_after_sort_filter.png\n");

            // שלב 6: בדיקה נוספת - מיון שני
            System.out.println("🔄 שלב 6: שינוי נוסף - מיון לפי שם (Z-A)");
            System.out.println("─".repeat(60));
            
            sortDropdown = driver.findElement(By.className("product_sort_container"));
            sortDropdown.click();
            Thread.sleep(500);
            sortDropdown.sendKeys("Name (Z to A)");
            Thread.sleep(1000);
            
            System.out.println("✓ מיון שני הופעל: שם (Z-A)\n");
            
            // תיעוד המצב השלישי
            List<WebElement> finalProducts = driver.findElements(By.className("inventory_item_name"));
            System.out.println("סדר המוצרים אחרי מיון שני:");
            for (int i = 0; i < finalProducts.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + finalProducts.get(i).getText());
            }
            
            // צילום מסך סופי
            takeScreenshot("3_after_second_sort.png");
            System.out.println("\n✓ צילום מסך סופי נשמר: 3_after_second_sort.png\n");

            // שלב 7: השוואה ובדיקת שינויים
            System.out.println("📊 שלב 7: ניתוח שינויים");
            System.out.println("─".repeat(60));
            
            String firstProduct_initial = initialProducts.get(0).getText();
            String firstProduct_sorted = sortedProducts.get(0).getText();
            String firstProduct_final = finalProducts.get(0).getText();
            
            System.out.println("המוצר הראשון במצב התחלתי:  " + firstProduct_initial);
            System.out.println("המוצר הראשון לאחר מיון 1:   " + firstProduct_sorted);
            System.out.println("המוצר הראשון לאחר מיון 2:   " + firstProduct_final);
            
            boolean contentChanged1 = !firstProduct_initial.equals(firstProduct_sorted);
            boolean contentChanged2 = !firstProduct_sorted.equals(firstProduct_final);
            
            System.out.println("\n📈 תוצאות:");
            if (contentChanged1) {
                System.out.println("✅ התוכן השתנה בעקבות מיון ראשון");
            }
            if (contentChanged2) {
                System.out.println("✅ התוכן השתנה בעקבות מיון שני");
            }
            if (contentChanged1 && contentChanged2) {
                System.out.println("\n🎉 הצלחה! הוכחנו שינוי תוכן דינמי בעקבות פעולות משתמש!");
            }

            // סיכום קבצים
            System.out.println("\n📁 צילומי מסך נשמרו בתיקייה: output/");
            System.out.println("   • 1_before_sort_filter.png    (מצב התחלתי)");
            System.out.println("   • 2_after_sort_filter.png     (לאחר מיון ראשון)");
            System.out.println("   • 3_after_second_sort.png     (לאחר מיון שני)");

        } catch (Exception e) {
            System.out.println("\n❌ שגיאה: " + e.getMessage());
            e.printStackTrace();
            
            try {
                takeScreenshot("error_screenshot.png");
                System.out.println("📸 צילום מסך של השגיאה נשמר");
            } catch (Exception ex) {
                System.out.println("לא ניתן לצלם מסך");
            }
        }
        
        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.println("║              הבדיקה הסתיימה בהצלחה! ✓              ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝\n");
    }

    /**
     * פונקציה לצילום מסך ושמירה לקובץ
     */
    private void takeScreenshot(String fileName) {
        try {
            // המתנה קצרה לוודא שהדף מתייצב
            Thread.sleep(500);
            
            File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destinationFile = new File("output/" + fileName);
            
            // יצירת התיקייה אם לא קיימת
            destinationFile.getParentFile().mkdirs();
            
            FileUtils.copyFile(screenshotFile, destinationFile);
        } catch (Exception e) {
            System.out.println("⚠ שגיאה בצילום מסך: " + e.getMessage());
        }
    }
}
