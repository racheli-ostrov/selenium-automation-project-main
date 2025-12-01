package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

/**
 * Page Object עבור דף קטגוריית בשמים עם סינונים
 */
public class PerfumeCategoryPage extends BasePage {

    // מזהי אלמנטים למסננים
    private By womenPerfumeFilter = By.xpath("//span[contains(text(), 'בשמים לנשים')] | //label[contains(text(), 'בשמים לנשים')] | //a[contains(text(), 'בשמים לנשים')]");
    private By calvinKleinFilter = By.xpath("//span[contains(text(), 'Calvin Klein')] | //label[contains(text(), 'Calvin Klein')] | //a[contains(text(), 'Calvin Klein')]");
    
    // מזהי כלליים למסננים
    private By filterOptions = By.cssSelector(".filter-option, .facet-option, .filter-item, [class*='filter'] label, [class*='facet'] label");
    private By brandFilters = By.xpath("//div[contains(@class, 'brand') or contains(@class, 'manufacturer')]//label | //div[contains(@class, 'brand') or contains(@class, 'manufacturer')]//span");
    private By categoryFilters = By.xpath("//div[contains(@class, 'category') or contains(@class, 'type')]//label | //div[contains(@class, 'category') or contains(@class, 'type')]//span");
    
    // רשימת מוצרים
    private By productItems = By.cssSelector(".product-item, .product-card, .product, [class*='product-']");
    private By productTitles = By.cssSelector(".product-name, .product-title, h3, h4");
    
    // מזהה לטעינת דף (loader)
    private By loadingIndicator = By.cssSelector(".loading, .spinner, .loader, [class*='loading']");

    public PerfumeCategoryPage(WebDriver driver) {
        super(driver);
    }

    /**
     * לחיצה על מסנן בשמים לנשים
     */
    public void clickWomenPerfumeFilter() {
        System.out.println("מחפש ולוחץ על מסנן 'בשמים לנשים'...");
        scrollAndClick(womenPerfumeFilter, "בשמים לנשים");
    }

    /**
     * לחיצה על מסנן מותג Calvin Klein
     */
    public void clickCalvinKleinFilter() {
        System.out.println("מחפש ולוחץ על מסנן 'Calvin Klein'...");
        scrollAndClick(calvinKleinFilter, "Calvin Klein");
    }

    /**
     * לחיצה על מסנן כלשהו לפי טקסט
     */
    public void clickFilterByText(String filterText) {
        System.out.println("מחפש ולוחץ על מסנן: " + filterText);
        By filterLocator = By.xpath("//div[contains(text(), '" + filterText + "')] | " +
                                    "//span[contains(text(), '" + filterText + "')] | " +
                                    "//label[contains(text(), '" + filterText + "')] | " +
                                    "//a[contains(text(), '" + filterText + "')]");
        scrollAndClick(filterLocator, filterText);
    }

