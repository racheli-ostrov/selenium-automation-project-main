package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * מנהל תוצאות בדיקות מרוכז - קובץ Excel אחד עם 4 גיליונות
 * 
 * גיליונות:
 * 1. RegistrationTests - בדיקות הרשמה
 * 2. AccessibilityTests - בדיקות נגישות
 * 3. CartTests - בדיקות עגלת קניות
 * 4. SearchAndFilterTests - בדיקות חיפוש וסינון
 * 
 * כל גיליון מכיל:
 * - Test ID
 * - Description
 * - Input Data
 * - Expected
 * - Actual
 * - Status
 * - Screenshot Path (אופציונלי)
 */
public class ConsolidatedTestResultsManager {
    
    // מפה של תוצאות לפי שם גיליון
    private static Map<String, List<TestResult>> allResults = new HashMap<>();
    
    // שמות הגיליונות
    public static final String SHEET_REGISTRATION = "RegistrationTests";
    public static final String SHEET_ACCESSIBILITY = "AccessibilityTests";
    public static final String SHEET_CART = "CartTests";
    public static final String SHEET_SEARCH_FILTER = "SearchAndFilterTests";
    
    static {
        // אתחול הגיליונות
        allResults.put(SHEET_REGISTRATION, new ArrayList<>());
        allResults.put(SHEET_ACCESSIBILITY, new ArrayList<>());
        allResults.put(SHEET_CART, new ArrayList<>());
        allResults.put(SHEET_SEARCH_FILTER, new ArrayList<>());
    }
    
    /**
     * מחלקה המייצגת תוצאת בדיקה אחת - מורחבת לכל סוגי הבדיקות
     */
    public static class TestResult {
        private String testId;
        private String description;
        private String inputData;
        private String expected;
        private String actual;
        private String status;
        private String screenshotPath;
        private String timestamp;
        
        // שדות נוספים לבדיקות נגישות
        private String mode;  // Dark/Light
        private String action;
        private String expectedChange;
        private String actualChange;
        
        // שדות נוספים לבדיקות עגלה
        private String category;
        private String productName;
        private String qtyExpected;
        private String qtyActual;
        private String unitPriceExpected;
        private String unitPriceActual;
        private String totalExpected;
        private String totalActual;
        
        // שדות נוספים לבדיקות חיפוש
        private String step;
        private String expectedResult;
        private String actualResult;
        
        // Constructor בסיסי - לבדיקות רישום
        public TestResult(String testId, String description, String inputData, 
                         String expected, String actual, String status) {
            this.testId = testId;
            this.description = description;
            this.inputData = inputData;
            this.expected = expected;
            this.actual = actual;
            this.status = status;
            this.screenshotPath = "";
            this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        }
        
        public TestResult(String testId, String description, String inputData, 
                         String expected, String actual, String status, String screenshotPath) {
            this(testId, description, inputData, expected, actual, status);
            this.screenshotPath = screenshotPath != null ? screenshotPath : "";
        }
        
        // Getters בסיסיים
        public String getTestId() { return testId; }
        public String getDescription() { return description; }
        public String getInputData() { return inputData; }
        public String getExpected() { return expected; }
        public String getActual() { return actual; }
        public String getStatus() { return status; }
        public String getScreenshotPath() { return screenshotPath; }
        public String getTimestamp() { return timestamp; }
        
        // Getters לנגישות
        public String getMode() { return mode; }
        public String getAction() { return action; }
        public String getExpectedChange() { return expectedChange; }
        public String getActualChange() { return actualChange; }
        
        // Getters לעגלה
        public String getCategory() { return category; }
        public String getProductName() { return productName; }
        public String getQtyExpected() { return qtyExpected; }
        public String getQtyActual() { return qtyActual; }
        public String getUnitPriceExpected() { return unitPriceExpected; }
        public String getUnitPriceActual() { return unitPriceActual; }
        public String getTotalExpected() { return totalExpected; }
        public String getTotalActual() { return totalActual; }
        
        // Getters לחיפוש
        public String getStep() { return step; }
        public String getExpectedResult() { return expectedResult; }
        public String getActualResult() { return actualResult; }
        
