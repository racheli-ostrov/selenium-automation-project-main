package utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class VisualUtils {

    public static void highlight(WebDriver driver, WebElement el, String label) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior:'smooth',block:'center'});", el);
            ((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute('data-visual-label', arguments[1]);", el, label);
            ((JavascriptExecutor) driver).executeScript("arguments[0].style.outline='3px solid magenta'; arguments[0].style.transition='outline 0.3s';", el);
            Thread.sleep(600);
            ScreenshotUtils.captureScreenshot(driver, "highlight_" + label.replaceAll("[^A-Za-z0-9_]","_"));
        } catch (Exception e) {
            System.out.println("Could not highlight element: " + e.getMessage());
        }
    }
}