    /**
     * המתנה לשינוי בתוכן הדף (המתנה ל-loader להיעלם ולמוצרים להתעדכן)
     */
    public void waitForContentChange() {
        System.out.println("ממתין לשינוי תוכן...");
        try {
            // המתנה קצרה לתחילת הטעינה
            Thread.sleep(500);
            
            // המתנה ל-loader להיעלם (אם קיים)
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            try {
                shortWait.until(ExpectedConditions.invisibilityOfElementLocated(loadingIndicator));
                System.out.println("Loading indicator נעלם");
            } catch (Exception e) {
                // אם אין loader, זה בסדר
            }
            
            // המתנה למוצרים להיות גלויים
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(productItems));
            System.out.println("תוכן עודכן בהצלחה");
            
            // המתנה נוספת לוודא שה-DOM התייצב
            Thread.sleep(1000);
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * קבלת מספר המוצרים המוצגים בדף
     */
    public int getProductCount() {
        try {
            List<WebElement> products = driver.findElements(productItems);
            System.out.println("מספר מוצרים בדף: " + products.size());
            return products.size();
        } catch (Exception e) {
            System.out.println("לא נמצאו מוצרים");
            return 0;
        }
    }

    /**
     * קבלת רשימת שמות המוצרים המוצגים
     */
    public List<String> getProductNames() {
        List<WebElement> titles = driver.findElements(productTitles);
        List<String> names = new java.util.ArrayList<>();
        for (WebElement title : titles) {
            try {
                String text = title.getText().trim();
                if (!text.isEmpty()) {
                    names.add(text);
                }
            } catch (Exception e) {
                // המשך לאלמנט הבא
            }
        }
        System.out.println("נמצאו " + names.size() + " שמות מוצרים");
        return names;
    }

    /**
     * צילום מסך ושמירה לקובץ
     */
    public void takeScreenshot(String fileName) {
        try {
            File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destinationFile = new File("output/" + fileName);
            FileUtils.copyFile(screenshotFile, destinationFile);
            System.out.println("צילום מסך נשמר: " + destinationFile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("שגיאה בשמירת צילום מסך: " + e.getMessage());
        }
    }

    /**
     * הדפסת כל המסננים הזמינים (לצורך דיבאג)
     */
    public void printAllAvailableFilters() {
        System.out.println("=== רשימת מסננים זמינים ===");
        List<WebElement> filters = driver.findElements(filterOptions);
        System.out.println("נמצאו " + filters.size() + " אפשרויות סינון");
        
        int count = 0;
        for (WebElement filter : filters) {
            try {
                String text = filter.getText().trim();
                if (!text.isEmpty() && text.length() < 100) { // סינון תוכן ארוך מדי
                    System.out.println((++count) + ". " + text);
                }
            } catch (Exception e) {
                // המשך
            }
        }
    }

    /**
     * גלילה לאלמנט ולחיצה עליו (עם טיפול באלמנטים מוסתרים)
     */
    private void scrollAndClick(By locator, String elementName) {
        try {
            // המתנה לנוכחות האלמנט
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            
            // גלילה לאלמנט
            ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", 
                element
            );
            
            // המתנה קצרה לאחר הגלילה
            Thread.sleep(500);
            
            // ניסיון ללחיצה רגילה
            try {
                wait.until(ExpectedConditions.elementToBeClickable(element)).click();
                System.out.println("✓ נלחץ על: " + elementName);
            } catch (Exception e) {
                // אם הלחיצה הרגילה נכשלה, ננסה JavaScript click
                System.out.println("משתמש ב-JavaScript click עבור: " + elementName);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
                System.out.println("✓ נלחץ על: " + elementName + " (דרך JS)");
            }
            
        } catch (Exception e) {
            System.out.println("✗ שגיאה בלחיצה על: " + elementName);
            System.out.println("  פרטי שגיאה: " + e.getMessage());
            
            // ננסה לחפש את האלמנט בדרך אחרת
            try {
                System.out.println("  ניסיון חיפוש חלופי...");
                List<WebElement> allElements = driver.findElements(By.xpath("//*[contains(text(), '" + elementName + "')]"));
                System.out.println("  נמצאו " + allElements.size() + " אלמנטים עם הטקסט '" + elementName + "'");
                
                for (WebElement el : allElements) {
                    try {
                        if (el.isDisplayed() && el.getText().trim().startsWith(elementName)) {
                            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", el);
                            Thread.sleep(300);
                            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
                            System.out.println("✓ הצלחנו ללחוץ בדרך חלופית!");
                            return;
                        }
                    } catch (Exception ex) {
                        // המשך לאלמנט הבא
                    }
                }
            } catch (Exception ex2) {
                System.out.println("  גם החיפוש החלופי נכשל");
            }
            
            throw new RuntimeException("לא ניתן למצוא או ללחוץ על: " + elementName, e);
        }
    }

    /**
     * המתנה לעדכון תוכן באמצעות בדיקת DOM
     */
    public void waitForDOMUpdate() {
        try {
            // המתנה לשינוי ב-DOM באמצעות JavaScript
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(driver -> {
                String readyState = ((JavascriptExecutor) driver)
                    .executeScript("return document.readyState").toString();
                return "complete".equals(readyState);
            });
            
            Thread.sleep(1000); // המתנה נוספת לוודא יציבות
            System.out.println("DOM עודכן והתייצב");
            
        } catch (Exception e) {
            System.out.println("המתנה ל-DOM התעדכן: " + e.getMessage());
        }
    }
}
