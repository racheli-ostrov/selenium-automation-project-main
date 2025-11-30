package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pages.AccessibilityPage;
import utils.ScreenshotUtils;

@Listeners(TestResultLogger.class)
public class AccessibilityTests extends BaseTest {

    private AccessibilityPage page;

    @BeforeClass
    public void setupPage() throws Exception {
        System.out.println("=== Page loaded, initializing AccessibilityPage");
        Thread.sleep(2500);
        page = new AccessibilityPage(driver);
    }

    @Test(priority = 1)
    public void testDarkContrast() throws Exception {
        System.out.println("=== Starting dark contrast test");
        page.openMenuIfCollapsed();
        Assert.assertTrue(page.isMenuOpen(), "Menu should be open at start");

        System.out.println("=== Activating dark contrast...");
        page.clickDarkContrast();
        Thread.sleep(6000); // extended visibility
        boolean darkActive = page.verifyDarkContrastActive();
        System.out.println("=== Dark contrast active? " + darkActive);
        try { System.out.println("=== Screenshot: " + ScreenshotUtils.takeScreenshot(driver, "dark_contrast_test.png")); } catch (Exception ignored) {}
        Assert.assertTrue(darkActive, "Dark contrast should be active");

        System.out.println("=== Resetting and closing menu...");
        page.resetContrast();
        Thread.sleep(2000);
        page.closeMenuIfOpen();
        Thread.sleep(1500);
    }

    @Test(priority = 2)
    public void testLightContrast() throws Exception {
        System.out.println("=== Starting light contrast test");
        page.openMenuIfCollapsed();
        Assert.assertTrue(page.isMenuOpen(), "Menu should be open at start");

        System.out.println("=== Activating light contrast...");
        page.clickLightContrast();
        Thread.sleep(6000); // extended visibility
        boolean lightActive = page.verifyLightContrastActive();
        if(!lightActive){
            System.out.println("=== Light contrast not detected, retrying...");
            page.clickLightContrast();
            Thread.sleep(2000);
            lightActive = page.verifyLightContrastActive();
        }
        System.out.println("=== Light contrast active? " + lightActive);
        try { System.out.println("=== Screenshot: " + ScreenshotUtils.takeScreenshot(driver, "light_contrast_test.png")); } catch (Exception ignored) {}
        Assert.assertTrue(lightActive, "Light contrast should be active");

        System.out.println("=== Resetting and closing menu...");
        page.resetContrast();
        Thread.sleep(2000);
        page.closeMenuIfOpen();
        Thread.sleep(1500);
    }
}