        // Setters לנגישות
        public void setMode(String mode) { this.mode = mode; }
        public void setAction(String action) { this.action = action; }
        public void setExpectedChange(String expectedChange) { this.expectedChange = expectedChange; }
        public void setActualChange(String actualChange) { this.actualChange = actualChange; }
        
        // Setters לעגלה
        public void setCategory(String category) { this.category = category; }
        public void setProductName(String productName) { this.productName = productName; }
        public void setQtyExpected(String qtyExpected) { this.qtyExpected = qtyExpected; }
        public void setQtyActual(String qtyActual) { this.qtyActual = qtyActual; }
        public void setUnitPriceExpected(String unitPriceExpected) { this.unitPriceExpected = unitPriceExpected; }
        public void setUnitPriceActual(String unitPriceActual) { this.unitPriceActual = unitPriceActual; }
        public void setTotalExpected(String totalExpected) { this.totalExpected = totalExpected; }
        public void setTotalActual(String totalActual) { this.totalActual = totalActual; }
        
        // Setters לחיפוש
        public void setStep(String step) { this.step = step; }
        public void setExpectedResult(String expectedResult) { this.expectedResult = expectedResult; }
        public void setActualResult(String actualResult) { this.actualResult = actualResult; }
    }
    
    /**
     * הוספת תוצאת בדיקה לגיליון מסוים
     * 
     * @param sheetName שם הגיליון (SHEET_REGISTRATION, SHEET_ACCESSIBILITY, וכו')
     * @param testId מזהה הבדיקה (למשל "REG-001", "ACC-001")
     * @param description תיאור הבדיקה
     * @param inputData נתוני הקלט שהוזנו
     * @param expected תוצאה מצופה
     * @param actual תוצאה בפועל
     * @param status סטטוס: "PASS" / "FAIL" / "SKIP"
     */
    public static void addTestResult(String sheetName, String testId, String description, 
                                     String inputData, String expected, String actual, String status) {
        addTestResult(sheetName, testId, description, inputData, expected, actual, status, "");
    }
    
    /**
     * הוספת תוצאת בדיקה לגיליון מסוים עם screenshot
     */
    public static void addTestResult(String sheetName, String testId, String description, 
                                     String inputData, String expected, String actual, 
                                     String status, String screenshotPath) {
        if (!allResults.containsKey(sheetName)) {
            System.out.println("⚠ אזהרה: שם גיליון לא חוקי: " + sheetName);
            return;
        }
        
        TestResult result = new TestResult(testId, description, inputData, expected, actual, status, screenshotPath);
        allResults.get(sheetName).add(result);
        
        System.out.println("✓ נוספה תוצאה ל-" + sheetName + ": " + testId + " - " + status);
    }
    
    /**
     * ניקוי כל התוצאות (לשימוש בתחילת ריצה חדשה)
     */
    public static void clearAllResults() {
        for (String sheetName : allResults.keySet()) {
            allResults.get(sheetName).clear();
        }
        System.out.println("✓ כל התוצאות נוקו");
    }
    
    /**
     * ניקוי תוצאות של גיליון ספציפי
     */
    public static void clearSheetResults(String sheetName) {
        if (allResults.containsKey(sheetName)) {
            allResults.get(sheetName).clear();
            System.out.println("✓ תוצאות " + sheetName + " נוקו");
        }
    }
    
    /**
     * הוספת תוצאת בדיקת חיפוש וסינון
     */
    public static void addSearchFilterResult(String step, String action, String expectedResult, 
                                             String actualResult, String status, String screenshotPath) {
        TestResult result = new TestResult("", "", "", "", "", status, screenshotPath);
        result.setStep(step);
        result.setAction(action);
        result.setExpectedResult(expectedResult);
        result.setActualResult(actualResult);
        
        allResults.get(SHEET_SEARCH_FILTER).add(result);
        System.out.println("✓ נוספה תוצאה ל-" + SHEET_SEARCH_FILTER + ": " + step + " - " + status);
    }
    
