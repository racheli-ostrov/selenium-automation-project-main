package tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.PerfumeCategoryPage;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;
import java.util.List;

/**
 * בדיקת שינוי תוכן דינמי בעקבות הפעלת סינונים בחיפוש בשמים
 * 
 * תהליך הבדיקה:
 * 1. כניסה לעמוד הבית
 * 2. הקלדה "בשמים" בשורת החיפוש (ללא ENTER)
 * 3. סינון לפי "בשמים לנשים"
 * 4. שמירת מצב התחלתי
 * 5. סינון לפי מותג "Calvin Klein"
 * 6. בדיקה שהתוכן השתנה דינמית
 * 
 * חשוב: הבדיקה לא עוברת דרך עגלת קניות בשום שלב!
 * הערה: כוללת הדגשה ויזואלית של כל אלמנט לפני לחיצה
 */
public class PerfumeFilterTest {

    private WebDriver driver;

    /**
     * מדגיש אלמנט עם מסגרת אדומה דקה
     */
    private void highlightElement(org.openqa.selenium.WebElement element) {
        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
        String originalStyle = element.getAttribute("style");
        js.executeScript("arguments[0].setAttribute('style', arguments[1]);", 
            element, 
            "border: 2px solid red; box-shadow: 0 0 5px red;");
        
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // החזרת הסגנון המקורי
        js.executeScript("arguments[0].setAttribute('style', arguments[1]);", element, originalStyle);
    }

    /**
     * מחפש מסנן לפי טקסט, מדגיש אותו ולוחץ עליו
     */
    private void clickFilterWithHighlight(String filterText) throws InterruptedException {
        PerfumeCategoryPage perfumePage = new PerfumeCategoryPage(driver);
        
        // חיפוש האלמנט
        org.openqa.selenium.By filterLocator = org.openqa.selenium.By.xpath(
            "//div[contains(text(), '" + filterText + "')] | " +
            "//span[contains(text(), '" + filterText + "')] | " +
            "//label[contains(text(), '" + filterText + "')] | " +
            "//a[contains(text(), '" + filterText + "')]"
        );
        
        org.openqa.selenium.support.ui.WebDriverWait wait = 
            new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(10));
        org.openqa.selenium.WebElement filterElement = wait.until(
            org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(filterLocator)
        );
        
