package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
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
}
