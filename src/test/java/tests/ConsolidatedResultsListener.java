package tests;

import org.testng.IExecutionListener;
import utils.ConsolidatedTestResultsManager;

/**
 * Listener שיוצר את קובץ ה-Excel המאוחד בסוף כל הבדיקות
 */
public class ConsolidatedResultsListener implements IExecutionListener {
    
    @Override
    public void onExecutionStart() {
        System.out.println("\n========================================");
        System.out.println("התחלת ריצת בדיקות - Consolidated Results");
        System.out.println("========================================\n");
        
        // ניקוי כל התוצאות הקודמות
        ConsolidatedTestResultsManager.clearAllResults();
    }
    
    @Override
    public void onExecutionFinish() {
        System.out.println("\n========================================");
        System.out.println("סיום ריצת כל הבדיקות");
        System.out.println("========================================\n");
        
        // הדפסת סיכום
        ConsolidatedTestResultsManager.printSummary();
        
        // יצירת קובץ Excel מאוחד
        String excelPath = "output/all_test_results.xlsx";
        try {
            ConsolidatedTestResultsManager.writeAllResultsToExcel(excelPath);
            System.out.println("\n✓✓✓ קובץ Excel מאוחד נוצר בהצלחה! ✓✓✓");
            System.out.println("נתיב: " + excelPath);
            System.out.println("\nהקובץ מכיל 4 גיליונות:");
            System.out.println("  1. RegistrationTests");
            System.out.println("  2. AccessibilityTests");
            System.out.println("  3. CartTests");
            System.out.println("  4. SearchAndFilterTests");
            System.out.println("\n========================================\n");
        } catch (Exception e) {
            System.err.println("שגיאה ביצירת קובץ Excel: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