    /**
     * הוספת תוצאת בדיקת נגישות
     */
    public static void addAccessibilityResult(String testId, String mode, String action,
                                              String expectedChange, String actualChange,
                                              String status, String screenshotPath) {
        TestResult result = new TestResult(testId, "", "", "", "", status, screenshotPath);
        result.setMode(mode);
        result.setAction(action);
        result.setExpectedChange(expectedChange);
        result.setActualChange(actualChange);
        
        allResults.get(SHEET_ACCESSIBILITY).add(result);
        System.out.println("✓ נוספה תוצאה ל-" + SHEET_ACCESSIBILITY + ": " + testId + " - " + status);
    }
    
    /**
     * הוספת תוצאת בדיקת עגלה
     */
    public static void addCartResult(String category, String productName, String qtyExpected,
                                     String qtyActual, String unitPriceExpected, String unitPriceActual,
                                     String totalExpected, String totalActual, String status) {
        TestResult result = new TestResult("", "", "", "", "", status);
        result.setCategory(category);
        result.setProductName(productName);
        result.setQtyExpected(qtyExpected);
        result.setQtyActual(qtyActual);
        result.setUnitPriceExpected(unitPriceExpected);
        result.setUnitPriceActual(unitPriceActual);
        result.setTotalExpected(totalExpected);
        result.setTotalActual(totalActual);
        
        allResults.get(SHEET_CART).add(result);
        System.out.println("✓ נוספה תוצאה ל-" + SHEET_CART + ": " + productName + " - " + status);
    }
    
    /**
     * כתיבת כל התוצאות לקובץ Excel אחד עם 4 גיליונות
     * 
     * @param filePath נתיב לקובץ Excel (למשל "output/all_test_results.xlsx")
     */
    public static void writeAllResultsToExcel(String filePath) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        
        // יצירת סגנונות
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle passStyle = createPassStyle(workbook);
        CellStyle failStyle = createFailStyle(workbook);
        CellStyle skipStyle = createSkipStyle(workbook);
        CellStyle normalStyle = createNormalStyle(workbook);
        
        // יצירת כל 4 הגיליונות
        createSheet(workbook, SHEET_REGISTRATION, allResults.get(SHEET_REGISTRATION), 
                   headerStyle, passStyle, failStyle, skipStyle, normalStyle);
        
        createSheet(workbook, SHEET_ACCESSIBILITY, allResults.get(SHEET_ACCESSIBILITY), 
                   headerStyle, passStyle, failStyle, skipStyle, normalStyle);
        
        createSheet(workbook, SHEET_CART, allResults.get(SHEET_CART), 
                   headerStyle, passStyle, failStyle, skipStyle, normalStyle);
        
        createSheet(workbook, SHEET_SEARCH_FILTER, allResults.get(SHEET_SEARCH_FILTER), 
                   headerStyle, passStyle, failStyle, skipStyle, normalStyle);
        
