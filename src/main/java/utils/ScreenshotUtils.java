package utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class ScreenshotUtils {

    public static String takeScreenshot(WebDriver driver, String fileName) throws Exception {
        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        Path out = Path.of("output","screenshots", fileName);
        Files.createDirectories(out.getParent());
        Files.copy(src.toPath(), out);
        return out.toString();
    }

    public static void captureScreenshot(WebDriver driver, String fileName) {
        try {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path out = Path.of("output", "screenshots", fileName + ".png");
            Files.createDirectories(out.getParent());
            Files.copy(src.toPath(), out, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            System.out.println("  📸 Screenshot saved: " + fileName + ".png");
        } catch (Exception e) {
            System.out.println("  ⚠ Could not save screenshot: " + e.getMessage());
        }
    }
}
