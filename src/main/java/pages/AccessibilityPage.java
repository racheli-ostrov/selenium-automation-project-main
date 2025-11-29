package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;



public class AccessibilityPage extends BasePage {

    public AccessibilityPage(WebDriver driver) {
        super(driver, true); // Skip PageFactory - we manually locate elements
    }

    // Floating accessibility trigger button (circle) - manually resolved, NOT via PageFactory
    private WebElement accessibilityTrigger = null;

    // Open menu if collapsed (aria-expanded = false)
    private void ensureTriggerPresent() {
        if (accessibilityTrigger != null) {
            return;
        }
        
        JavascriptExecutor js = (JavascriptExecutor) driver;
        System.out.println("=== Starting search for accessibility trigger...");
        
        // Trigger page interactions to force widget load
        js.executeScript("window.scrollTo(0, 100);");
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        js.executeScript("window.scrollTo(0, 0);");
        System.out.println("=== Triggered scroll interactions");
        
        // Phase 1: JS polling loop (up to 20 seconds with interactions)
        long deadline = System.currentTimeMillis() + 20000;
        int attempt = 0;
        while (System.currentTimeMillis() < deadline) {
            // Try regular DOM first
            Object el = js.executeScript(
                "return document.querySelector('#INDmenu-btn') || " +
                "document.querySelector('button[accesskey=\"m\"]') || " +
                "document.querySelector('button[class*=\"INDcircle-btn\"]') || " +
                "document.querySelector('[id*=\"menu-btn\"]') || " +
                "document.querySelector('[class*=\"accessibility\"][class*=\"btn\"]');"
            );
            
            // Try Shadow DOM (INDShadowRootHost)
            if (el == null || !(el instanceof WebElement)) {
                el = js.executeScript(
                    "var host = document.querySelector('#INDShadowRootHost') || document.querySelector('[id*=\"Shadow\"]'); " +
                    "if (host && host.shadowRoot) { " +
                    "  return host.shadowRoot.querySelector('#INDmenu-btn') || " +
                    "         host.shadowRoot.querySelector('button[accesskey=\"m\"]') || " +
                    "         host.shadowRoot.querySelector('button[class*=\"INDcircle\"]'); " +
                    "} " +
                    "return null;"
                );
            }
            
            if (el instanceof WebElement) {
                System.out.println("=== ✓ Found accessibility trigger" + (attempt > 0 ? " after " + attempt + " attempts!" : "!"));
                accessibilityTrigger = (WebElement) el;
                return;
            }
            
            if (attempt == 0 || attempt == 10 || attempt == 20) {
                System.out.println("=== Polling attempt " + attempt + ", element not found yet...");
            }
            
            // Every 5 attempts, trigger mouse movement
            if (++attempt % 5 == 0) {
                js.executeScript("document.body.dispatchEvent(new MouseEvent('mousemove', {bubbles: true}));");
            }
            
            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        }
        
        // Phase 2: Check all iframes
        try {
            for (WebElement iframe : driver.findElements(By.tagName("iframe"))) {
                try {
                    driver.switchTo().frame(iframe);
                    Object el = js.executeScript(
                        "return document.querySelector('#INDmenu-btn') || " +
                        "document.querySelector('button[accesskey=\"m\"]');"
                    );
                    if (el instanceof WebElement) {
                        accessibilityTrigger = (WebElement) el;
                        return;
                    }
                    driver.switchTo().defaultContent();
                } catch (Exception ignored) {
                    driver.switchTo().defaultContent();
                }
            }
        } catch (Exception ignored) {}
        
        // Phase 3: Last resort - search ALL buttons and find by text/aria
        System.out.println("=== Phase 3: Searching all buttons by text/aria...");
        try {
            driver.switchTo().defaultContent();
            Object el = js.executeScript(
                "var btns = Array.from(document.querySelectorAll('button, [role=\"button\"]'));" +
                "console.log('Total buttons found: ' + btns.length);" +
                "return btns.find(b => " +
                "  (b.textContent||'').includes('נגישות') || " +
                "  (b.getAttribute('aria-label')||'').includes('נגישות') || " +
                "  (b.getAttribute('data-drag-content')||'').includes('נגישות') || " +
                "  (b.getAttribute('title')||'').includes('נגישות')" +
                ");"
            );
            if (el instanceof WebElement) {
                System.out.println("=== ✓ Found accessibility trigger by text search!");
                accessibilityTrigger = (WebElement) el;
                return;
            }
        } catch (Exception e) {
            System.out.println("=== Phase 3 failed: " + e.getMessage());
        }
        
        // Save page source info for debugging
        try {
            String pageSource = driver.getPageSource();
            System.out.println("=== Page source length: " + pageSource.length());
            System.out.println("=== Contains 'INDmenu': " + pageSource.contains("INDmenu"));
            System.out.println("=== Contains 'נגישות': " + pageSource.contains("נגישות"));
            System.out.println("=== Contains 'accessibility': " + pageSource.contains("accessibility"));
        } catch (Exception ignored) {}
        
        throw new RuntimeException("Accessibility trigger not found after 20s polling + iframe + text search. Widget may not be present on this page.");
    }