        // שמירת הקובץ
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            workbook.write(outputStream);
        }
        
        workbook.close();
        
        // הדפסת סיכום
        System.out.println("\n========================================");
        System.out.println("✓ קובץ Excel נוצר בהצלחה: " + filePath);
        System.out.println("========================================");
        System.out.println("סיכום תוצאות:");
        
        for (String sheetName : allResults.keySet()) {
            int count = allResults.get(sheetName).size();
            System.out.println("  " + sheetName + ": " + count + " בדיקות");
        }
        
        System.out.println("========================================\n");
    }
    
    /**
     * יצירת גיליון אחד בקובץ Excel - עם עמודות מותאמות לסוג הבדיקה
     */
    private static void createSheet(Workbook workbook, String sheetName, List<TestResult> results,
                                    CellStyle headerStyle, CellStyle passStyle, CellStyle failStyle,
                                    CellStyle skipStyle, CellStyle normalStyle) {
        Sheet sheet = workbook.createSheet(sheetName);
        
        // בחירת העמודות לפי סוג הבדיקה
        String[] headers;
        if (sheetName.equals(SHEET_REGISTRATION)) {
            // בדיקות רישום - טפסים
            headers = new String[]{"Test ID", "Description", "Input Data", "Expected", "Actual", "Status"};
        } else if (sheetName.equals(SHEET_ACCESSIBILITY)) {
            // בדיקות נגישות - שינוי ויזואלי
            headers = new String[]{"Test ID", "Mode", "Action", "Expected Change", "Actual Change", "Status", "Screenshot Path"};
        } else if (sheetName.equals(SHEET_CART)) {
            // בדיקות עגלה - מחירים וכמויות
            headers = new String[]{"Category", "Product Name", "Qty Expected", "Qty Actual", "Unit Price Expected", "Unit Price Actual", "Total Expected", "Total Actual", "Status"};
        } else {
            // בדיקות חיפוש וסינון - תוצאות
            headers = new String[]{"Step", "Action", "Expected Result", "Actual Result", "Status", "Screenshot Path"};
        }
        
        // יצירת שורת כותרת
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // כתיבת התוצאות
        int rowNum = 1;
        for (TestResult result : results) {
            Row row = sheet.createRow(rowNum++);
            int col = 0;
            
            if (sheetName.equals(SHEET_REGISTRATION)) {
                // עמודות לבדיקות רישום
                createCell(row, col++, result.getTestId(), normalStyle);
                createCell(row, col++, result.getDescription(), normalStyle);
                createCell(row, col++, result.getInputData(), normalStyle);
                createCell(row, col++, result.getExpected(), normalStyle);
                createCell(row, col++, result.getActual(), normalStyle);
                createCellWithStatus(row, col++, result.getStatus(), passStyle, failStyle, skipStyle, normalStyle);
                
            } else if (sheetName.equals(SHEET_ACCESSIBILITY)) {
                // עמודות לבדיקות נגישות
                createCell(row, col++, result.getTestId(), normalStyle);
                createCell(row, col++, result.getMode(), normalStyle);
                createCell(row, col++, result.getAction(), normalStyle);
                createCell(row, col++, result.getExpectedChange(), normalStyle);
                createCell(row, col++, result.getActualChange(), normalStyle);
                createCellWithStatus(row, col++, result.getStatus(), passStyle, failStyle, skipStyle, normalStyle);
                createCell(row, col++, result.getScreenshotPath(), normalStyle);
                
            } else if (sheetName.equals(SHEET_CART)) {
                // עמודות לבדיקות עגלה
                createCell(row, col++, result.getCategory(), normalStyle);
                createCell(row, col++, result.getProductName(), normalStyle);
                createCell(row, col++, result.getQtyExpected(), normalStyle);
                createCell(row, col++, result.getQtyActual(), normalStyle);
                createCell(row, col++, result.getUnitPriceExpected(), normalStyle);
                createCell(row, col++, result.getUnitPriceActual(), normalStyle);
                createCell(row, col++, result.getTotalExpected(), normalStyle);
                createCell(row, col++, result.getTotalActual(), normalStyle);
                createCellWithStatus(row, col++, result.getStatus(), passStyle, failStyle, skipStyle, normalStyle);
                
            } else {
                // עמודות לבדיקות חיפוש וסינון
                createCell(row, col++, result.getStep(), normalStyle);
                createCell(row, col++, result.getAction(), normalStyle);
                createCell(row, col++, result.getExpectedResult(), normalStyle);
                createCell(row, col++, result.getActualResult(), normalStyle);
                createCellWithStatus(row, col++, result.getStatus(), passStyle, failStyle, skipStyle, normalStyle);
                createCell(row, col++, result.getScreenshotPath(), normalStyle);
            }
        }
        
        // התאמת רוחב עמודות
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
            // הוספת מרווח נוסף
            int currentWidth = sheet.getColumnWidth(i);
            sheet.setColumnWidth(i, currentWidth + 1000);
        }
    }
    
    /**
     * יצירת תא רגיל
     */
    private static void createCell(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value != null ? value : "");
        cell.setCellStyle(style);
    }
    
    /**
     * יצירת תא עם סטטוס וצבע
     */
    private static void createCellWithStatus(Row row, int col, String status, 
                                            CellStyle passStyle, CellStyle failStyle, 
                                            CellStyle skipStyle, CellStyle normalStyle) {
        Cell cell = row.createCell(col);
        cell.setCellValue(status);
        
        if (status != null && (status.equalsIgnoreCase("PASS") || status.contains("✓"))) {
            cell.setCellStyle(passStyle);
        } else if (status != null && (status.equalsIgnoreCase("FAIL") || status.contains("✗"))) {
            cell.setCellStyle(failStyle);
        } else if (status != null && status.equalsIgnoreCase("SKIP")) {
            cell.setCellStyle(skipStyle);
        } else {
            cell.setCellStyle(normalStyle);
        }
    }
    
    /**
     * קביעת שם קטגוריה לפי שם הגיליון
     */
    private static String getCategoryName(String sheetName) {
        switch (sheetName) {
            case SHEET_REGISTRATION:
                return "בדיקות הרשמה";
            case SHEET_ACCESSIBILITY:
                return "בדיקות נגישות";
            case SHEET_CART:
                return "בדיקות עגלת קניות";
            case SHEET_SEARCH_FILTER:
                return "בדיקות חיפוש וסינון";
            default:
                return sheetName;
        }
    }
    
    /**
     * יצירת סגנון לכותרת
     */
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorders(style);
        return style;
    }
    
    /**
     * יצירת סגנון להצלחה (PASS)
     */
    private static CellStyle createPassStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        setBorders(style);
        return style;
    }
    
    /**
     * יצירת סגנון לכשלון (FAIL)
     */
    private static CellStyle createFailStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        setBorders(style);
        return style;
    }
    
    /**
     * יצירת סגנון לדילוג (SKIP)
     */
    private static CellStyle createSkipStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        setBorders(style);
        return style;
    }
    
    /**
     * יצירת סגנון רגיל
     */
    private static CellStyle createNormalStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);
        style.setVerticalAlignment(VerticalAlignment.TOP);
        setBorders(style);
        return style;
    }
    
    /**
     * הוספת גבולות לתא
     */
    private static void setBorders(CellStyle style) {
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
    }
    
    /**
     * הדפסת סיכום תוצאות לקונסול
     */
    public static void printSummary() {
        System.out.println("\n========================================");
        System.out.println("סיכום כל התוצאות");
        System.out.println("========================================");
        
        int totalTests = 0;
        int totalPassed = 0;
        int totalFailed = 0;
        int totalSkipped = 0;
        
        for (String sheetName : allResults.keySet()) {
            List<TestResult> results = allResults.get(sheetName);
            
            int passed = 0;
            int failed = 0;
            int skipped = 0;
            
            for (TestResult result : results) {
                String status = result.getStatus().toUpperCase();
                if (status.contains("PASS") || status.contains("✓")) {
                    passed++;
                } else if (status.contains("FAIL") || status.contains("✗")) {
                    failed++;
                } else if (status.contains("SKIP")) {
                    skipped++;
                }
            }
            
            totalTests += results.size();
            totalPassed += passed;
            totalFailed += failed;
            totalSkipped += skipped;
            
            System.out.println("\n" + sheetName + ":");
            System.out.println("  סה\"כ בדיקות: " + results.size());
            System.out.println("  עברו: " + passed + " ✓");
            System.out.println("  נכשלו: " + failed + " ✗");
            if (skipped > 0) {
                System.out.println("  דולגו: " + skipped);
            }
        }
        
        System.out.println("\n========================================");
        System.out.println("סה\"כ כולל:");
        System.out.println("  בדיקות: " + totalTests);
        System.out.println("  עברו: " + totalPassed + " ✓");
        System.out.println("  נכשלו: " + totalFailed + " ✗");
        if (totalSkipped > 0) {
            System.out.println("  דולגו: " + totalSkipped);
        }
        System.out.println("========================================\n");
    }
    
    /**
     * קבלת מספר תוצאות בגיליון מסוים
     */
    public static int getResultCount(String sheetName) {
        if (allResults.containsKey(sheetName)) {
            return allResults.get(sheetName).size();
        }
        return 0;
    }
    
    /**
     * קבלת מספר כולל של תוצאות
     */
    public static int getTotalResultCount() {
        int total = 0;
        for (List<TestResult> results : allResults.values()) {
            total += results.size();
        }
        return total;
    }
}
