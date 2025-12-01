package tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;
import java.util.List;

public class DebugHomePage extends BaseTest {

    @Test
    public void debugHomePageElements() throws Exception {
        String homeUrl = "https://www.lastprice.co.il";
        System.out.println("=== פותח עמוד הבית ===");
        driver.get(homeUrl);
        Thread.sleep(5000); // המתנה לטעינה מלאה
        
        System.out.println("\n=== חיפוש ה-span עם טקסט 'התחבר/הרשם' ===");
        WebElement spanElement = driver.findElement(By.xpath("//span[contains(text(), 'התחבר/הרשם')]"));
        System.out.println("נמצא span: '" + spanElement.getText() + "'");
        System.out.println("  Tag: " + spanElement.getTagName());
        System.out.println("  Class: " + spanElement.getAttribute("class"));
        System.out.println("  ID: " + spanElement.getAttribute("id"));
        
        // בודקים את ההורה (parent)
        WebElement parent = spanElement.findElement(By.xpath("./.."));
        System.out.println("\nהורה של ה-span:");
        System.out.println("  Tag: " + parent.getTagName());
        System.out.println("  Class: " + parent.getAttribute("class"));
        System.out.println("  ID: " + parent.getAttribute("id"));
        System.out.println("  Text: '" + parent.getText() + "'");
        
        if (parent.getTagName().equals("a")) {
            System.out.println("  Href: " + parent.getAttribute("href"));
        }
        
        // בודקים את הסבא (grandparent)
        WebElement grandParent = parent.findElement(By.xpath("./.."));
        System.out.println("\nסבא של ה-span:");
        System.out.println("  Tag: " + grandParent.getTagName());
        System.out.println("  Class: " + grandParent.getAttribute("class"));
        System.out.println("  ID: " + grandParent.getAttribute("id"));
        
        if (grandParent.getTagName().equals("a")) {
            System.out.println("  Href: " + grandParent.getAttribute("href"));
        }
        
        // מחפשים את הקישור הקרוב ביותר
        System.out.println("\n=== מחפש את הקישור הקרוב ביותר ===");
        try {
            // מנסה למצוא קישור סביב ה-span
            WebElement linkElement = (WebElement) ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("return arguments[0].closest('a');", spanElement);
            
            if (linkElement != null) {
                System.out.println("נמצא קישור עם closest:");
                System.out.println("  Tag: " + linkElement.getTagName());
                System.out.println("  Href: " + linkElement.getAttribute("href"));
                System.out.println("  Text: '" + linkElement.getText() + "'");
                System.out.println("  Displayed: " + linkElement.isDisplayed());
            } else {
                System.out.println("לא נמצא קישור עם closest");
            }
        } catch (Exception e) {
            System.out.println("שגיאה ב-closest: " + e.getMessage());
        }
        
        // מדפיס את כל המבנה סביב ה-span
        System.out.println("\n=== HTML של ה-span והאזור סביבו ===");
        String html = (String) ((org.openqa.selenium.JavascriptExecutor) driver)
            .executeScript("return arguments[0].parentElement.parentElement.parentElement.outerHTML;", spanElement);
        System.out.println(html.substring(0, Math.min(1000, html.length())));
    }
}
