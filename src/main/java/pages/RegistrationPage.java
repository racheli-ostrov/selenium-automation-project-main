package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;


public class RegistrationPage extends BasePage {

    // Example typical fields
    private By firstName = By.cssSelector("input[name='firstName'], input[id*='firstname']");
    private By lastName = By.cssSelector("input[name='lastName'], input[id*='lastname']");
    private By email = By.cssSelector("input[type='email'], input[name*='email']");
    private By password = By.cssSelector("input[type='password'], input[name*='password']");
    private By phone = By.cssSelector("input[type='tel'], input[name*='phone']");
    private By dob = By.cssSelector("input[name*='birth'], input[id*='dob']");
    private By submitBtn = By.cssSelector("button[type='submit'], input[type='submit']");

    public RegistrationPage(WebDriver driver) {
        super(driver);
    }

    public void open(String url) {
        driver.get(url);
    }

    public void fillGenericRegistration(String fName, String lName, String mail, String pwd, String phoneNum, String birth) {
        type(firstName, fName);
        type(lastName, lName);
        type(email, mail);
        type(password, pwd);
        type(phone, phoneNum);
        type(dob, birth);
    }

    public void submit() {
        click(submitBtn);
    }
}