    public void openMenuIfCollapsed() {
        ensureTriggerPresent();
        
        // Check if already expanded
        String expanded = accessibilityTrigger.getAttribute("aria-expanded");
        System.out.println("=== Menu aria-expanded: " + expanded);
        
        if ("false".equals(expanded) || expanded == null) {
            System.out.println("=== Clicking trigger to open menu...");
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", accessibilityTrigger);
            
            // Wait for menu to open
            WebDriverWait localWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            try {
                localWait.until(d -> {
                    String attr = accessibilityTrigger.getAttribute("aria-expanded");
                    return "true".equals(attr);
                });
                System.out.println("=== ✓ Menu opened successfully!");
            } catch (Exception e) {
                System.out.println("=== Warning: aria-expanded didn't change to true, but proceeding...");
            }
        } else {
            System.out.println("=== Menu already open");
        }
    }

    public boolean isMenuOpen() {
        ensureTriggerPresent();
        String expanded = accessibilityTrigger.getAttribute("aria-expanded");
        return "true".equals(expanded);
    }

    // Attempt to locate and click the contrast/dim button using multiple fallback selectors
    public void clickContrast() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        
        // Debug: List all buttons in shadow DOM
        String shadowButtons = (String) js.executeScript(
            "var host = document.querySelector('#INDShadowRootHost'); " +
            "if (host && host.shadowRoot) { " +
            "  var btns = Array.from(host.shadowRoot.querySelectorAll('button, [role=\\\"button\\\"]')); " +
            "  return btns.map(b => b.className + '|' + (b.getAttribute('aria-label')||'') + '|' + b.textContent.substring(0,15)).join('\\\\n'); " +
            "} " +
            "return 'Shadow root not found';"
        );
        System.out.println("=== Buttons in Shadow DOM:\n" + shadowButtons);
        
        // Try Shadow DOM with simpler approach
        Object shadowBtn = js.executeScript(
            "var host = document.querySelector('#INDShadowRootHost'); " +
            "if (host && host.shadowRoot) { " +
            "  var btns = host.shadowRoot.querySelectorAll('.INDshortcutBtn'); " +
            "  if (btns.length > 0) return btns[0]; " +
            "  btns = host.shadowRoot.querySelectorAll('button:not(.INDcircle-btn)'); " +
            "  if (btns.length > 0) return btns[0]; " +
            "} " +
            "return null;"
        );
        
        if (shadowBtn instanceof WebElement) {
            System.out.println("=== ✓ Found contrast button in Shadow DOM");
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", shadowBtn);
            return;
        }
        
