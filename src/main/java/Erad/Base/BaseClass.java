package Erad.Base;

import Erad.config.DefineConstants;
import Erad.helperMethods.JsonUtils;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.io.FileHandler;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class BaseClass {
    public static WebDriver driver;
    public static ExtentSparkReporter extentSparkReporter;
    public static ExtentReports extentReports;
    public static ExtentTest extentTest;

    @Parameters("browserName")
    @BeforeTest
    public void setUp(ITestContext context, @Optional("chrome") String browserName) {

        extentSparkReporter = new ExtentSparkReporter(".//extentReports//" + "ExtentSpark.html");
        extentReports = new ExtentReports();
        extentReports.attachReporter(extentSparkReporter);
        extentReports.setSystemInfo("OS", System.getProperty("os.name"));
        switch (browserName.toLowerCase()) {
            case ("chrome"):
                System.setProperty("webdriver.chrome.driver", DefineConstants.ChromeDriver);

                ChromeOptions options=new ChromeOptions();
                Map<String,Object> prefs=new HashMap<String,Object>();
                // Set the browser language to English
                prefs.put("intl.accept_languages", "en");
                // Add the preferences to ChromeOptions
                options.setExperimentalOption("prefs", prefs);
                options.addArguments("--incognito");
                // Initialize ChromeDriver instance
                driver = new ChromeDriver(options);

                break;
            case ("edge"):
                System.setProperty("webdriver.edge.driver", DefineConstants.EdgeDriver);
                // Initialize ChromeDriver instance
                driver = new EdgeDriver();
                break;
            default:
                System.out.println("Invalid browser");
                break;
        }
        driver.manage().timeouts().pageLoadTimeout(Duration.ofMillis(60000L));
        driver.manage().window().maximize();
        extentTest = extentReports.createTest(context.getName());//To provide test name to report
        driver.get(JsonUtils.getData(DefineConstants.LogInData, "URL"));
    }

//    @BeforeSuite
//    public void initializeExtentReport(ITestContext context, Method m) {
//        extentSparkReporter = new ExtentSparkReporter(".//extentReports//" + m.getName()+".html");
//        extentReports = new ExtentReports();
//        extentReports.attachReporter(extentSparkReporter);
//        extentReports.setSystemInfo("OS", System.getProperty("os.name"));
//
//    }

    @AfterSuite
    public void createExtentReport() {
        extentReports.flush();
    }

    @AfterMethod
    public void verifyStatus(ITestResult result, ITestContext context, Method method) throws IOException {
        if (result.getStatus() == ITestResult.FAILURE) {
            extentTest.addScreenCaptureFromBase64String(takeScreenshotAsBase64());//To add screenshot from Base64 code which is captured in code
//            //we can provide the title to that screenshot in second parameter
            extentTest.fail(result.getThrowable());
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            extentTest.pass(method.getName() + " get passed", MediaEntityBuilder.createScreenCaptureFromBase64String(takeScreenshotAsBase64()).build());
        }
        extentTest.info(MarkupHelper.createLabel("Executed the Test As " + method.getName(), ExtentColor.GREEN));
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        extentSparkReporter.config().setDocumentTitle(method.getName() + "_ExtentReport_On " + date.format(dateTimeFormatter));
        extentTest.assignAuthor(context.getCurrentXmlTest().getParameter("Author"));
    }

    @AfterTest
    public void tearDown() {
        // Close the browser
        driver.quit();
    }

    public static WebDriver getDriver() {
        return driver;
    }

    public static String takeScreenshot(String fileName) throws IOException {
        File sourceFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        File destinationFile = new File("./Screenshots/" + fileName + ".jpg");
        FileHandler.copy(sourceFile, destinationFile);
        return destinationFile.getAbsolutePath();
    }

    public static String takeScreenshotAsBase64() throws IOException {
        String base64Code = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
        return base64Code;
    }
}
