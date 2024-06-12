package Erad.helperMethods;


import Erad.Base.BaseClass;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class WaitTypes {

	WebDriver driver = BaseClass.getDriver();
	WebDriverWait wait;

	public  WaitTypes(WebDriver driver) {
		this.driver = driver;
	}

	public WebElement waitForElementToBeClickable(WebElement Element, Duration timeout) {

		try {
			wait = new WebDriverWait(driver, timeout);
			wait.until(ExpectedConditions.elementToBeClickable(Element));

		} catch (Exception e) {
			e.getMessage();
		}

		return Element;
	}

	public WebElement waitforElementToBeDisplayed(WebElement Element, Duration timeout) {
		try {
			wait = new WebDriverWait(driver, timeout);
			wait.until(ExpectedConditions.visibilityOf(Element));
		} catch (Exception e) {
			e.getMessage();
			System.out.println("" + e.getStackTrace());
		}
		return Element;
	}
	
	public WebElement waitforPresenceOfElementLocated(WebElement Element, Duration timeout) {
		try {
			wait = new WebDriverWait(driver, timeout);
			wait.until(ExpectedConditions.refreshed(ExpectedConditions.stalenessOf(Element)));
		} catch (Exception e) {
			e.getMessage();
			System.out.println("" + e.getStackTrace());
		}
		return Element;
	}
	
	public void scrollUptoBotton()
	{
	JavascriptExecutor js=	(JavascriptExecutor)driver;
	js.executeAsyncScript("window.scrollTo(0,document.body.scrollHeight);");
	}

}