package runner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import utils.SimsConstants;
import actionlib.SimsActionLibrary;

import com.compro.core.actionlib.BaseActionLib;
import com.compro.core.locator.WebElementLocatorFromConfig;
import com.compro.core.model.TestData;
import com.compro.core.testframework.BaseTest;
import com.compro.utils.FileUtils;
import com.compro.utils.MemoryUsageTracker;
import com.opencsv.CSVReader;

public class MemoryTestRunner{
	public static boolean isMemoryUsageLogging = false;
	public static String _CurrentFileTime = "";
	public static String _serverName="";
	
	private static final String launchXPATH = "/html/body/div[@class='SIMLaunch_bodyShell_Top']/div[@class='SIMLaunch_WhiteContainerBox']/div[@class='SIMLaunch_text']/div[@class='SIMLaunch_LeftPane']/div[@class='SIMLaunch_launchButton']/img";
	
	public static int currentItemCount = 0;
	public static String currentTaskDescription = "";
	
	MemoryUsageTracker memoryTracker = null;
	public static WebDriver driver;
	public static String SIMSPID = "";
	BaseActionLib actionLibrary = null;
	WebElementLocatorFromConfig webLocator = null;
	WebDriverWait wait = null;
	FileUtils fileUtils = null;
	public static String memoryUsageFile = "MemoryUsageReport";
	public static String memoryUsageChartFile = "";

	String className = this.getClass().getSimpleName() ;
	public MemoryTestRunner(MemoryUsageTracker memoryTrackerObj) {
		// TODO Auto-generated constructor stub
		memoryTracker = memoryTrackerObj;
		fileUtils = new FileUtils();
		this.isMemoryUsageLogging = true;
	}

