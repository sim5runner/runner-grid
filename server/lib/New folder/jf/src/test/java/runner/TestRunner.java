package runner;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import locator.SimsXpathPropertiesService;
import locator.xpathLocator;

import org.dom4j.Document;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import utils.SimsConstants;
import actionlib.SimsActionLibrary;

import com.compro.core.model.TestData;
import com.compro.core.testframework.BaseTest;
import com.compro.utils.FileUtils;
import com.compro.utils.MemoryUsageTracker;

import exceptions.InvalidTaskActionException;
import exceptions.InvalidTaskXMLException;

public class TestRunner extends BaseTest{
	static WebDriverWait wait; //make the driver wait for 
	boolean pathwayFailed = true; //boolean for pathway pass status
	int currentItemNum = 0;

	String taskApplication = "";
	
	Document taskXMLDoc = null; //document for task xml
	String propertyFilename = "";
	//public WebDriver driver = null;
	
	TestData testData = null;
	
	String className = "";
	String distributed = "";
	String taskName= "";
	
	String taskClassname = "";
	SimsActionLibrary  seleniumActionLib = null;
	Properties config_properties = null;
	
	String pathwyName = "";
	public boolean memoryTrackingEnabled;
	public static MemoryTestRunner memoryTestRunner = null;
	public static MemoryUsageTracker memoryTracker = null;
	public String SimsPID = "";
	public static String _CurrentTaskMemory = "";
	public static String _CurrentClassName = "";
	
	public static String memoryLogFile = "";
	String current_method_no = "";
	boolean xpathFromDb = true;
	
	@Override
	public void setTestData(TestData testData) {
		// TODO Auto-generated method stub
		this.testData = testData;
	}

	
	@Override
	public void setDriver(WebDriver driver) {
		// TODO Auto-generated method stub
		this.driver = driver;
	}
	@Override
	public void beforeSuite() throws Exception {
		// TODO Auto-generated method stub
	}
	
	
	@Parameters({SimsConstants.MEMEORY_USAGE_TRACKING_PARAM_NAME , SimsConstants.MODE_PARAM})	
	@BeforeSuite(groups = {"Acceptance", "Keyboard", "Primary", "Ribbon", "Other", "Right Click", "Toolbar", "Robot"})
	public void beforeSuite(@Optional("false") String memoryUsage , @Optional("local") String mode) throws Exception {
		// TODO Auto-generated method stub
		if(memoryUsage != null || !memoryUsage.equalsIgnoreCase("")){
			memoryTrackingEnabled = Boolean.parseBoolean(memoryUsage);
		}
		
		if(memoryTrackingEnabled){
			logInstruction("===================================================================== \n MEMORY USAGE TRACKING ON  - RUNNING "+mode+" MODE \n=====================================================================");

			memoryTracker = new MemoryUsageTracker(SimsConstants.DEFAULT_MEMORY_BROWSER);
			memoryTestRunner =  new MemoryTestRunner(memoryTracker);
			BaseTest obj = super.getClass().newInstance();
			if(mode.equalsIgnoreCase("local")){
				driver = memoryTestRunner.beforeSuiteLOCALMODE(testData,obj,SimsConstants.DEFAULT_MEMORY_BROWSER);
			}else{
				driver = memoryTestRunner.beforeSuiteLMSMODE(testData,obj,SimsConstants.DEFAULT_MEMORY_BROWSER);
			}
			SimsPID = MemoryTestRunner.getSIMSPID();
			memoryLogFile = MemoryTestRunner.getMemoryLoggerFile();
			String csvHeaderContent = "Task id,Task Launch,Item 1 Practice On,Item 1 Practice Complete,Item 2 Launch,Item 2 Practice On,Item 2 Practice Complete,Item 3 Launch,Item 3 Practice On,Item 3 Practice Complete";
			MemoryUsageTracker.writeFile(csvHeaderContent, memoryLogFile);
		}
	}
	
	@Override
	@BeforeClass()
	public void beforeClass() throws Exception {
		
	}
	
