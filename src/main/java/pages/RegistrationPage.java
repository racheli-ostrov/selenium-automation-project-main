package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;


public class RegistrationPage extends BasePage {

    public RegistrationPage(WebDriver driver) {
        super(driver);
    }

    public void open(String url) {
        driver.get(url);
    }

    // מילוי טופס כללי - מוצא את טופס ההרשמה וממלא את השדות באיטיות
    public void fillFormFields(List<String> values) throws Exception {
        // חיפוש טופס ההרשמה
        List<WebElement> forms = driver.findElements(By.cssSelector("form.reg-form"));
        
        if (forms.isEmpty()) {
            System.out.println("לא נמצא טופס הרשמה, מחפש טופס כללי");
            forms = driver.findElements(By.tagName("form"));
        }
        
        if (forms.isEmpty()) {
            System.out.println("אין טפסים בדף");
            return;
        }
        
        // שימוש בטופס ההרשמה
        WebElement regForm = forms.get(0);
        System.out.println("נמצא טופס: " + regForm.getAttribute("class"));
        
        // הסדר הנכון: מייל, סיסמא, סיסמא בשנית, שם פרטי, שם משפחה, עיר, רחוב, מספר בית, כניסה, דירה, טלפון נייד, טלפון נוסף
        String[] fieldNames = {"cemail", "cpass1", "cpass2", "cfname", "clname", "ccity", "cshn", "cstreetshn-1", "caddress2", "caddress3", "cphone1", "cphone"};
        String[] fieldLabels = {"אימייל", "סיסמא", "סיסמא בשנית", "שם פרטי", "שם משפחה", "עיר/יישוב", "שם הרחוב", "מספר בית", "כניסה", "מספר דירה", "טלפון נייד", "טלפון נוסף"};
        
        int index = 0;
        for (int i = 0; i < fieldNames.length; i++) {
            if (index >= values.size()) break;
            
            try {
                // טיפול מיוחד בשדה עיר (select)
                if (fieldNames[i].equals("ccity")) {
                    WebElement citySelect = driver.findElement(By.id("ccity-selection"));
                    if (citySelect.isDisplayed() && citySelect.isEnabled()) {
                        org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(citySelect);
                        select.selectByVisibleText(values.get(index));
                        System.out.println("✓ נבחרה " + fieldLabels[i] + ": " + values.get(index));
                        Thread.sleep(2000); // המתנה ארוכה יותר לאחר בחירת עיר - הדף מבצע פעולות JavaScript
                        index++;
                    }
                } else {
                    // טיפול בשדות רגילים
                    WebElement input = regForm.findElement(By.name(fieldNames[i]));
                    if (input.isDisplayed() && input.isEnabled()) {
                        // גלילה לשדה ווידוא שהוא גלוי
                        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", input);
                        Thread.sleep(300);
                        
                        input.clear();
                        Thread.sleep(300);
                        input.sendKeys(values.get(index));
                        String displayValue = (fieldNames[i].contains("pass")) ? "****" : values.get(index);
                        System.out.println("✓ מולא " + fieldLabels[i] + ": " + displayValue);
                        Thread.sleep(500);
                        index++;
                    }
                }
            } catch (Exception e) {
                System.out.println("לא נמצא שדה: " + fieldNames[i] + " - " + e.getMessage());
            }
        }
        
        // סימון צ'קבוקסים - הצ'קבוקסים מוסתרים, צריך ללחוץ על ה-label שלהם
        System.out.println("\n=== מסמן צ'קבוקסים ===");
        
        try {
            // גלילה לסוף הטופס
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
            Thread.sleep(1000);
            
            int checkedCount = 0;
            
            // צ'קבוקס 1: הצטרפות לרשימת תפוצה (ContactList)
            try {
                WebElement contactLabel = driver.findElement(By.cssSelector("label[for='rf-ContactList'], label:has(input[name='ContactList'])"));
                if (contactLabel != null) {
                    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", contactLabel);
                    System.out.println("✓ אושר: הצטרפות לרשימת תפוצה");
                    Thread.sleep(400);
                    checkedCount++;
                }
            } catch (Exception e) {
                // ניסיון חלופי - חיפוש לפי טקסט
                try {
                    List<WebElement> labels = driver.findElements(By.tagName("label"));
                    for (WebElement label : labels) {
                        if (label.getText().contains("רשימת תפוצה") || label.getText().contains("מעודכן")) {
                            label.click();
                            System.out.println("✓ אושר: הצטרפות לרשימת תפוצה (לפי טקסט)");
                            Thread.sleep(400);
                            checkedCount++;
                            break;
                        }
                    }
                } catch (Exception e2) {
                    System.out.println("לא ניתן לסמן צ'קבוקס רשימת תפוצה");
                }
            }
            
            // צ'קבוקס 2: תנאי שימוש (UseTerms)
            try {
                WebElement termsLabel = driver.findElement(By.cssSelector("label[for='rf-UseTerms'], label:has(input[name='UseTerms'])"));
                if (termsLabel != null) {
                    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", termsLabel);
                    System.out.println("✓ אושר: תנאי שימוש");
                    Thread.sleep(400);
                    checkedCount++;
                }
            } catch (Exception e) {
                // ניסיון חלופי - חיפוש לפי טקסט
                try {
                    List<WebElement> labels = driver.findElements(By.tagName("label"));
                    for (WebElement label : labels) {
                        if (label.getText().contains("תנאי שימוש") || label.getText().contains("תקנון")) {
                            label.click();
                            System.out.println("✓ אושר: תנאי שימוש (לפי טקסט)");
                            Thread.sleep(400);
                            checkedCount++;
                            break;
                        }
                    }
                } catch (Exception e2) {
                    System.out.println("לא ניתן לסמן צ'קבוקס תנאי שימוש");
                }
            }
            
            System.out.println("סה\"כ אושרו " + checkedCount + " צ'קבוקסים");
            
        } catch (Exception e) {
            System.out.println("שגיאה בסימון צ'קבוקסים: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    // לחיצה על כפתור הרשמה
    public void clickRegisterButton() throws Exception {
        List<WebElement> forms = driver.findElements(By.cssSelector("form.reg-form"));
        if (!forms.isEmpty()) {
            WebElement regForm = forms.get(0);
            try {
                // מחפש כפתור submit
                List<WebElement> submitButtons = regForm.findElements(By.cssSelector("input[type='submit'], button[type='submit']"));
                
                if (!submitButtons.isEmpty()) {
                    WebElement submitButton = submitButtons.get(0);
                    System.out.println("לוחץ על כפתור הרשמה...");
                    
                    // גלילה לכפתור והמתנה
                    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", submitButton);
                    Thread.sleep(1000);
                    
                    System.out.println("לוחץ על כפתור הרשמה...");
                    
                    // ניסיון ללחיצה רגילה
                    try {
                        submitButton.click();
                    } catch (Exception e) {
                        // אם לא הצליח, לחיצה עם JavaScript
                        System.out.println("  מנסה לחיצה עם JavaScript...");
                        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", submitButton);
                    }
                    
                    System.out.println("✓ נלחץ כפתור הרשמה");
                    Thread.sleep(1000); // המתנה קצרה לאחר הלחיצה
                } else {
                    System.out.println("לא נמצא כפתור הרשמה");
                }
            } catch (Exception e) {
                System.out.println("שגיאה בלחיצה על כפתור: " + e.getMessage());
            }
        }
    }

    public int countVisibleInputFields() {
        List<WebElement> forms = driver.findElements(By.cssSelector("form.reg-form"));
        
        if (forms.isEmpty()) {
            forms = driver.findElements(By.tagName("form"));
        }
        
        if (forms.isEmpty()) {
            return 0;
        }
        
        WebElement regForm = forms.get(0);
        String[] fieldNames = {"cfname", "clname", "cemail", "cphone1", "ccity", "cshn", "cstreetshn-1", "caddress2", "cphone"};
        
        int count = 0;
        for (String fieldName : fieldNames) {
            try {
                WebElement input = regForm.findElement(By.name(fieldName));
                if (input.isDisplayed() && input.isEnabled()) {
                    count++;
                }
            } catch (Exception e) {
                // שדה לא נמצא
            }
        }
        return count;
    }

    public void submit() {
        List<WebElement> submitButtons = driver.findElements(By.cssSelector("button[type='submit'], input[type='submit'], button.submit, .submit-button"));
        for (WebElement btn : submitButtons) {
            if (btn.isDisplayed() && btn.isEnabled()) {
                btn.click();
                return;
            }
        }
    }
}
