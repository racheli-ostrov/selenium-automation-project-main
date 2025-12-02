package tests;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.AccessibilityPage;
import utils.ConsolidatedTestResultsManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AccessibilityTests extends BaseTest {

    private AccessibilityPage page;
    private static final String SHEET_NAME = ConsolidatedTestResultsManager.SHEET_ACCESSIBILITY;

    @BeforeClass
    public void setupPage() throws Exception {
        ConsolidatedTestResultsManager.clearSheetResults(SHEET_NAME);
        System.out.println("=== ניקוי תוצאות קודמות - " + SHEET_NAME + " ===");
        System.out.println("=== Page loaded, initializing AccessibilityPage");
        Thread.sleep(2500);
        page = new AccessibilityPage(driver);
    }
    
    /**
     * צילום מסך
     */
    private String takeScreenshot(String fileName) {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fullFileName = fileName + "_" + timestamp + ".png";
            String screenshotPath = "output/screenshots/" + fullFileName;
            
            File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destinationFile = new File(screenshotPath);
            destinationFile.getParentFile().mkdirs();
            FileUtils.copyFile(screenshotFile, destinationFile);
            
            System.out.println("✓ צילום מסך נשמר: " + screenshotPath);
            return screenshotPath;
        } catch (Exception e) {
            System.out.println("⚠ שגיאה בצילום מסך: " + e.getMessage());
            return "";
        }
    }

    @Test(priority = 1)
    public void testDarkContrast() throws Exception {
        System.out.println("\n========================================");
        System.out.println("טסט 1: בדיקת ניגודיות כהה");
        System.out.println("========================================\n");
        
        System.out.println("=== פתיחת תפריט נגישות");
        page.openMenuIfCollapsed();
        Assert.assertTrue(page.isMenuOpen(), "Menu should be open at start");

        // צילום מסך לפני הפעלת ניגודיות כהה
        String screenshotBefore = takeScreenshot("accessibility_dark_before");
        Thread.sleep(1000);

        System.out.println("=== הפעלת ניגודיות כהה...");
        page.clickDarkContrast();
        Thread.sleep(6000); // המתנה לשינוי ויזואלי
        
        // צילום מסך אחרי הפעלת ניגודיות כהה
        String screenshotAfter = takeScreenshot("accessibility_dark_after");
        
        boolean darkActive = page.verifyDarkContrastActive();
        System.out.println("=== ניגודיות כהה פעילה? " + darkActive);
        
        if (darkActive) {
            ConsolidatedTestResultsManager.addAccessibilityResult(
                "ACC-001", "Dark", "לחיצה על כפתור ניגודיות כהה",
                "הרקע הופך לכהה והטקסט לבהיר", "הניגודיות הכהה הופעלה - רקע כהה וטקסט בהיר",
                "PASS", screenshotAfter
            );
        } else {
            ConsolidatedTestResultsManager.addAccessibilityResult(
                "ACC-001", "Dark", "לחיצה על כפתור ניגודיות כהה",
                "הרקע הופך לכהה והטקסט לבהיר", "הניגודיות הכהה לא הופעלה",
                "FAIL", screenshotAfter
            );
        }
        
        Assert.assertTrue(darkActive, "Dark contrast should be active");

        System.out.println("=== איפוס וסגירת תפריט...");
        page.resetContrast();
        Thread.sleep(2000);
        page.closeMenuIfOpen();
        Thread.sleep(1500);
        
        System.out.println("=== טסט 1 הסתיים ===\n");
    }

    @Test(priority = 2)
    public void testLightContrast() throws Exception {
        System.out.println("\n========================================");
        System.out.println("טסט 2: בדיקת ניגודיות בהירה");
        System.out.println("========================================\n");
        
        System.out.println("=== פתיחת תפריט נגישות");
        page.openMenuIfCollapsed();
        Assert.assertTrue(page.isMenuOpen(), "Menu should be open at start");

        // צילום מסך לפני הפעלת ניגודיות בהירה
        String screenshotBefore = takeScreenshot("accessibility_light_before");
        Thread.sleep(1000);

        System.out.println("=== הפעלת ניגודיות בהירה...");
        page.clickLightContrast();
        Thread.sleep(6000); // המתנה לשינוי ויזואלי
        
        boolean lightActive = page.verifyLightContrastActive();
        if(!lightActive){
            System.out.println("=== ניגודיות בהירה לא זוהתה, מנסה שוב...");
            page.clickLightContrast();
            Thread.sleep(2000);
            lightActive = page.verifyLightContrastActive();
        }
        
        // צילום מסך אחרי הפעלת ניגודיות בהירה
        String screenshotAfter = takeScreenshot("accessibility_light_after");
        
        System.out.println("=== ניגודיות בהירה פעילה? " + lightActive);
        
        if (lightActive) {
            ConsolidatedTestResultsManager.addAccessibilityResult(
                "ACC-002", "Light", "לחיצה על כפתור ניגודיות בהירה",
                "הרקע הופך לבהיר והטקסט לכהה", "הניגודיות הבהירה הופעלה - רקע בהיר וטקסט כהה",
                "PASS", screenshotAfter
            );
        } else {
            ConsolidatedTestResultsManager.addAccessibilityResult(
                "ACC-002", "Light", "לחיצה על כפתור ניגודיות בהירה",
                "הרקע הופך לבהיר והטקסט לכהה", "הניגודיות הבהירה לא הופעלה",
                "FAIL", screenshotAfter
            );
        }
        
        Assert.assertTrue(lightActive, "Light contrast should be active");

        System.out.println("=== איפוס וסגירת תפריט...");
        page.resetContrast();
        Thread.sleep(2000);
        page.closeMenuIfOpen();
        Thread.sleep(1500);
        
        System.out.println("=== טסט 2 הסתיים ===\n");
    }
    
    @AfterClass
    public void tearDownTests() {
        System.out.println("\n========================================");
        System.out.println("סיום בדיקות נגישות");
        System.out.println("========================================\n");
        
        try {
            ConsolidatedTestResultsManager.writeAllResultsToExcel("output/all_test_results.xlsx");
            ConsolidatedTestResultsManager.printSummary();
        } catch (Exception e) {
            System.out.println("שגיאה בשמירת תוצאות: " + e.getMessage());
        }
    }
}
