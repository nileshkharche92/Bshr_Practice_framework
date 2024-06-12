package Erad.Pages;

import Erad.Base.BaseClass;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static Erad.Objects.LoginObjects.signin;
import static Erad.Objects.LoginObjects.signup;

public class LoginPage extends BaseClass {
    WebDriver driver = null;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public void verifybtn() {
        WebElement signupbtn = driver.findElement(signup);
        signupbtn.click();
        extentTest.log(Status.INFO, MarkupHelper.createLabel("Clicked On Sign Up button", ExtentColor.BLUE));
        //driver.findElement(signin);
    }
}