        // Fallback: try regular DOM
        By[] candidates = new By[]{
                By.cssSelector(".accessibility-contrast"),
                By.cssSelector(".contrast-toggle"),
                By.cssSelector(".INDcontrast-btn"),
                By.cssSelector("button[aria-label*='contrast' i]"),
                By.cssSelector("button[aria-label*='ניגוד']"),
                By.cssSelector("[role='button'][aria-label*='contrast' i]"),
                By.cssSelector("[role='button'][aria-label*='ניגוד']")
        };
        WebElement found = null;
        for (By c : candidates) {
            try {
                found = driver.findElement(c);
                if (found != null && found.isDisplayed()) {
                    break;
                }
            } catch (Exception ignored) {}
        }
        if (found == null) {
            throw new RuntimeException("Contrast button not found in Shadow DOM or regular DOM.");
        }
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", found);
    }

    // Helper to get current body background color for asserting change
    public String getBodyBackgroundColor() {
        WebElement body = driver.findElement(By.tagName("body"));
        return body.getCssValue("background-color");
    }

    // Click dark contrast button specifically (ניגודיות כהה)
    public boolean clickDarkContrast() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String darkHeb = "\u05E0\u05D9\u05D2\u05D5\u05D3\u05D9\u05D5\u05EA \u05DB\u05D4\u05D4"; // ניגודיות כהה
        String genericHeb = "\u05E0\u05D9\u05D2\u05D5\u05D3\u05D9\u05D5\u05EA"; // ניגודיות
        Object el = js.executeScript(
                "var host=document.querySelector('#INDShadowRootHost');" +
                "if(host&&host.shadowRoot){" +
                "  var btns=Array.from(host.shadowRoot.querySelectorAll('button, [role=\"button\"]'));" +
                "  for(var i=0;i<btns.length;i++){var b=btns[i];var t=(b.textContent||'').trim();var a=(b.getAttribute('aria-label')||'');" +
                "    if(t.indexOf(arguments[0])>-1||a.indexOf(arguments[0])>-1){return b;}}" +
                "  for(var i=0;i<btns.length;i++){var b=btns[i];var t=(b.textContent||'').trim();var a=(b.getAttribute('aria-label')||'');" +
                "    if(t.indexOf(arguments[1])>-1||a.indexOf(arguments[1])>-1){return b;}}" +
                "}return null;",
                darkHeb, genericHeb
        );
        if (el instanceof WebElement) {
            System.out.println("=== \u2713 Dark contrast button found and clicked");
            js.executeScript("arguments[0].click();", el);
            return true;
        }
        System.out.println("=== Dark contrast button not found");
        return false;
    }

    // Click screen reader adaptation button specifically (התאמה לקורא-מסך)
    public boolean clickScreenReaderAdaptation() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String exactHeb = "\u05D4\u05EA\u05D0\u05DE\u05D4 \u05DC\u05E7\u05D5\u05E8\u05D0-\u05DE\u05E1\u05DA"; // התאמה לקורא-מסך
        String noHyphenHeb = "\u05D4\u05EA\u05D0\u05DE\u05D4 \u05DC\u05E7\u05D5\u05E8\u05D0 \u05DE\u05E1\u05DA"; // התאמה לקורא מסך
        String genericHeb = "\u05E7\u05D5\u05E8\u05D0 \u05DE\u05E1\u05DA"; // קורא מסך
        Object el = js.executeScript(
                "var host=document.querySelector('#INDShadowRootHost');" +
                "if(host&&host.shadowRoot){" +
                "  var btns=Array.from(host.shadowRoot.querySelectorAll('button, [role=\"button\"]'));" +
                "  for(var i=0;i<btns.length;i++){var b=btns[i];var t=(b.textContent||'').trim();var a=(b.getAttribute('aria-label')||'');" +
                "    if(t.indexOf(arguments[0])>-1||a.indexOf(arguments[0])>-1){return b;}}" +
                "  for(var i=0;i<btns.length;i++){var b=btns[i];var t=(b.textContent||'').trim();var a=(b.getAttribute('aria-label')||'');" +
                "    if(t.indexOf(arguments[1])>-1||a.indexOf(arguments[1])>-1){return b;}}" +
                "  for(var i=0;i<btns.length;i++){var b=btns[i];var t=(b.textContent||'').trim();var a=(b.getAttribute('aria-label')||'');" +
                "    if(t.indexOf(arguments[2])>-1||a.indexOf(arguments[2])>-1){return b;}}" +
                "}return null;",
                exactHeb, noHyphenHeb, genericHeb
        );
        if (el instanceof WebElement) {
            System.out.println("=== \u2713 Screen reader adaptation button found and clicked");
            js.executeScript("arguments[0].click();", el);
            return true;
        }
        System.out.println("=== Screen reader adaptation button not found");
        return false;
    }
}