	@Parameters({SimsConstants.MEMEORY_USAGE_TRACKING_PARAM_NAME})
	@BeforeClass(groups = {"Acceptance", "Keyboard", "Primary", "Ribbon", "Other", "Right Click", "Toolbar", "Robot"})
	public void beforeClass(@Optional("false") String memoryUsage) throws Exception {
			className = testData.getTestClassName();
			taskName = className.substring(className.lastIndexOf(".")+1);
			taskApplication = className.substring(0, className.lastIndexOf(".Test_"));
			taskClassname = className.substring(className.lastIndexOf("Test_"),className.length()).replaceAll("Test_", "").replaceAll(".java", "");
			distributed = testData.getDistributeEnv();
			propertyFilename = taskApplication + "_config.properties";
			logInstruction("===================================================================== \n TEST STARTED  "+taskName  +"\n=====================================================================");

			 // load task xml of the launched task
		    getTaskXMLToDoc(taskClassname);
		  
		    SimsXpathPropertiesService xpathService = new xpathLocator();
		    this.config_properties = xpathService.getProperties(xpathFromDb, taskName , propertyFilename);
		    //this.config_properties = getValidatedConfigFile();
			
			if(memoryUsage != null){
				memoryTrackingEnabled = Boolean.parseBoolean(memoryUsage);
			}
			if(memoryTrackingEnabled){
				String currentDescription = testData.getTaskFeatureMap().get("description");
				SimsPID = MemoryTestRunner.getSIMSPID();
				driver = MemoryTestRunner.getMemoryDriver();
				TestRunner._CurrentClassName = taskName;
				MemoryUsageTracker.writeFile("\n "+TestRunner._CurrentClassName + "," , memoryLogFile);
				try{
					if(!validateTask(currentDescription)){
						memoryTestRunner.moveToNextTask(currentDescription);
					}
			     }catch(Exception e){
			    	 e.printStackTrace();
			     }	
			}
		
			if(!memoryTrackingEnabled){
				if(!distributed.equals(null) && distributed.equals("true")){
					   String strTagName = className.substring(0, className.lastIndexOf("Test_"));
					   driver = super.getDriver(testData);
				}else{
					  String strTagName = className.substring(0, className.lastIndexOf("Test_"));
					   driver = super.getDriver(testData);
				}
			}
	}

	@Override
	@BeforeMethod(groups = {"Acceptance", "Keyboard", "Primary", "Ribbon", "Other", "Right Click", "Toolbar", "Robot"})
	public void beforeMethod(Method method) throws Exception {
		// create task id from class name and launch task
		 pathwyName = method.getName();
		 logInstruction("=====================================================================\n PATHWAY STARTED  "+pathwyName  +"\n====================================================================="); 
		 seleniumActionLib = new SimsActionLibrary(driver, config_properties, taskApplication);
		 if(!memoryTrackingEnabled){
			 String taskID = taskClassname.replaceAll("_", ".");
			   if(testData.getTaskFeatureMap().get(SimsConstants.TASKID_ATTRIBUTE_NAME) != null){
				   taskID = testData.getTaskFeatureMap().get("taskId");
			   }
		   	launchTask(taskID); 
		 }
	}
	
	
	@Override
	@AfterMethod
	public void AfterMethod() throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	@AfterClass(groups = {"Acceptance", "Keyboard", "Primary", "Ribbon", "Other", "Right Click", "Toolbar", "Robot"})
	public void afterClass() throws Exception {
		Alert alert = null;
		try {
			  // Check the presence of alert
			  alert = driver.switchTo().alert();
			  logInfo( pathwyName , "Alert found with text: " + alert.getText());
			  alert.accept();
			  logInfo( pathwyName ,"Closed successfully");
		  } catch (org.openqa.selenium.NoAlertPresentException ex) {
			   // Alert not present
			  logInfo( pathwyName ,"No Problem found while exiting the task");
		  } catch(org.openqa.selenium.UnhandledAlertException ex){
			  logInfo( pathwyName ,"Unexpected Alert open ");
			  if(alert != null){
				  alert.dismiss();
			  }
		}
		if(driver != null){
			if(!memoryTrackingEnabled){
				driver.quit();
				logInfo("Destroying driver");
			}else{
				MemoryUsageTracker.exportToCsv(TestRunner._CurrentTaskMemory , memoryLogFile);
				TestRunner._CurrentTaskMemory = "";
				TestRunner._CurrentClassName = "";
			}
			logInstruction("=====================================================================\n TEST ENDED "+taskName+" \n=====================================================================");
		}
	}

