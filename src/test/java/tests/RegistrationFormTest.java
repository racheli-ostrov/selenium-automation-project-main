package tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.AfterClass;
import pages.RegistrationPage;
import utils.ScreenshotUtils;
import utils.TestResultsExcelWriter;
import java.util.Arrays;
import java.util.List;

public class RegistrationFormTest extends BaseTest {

    @Test
    public void testRegistrationForm() throws Exception {
        // ניקוי תוצאות קודמות
        TestResultsExcelWriter.clearResults();
        
        RegistrationPage reg = new RegistrationPage(driver);

        // שלב 1: כניסה לעמוד הבית
        String homeUrl = "https://www.lastprice.co.il";
        System.out.println("=== כניסה לעמוד הבית ===");
        System.out.println("פותח: " + homeUrl);
        
        try {
            driver.get(homeUrl);
            Thread.sleep(3000); // המתנה לטעינת עמוד הבית
            
            // צילום מסך של עמוד הבית
            ScreenshotUtils.takeScreenshot(driver, "01_home_page.png");
            System.out.println("✓ צילום מסך של עמוד הבית");
            Thread.sleep(2000);
            
            TestResultsExcelWriter.addTestResult("כניסה לעמוד הבית", true, "נפתח בהצלחה: " + homeUrl);
        } catch (Exception e) {
            TestResultsExcelWriter.addTestResult("כניסה לעמוד הבית", false, "שגיאה: " + e.getMessage());
            throw e;
        }
        
        // שלב 2: חיפוש והדגשת כפתור "הרשם" בעמוד הבית
        System.out.println("\n=== מחפש כפתור הרשם בעמוד הבית ===");
        WebElement registerButton = null;
        boolean navigationSuccessful = false;
        
        try {
            // שלב 1: מוצאים את האזור של "התחבר/הרשם" כדי לפתוח את התפריט
            WebElement loginDropdown = driver.findElement(By.xpath("//span[contains(text(), 'התחבר/הרשם')]"));
            WebElement dropdownTrigger = loginDropdown.findElement(By.xpath("./.."));
            
            System.out.println("נמצא אזור התחבר/הרשם");
            
            // גלילה לאזור
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", dropdownTrigger);
            Thread.sleep(2000);
            
            // הדגשת האזור - עדינה
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "arguments[0].style.border='1px solid #000000ff';" +
                "arguments[0].style.boxShadow='0 0 3px rgba(135, 206, 235, 0.5)';" +
                "arguments[0].style.borderRadius='3px';" +
                "arguments[0].style.outline='1px solid rgba(135, 206, 235, 0.3)';" +
                "arguments[0].style.outlineOffset='2px';", 
                dropdownTrigger
            );
            Thread.sleep(2000);
            
            // צילום מסך עם אזור התחבר/הרשם מסומן
            ScreenshotUtils.takeScreenshot(driver, "02_login_dropdown_highlighted.png");
            System.out.println("✓ צילום מסך - אזור התחבר/הרשם מסומן");
            Thread.sleep(2000);
            
            TestResultsExcelWriter.addTestResult("מציאת אזור התחבר/הרשם", true, "אזור נמצא והודגש בהצלחה");
            
            // לחיצה על האזור לפתיחת התפריט
            System.out.println("\nלוחץ על אזור התחבר/הרשם לפתיחת התפריט...");
            try {
                dropdownTrigger.click();
            } catch (Exception e) {
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", dropdownTrigger);
            }
            Thread.sleep(2000);
            System.out.println("✓ התפריט נפתח");
            
            TestResultsExcelWriter.addTestResult("פתיחת תפריט התחבר/הרשם", true, "תפריט נפתח בהצלחה");
            
            // שלב 2: מחפשים את הקישור "הרשמה" בתפריט
            registerButton = driver.findElement(By.xpath("//a[@href='Register' or contains(@href, 'Register')]"));
            System.out.println("נמצא קישור הרשמה בתפריט: '" + registerButton.getText() + "'");
            
            // גלילה לכפתור
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", registerButton);
            Thread.sleep(1500);
            
            // הדגשת כפתור "הרשמה" בתפריט - עדינה
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "arguments[0].style.border='1px solid #FFB6C1';" +
                "arguments[0].style.boxShadow='0 0 3px rgba(255, 182, 193, 0.5)';" +
                "arguments[0].style.borderRadius='3px';" +
                "arguments[0].style.outline='1px solid rgba(255, 182, 193, 0.3)';" +
                "arguments[0].style.outlineOffset='2px';", 
                registerButton
            );
            Thread.sleep(3000);
            
