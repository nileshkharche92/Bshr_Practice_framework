package Erad.config;

public class DefineConstants {
	// Test data file Path
	public static final String Path_TestData = "";
	

	public static final String PROJECT_PATH = System.getProperty("user.dir");

	public static final String PROJECT_OS = System.getProperty("os.name");
	
	public static final String browser = "chrome";

	public static final String TestData_Folder = "Test_Data";
	public static final String Erad_TestData_Folder = "Erad";

	public static final String ChromeDriver = PROJECT_PATH+"//drivers//chromedriver.exe";
	public static final String EdgeDriver = PROJECT_PATH+"//drivers//msedgedriver.exe";

	public static final String LogInData = TestData_Folder + "//" + Erad_TestData_Folder + "//"+ "Login.json";
	public static final String ExcelPath = PROJECT_PATH+"//"+TestData_Folder + "//" + Erad_TestData_Folder + "//"+ "DataExcel.xlsx";

}