	@Override
	@AfterSuite(groups = {"Acceptance", "Keyboard", "Primary", "Ribbon", "Other", "Right Click", "Toolbar", "Robot"})
	public void afterSuite() throws Exception {
		// TODO Auto-generated method stub
		if(driver != null){
			if(memoryTrackingEnabled){
				memoryTestRunner.submitAssignment(seleniumActionLib);
				driver.quit();
				logInfo("Destroying driver");
				memoryTestRunner.generateMemoryUsageReport(memoryLogFile);
				logInstruction("=====================================================================\n MEMORY USAGE TRACKING COMPLETED - REPORT GENERATED - 'src/test/resources/memoryLogs/"+memoryLogFile+"' \n CHART GENERATED - 'src/test/resources/memoryLogs/reports/" + memoryLogFile +"' \n=====================================================================");
			}
		}
	}
	
	/**
	 * Method run by @test annotation in java class
	 * 
	 * input params 1.taskId
	 * 				2.Scenario A1/T1
	 * 				3.itemNo  
	 * 				4.method
	 * */
	public void executeItem(String taskID, String scenario, String itemNo, String methodNo) {
		currentItemNum = Integer.parseInt(itemNo);
		 // validate task has opened or not
		current_method_no = methodNo;
		try {
			new ItemRunner(taskID, scenario, itemNo , methodNo, driver, taskApplication, this.taskXMLDoc ,this.config_properties, this.testData.getGenericFeatureMap(), pathwyName ,seleniumActionLib);
		} catch (InvalidTaskActionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	     // validate if current item has been completed successfully
	    try{
		     if(currentItemNum == getTotalItemCount()){
		    	if(!memoryTrackingEnabled){
		    		 validateTaskComplete(currentItemNum);
		    	}else{
		    		 logInfo( pathwyName ,"Item " + currentItemNum + " completed successfully");
					 logInstruction("=====================================================================\n PATHWAY ENDED "+pathwyName+" \n===================================================================== ");
		    	}
		     }else {
		    	 validateItemCount(currentItemNum);
		     }
		     pathwayFailed = false;
	     }catch(Exception e){
	    	 e.printStackTrace();
	     }
	}
	
	/*Launches task in browser with url created from taskid*/
	 private void launchTask(String taskid) throws Exception {
		String URL = "";
		URL = System.getProperty("appURL");//Change for jenkins job
		if((null == URL)){
			URL = testData.getTestURL();
		}
		logInfo( pathwyName ,"Launching URL: " + URL + "?resLinkID=TaskID:" + taskid);
		if(driver != null){
			Alert alert1 = null;
			try {
				alert1 = driver.switchTo().alert();  
				  if(alert1 != null){
					  alert1.dismiss();
				  }
			}catch (org.openqa.selenium.NoAlertPresentException ex) {
				   // Alert not present
			} catch(org.openqa.selenium.UnhandledAlertException ex){
				logInfo( pathwyName ,"Unexpected Alert open ");
				  if(alert1 != null){
					  alert1.dismiss();
				  }
			}
			driver.get(URL + "?resLinkID=TaskID:"+taskid);
			Alert alert = null;
			try {
				  // Check the presence of alert
				  alert = driver.switchTo().alert();
				  logInfo( pathwyName ,"Alert found with text: " + alert.getText());
				  alert.accept();
				  logInfo( pathwyName ,"Alert Closed successfully");
			  } catch (org.openqa.selenium.NoAlertPresentException ex) {
				   // Alert not present
				  logInfo( pathwyName ,"Task launched successfully.");
			  } catch(org.openqa.selenium.UnhandledAlertException ex){
				  logInfo( pathwyName ,"Unexpected Alert open ");
				  if(alert != null){
					  alert.dismiss();
				  }
			  }
			String brNameVal = testData.getBrowserName();
			// handling for IE/Safari to click on 'CLICK HERE'
			clickHereAndLaunchSIMS(brNameVal);	
			
			
			try{
				WebDriverWait wdWait = new WebDriverWait(driver, 300);  // wait for max 5 minutes
				wdWait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("LoadingCurtain")));
			}catch(Exception e){ 
				logError("Timed out error.\ntask not loaded in 7 minutes." );
				//Assert.fail("Timed out error.\ntask not loaded in 7 minutes.");
			}
			
		}else {
			logInfo("Driver is null");
			Assert.fail("Driver is null");
		}
	 }
	
	 private void clickHereAndLaunchSIMS(String browser) throws Exception{
			if(browser.equalsIgnoreCase("IEXPLORER") || browser.equalsIgnoreCase("SAFARI")){
				WebDriverWait wait;
				wait = new WebDriverWait(driver, 300);// max 5 minute
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[text()='CLICK HERE']")));
				seleniumActionLib.clickAndWait("clickHere");
			}
		}
	
	 private void validateItemCount(int previousItemCount) throws InterruptedException {
			int curentItemNum = getCurrentItemCount(previousItemCount);
				if(previousItemCount == curentItemNum ){
					clickSubmitToCheckClickstream(previousItemCount);				
				 } else if((previousItemCount + 1) == curentItemNum ) {
					 logInfo( pathwyName ,"Item " + previousItemCount  + " completed successfully");
				 }else {
					 logInfo( pathwyName ,"Unexpected error in item complete validation !!");
					 Assert.fail("Unexpected error in Item complete validation!!");
				 }
		 }
		
		private void clickSubmitToCheckClickstream(int previousItemCount){
			try {
				seleniumActionLib.clickAndWait("submitButton");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				seleniumActionLib.clickAndWait("Assignment_complete_ok");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logInfo( pathwyName ,"Item " + previousItemCount + " with method no "+current_method_no+" not completed.");
			Assert.fail("Item " + previousItemCount + " with method no "+current_method_no+" not completed.");
			
		}
	
	 private int getCurrentItemCount(int previousItemCount) throws InterruptedException{
			int ret = previousItemCount;
			WebDriverWait wait;
			wait = new WebDriverWait(driver, 60);// 1 minute
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("InstructionText")));
			Thread.sleep(2000);

			List<WebElement> li = driver.findElements(By.xpath("//*[@id='InstructionText']/span"));
			Iterator<WebElement> iter = li.iterator();

			int repeatCheck = 0;
			boolean itemChanged = false;
			do{
				repeatCheck++;
				int count = 0;
				Thread.sleep(1000);
				while(iter.hasNext()) {
					count++;
				    WebElement we = iter.next();
				    if (we.getAttribute("class").contains("highlighted")) {
				    	if(count==ret){
				    		itemChanged = false;
				    	}else{
				    		itemChanged = true;
				    		ret = count;
				    		break;
				    	}
				    }
				}
			}while(!itemChanged && (repeatCheck<=20));
			return ret;
		}
	 
	 private int getTotalItemCount() throws InterruptedException{
		  WebDriverWait wait;
		  wait = new WebDriverWait(driver, 60);// 1 minute
		  wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(SimsConstants.TASK_INSTRUCTION_TEXT_ID)));
		  Thread.sleep(2000);

		  List<WebElement> li = driver.findElements(By.xpath(SimsConstants.INSTRUCTION_TEXT_SPAN_XPATH));
		  return li.size();
	 }
	 
	 private void validateTaskComplete(int previousItemCount) {
		  WebDriverWait wait;
		  try{
			  wait = new WebDriverWait(driver, 60);// 1 minute
			  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(seleniumActionLib.webLocator.getFixedElementXpath("okButton")[1])));
			  seleniumActionLib.clickAndWait("okButton");
			  logInfo( pathwyName ,"Item " + previousItemCount + " completed successfully");
			  logInstruction("=====================================================================\n PATHWAY ENDED "+pathwyName+" \n===================================================================== ");
		  }catch(Exception e) {
			  clickSubmitToCheckClickstream(previousItemCount);
			 /* logInfo( pathwyName ,"Item " + previousItemCount + " Not Completed.");
			  Assert.fail("Item " + previousItemCount + " Not Completed.");*/
		  }
	}
	 
	 private boolean validateTask(String description) throws InterruptedException {
		WebDriverWait wait;
		wait = new WebDriverWait(driver, 60);// 1 minute
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("InstructionText")));
		Thread.sleep(2000);
		List<WebElement> li = driver.findElements(By.xpath("//span[@id='questionAndPoints']"));
		if(! li.get(0).getText().contains(description)){
			 logInfo( TestRunner._CurrentClassName ,"Task loaded is incorrect!");
			return false;
		}else{
			logInfo( TestRunner._CurrentClassName ,"Task loaded is correct!");
			return true;
		}
	}
	 
	public WebDriver getCurrentDriver(){
		if(driver == null)  {
			logInfo("Unexpected Error: Parameter driver null");
			Assert.fail("Unexpected Error: Parameter driver null");
		}
		return driver ;
	}
	
	private void getTaskXMLToDoc(String taskXMLName) throws InvalidTaskXMLException {
		String taskXMLDir = "";
		//create task xml dir path using task xml filename
		taskXMLDir = SimsConstants.TASK_XML_BASE_PATH + getTaskDir(taskXMLName);
		FileUtils utils = new FileUtils();
		Document tmpDoc = utils.XMLtoDoc(taskXMLDir + taskXMLName + ".xml");
	
		if(tmpDoc != null){//check task xml should not be null
			String taskID = taskXMLName.replaceAll("_",	".");
			//check if task xml is correct
			if(!(tmpDoc.selectSingleNode("//friendlyTaskID").getText().equals(taskID))){
				throw new InvalidTaskXMLException("Invalid XML for task: " + taskID);
			}
			logInfo( taskName ,"Task XML found - " + " & Task ID validated with friendly taskid ");
			this.taskXMLDoc = tmpDoc ;
		}
	}
	
	private String getTaskDir(String taskXMLName) {
		String[] folderNames = taskXMLName.split("_");
		if(folderNames.length == 6) {;
			String dirName = "";
			dirName = folderNames[0] + "/" + folderNames[1] + "/" + folderNames[2] + "/";
			String tmpFolderName = folderNames[0] + "_" + folderNames[1] + "_" + folderNames[2] + "_";
			dirName = dirName + taskXMLName.replaceAll(tmpFolderName, "").replaceAll("_", ".") + "/";
			return dirName;
		} else {
			logInfo( taskName ,"Invalid task name: " + taskXMLName.replaceAll("_", "."));
			Assert.fail("Invalid task name: " + taskXMLName.replaceAll("_", "."));
			return null;
		}
	}
	
	private Properties getValidatedConfigFile(){
		  InputStream fis = null;
		  InputStream fisLine = null;
		  String EQUALs = "=";
		  int lineNo = 0;
		  Properties prop = new Properties();
		  boolean assertFail = false;
		  BufferedReader br = null;
		  try {
			   fis = getClass().getClassLoader().getResourceAsStream(SimsConstants.CONFIG_PROPERTIES_BASE_PATH + propertyFilename);
			   fisLine = getClass().getClassLoader().getResourceAsStream(SimsConstants.CONFIG_PROPERTIES_BASE_PATH + propertyFilename);
			   Set<String> set = new TreeSet<String>();
			   Map<String, Integer> map = new TreeMap<String, Integer>();
			   List<String> invalidLines = new ArrayList<String>();
			   List<String> errorlog = new ArrayList<String>();
			   br = new BufferedReader(new InputStreamReader(fis));
			   String strLine;
			   String keyValuePair[] = null;
			   while ((strLine = br.readLine()) != null) {
				   lineNo++;
				   if(!(strLine.trim().matches("(!|#).*"))){
					    if (strLine.contains(EQUALs)) {
						     keyValuePair = strLine.split(EQUALs);
						     if(keyValuePair.length == 0 ){
						    	 invalidLines.add(String.valueOf(lineNo));							    	 
						     }else if(keyValuePair.length > 0){
							     String key = keyValuePair[0];
		
							     if (!set.add(key)) {
								      if (map.containsKey(key)) {
								       map.put(key, map.get(key).intValue() + 1);
								      } else {
								       map.put(key, 2);
								      }
							     }
						    } 
					    } 
				    }
			   }
			   prop.load(fisLine);
			   if(!(map.isEmpty())){
				   errorlog.add("Duplicate element name in " +propertyFilename+ " with duplication count:" + map);
				   assertFail = true;
			   }
			   if(!(invalidLines.isEmpty())){
				   errorlog.add("Invalid content found at line no:" + invalidLines);
				   assertFail = true;
			   }
			   if(assertFail){
				   logError("Parsing error found in properties file in:"+ propertyFilename);
				   for(int i = 0 ; i < errorlog.size() ;i++){
					   logError(errorlog.get(i));
				   }
				   Assert.fail("Parsing error found in properties file in:"+ propertyFilename);
			   }
			   logInfo( taskName ,"Poperties file loaded successfully: "+propertyFilename);
			  } catch (Exception e) {
				  logError("Error: " + e.getMessage());
			  } finally {
				   try {
					   fis.close();
				   } catch (Exception e) {
					   // do nothing
				   }
			  }
		  return  prop;

	 }
}
