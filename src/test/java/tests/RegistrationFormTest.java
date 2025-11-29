package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.RegistrationPage;
import utils.ScreenshotUtils;

public class RegistrationFormTest extends BaseTest {

    @Test
    public void testRegistrationFormValidAndInvalid() throws Exception {
        RegistrationPage reg = new RegistrationPage(driver);

        // Example: open a registration page on lastprice (replace with actual reg URL)
        // LastPrice may require login flow; if not available, use other provided registration URL
        String registrationUrl = "https://www.lastprice.co.il/account/register"; // may need update
        reg.open(registrationUrl);

        // Positive case
        reg.fillGenericRegistration("TestFirst", "TestLast", "autotest+" + System.currentTimeMillis() + "@example.com", "StrongPass123!", "0501234567", "01/01/1990");
        ScreenshotUtils.takeScreenshot(driver, "registration_filled.png");
        reg.submit();

        // Wait and check success message or redirection
        // For robustness, just assert that URL changed or success message present
        Thread.sleep(2000);
        Assert.assertNotEquals(driver.getCurrentUrl(), registrationUrl, "Expect redirect or form processed");

        // Negative case: invalid email
        reg.open(registrationUrl);
        reg.fillGenericRegistration("T", "L", "invalid-email", "123", "abc", ""); // intentionally bad data
        ScreenshotUtils.takeScreenshot(driver, "registration_invalid.png");
        reg.submit();
        Thread.sleep(1000);
        // look for error messages - best effort
        boolean hasError = driver.getPageSource().toLowerCase().contains("error")
                || driver.getPageSource().toLowerCase().contains("שגיאה")
                || driver.getPageSource().toLowerCase().contains("invalid");
        Assert.assertTrue(hasError, "Expect some error message for invalid data (best-effort check)");
    }
}