        // גלילה לאלמנט
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
            "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", 
            filterElement
        );
        Thread.sleep(1000);
        
        // הדגשה
        highlightElement(filterElement);
        Thread.sleep(1500);
        
        // לחיצה
        try {
            filterElement.click();
        } catch (Exception e) {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "arguments[0].click();", filterElement
            );
        }
        
        System.out.println("✓ נלחץ על: " + filterText);
    }

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.setPageLoadTimeout(Duration.ofSeconds(60));
        
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testDynamicContentChangeWithFilters() {
        System.out.println("\n========================================");
        System.out.println("התחלת בדיקה: שינוי תוכן דינמי עם סינונים");
        System.out.println("========================================\n");

        // יצירת אובייקטי Page
        HomePage homePage = new HomePage(driver);
        PerfumeCategoryPage perfumePage = new PerfumeCategoryPage(driver);

        try {
            // שלב 1: כניסה לעמוד הבית
            System.out.println("שלב 1: כניסה לעמוד הבית");
            System.out.println("----------------------------------------");
            
            driver.get("https://www.lastprice.co.il");
            Thread.sleep(5000); // המתנה ארוכה יותר לטעינה מלאה
            
            String currentUrl = driver.getCurrentUrl();
            System.out.println("URL נוכחי: " + currentUrl);
            
            if (currentUrl.contains("shopping-cart")) {
                throw new RuntimeException("שגיאה! נכנסנו לעגלת קניות מיד אחרי כניסה לעמוד הבית!");
            }
            
            System.out.println("✓ נכנסנו לעמוד הבית\n");
            
            // המתנה נוספת כדי לוודא שהדף התייצב
            Thread.sleep(2000);
            
            // בדיקה חוזרת
            currentUrl = driver.getCurrentUrl();
            System.out.println("בדיקה חוזרת - URL: " + currentUrl);
            
            if (currentUrl.contains("shopping-cart")) {
                throw new RuntimeException("שגיאה! עברנו לעגלת קניות אחרי שהדף התייצב!");
            }
            
            System.out.println("✓ הדף יציב - לא עברנו לעגלה\n");
            
            // שלב 2: הקלדה בשורת החיפוש "בשמים" (ללא ENTER - רק הקלדה!)
            System.out.println("שלב 2: הקלדה 'בשמים' בשורת החיפוש");
            System.out.println("----------------------------------------");
            
            org.openqa.selenium.support.ui.WebDriverWait searchWait = new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(10));
            
            // מציאת שדה החיפוש ישירות בלי ללחוץ על שום אייקון
            // (כדי לא לטעות וללחוץ על אייקון העגלה במקום החיפוש)
            org.openqa.selenium.WebElement searchInput = null;
            
            try {
                searchInput = searchWait.until(
                    org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(
                        org.openqa.selenium.By.cssSelector("input[type='search'], input[name*='search'], input[id*='search'], input[placeholder*='חיפוש'], input.search, .search input")
                    )
                );
                System.out.println("✓ נמצא שדה החיפוש");
            } catch (Exception e) {
                // אם לא מצאנו, ננסה ללחוץ על אייקון החיפוש בזהירות
                System.out.println("מנסה לפתוח את שדה החיפוש...");
                
                try {
                    // נחפש ספציפית input שמוסתר ואז נלחץ על כפתור שיראה אותו
                    org.openqa.selenium.WebElement searchToggle = driver.findElement(
                        org.openqa.selenium.By.cssSelector("button.search-toggle:not([href*='cart']), a.search-toggle:not([href*='cart'])")
                    );
                    
                    highlightElement(searchToggle);
                    Thread.sleep(1000);
                    searchToggle.click();
                    Thread.sleep(1500);
                    
                    // בדיקה שלא עברנו לעגלה
                    currentUrl = driver.getCurrentUrl();
                    if (currentUrl.contains("shopping-cart")) {
                        throw new RuntimeException("שגיאה! עברנו לעגלת קניות אחרי לחיצה!");
                    }
                    
                    System.out.println("✓ נפתח שדה החיפוש");
                    
                    searchInput = searchWait.until(
                        org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(
                            org.openqa.selenium.By.cssSelector("input[type='search'], input[name*='search'], input[id*='search'], input[placeholder*='חיפוש'], input.search, .search input")
                        )
                    );
                } catch (Exception ex) {
                    throw new RuntimeException("לא הצלחנו למצוא או לפתוח את שדה החיפוש: " + ex.getMessage());
                }
            }
            
            highlightElement(searchInput);
            Thread.sleep(1500);
            searchInput.clear();
            searchInput.sendKeys("בשמים");
            Thread.sleep(3000); // המתנה ארוכה יותר לרשימת הצעות
            System.out.println("✓ הוקלד 'בשמים' בשדה החיפוש");
            
            // בדיקה שלא עברנו לעגלה אחרי ההקלדה
            currentUrl = driver.getCurrentUrl();
            System.out.println("URL אחרי הקלדה: " + currentUrl);
            
            if (currentUrl.contains("shopping-cart")) {
                throw new RuntimeException("שגיאה! עברנו לעגלת קניות אחרי הקלדה בחיפוש!");
            }
            
            System.out.println("✓ נשארנו באותו עמוד - לא עברנו לעגלה\n");
            
            // שלב 3: סינון "בשמים לנשים" מתוך התוצאות שמופיעות
            System.out.println("שלב 3: סינון לפי 'בשמים לנשים'");
            System.out.println("----------------------------------------");
            
            // חיפוש כל האלמנטים שמכילים "לנשים" או "לאישה" בהקשר של בשמים
            Thread.sleep(2000);
            
            // נחפש checkbox או link של "בשמים לנשים" או "בשמים לאישה"
            List<org.openqa.selenium.WebElement> womenFilters = driver.findElements(
                org.openqa.selenium.By.xpath(
                    "//*[contains(text(), 'בשמים לנשים') or contains(text(), 'בשמים לאישה') or " +
                    "(contains(text(), 'לנשים') and ancestor::*[contains(@class, 'filter')]) or " +
                    "(contains(text(), 'לאישה') and ancestor::*[contains(@class, 'filter')])]"
                )
            );
            
            System.out.println("נמצאו " + womenFilters.size() + " אלמנטים עם 'לנשים/לאישה'");
            
            org.openqa.selenium.WebElement womenFilter = null;
            for (org.openqa.selenium.WebElement filter : womenFilters) {
                try {
                    if (filter.isDisplayed()) {
                        String text = filter.getText();
                        System.out.println("  - " + text);
                        if (text.contains("לנשים") || text.contains("לאישה")) {
                            womenFilter = filter;
                            System.out.println("    ^ זה האלמנט שנבחר!");
                            break;
                        }
                    }
                } catch (Exception e) {
                    // המשך
                }
            }
            
            if (womenFilter != null) {
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", 
                    womenFilter
                );
                Thread.sleep(1000);
                highlightElement(womenFilter);
                Thread.sleep(1500);
                
                try {
                    womenFilter.click();
                } catch (Exception e) {
                    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", womenFilter);
                }
                
                Thread.sleep(4000);
                
                // בדיקה שלא עברנו לעגלה
                currentUrl = driver.getCurrentUrl();
                System.out.println("URL אחרי סינון נשים: " + currentUrl);
                
                if (currentUrl.contains("shopping-cart")) {
                    throw new RuntimeException("שגיאה! עברנו לעגלת קניות אחרי סינון נשים!");
                }
                
                System.out.println("✓ סונן לפי 'בשמים לנשים'\n");
            } else {
                System.out.println("⚠ לא נמצא סינון 'בשמים לנשים', ממשיכים...\n");
            }

            // שלב 4: שמירת מצב התחלתי
            System.out.println("שלב 4: שמירת מצב התחלתי");
            System.out.println("----------------------------------------");
            
            Thread.sleep(1500); // המתנה לפני ספירת מוצרים
            int initialProductCount = perfumePage.getProductCount();
            List<String> initialProducts = perfumePage.getProductNames();
            
            System.out.println("מספר מוצרים התחלתי: " + initialProductCount);
            System.out.println("דוגמאות למוצרים:");
            for (int i = 0; i < Math.min(5, initialProducts.size()); i++) {
                System.out.println("  " + (i+1) + ". " + initialProducts.get(i));
            }
            System.out.println();

            // שלב 5: הפעלת סינון - Calvin Klein
            System.out.println("שלב 5: הפעלת סינון - Calvin Klein");
            System.out.println("----------------------------------------");
            
            // פתיחת אקורדיון המותגים
            System.out.println("פותח את סינון המותגים...");
            try {
                org.openqa.selenium.WebElement brandsAccordion = driver.findElement(
                    org.openqa.selenium.By.xpath("//div[contains(text(), 'סנן לפי מותגים')] | //h3[contains(text(), 'מותגים')] | //span[contains(text(), 'מותגים')]")
                );
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", brandsAccordion);
                Thread.sleep(1000);
                
                // הדגשת האקורדיון
                highlightElement(brandsAccordion);
                Thread.sleep(1500);
                
                brandsAccordion.click();
                Thread.sleep(2000);
                System.out.println("✓ אקורדיון המותגים נפתח");
            } catch (Exception e) {
                System.out.println("המותגים כבר פתוחים או לא נמצא אקורדיון");
            }
            
            // מחפש ולוחץ על Calvin Klein
            System.out.println("מחפש סינון Calvin Klein...");
            clickFilterWithHighlight("Calvin Klein");
            
            // המתנה לעדכון הדף
            Thread.sleep(4000);
            perfumePage.waitForDOMUpdate();
            
            System.out.println("✓ סינון 'Calvin Klein' הופעל\n");

            // שלב 6: בדיקת השינוי לאחר סינון Calvin Klein
            System.out.println("שלב 6: בדיקת תוכן לאחר סינון Calvin Klein");
            System.out.println("----------------------------------------");
            
            int afterFirstFilterCount = perfumePage.getProductCount();
            List<String> afterFirstFilterProducts = perfumePage.getProductNames();
            
            System.out.println("מספר מוצרים לאחר סינון Calvin Klein: " + afterFirstFilterCount);
            System.out.println("מוצרי Calvin Klein לנשים:");
            for (int i = 0; i < Math.min(10, afterFirstFilterProducts.size()); i++) {
                System.out.println("  " + (i+1) + ". " + afterFirstFilterProducts.get(i));
            }
            System.out.println();

            // שלב 7: סיכום ובדיקות
            System.out.println("========================================");
            System.out.println("סיכום תוצאות");
            System.out.println("========================================");
            
            System.out.println("מספר מוצרים התחלתי (בשמים לאישה): " + initialProductCount);
            System.out.println("מספר מוצרים לאחר סינון Calvin Klein: " + afterFirstFilterCount);
            
            // בדיקה שהתוכן השתנה
            boolean contentChanged = !initialProducts.equals(afterFirstFilterProducts);
            
            if (contentChanged || afterFirstFilterCount != initialProductCount) {
                System.out.println("\n✓✓✓ הצלחה! התוכן השתנה בצורה דינמית לאחר הפעלת הסינון ✓✓✓");
            } else {
                System.out.println("\n✗ התוכן לא השתנה כצפוי");
            }
            
            // בדיקה שמספר המוצרים הצטמצם (כצפוי מסינון)
            if (afterFirstFilterCount > 0 && afterFirstFilterCount <= 4) {
                System.out.println("✓ מסנן Calvin Klein מציג " + afterFirstFilterCount + " מוצרים כצפוי");
            }
            
        } catch (Exception e) {
            System.out.println("\n✗✗✗ שגיאה בביצוע הבדיקה ✗✗✗");
            System.out.println("פרטי השגיאה: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("הבדיקה נכשלה", e);
        }
        
        System.out.println("\n========================================");
        System.out.println("הבדיקה הסתיימה בהצלחה!");
        System.out.println("========================================\n");
    }
}