	public WebDriver beforeSuiteLMSMODE(TestData testData , BaseTest superClass ,String browser) throws Exception{
		BaseTest.logInfo( className, "STARTING BEFORE SUITE METHOD");
		try{
			driver = superClass.getDriverForMemoryTracking(browser);
			String systemEnvStr =  System.getProperty("envString");
			
			if(systemEnvStr == null || systemEnvStr.equalsIgnoreCase("")){
				_serverName = "stag";
			}else {
				_serverName = systemEnvStr;
			}
			String URL = "";
			URL = System.getProperty("appURL");//Change for jenkins job
			if((null == URL)){
				URL = testData.getTestURL();
			}
			memoryTracker.logPIDlist();
			launchSIMS(URL);
			Thread.sleep(2000);
			  // loging in
			SIMSPID = memoryTracker.logAppPID();
			  driver.findElement(By.xpath("//*[@id='username']")).sendKeys(SimsConstants.MEMEORYTRACKCONFIG.valueOf(_serverName+"_username_value").getValue());
			  driver.findElement(By.xpath("//*[@id='password']")).sendKeys(SimsConstants.MEMEORYTRACKCONFIG.valueOf(_serverName+"_password_value").getValue());
			  Thread.sleep(2000);
			  driver.findElement(By.xpath("//*[@class='lgn_btn']")).click();
			  Thread.sleep(1000);
			  BaseTest.logInfo(className ,"Logging in with credentials username :"+SimsConstants.MEMEORYTRACKCONFIG.valueOf(_serverName+"_username_value").getValue()+" password: "+SimsConstants.MEMEORYTRACKCONFIG.valueOf(_serverName+"_password_value").getValue());
			  Thread.sleep(5000);
			  // selecting Cource
			  wait = new WebDriverWait(driver, 100);  // wait for max 5 minutes
			  wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.partialLinkText(SimsConstants.MEMEORYTRACKCONFIG.valueOf(_serverName+"_myCourses_value").getValue()))));
			  Thread.sleep(2000);
			  driver.findElement(By.linkText(SimsConstants.MEMEORYTRACKCONFIG.valueOf(_serverName+"_myCourses_value").getValue())).click();
			  BaseTest.logInfo(className ,"Clicking course material tab");
			  driver.findElement(By.xpath("//*[@title='Course Materials']")).click();
			  Thread.sleep(2000);
			  String parentHandle = driver.getWindowHandle();

			  driver.switchTo().frame("ifrmCoursePreview");
			  // launch sims
			  String application = testData.getTestClassName().substring(0, testData.getTestClassName().lastIndexOf(".Test_"));
			  String launchAssignment = "";
			  if(!application.equalsIgnoreCase("")){
				  launchAssignment = "_launch_"+application+"_assignment";
			  }
			  wait.until(ExpectedConditions.presenceOfElementLocated((By.linkText(SimsConstants.MEMEORYTRACKCONFIG.valueOf(_serverName+launchAssignment).getValue()))));
			  Thread.sleep(2000);
			  driver.findElement(By.linkText(SimsConstants.MEMEORYTRACKCONFIG.valueOf(_serverName+launchAssignment).getValue())).click();
			  driver.switchTo().defaultContent();
			  BaseTest.logInfo(className, "Launching assignment - "+SimsConstants.MEMEORYTRACKCONFIG.valueOf(_serverName+launchAssignment).getValue() +" in new window.");
			  // switching to new window
			  Thread.sleep(2000);
			  driver = switchToNewWindow(parentHandle); // switching to window other than parent window
				try{
					WebDriverWait wdWait = new WebDriverWait(driver, 420);  // wait for max 5 minutes
					wdWait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("LoadingCurtain")));
				}catch(Exception e){ 
					BaseTest.logError(className , "Timed out error.Task not loaded in 7 minutes." );
				}
		}catch(InterruptedException ie){
			ie.printStackTrace();
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
		return driver;
	};
	
	
	public WebDriver beforeSuiteLOCALMODE(TestData testData , BaseTest superClass ,String browser) throws Exception{
		Calendar calendar = Calendar.getInstance();
		Date now = calendar.getTime();
		this._CurrentFileTime = new SimpleDateFormat("yyyy-MM-dd_hh-mm").format(new java.sql.Timestamp(now.getTime()));
		BaseTest.logInfo(className , "STARTING BEFORE SUITE METHOD");
		try{
			driver = superClass.getDriverForMemoryTracking(browser);
			BaseTest.logInfo(className ,"Driver launched successfully for memory usage.");
			String URL = "";
			URL = System.getProperty("appURL");//Change for jenkins job
			if((null == URL)){
				URL = testData.getTestURL();
			}
			memoryTracker.logPIDlist();
			launchSIMS(URL);
			SIMSPID = memoryTracker.logAppPID();
			driver.findElement(By.xpath(launchXPATH)).click();
			BaseTest.logInfo(className ,"Clicking launch button.");
			String parentHandle = driver.getWindowHandle();
		    driver = switchToNewWindow(parentHandle);
		}catch(InterruptedException ie){
			ie.printStackTrace();
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
		return driver;
	};
	
	public static WebDriver getMemoryDriver(){
		return driver;
	}
	
	public static String getSIMSPID(){
		return SIMSPID;
	}
	
	public static String getMemoryLoggerFile(){
		Calendar calendar = Calendar.getInstance();
		Date now = calendar.getTime();
		String time = new SimpleDateFormat("yyyy-MM-dd_hh-mm").format(new java.sql.Timestamp(now.getTime()));
		String fileName = memoryUsageFile + time+ ".csv";
		memoryUsageChartFile = memoryUsageFile + time;
		return fileName;
	}
	
	public void moveToNextTask(String currentDescription) throws Exception{
		driver.findElement(By.xpath("//*[@id='fwdtaskbtn']")).click();
		Thread.sleep(1000);
		BaseTest.logInfo(className ,"Clicking Next Item button.");
    	Thread.sleep(1500);
    	WebDriverWait wait;
		wait = new WebDriverWait(driver, 60);// 1 minute
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("InstructionText")));
		Thread.sleep(2000);
		List<WebElement> li = driver.findElements(By.xpath("//span[@id='questionAndPoints']"));
		if(! li.get(0).getText().contains(currentDescription)){
			BaseTest.logInfo(className ,"Task loaded is incorrect !!!");
		}else{
			BaseTest.logInfo(className ,"Task loaded is correct !!!");
		}
    	
	}
	
	public void submitAssignment(SimsActionLibrary simsActionLib) throws Exception{
		WebDriverWait wait;
		wait = new WebDriverWait(driver, 60);// 1 minute
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("LoadingImage")));
		boolean submitOnFailure;
		try{
			BaseTest.logInfo(className ,"Checking presence of submit button");
			simsActionLib.waitForOneSec();
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='CustomMessageBox']/div/div[3]/button[2]")));
			submitOnFailure = false;
		  }catch(Exception e){
			  submitOnFailure = true;
			  BaseTest.logError(className ,"Assignment OK not found");
		  }

		if(submitOnFailure){
			BaseTest.logInfo(className ,"Submit button found");
			BaseTest.logError(className , "Submitting assignment after last test (one or more tests have failed).");
			simsActionLib.clickAndWait("submitButton");
			simsActionLib.clickAndWait("Assignment_complete_ok");

		}else{
			BaseTest.logInfo(className ,"Assignment complete.Clicking Ok button");
			simsActionLib.clickAndWait("Assignment_complete_ok");

		}
	}
	
	private void launchSIMS(String assignLaunchURL) throws Exception {
		driver.get(assignLaunchURL);
		Thread.sleep(1000);
		BaseTest.logInfo(className ,"Launching URL:" + assignLaunchURL);
	}

	
	private WebDriver switchToNewWindow(String parentHandle)
	 {
		WebDriver driverOtherWindow = null;
		for(String winHandle : driver.getWindowHandles()) {
			 if(!(winHandle.equals(parentHandle))){
				 driverOtherWindow = driver.switchTo().window(winHandle); 
			 }
		}
		return driverOtherWindow;
	}
	
	
	public void generateMemoryUsageReport(String inputXlsFile) throws IOException{
	    	FileInputStream input_document =  new FileInputStream("src/test/resources/memoryLogs/template/ReportGeneratorTemplate.xlsx");    	
	    	XSSFWorkbook wb = new XSSFWorkbook(input_document);        
	    	
	        XSSFSheet raw_sheet = wb.getSheet("Raw Data");

	        CSVReader reader = new CSVReader(new FileReader("src/test/resources/memoryLogs/"+inputXlsFile));
	        String[] line;
	        int r = 0;
	        Row row;
	        while ((line = reader.readNext()) != null) {
	        	row = raw_sheet.getRow(r);
	            for (int i = 0; i < line.length; i++){
	            	row.getCell(i).setCellValue(line[i]);
	            }
	            r++;
	        }
	        wb.setForceFormulaRecalculation(true);
	       
	        reader.close();
	        File reportChartFile = new File("src/test/resources/memoryLogs/reports/"+memoryUsageChartFile+".xlsx");
	        if(!reportChartFile.exists()) {
	        	reportChartFile.createNewFile();
	        } 
	        FileOutputStream fileOut = new FileOutputStream(reportChartFile ,false);
	        wb.write(fileOut);
	        fileOut.close();
		    input_document.close();   
		    wb.close();
	}
/*
	public void submitAssignment() throws Exception{

		memoryTracker.exportToCsv(); // saving last test memory usage data

			    WebDriverWait wait;
				wait = new WebDriverWait(driver, 60);// 1 minute
				wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("LoadingImage")));
				boolean submitOnFailure;
				try{
					System.out.println("checking presence of submit button");
					actionLibrary.waitForOneSec();
					wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='CustomMessageBox']/div/div[3]/button[2]")));
					submitOnFailure = false;
	//				  wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id='submitbutton']")));
				  }catch(Exception e){
					  submitOnFailure = true;
					  System.err.println("Assignment OK not found");
				  }

				if(submitOnFailure){
					System.out.println("submit button found");
					System.err.println("Submitting assignment after last test (one or more tests have failed).");
					actionLibrary.clickAndWait("submitButton");
					actionLibrary.clickAndWait("Assignment_complete_ok");

				}
				else
					actionLibrary.clickAndWait("Assignment_complete_ok");

		  }

		  		private  final static String getTime(  )   {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss" ) ;
        df.setTimeZone( TimeZone.getTimeZone( "IST" )  ) ;


        return ( df.format( new Date(  )  )  ) ;
    }*/
}
