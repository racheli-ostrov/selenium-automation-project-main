package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TestResultsExcelWriter {
    
    private static List<TestResult> testResults = new ArrayList<>();
    
    public static class TestResult {
        private String testName;
        private String status;
        private String timestamp;
        private String details;
        
        public TestResult(String testName, String status, String details) {
            this.testName = testName;
            this.status = status;
            this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            this.details = details;
        }
        
        public String getTestName() { return testName; }
        public String getStatus() { return status; }
        public String getTimestamp() { return timestamp; }
        public String getDetails() { return details; }
    }
    
    /**
     * הוספת תוצאת בדיקה לרשימה
     */
    public static void addTestResult(String testName, boolean passed, String details) {
        String status = passed ? "הצליח ✓" : "נכשל ✗";
        testResults.add(new TestResult(testName, status, details));
    }
    
    /**
     * הוספת תוצאת בדיקה עם סטטוס מותאם אישית
     */
    public static void addTestResult(String testName, String status, String details) {
        testResults.add(new TestResult(testName, status, details));
    }
    
    /**
     * ניקוי כל התוצאות (לשימוש בתחילת ריצה חדשה)
     */
    public static void clearResults() {
        testResults.clear();
    }
    
    /**
     * כתיבת כל התוצאות לקובץ Excel
     */
    public static void writeResultsToExcel(String filePath) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("תוצאות בדיקות");
        
        // יצירת סגנון לכותרת
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        
        // יצירת סגנון להצלחה (רקע ירוק בהיר)
        CellStyle successStyle = workbook.createCellStyle();
        successStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        successStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        successStyle.setBorderBottom(BorderStyle.THIN);
        successStyle.setBorderTop(BorderStyle.THIN);
        successStyle.setBorderRight(BorderStyle.THIN);
        successStyle.setBorderLeft(BorderStyle.THIN);
        
        // יצירת סגנון לכשלון (רקע אדום בהיר)
        CellStyle failStyle = workbook.createCellStyle();
        failStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        failStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        failStyle.setBorderBottom(BorderStyle.THIN);
        failStyle.setBorderTop(BorderStyle.THIN);
        failStyle.setBorderRight(BorderStyle.THIN);
        failStyle.setBorderLeft(BorderStyle.THIN);
        
        // יצירת סגנון רגיל
        CellStyle normalStyle = workbook.createCellStyle();
        normalStyle.setBorderBottom(BorderStyle.THIN);
        normalStyle.setBorderTop(BorderStyle.THIN);
        normalStyle.setBorderRight(BorderStyle.THIN);
        normalStyle.setBorderLeft(BorderStyle.THIN);
        
        // יצירת שורת כותרת
        Row headerRow = sheet.createRow(0);
        
        String[] headers = {"שם הבדיקה", "סטטוס", "זמן", "פרטים"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // כתיבת התוצאות
        int rowNum = 1;
        for (TestResult result : testResults) {
            Row row = sheet.createRow(rowNum++);
            
            // עמודת שם הבדיקה
            Cell nameCell = row.createCell(0);
            nameCell.setCellValue(result.getTestName());
            nameCell.setCellStyle(normalStyle);
            
            // עמודת סטטוס (עם צבע)
            Cell statusCell = row.createCell(1);
            statusCell.setCellValue(result.getStatus());
            if (result.getStatus().contains("הצליח") || result.getStatus().contains("✓")) {
                statusCell.setCellStyle(successStyle);
            } else if (result.getStatus().contains("נכשל") || result.getStatus().contains("✗")) {
                statusCell.setCellStyle(failStyle);
            } else {
                statusCell.setCellStyle(normalStyle);
            }
            
            // עמודת זמן
            Cell timeCell = row.createCell(2);
            timeCell.setCellValue(result.getTimestamp());
            timeCell.setCellStyle(normalStyle);
            
            // עמודת פרטים
            Cell detailsCell = row.createCell(3);
            detailsCell.setCellValue(result.getDetails());
            detailsCell.setCellStyle(normalStyle);
        }
        
        // התאמת רוחב העמודות
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
            // הוספת מרווח נוסף
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1000);
        }
        
        // שמירת הקובץ
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            workbook.write(outputStream);
        }
        
        workbook.close();
        
        System.out.println("✓ קובץ Excel נוצר בהצלחה: " + filePath);
        System.out.println("  סה\"כ בדיקות: " + testResults.size());
    }
    
    /**
     * הדפסת סיכום התוצאות לקונסול
     */
    public static void printSummary() {
        int passed = 0;
        int failed = 0;
        
        for (TestResult result : testResults) {
            if (result.getStatus().contains("הצליח") || result.getStatus().contains("✓")) {
                passed++;
            } else if (result.getStatus().contains("נכשל") || result.getStatus().contains("✗")) {
                failed++;
            }
        }
        
        System.out.println("\n========== סיכום תוצאות ==========");
        System.out.println("סה\"כ בדיקות: " + testResults.size());
        System.out.println("הצליחו: " + passed + " ✓");
        System.out.println("נכשלו: " + failed + " ✗");
        System.out.println("===================================\n");
    }
}