            // צילום מסך עם כפתור הרשמה מסומן
            ScreenshotUtils.takeScreenshot(driver, "03_register_button_in_menu.png");
            System.out.println("✓ צילום מסך - כפתור הרשמה מסומן בתפריט");
            Thread.sleep(2000);
            
            TestResultsExcelWriter.addTestResult("מציאת כפתור הרשמה בתפריט", true, "כפתור נמצא והודגש: " + registerButton.getText());
            
            // לחיצה על כפתור הרשמה
            System.out.println("\nלוחץ על כפתור הרשמה...");
            try {
                registerButton.click();
            } catch (Exception e) {
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", registerButton);
            }
            Thread.sleep(3000);
            System.out.println("✓ לחץ על כפתור הרשמה");
            
            TestResultsExcelWriter.addTestResult("לחיצה על כפתור הרשמה", true, "לחיצה בוצעה בהצלחה");
            navigationSuccessful = true;
            
        } catch (Exception e) {
            System.out.println("שגיאה: " + e.getMessage());
            e.printStackTrace();
            TestResultsExcelWriter.addTestResult("ניווט לעמוד הרשמה דרך תפריט", false, "שגיאה: " + e.getMessage() + " - עובר ישירות לעמוד");
            System.out.println("עובר ישירות לעמוד ההרשמה");
            driver.get("https://www.lastprice.co.il/Register");
            Thread.sleep(3000);
        }
        
        // צילום מסך של טופס ההרשמה
        ScreenshotUtils.takeScreenshot(driver, "04_registration_form_page.png");
        System.out.println("✓ הגיע לעמוד טופס ההרשמה");
        Thread.sleep(2000);
        
        TestResultsExcelWriter.addTestResult("הגעה לעמוד טופס ההרשמה", true, "עמוד הטופס נפתח בהצלחה");

        // נתוני דוגמה למילוי לפי הסדר הנכון
        List<String> testData = Arrays.asList(
            "yaakov.levi@example.com",          // אימייל (cemail)
            "Password123!",                    // סיסמא (cpass1)
            "Password123!",                    // סיסמא בשנית (cpass2)
            "יעקב",                              // שם פרטי (cfname)
            "לוי",                              // שם משפחה (clname)
            "תל אביב - יפו",                    // עיר/יישוב (ccity - select)
            "הרצל",                        // שם הרחוב (cshn)
            "25",                              // מספר בית (cstreetshn-1)
            "ג",                               // כניסה (caddress2)
            "5",                               // מספר דירה (caddress3)
            "0541234579",                      // טלפון נייד (cphone1)
            "0501234579"                       // טלפון נוסף (cphone)
        );

        System.out.println("\n=== מתחיל למלא את הטופס ===");
        String[] fieldNames = {"אימייל", "סיסמא", "סיסמא בשנית", "שם פרטי", "שם משפחה", "עיר/יישוב", "רחוב", "מספר בית", "כניסה", "דירה", "טלפון נייד", "טלפון נוסף"};
        for (int i = 0; i < testData.size() && i < fieldNames.length; i++) {
            String displayValue = i == 1 || i == 2 ? "****" : testData.get(i);
            System.out.println(fieldNames[i] + ": " + displayValue);
        }
        System.out.println();

        // מילוי השדות
        try {
            reg.fillFormFields(testData);
            System.out.println("\n✓ הטופס מולא בהצלחה!");
            TestResultsExcelWriter.addTestResult("מילוי שדות הטופס", true, "כל 12 השדות + 2 צ'קבוקסים מולאו בהצלחה");
        } catch (Exception e) {
            TestResultsExcelWriter.addTestResult("מילוי שדות הטופס", false, "שגיאה במילוי: " + e.getMessage());
            throw e;
        }
        
        // צילום מסך לפני לחיצה
        ScreenshotUtils.takeScreenshot(driver, "05_form_filled_before_submit.png");
        System.out.println("✓ צילום מסך נשמר (לפני שליחה)");
        
        System.out.println("\nהמתן 2 שניות לצפייה...");
        Thread.sleep(2000);
        
        // מציאת כפתור ההרשמה והדגשתו
        System.out.println("\n=== מוכן ללחוץ על כפתור הרשמה ===");
        try {
            WebElement submitButton = driver.findElement(By.cssSelector("form.reg-form input[type='submit'], form.reg-form button[type='submit']"));
            
            System.out.println("נמצא כפתור הרשמה");
            
            // גלילה לכפתור
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", submitButton);
            Thread.sleep(1500);
            
            // הדגשת הכפתור - עדינה
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "arguments[0].style.border='1px solid #FFB6C1';" +
                "arguments[0].style.boxShadow='0 0 3px rgba(255, 182, 193, 0.5)';" +
                "arguments[0].style.borderRadius='3px';" +
                "arguments[0].style.outline='1px solid rgba(255, 182, 193, 0.3)';" +
                "arguments[0].style.outlineOffset='2px';", 
                submitButton
            );
            Thread.sleep(2000);
            
            // צילום מסך עם כפתור ההרשמה מסומן
            ScreenshotUtils.takeScreenshot(driver, "06_submit_button_highlighted.png");
            System.out.println("✓ צילום מסך - כפתור הרשמה מסומן");
            Thread.sleep(2000);
            
            TestResultsExcelWriter.addTestResult("הדגשת כפתור שליחת הטופס", true, "כפתור נמצא והודגש בהצלחה");
        } catch (Exception e) {
            System.out.println("לא הצליח להדגיש כפתור הרשמה: " + e.getMessage());
            TestResultsExcelWriter.addTestResult("הדגשת כפתור שליחת הטופס", false, "שגיאה: " + e.getMessage());
        }
        
        // לחיצה על כפתור הרשמה
        try {
            reg.clickRegisterButton();
            TestResultsExcelWriter.addTestResult("לחיצה על כפתור שליחת הטופס", true, "לחיצה בוצעה בהצלחה");
        } catch (Exception e) {
            TestResultsExcelWriter.addTestResult("לחיצה על כפתור שליחת הטופס", false, "שגיאה בלחיצה: " + e.getMessage());
            throw e;
        }
        
        // צילום מסך מיד אחרי לחיצה
        ScreenshotUtils.takeScreenshot(driver, "07_form_after_click.png");
        System.out.println("✓ צילום מסך נשמר (מיד אחרי לחיצה)");
        
        System.out.println("\nהמתן 5 שניות לראות את התוצאה...");
        Thread.sleep(5000);
        
        // צילום מסך סופי
        ScreenshotUtils.takeScreenshot(driver, "08_form_final_result.png");
        System.out.println("✓ צילום מסך סופי נשמר");
        
        TestResultsExcelWriter.addTestResult("צילום מסכים", true, "נשמרו 8 צילומי מסך מתהליך הבדיקה");
        
        System.out.println("\n=== הבדיקה הסתיימה ===");
        
        // הדפסת סיכום
        TestResultsExcelWriter.printSummary();
        
        // כתיבה לקובץ Excel עם שם מתאר
        String excelPath = "output/registration_test_results.xlsx";
        TestResultsExcelWriter.writeResultsToExcel(excelPath);
    }
}
