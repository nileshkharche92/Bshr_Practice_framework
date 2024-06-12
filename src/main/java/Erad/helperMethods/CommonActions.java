package Erad.helperMethods;

import Erad.Base.BaseClass;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class CommonActions {
    public static Actions actions;

    public static void scrollUptoBottom() {
        JavascriptExecutor js = (JavascriptExecutor) BaseClass.getDriver();
//        // Scroll down by a certain number of pixels (e.g., 1000 pixels)
//        js.executeScript("window.scrollBy(0, 1000);");

        // Alternatively, scroll to the bottom of the page
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    public static void scrollDownToElementByActions(By by) {
        WebElement element=BaseClass.getDriver().findElement(by);
        actions = new Actions(BaseClass.getDriver());
        actions.scrollToElement(element).perform();


    }
    public static void scrollDownByActions(int YCoordinate) {
        actions = new Actions(BaseClass.getDriver());

        actions.scrollByAmount(0,YCoordinate).perform();

    }

}
