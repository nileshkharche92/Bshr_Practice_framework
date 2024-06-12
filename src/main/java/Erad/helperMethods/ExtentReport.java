package Erad.helperMethods;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import java.io.IOException;

public class ExtentReport {
    public static void main(String[] args) throws IOException {
        // Create ExtentReports and attach reporter(s)
        ExtentSparkReporter htmlReporter = new ExtentSparkReporter("extent.html");
        ExtentReports extent = new ExtentReports();
        extent.attachReporter(htmlReporter);

        // Create a Test
        ExtentTest test = extent.createTest("MyFirstTest", "Sample description");

        // Log test steps
        test.log(Status.INFO, "Starting test execution");
        test.log(Status.PASS, "Step 1 - Test step passed");

        // Simulate a failure
        test.log(Status.FAIL, "Step 2 - Test step failed");
        test.fail("Failed due to assertion failure");

//        // Add a screenshot
//        String screenshotPath = "path/to/screenshot.png"; // Replace with actual screenshot path
//        test.addScreenCaptureFromPath(screenshotPath, "Screenshot");

        // Log another step
        test.log(Status.INFO, "Step 3 - Another test step");

        // Mark test as completed
        extent.flush();
    }
}

