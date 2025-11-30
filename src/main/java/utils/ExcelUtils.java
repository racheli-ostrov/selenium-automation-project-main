package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.List;

public class ExcelUtils {

    public static void writeCartToExcel(String path, List<pages.CartPage.CartItem> items, String total) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Cart");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Name");
        header.createCell(1).setCellValue("Price");
        header.createCell(2).setCellValue("Quantity");
        header.createCell(3).setCellValue("RowTotal");

        int rowIdx = 1;
        for (pages.CartPage.CartItem it : items) {
            Row r = sheet.createRow(rowIdx++);
            r.createCell(0).setCellValue(it.name);
            r.createCell(1).setCellValue(it.price);
            r.createCell(2).setCellValue(it.qty);
            r.createCell(3).setCellValue(it.rowTotal);
        }
        Row totalRow = sheet.createRow(rowIdx + 1);
        totalRow.createCell(2).setCellValue("Cart Total");
        totalRow.createCell(3).setCellValue(total);

        File file = new File(path);
        file.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
        }
        workbook.close();
    }

    // Append a single test result (testName, status) into an Excel file.
    // If the file does not exist, create it and write a header row first.
    public static synchronized void appendTestResult(String path, String testName, String status) {
        File file = new File(path);
        Workbook workbook = null;
        Sheet sheet = null;
        try {
            if (file.exists()) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    workbook = new XSSFWorkbook(fis);
                }
            } else {
                workbook = new XSSFWorkbook();
            }
            sheet = workbook.getSheet("Results");
            if (sheet == null) {
                sheet = workbook.createSheet("Results");
                Row header = sheet.createRow(0);
                header.createCell(0).setCellValue("Test Name");
                header.createCell(1).setCellValue("Status");
            }
            int lastRow = sheet.getLastRowNum();
            // If sheet only has header and no data yet, lastRow might be 0
            // Determine actual next row index (handle empty sheet case)
            int nextRowIdx = (sheet.getPhysicalNumberOfRows() == 0) ? 0 : lastRow + 1;
            Row r = sheet.createRow(nextRowIdx);
            r.createCell(0).setCellValue(testName);
            r.createCell(1).setCellValue(status);
            // Autosize columns for readability (lightweight for small sheet)
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            file.getParentFile().mkdirs();
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (workbook != null) {
                try { workbook.close(); } catch (Exception ignored) {}
            }
        }
    }
}
