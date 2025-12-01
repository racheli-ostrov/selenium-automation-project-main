package tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;
import pages.RegistrationPage;
import java.util.List;

public class DebugRegisterPage extends BaseTest {

    @Test
    public void debugRegisterPageStructure() throws Exception {
        RegistrationPage reg = new RegistrationPage(driver);
        String formUrl = "https://www.lastprice.co.il/Register";
        System.out.println("פותח דף: " + formUrl);
        reg.open(formUrl);
        Thread.sleep(3000);

        System.out.println("\n=== בדיקת כל שדות הקלט בדף ===");
        List<WebElement> allInputs = driver.findElements(By.tagName("input"));
        System.out.println("סה\"כ שדות input: " + allInputs.size());
        
        for (int i = 0; i < allInputs.size(); i++) {
            WebElement input = allInputs.get(i);
            String type = input.getAttribute("type");
            String name = input.getAttribute("name");
            String id = input.getAttribute("id");
            String placeholder = input.getAttribute("placeholder");
            boolean isDisplayed = input.isDisplayed();
            boolean isEnabled = input.isEnabled();
            
            System.out.println("\nשדה " + (i+1) + ":");
            System.out.println("  type: " + type);
            System.out.println("  name: " + name);
            System.out.println("  id: " + id);
            System.out.println("  placeholder: " + placeholder);
            System.out.println("  displayed: " + isDisplayed);
            System.out.println("  enabled: " + isEnabled);
        }

        System.out.println("\n=== בדיקת טפסים (forms) ===");
        List<WebElement> forms = driver.findElements(By.tagName("form"));
        System.out.println("סה\"כ טפסים: " + forms.size());
        
        for (int i = 0; i < forms.size(); i++) {
            WebElement form = forms.get(i);
            String action = form.getAttribute("action");
            String method = form.getAttribute("method");
            String id = form.getAttribute("id");
            String className = form.getAttribute("class");
            
            System.out.println("\nטופס " + (i+1) + ":");
            System.out.println("  action: " + action);
            System.out.println("  method: " + method);
            System.out.println("  id: " + id);
            System.out.println("  class: " + className);
            
            List<WebElement> formInputs = form.findElements(By.tagName("input"));
            System.out.println("  מספר שדות בטופס: " + formInputs.size());
        }
        
        System.out.println("\n=== בדיקת רשימות נפתחות (select) ===");
        List<WebElement> selects = driver.findElements(By.tagName("select"));
        System.out.println("סה\"כ select: " + selects.size());
        
        for (int i = 0; i < selects.size(); i++) {
            WebElement select = selects.get(i);
            String name = select.getAttribute("name");
            String id = select.getAttribute("id");
            boolean isDisplayed = select.isDisplayed();
            
            System.out.println("\nרשימה " + (i+1) + ":");
            System.out.println("  name: " + name);
            System.out.println("  id: " + id);
            System.out.println("  displayed: " + isDisplayed);
            
            // הצגת כל האופציות ברשימה
            if (isDisplayed) {
                org.openqa.selenium.support.ui.Select selectObj = new org.openqa.selenium.support.ui.Select(select);
                List<WebElement> options = selectObj.getOptions();
                System.out.println("  מספר אופציות: " + options.size());
                
                // חיפוש אופציות עם "תל אביב"
                System.out.println("\n  חיפוש ערים עם 'תל אביב':");
                for (WebElement opt : options) {
                    String text = opt.getText();
                    if (text.contains("תל אביב") || text.contains("תל-אביב")) {
                        String value = opt.getAttribute("value");
                        System.out.println("    ✓ value='" + value + "' text='" + text + "'");
                    }
                }
                
                System.out.println("\n  20 האופציות הראשונות:");
                for (int j = 0; j < Math.min(20, options.size()); j++) {
                    WebElement opt = options.get(j);
                    String value = opt.getAttribute("value");
                    String text = opt.getText();
                    System.out.println("    " + (j+1) + ". value='" + value + "' text='" + text + "'");
                }
                if (options.size() > 20) {
                    System.out.println("    ... ועוד " + (options.size() - 20) + " אופציות");
                }
            }
        }
        
        System.out.println("\n=== בדיקת datalist ===");
        List<WebElement> datalists = driver.findElements(By.tagName("datalist"));
        System.out.println("סה\"כ datalist: " + datalists.size());
        
        for (int i = 0; i < datalists.size(); i++) {
            WebElement datalist = datalists.get(i);
            String id = datalist.getAttribute("id");
            List<WebElement> options = datalist.findElements(By.tagName("option"));
            
            System.out.println("\nDatalist " + (i+1) + ":");
            System.out.println("  id: " + id);
            System.out.println("  מספר אופציות: " + options.size());
            
            if (options.size() > 0 && options.size() <= 10) {
                System.out.println("  אופציות:");
                for (WebElement opt : options) {
                    System.out.println("    - " + opt.getAttribute("value"));
                }
            }
        }
        
        System.out.println("\n=== בדיקת שדות עם list attribute ===");
        List<WebElement> inputsWithList = driver.findElements(By.cssSelector("input[list]"));
        System.out.println("סה\"כ inputs עם list: " + inputsWithList.size());
        
        for (WebElement input : inputsWithList) {
            String name = input.getAttribute("name");
            String id = input.getAttribute("id");
            String listId = input.getAttribute("list");
            String placeholder = input.getAttribute("placeholder");
            boolean isDisplayed = input.isDisplayed();
            
            System.out.println("\nשדה עם רשימה:");
            System.out.println("  name: " + name);
            System.out.println("  id: " + id);
            System.out.println("  list: " + listId);
            System.out.println("  placeholder: " + placeholder);
            System.out.println("  displayed: " + isDisplayed);
        }
    }
}
