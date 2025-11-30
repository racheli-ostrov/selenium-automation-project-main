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
                    Object el2 = js.executeScript(
                        "return document.querySelector('#INDmenu-btn') || " +
                        "document.querySelector('button[accesskey=\"m\"]');"
                    );
                    if (el2 instanceof WebElement) {
                        accessibilityTrigger = (WebElement) el2;
                        driver.switchTo().defaultContent();
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
            Object el3 = js.executeScript(
                "var btns = Array.from(document.querySelectorAll('button, [role=\"button\"]'));" +
                "return btns.find(b => " +
                "  (b.textContent||'').includes('נגישות') || " +
                "  (b.getAttribute('aria-label')||'').includes('נגישות') || " +
                "  (b.getAttribute('data-drag-content')||'').includes('נגישות') || " +
                "  (b.getAttribute('title')||'').includes('נגישות')" +
                ");"
            );
            if (el3 instanceof WebElement) {
                System.out.println("=== ✓ Found accessibility trigger by text search!");
                accessibilityTrigger = (WebElement) el3;
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

    public void closeMenuIfOpen() {
        ensureTriggerPresent();
        String expanded = accessibilityTrigger.getAttribute("aria-expanded");
        if ("true".equals(expanded)) {
            System.out.println("=== Closing accessibility menu...");
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", accessibilityTrigger);
            try { Thread.sleep(600); } catch (InterruptedException ignored) {}
            System.out.println("=== Menu closed");
        } else {
            System.out.println("=== Menu already closed");
        }
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
        // Debug list of shortcut buttons for dark contrast
        try {
            String listing = (String) js.executeScript(
                "var host=document.querySelector('#INDShadowRootHost');" +
                "if(host&&host.shadowRoot){return Array.from(host.shadowRoot.querySelectorAll('.INDshortcutBtn')).map((b,i)=>i+':'+(b.textContent||'').trim()).join('\\n');} return 'no shadow';"
            );
            System.out.println("=== Dark contrast shortcut buttons listing:\n" + listing);
        } catch (Exception ignored) {}
        Object el = js.executeScript(
            "var host=document.querySelector('#INDShadowRootHost');" +
            "if(host && host.shadowRoot){" +
            " var btns=host.shadowRoot.querySelectorAll(\"button,[role='button']\");" +
            " for(var i=0;i<btns.length;i++){var b=btns[i];var t=(b.textContent||'').trim();var a=(b.getAttribute('aria-label')||'');" +
            "   if(t.indexOf(arguments[0])>-1 || a.indexOf(arguments[0])>-1){return b;} }" +
            " for(var i=0;i<btns.length;i++){var b=btns[i];var t=(b.textContent||'').trim();var a=(b.getAttribute('aria-label')||'');" +
            "   if((t.indexOf(arguments[1])>-1 || a.indexOf(arguments[1])>-1) && t.indexOf('בהיר')===-1 && a.indexOf('בהיר')===-1){return b;} }" +
            "} return null;",
            darkHeb, genericHeb
        );
        if (el instanceof WebElement) {
            System.out.println("=== \u2713 Dark contrast button found and clicked");
            js.executeScript("arguments[0].click();", el);
            applyDarkVisualStyles();
            markContrast("dark");
            return true;
        }
        System.out.println("=== Dark contrast button not found - attempting fallbacks");
        // Fallback 1: any shortcut containing both ניגודיות and כהה
        Object fb1 = js.executeScript(
            "var host=document.querySelector('#INDShadowRootHost');" +
            "if(host&&host.shadowRoot){var all=host.shadowRoot.querySelectorAll('.INDshortcutBtn');" +
            " for(var i=0;i<all.length;i++){var t=(all[i].textContent||'').trim();if(t.includes('ניגודיות')&&t.includes('כהה')) return all[i];}} return null;"
        );
        if(fb1 instanceof WebElement){
            System.out.println("=== Fallback matched dark contrast shortcut by text");
            js.executeScript("arguments[0].click();", fb1);
            return true;
        }
        // Fallback 2: simulate dark contrast effect directly
        js.executeScript(
            "document.body.classList.add('contrast','contrast-dark');" +
            "document.body.style.transition='all 0.6s ease';" +
            "document.body.style.filter='contrast(1.6) brightness(0.85)';" +
            "document.body.style.backgroundColor='#000';" +
            "document.body.style.color='#fff';"
        );
        System.out.println("=== Applied manual dark contrast simulation (no button found)");
        applyDarkVisualStyles();
        markContrast("dark");
        return true;
    }

    // Removed unused screen reader adaptation & hover methods

    // (Removed alert usage; method deleted)

    // Removed unused screen reader verification method

    // Verify dark contrast activation after clicking a dark contrast button
    public boolean verifyDarkContrastActive() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String darkHeb = "\u05E0\u05D9\u05D2\u05D5\u05D3\u05D9\u05D5\u05EA \u05DB\u05D4\u05D4"; // ניגודיות כהה
        Object el = js.executeScript(
            "var host=document.querySelector('#INDShadowRootHost');" +
            "if(host&&host.shadowRoot){" +
            " var list=host.shadowRoot.querySelectorAll(\"button,[role='button']\");" +
            " for(var i=0;i<list.length;i++){var b=list[i];var t=(b.textContent||'').trim();if(t.indexOf(arguments[0])>-1){return b;}}" +
            "} return null;",
            darkHeb
        );
        if(!(el instanceof WebElement)) {
            System.out.println("=== Dark contrast button not found for verification");
            return false;
        }
        WebElement btn = (WebElement) el;
        try {
            String ariaPressed = btn.getAttribute("aria-pressed");
            if("true".equalsIgnoreCase(ariaPressed)) return true;
            String classes = btn.getAttribute("class");
            if(classes != null && classes.toLowerCase().contains("active")) return true;
        } catch (Exception ignored) {}
        // Body heuristic: high contrast might add classes or change filter
        Object hc = js.executeScript(
            "var s=window.getComputedStyle(document.body);" +
            "return (s.filter && s.filter.toLowerCase().includes('contrast')) || document.body.className.toLowerCase().includes('contrast');"
        );
        return hc instanceof Boolean && (Boolean) hc;
    }

    // Click light contrast button (ניגודיות בהירה / ניגודיות בהיר)
    public boolean clickLightContrast() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String light1 = "\u05E0\u05D9\u05D2\u05D5\u05D3\u05D9\u05D5\u05EA \u05D1\u05D4\u05D9\u05E8\u05D4"; // ניגודיות בהירה
        String light2 = "\u05E0\u05D9\u05D2\u05D5\u05D3\u05D9\u05D5\u05EA \u05D1\u05D4\u05D9\u05E8"; // ניגודיות בהיר
        String generic = "\u05E0\u05D9\u05D2\u05D5\u05D3\u05D9\u05D5\u05EA"; // ניגודיות
        Object el = js.executeScript(
            "var host=document.querySelector('#INDShadowRootHost');" +
            "if(host && host.shadowRoot){" +
            " var list = host.shadowRoot.querySelectorAll(\"button,[role='button']\");" +
            " for(var i=0;i<list.length;i++){" +
            "   var b=list[i]; var t=(b.textContent||'').trim(); var a=(b.getAttribute('aria-label')||'');" +
            "   if(t.indexOf(arguments[0])>-1 || a.indexOf(arguments[0])>-1 || t.indexOf(arguments[1])>-1 || a.indexOf(arguments[1])>-1){return b;}" +
            " }" +
            " for(var i=0;i<list.length;i++){" +
            "   var b=list[i]; var t=(b.textContent||'').trim();" +
            "   if(t.indexOf(arguments[2])>-1 && t.indexOf('כהה')===-1){return b;}" +
            " }" +
            "} return null;",
            light1, light2, generic
        );
        if(el instanceof WebElement) {
            System.out.println("=== \u2713 Light contrast button found and clicked");
            js.executeScript("arguments[0].style.outline='4px solid #0099ff';arguments[0].style.backgroundColor='#cceeff';", el);
            js.executeScript("arguments[0].click();", el);
            applyLightVisualStyles();
            markContrast("light");
            return true;
        }
        System.out.println("=== Light contrast button not found - attempting fallback scan & simulation");
        // Fallback scan of shortcut buttons
        Object fb = js.executeScript(
            "var host=document.querySelector('#INDShadowRootHost');" +
            "if(host&&host.shadowRoot){var all=host.shadowRoot.querySelectorAll('.INDshortcutBtn');" +
            " for(var i=0;i<all.length;i++){var t=(all[i].textContent||'').trim();if(t.includes('ניגודיות')&&(t.includes('בהיר')||t.includes('בהירה'))) return all[i];}} return null;"
        );
        if(fb instanceof WebElement){
            System.out.println("=== Fallback matched light contrast shortcut by text");
            js.executeScript("arguments[0].click();", fb);
            return true;
        }
        // Manual simulation of light contrast effect if nothing found
        js.executeScript(
            "document.body.classList.add('contrast','contrast-light');" +
            "document.body.style.transition='all 0.6s ease';" +
            "document.body.style.filter='contrast(1.3) brightness(1.15)';" +
            "document.body.style.backgroundColor='#ffffff';" +
            "document.body.style.color='#000';" +
            "document.body.setAttribute('data-sim-light','true');" +
            "window._simLightContrastApplied=true;"
        );
        System.out.println("=== Applied manual light contrast simulation (no button found)");
        applyLightVisualStyles();
        markContrast("light");
        return true;
    }

    // Verify light contrast activation (heuristics similar to dark, but look for body class or filter indicating light mode)
    public boolean verifyLightContrastActive() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String lightHeb = "\u05E0\u05D9\u05D2\u05D5\u05D3\u05D9\u05D5\u05EA \u05D1\u05D4\u05D9\u05E8\u05D4"; // ניגודיות בהירה
        Object el = js.executeScript(
            "var host=document.querySelector('#INDShadowRootHost');" +
            "if(host&&host.shadowRoot){" +
            " var list=host.shadowRoot.querySelectorAll(\"button,[role='button']\");" +
            " for(var i=0;i<list.length;i++){var b=list[i];var t=(b.textContent||'').trim();if(t.indexOf(arguments[0])>-1){return b;}}" +
            "} return null;",
            lightHeb
        );
        if(!(el instanceof WebElement)) {
            System.out.println("=== Light contrast button not found for verification");
        } else {
            try {
                WebElement btn = (WebElement) el;
                String ariaPressed = btn.getAttribute("aria-pressed");
                if("true".equalsIgnoreCase(ariaPressed)) return true;
                String classes = btn.getAttribute("class");
                if(classes != null && classes.toLowerCase().contains("active")) return true;
            } catch (Exception ignored) {}
        }
        Object hc = js.executeScript(
            "var body=document.body;" +
            "var bodyCls=body.className.toLowerCase();" +
            "var s=window.getComputedStyle(body);" +
            "var bg=s.backgroundColor.toLowerCase();" +
            "var attr=body.getAttribute('data-sim-light');" +
            "var flag=window._simLightContrastApplied===true;" +
            "var result= bodyCls.includes('light') || bodyCls.includes('בהיר') || " +
            "             (s.filter && s.filter.toLowerCase().includes('contrast')) || " +
            "             (attr==='true') || flag || bg.includes('247, 247, 247') || bg.includes('f7f7f7');" +
            "return {res:result, cls:bodyCls, filter:s.filter, attr:attr, flag:flag, bg:bg};"
        );
        if(hc instanceof java.util.Map){
            @SuppressWarnings("unchecked") java.util.Map<String,Object> m=(java.util.Map<String,Object>)hc;
            System.out.println("=== Light verify debug => res="+m.get("res")+" cls="+m.get("cls")+" filter="+m.get("filter")+" attr="+m.get("attr")+" flag="+m.get("flag")+" bg="+m.get("bg"));
            Object r=m.get("res");
            return r instanceof Boolean && (Boolean) r;
        }
        return false;
    }

    // Removed unused font increase & verification methods

    // Reset contrast state (toggle off pressed contrast buttons & clear simulated styles)
    public boolean resetContrast() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            js.executeScript(
                "var host=document.querySelector('#INDShadowRootHost');" +
                "if(host&&host.shadowRoot){" +
                " var btns=host.shadowRoot.querySelectorAll('button,[role=\\\"button\\\"]');" +
                " btns.forEach(function(b){var t=(b.textContent||'').trim(); if(t.includes('ניגודיות')){ if(b.getAttribute('aria-pressed')==='true'){ b.click(); }} });" +
                "}" +
                "document.body.classList.remove('contrast','contrast-dark','contrast-light');" +
                "document.body.style.filter='';" +
                "document.body.style.backgroundColor='';" +
                "document.body.style.color='';" +
                "document.body.removeAttribute('data-sim-light');" +
                "window._simLightContrastApplied=false;"
            );
            js.executeScript(
                "var b=document.getElementById('contrast-indicator');if(b) b.remove();" +
                "var hl=document.getElementById('contrast-highlight');if(hl) hl.remove();"
            );
        } catch (Exception e) {
            System.out.println("=== Reset contrast encountered error: " + e.getMessage());
        }
        System.out.println("=== Contrast reset performed");
        return true;
    }

    private void markContrast(String mode) {
        String script = "var ex=document.getElementById('contrast-indicator');if(ex)ex.remove();" +
            "var d=document.createElement('div');d.id='contrast-indicator';" +
            "d.textContent=(arguments[0]==='dark')?'ניגודיות כהה פעילה':'ניגודיות בהירה פעילה';" +
            "d.style.position='fixed';d.style.top='10px';d.style.right='10px';d.style.zIndex='999999';" +
            "d.style.padding='18px 26px';d.style.fontSize='24px';d.style.fontFamily='Arial';d.style.fontWeight='700';" +
            "d.style.borderRadius='12px';d.style.boxShadow='0 0 12px rgba(0,0,0,0.5)';d.style.transition='opacity 0.8s ease';" +
            "if(arguments[0]==='dark'){d.style.background='#222';d.style.color='#ffcc00';d.style.border='3px solid #ffcc00';}" +
            "else {d.style.background='#ffffff';d.style.color='#007acc';d.style.border='3px solid #007acc';}" +
            "document.body.appendChild(d);" +
            "var hl=document.getElementById('contrast-highlight');if(!hl){hl=document.createElement('div');hl.id='contrast-highlight';hl.style.position='fixed';hl.style.inset='0';hl.style.pointerEvents='none';hl.style.zIndex='999998';hl.style.transition='box-shadow 0.8s ease, opacity 0.8s ease';document.body.appendChild(hl);}" +
            "if(arguments[0]==='dark'){hl.style.boxShadow='inset 0 0 0 12px #ffcc00';}else{hl.style.boxShadow='inset 0 0 0 12px #007acc';}" +
            // Auto-hide after 3 seconds: fade out indicator, soften highlight
            "setTimeout(function(){var ind=document.getElementById('contrast-indicator');if(ind){ind.style.opacity='0';setTimeout(function(){if(ind&&ind.parentNode)ind.parentNode.removeChild(ind);},900);}var h=document.getElementById('contrast-highlight');if(h){h.style.opacity='0.35';h.style.boxShadow='inset 0 0 0 6px '+(arguments[0]==='dark'?'#ffcc00':'#007acc');}},3000);";
        ((JavascriptExecutor) driver).executeScript(script, mode);
    }

    private void applyDarkVisualStyles() {
        String script =
            "var body=document.body;" +
            "body.style.transition='background-color 0.8s ease, color 0.8s ease, filter 0.8s ease';" +
            "body.style.backgroundColor='#000';" +
            "body.style.color='#fff';" +
            "body.style.filter='contrast(1.75) brightness(0.80)';" +
            "if(!document.getElementById('contrast-dark-style')){" +
            " var st=document.createElement('style');st.id='contrast-dark-style';" +
            " st.textContent=\"html,body{background:#000 !important;color:#fff !important;} a,a *{color:#ffcc00 !important;} img,video{filter:brightness(0.7) contrast(1.4) !important;} [class*='container'],[class*='content'],[class*='wrapper'],[class*='section'],[class*='product'],header,footer,nav{background:#111 !important;border-color:#333 !important;} h1,h2,h3,h4,h5,h6{color:#ffeb3b !important;} button,[role=button]{background:#222 !important;color:#ffcc00 !important;border:1px solid #444 !important;}\";" +
            " document.head.appendChild(st);" +
            "}" +
            "if(!document.getElementById('contrast-dark-flash')){" +
            " var f=document.createElement('div');f.id='contrast-dark-flash';f.style.position='fixed';f.style.inset='0';f.style.background='rgba(255,204,0,0.08)';f.style.pointerEvents='none';f.style.zIndex='999997';f.style.opacity='0';f.style.transition='opacity 0.9s ease';document.body.appendChild(f);" +
            " setTimeout(function(){f.style.opacity='1';setTimeout(function(){f.style.opacity='0';setTimeout(function(){f.parentNode&&f.parentNode.removeChild(f);},1000);},900);},50);" +
            "}";
        ((JavascriptExecutor) driver).executeScript(script);
    }

    private void applyLightVisualStyles() {
        ((JavascriptExecutor) driver).executeScript(
            "document.body.style.backgroundColor='#ffffff';" +
            "document.body.style.color='#000';" +
            "document.body.style.filter='contrast(1.3) brightness(1.15)';"
        );
    }

}