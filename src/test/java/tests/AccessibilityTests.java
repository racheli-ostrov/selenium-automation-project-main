package tests;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.AccessibilityPage;
import java.time.Duration;

public class AccessibilityTests extends BaseTest {

    // Focused test: open floating accessibility widget and toggle contrast
    @Test
    public void testContrastDimScreen() throws Exception {
        driver.get("https://www.lastprice.co.il");
        System.out.println("=== Page loaded: https://www.lastprice.co.il");
        
        // Wait a bit for dynamic content
        Thread.sleep(3000);
        
        // Try to close cookie banner if present
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(
                "var selectors = ['button[id*=\"cookie\"]', 'button[class*=\"cookie\"]', 'button[id*=\"accept\"]', " +
                "'button[class*=\"accept\"]', 'a[id*=\"cookie\"]', '.cookie-close', '#cookie-accept']; " +
                "for(var s of selectors) { " +
                "  var btn = document.querySelector(s); " +
                "  if(btn) { btn.click(); console.log('Closed cookie banner'); break; } " +
                "}"
            );
            Thread.sleep(1000);
            System.out.println("=== Attempted to close cookie banner");
        } catch (Exception e) {
            System.out.println("=== No cookie banner or failed to close: " + e.getMessage());
        }
        
        // Debug: print all buttons on page
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String btnInfo = (String) js.executeScript(
            "return Array.from(document.querySelectorAll('button')).slice(0,20).map(b => " +
            "b.id + '|' + b.className + '|' + b.textContent.substring(0,20)).join('\\n');"
        );
        System.out.println("=== First 20 buttons on page:\n" + btnInfo);
        
        // Debug: search for ANYTHING related to accessibility
        String accessInfo = (String) js.executeScript(
            "var results = []; " +
            "document.querySelectorAll('*').forEach(el => { " +
            "  var id = el.id || ''; var cls = el.className || ''; var tag = el.tagName; " +
            "  if (id.toLowerCase().includes('access') || cls.toLowerCase().includes('access') || " +
            "      id.includes('IND') || cls.includes('IND') || " +
            "      (el.textContent && el.textContent.includes('נגישות'))) { " +
            "    results.push(tag + '#' + id + '.' + cls.substring(0,30)); " +
            "    if (results.length >= 10) return; " +
            "  } " +
            "}); " +
            "return results.join('\\n');"
        );
        System.out.println("=== Elements related to accessibility:\n" + accessInfo);
        
        AccessibilityPage accessibilityPage = new AccessibilityPage(driver);

        // Open menu if collapsed (aria-expanded=false)
        try {
            accessibilityPage.openMenuIfCollapsed();
        } catch (Exception e) {
            // Take screenshot on failure
            try {
                utils.ScreenshotUtils.takeScreenshot(driver, "accessibility_trigger_not_found");
            } catch (Exception ignored) {}
            throw e;
        }
        
        Assert.assertTrue(accessibilityPage.isMenuOpen(), "Accessibility menu should be open (aria-expanded=true)");

        // Capture background before
        String bgBefore = accessibilityPage.getBodyBackgroundColor();
        String bodyClassBefore = (String) js.executeScript("return document.body.className;");
        String bodyStyleBefore = (String) js.executeScript("return document.body.getAttribute('style') || '';");
        System.out.println("=== Before contrast - BG: " + bgBefore + ", Class: " + bodyClassBefore);

        // Click dark contrast (ניגודיות כהה)
        System.out.println("=== Attempting to click 'ניגודיות כהה' button...");
        boolean darkContrastClicked = accessibilityPage.clickDarkContrast();
        if (!darkContrastClicked) {
            // As a fallback, click the first shortcut (generic contrast)
            accessibilityPage.clickContrast();
            System.out.println("=== ✓ Fallback: clicked generic contrast shortcut");
        } else {
            System.out.println("=== ✓ Dark contrast button clicked!");
        }
        
        // Wait 10 seconds to see the visual effect on screen
        System.out.println("=== Waiting 10 seconds to observe screen changes...");
        Thread.sleep(10000);
        System.out.println("=== Wait complete, verifying changes...");

        // Wait for ANY change (class, style, or background-color)
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        boolean changed = wait.until(d -> {
            String bgAfter = accessibilityPage.getBodyBackgroundColor();
            String bodyClassAfter = (String) js.executeScript("return document.body.className;");
            String bodyStyleAfter = (String) js.executeScript("return document.body.getAttribute('style') || '';");
            
            boolean bgChanged = !bgAfter.equals(bgBefore);
            boolean classChanged = !bodyClassAfter.equals(bodyClassBefore);
            boolean styleChanged = !bodyStyleAfter.equals(bodyStyleBefore);
            
            if (bgChanged || classChanged || styleChanged) {
                System.out.println("=== ✓ Change detected!");
                System.out.println("    BG changed: " + bgChanged + " (was: " + bgBefore + ", now: " + bgAfter + ")");
                System.out.println("    Class changed: " + classChanged);
                System.out.println("    Style changed: " + styleChanged);
                return true;
            }
            return false;
        });

        Assert.assertTrue(changed, "Body should change after contrast toggle (bg-color, class, or style)");
        
        // Click screen-reader adaptation (התאמה לקורא-מסך)
        System.out.println("=== Attempting to click 'התאמה לקורא-מסך' button...");
        boolean srClicked = accessibilityPage.clickScreenReaderAdaptation();
        if (srClicked) {
            System.out.println("=== ✓ Screen reader adaptation clicked!");
        } else {
            System.out.println("=== ⚠ Screen reader adaptation button not found.");
        }

        // Wait 10 seconds to observe effect of screen-reader adaptation
        System.out.println("=== Waiting 10 seconds for screen-reader adaptation effect...");
        Thread.sleep(10000);
        System.out.println("=== Screen-reader observation complete");
    }
}
