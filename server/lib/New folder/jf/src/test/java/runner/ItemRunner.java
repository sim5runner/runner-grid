package runner;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.Node;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import utils.SimsConstants;
import actionlib.SimsActionLibrary;

import com.compro.core.testframework.BaseListener;
import com.compro.core.testframework.BaseTest;
import com.compro.utils.Constants;
import com.compro.utils.MemoryUsageTracker;

import exceptions.InvalidTaskActionException;


public class ItemRunner {
	
	String taskID, taskScenario, itemNum , methodNum, taskName = null;
	Document taskDoc = null;
	SimsActionLibrary seleniumActionLib = null;
	Properties config_properties = null;
	// Constructor to get parameters to identify and run task 
	public ItemRunner(String taskID, String scenario, String itemNum, String itemMethodNum, WebDriver driver, String taskApplication, Document taskDoc , Properties config_prop ,HashMap<String, String> featureHashMap ,String taskName ,SimsActionLibrary simsActionLib) throws InvalidTaskActionException {
		this.taskID = taskID;
		this.taskScenario = scenario;
		this.itemNum = itemNum;
		this.methodNum = itemMethodNum;
		this.taskDoc = taskDoc;
		this.config_properties = config_prop;
		this.taskName = taskName;
		BaseTest.logInfo(taskName ,"Invoking - Item:" + this.itemNum + " :: Method:" + this.methodNum);
		seleniumActionLib = simsActionLib;
		executeCurrentItemActionList(driver, taskApplication , featureHashMap);
	}
	
	private void executeCurrentItemActionList(WebDriver driver, String taskApplication ,HashMap<String, String> featureHashMap) throws InvalidTaskActionException {
		
		String xPathStringForItem = "//Task/scenario[@name='" + this.taskScenario + "']/Items/Item[@sno='" + this.itemNum + "']";
		String xPathStringForMethod = "//Task/scenario[@name='" + this.taskScenario + "']/Items/Item[@sno='" + this.itemNum + "']/Method[@sno='" + this.methodNum + "']";
		String xPathStringForActions = "//Task/scenario[@name='" + this.taskScenario + "']/Items/Item[@sno='" + this.itemNum  + "']/Method[@sno='" + this.methodNum + "']/Actions/Action";
		
		Node taskItem = this.taskDoc.selectSingleNode(xPathStringForItem);
		Node ItemWithMethodNo = this.taskDoc.selectSingleNode(xPathStringForMethod);
		List<Node> taskActions = null;
		if(taskItem == null){
			BaseTest.logError(taskName , "Item node with sno: "+ this.itemNum + " is not available in task xml.");
			Assert.fail("[" + taskName + " - "+BaseListener.getTimeInSec()+"] "+ "Item node with sno: "+ this.itemNum + " is not available in task xml.");
		}else {
			if(ItemWithMethodNo == null ){
				BaseTest.logError(taskName , "Item "+ this.itemNum +" with method no: "+ this.methodNum + " is not available in task xml.");
				Assert.fail("[" + taskName + " - "+BaseListener.getTimeInSec()+"] "+"Item "+ this.itemNum +" with method no: "+ this.methodNum + " is not available in task xml.");
			}else{
				taskActions = this.taskDoc.selectNodes(xPathStringForActions);
				//Extract list of all pathway actions 
				if(taskActions.size() == 0){
					BaseTest.logError(taskName , "No Actions with item no: "+ this.itemNum + " method no: "+ this.methodNum +" are available in task xml.");
					Assert.fail("[" + taskName + " - "+BaseListener.getTimeInSec()+"] "+ "No Actions with item no: "+ this.itemNum + " method no: "+ this.methodNum +" are available in task xml.");
				}else{
					BaseTest.logInfo(taskName , "No. of Actions found: " + taskActions.size());
				}
			}
		}

		/**
		 * Handling for practice
		 * running all task actions for task and practice on basis of doIrunPractice
		 */
		
		/*
		 * running practice
		 */
		
		if(doIrunPractice(taskApplication , featureHashMap)){	
			//IMP: set driver wait to 3 seconds for practice actions.
			Constants.DRIVER_WAIT_FOR_ONE_SEC = 3000;

			try {
				 if(MemoryTestRunner.isMemoryUsageLogging){
					TestRunner._CurrentTaskMemory = MemoryUsageTracker.logMemory( TestRunner._CurrentTaskMemory, MemoryTestRunner.getSIMSPID());
					BaseTest.logInstruction("=====================================================================\nLOGGING MEMORY - ITEM START STATE \n=====================================================================");
				 }
			  } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			  }
			BaseTest.logInfo(taskName ,"PRACTICE Started for item no "+this.itemNum);
			
			
			try{
				practice_ON(driver, taskApplication);
			}catch(Exception e){
				BaseTest.logInfo(taskName ,"error in practice on " + e);
			}
			
			try{
				performActions(driver, taskApplication, taskActions , this.itemNum ,featureHashMap ,seleniumActionLib);
				seleniumActionLib.waitFor(3000);//wait for 3 secs before the practice completes
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				if(isPracticeOn(driver,taskApplication)){
					try {
						seleniumActionLib.clickAndWait("practice_off");
						seleniumActionLib.waitFor(3000);
						driver.findElement(By.xpath(SimsConstants.PRACTICE_CLOSE_XPATH)).click();
						seleniumActionLib.waitFor(3000);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					BaseTest.logInfo(taskName ,"Practice Not Complete for item no "+this.itemNum);
					Assert.fail("Practice Not Complete for item no "+this.itemNum );
				}
			}
			BaseTest.logInfo(taskName ,"PRACTICE Completed for item no "+this.itemNum);
			try {
				if(MemoryTestRunner.isMemoryUsageLogging){
					TestRunner._CurrentTaskMemory = MemoryUsageTracker.logMemory( TestRunner._CurrentTaskMemory, MemoryTestRunner.getSIMSPID());
					BaseTest.logInstruction("=====================================================================\nLOGGING MEMORY - PRACTICE END \n=====================================================================");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//IMP: set driver wait to 1 seconds for normal action execution.
			Constants.DRIVER_WAIT_FOR_ONE_SEC = 1000;
		}
		// Perform action of current item/method
		performActions(driver, taskApplication, taskActions , this.itemNum , featureHashMap ,seleniumActionLib);
	}
	
	private void performActions(WebDriver driver,String taskApplication,List<Node> taskActions , String currentItemNum ,HashMap<String, String> featureHashMap ,SimsActionLibrary simActionLib) throws InvalidTaskActionException{
		
		boolean stopListTraversal = false;
		int tmp = 0;
		
		for(Node taskAction : taskActions) {
			//Validate if action seq no. is correct
			if(!(taskAction.selectSingleNode("@sno").getText().equalsIgnoreCase(Integer.toString(++tmp)))){   
				stopListTraversal = true;
				throw new InvalidTaskActionException("Invalid Action No. " + tmp + "  for task: " + taskID);
			}
			// Exit action traversal loop; if sno doesn't matches
			if(stopListTraversal)
				break;
			else 
				// Perform current action
				new ActionRunner().executeAction(taskAction , tmp, driver, taskApplication , currentItemNum , config_properties ,taskName , simActionLib);
			
			/**
			 * Capture screenshot at the end of action
			 */
			
			if(doIcaptureActionScreens(taskApplication , featureHashMap)){
				seleniumActionLib.waitFor(3000);
				try {

					File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
							
						String appFolderName;
						
						switch(taskApplication.toLowerCase()){
						case "excel": appFolderName = "XL";
						break;
						
						case "word": appFolderName = "WD";
						break;
						
						case "access": appFolderName = "AC";
						break;
						
						case "ppt": appFolderName = "PPT";
						break;
						
						default: appFolderName = "Misc";
							
						}
						
						File tempDir = new File("screenshots/" + appFolderName + "/" + (this.taskID) + "/" + "I"+
								this.itemNum + "_M"+ this.methodNum);
						if (!tempDir.exists()) {
								tempDir.mkdirs();
								}
						
						FileUtils.copyFile(scrFile,(new File(tempDir+ "/Action_" + tmp + ".png")));
						
						
					} catch (Exception e) {
						System.err.println("Error taking action no. "+ tmp +" screenshot");
						e.printStackTrace();
					}
			}
			
			/** End of Screenshot Capture */
		
			// Item End
		}
	}
	
	boolean doIcaptureActionScreens(String taskApplication, HashMap<String, String> featureHashMap){
		boolean actionScreenshot = false;
		String appLevelactionScreenshot = "off";
		
		try{
			if(featureHashMap.get(SimsConstants.SCREENSHOT_ATTRIBUTE_NAME) != null){
				appLevelactionScreenshot = featureHashMap.get(SimsConstants.SCREENSHOT_ATTRIBUTE_NAME);
			}
		}catch(Exception e){
			System.out.println("error in reading actionScreenshot value from xml");
		}

		
		switch(appLevelactionScreenshot.toLowerCase()){
		case "on"	: actionScreenshot = true;
			
			break;
			
		case "off" : actionScreenshot = false;
		
			break;
			
		default: //System.err.println("");
		}
		
		return actionScreenshot;
	}
	
	boolean doIrunPractice(String taskApplication ,HashMap<String, String> featureHashMap){
		boolean runPractice = false;
		String taskLevelPractice = "";
		String appLevelPractice = "off";
		
		try{
			if(featureHashMap.get(SimsConstants.PRACTICE_ATTRIBUTE_NAME) != null){
				taskLevelPractice = featureHashMap.get(SimsConstants.PRACTICE_ATTRIBUTE_NAME);
				appLevelPractice = featureHashMap.get(SimsConstants.PRACTICE_ATTRIBUTE_NAME);
			}
			if(MemoryTestRunner.isMemoryUsageLogging){
				taskLevelPractice = "On";
				appLevelPractice = "On";
			}
		}catch(Exception e){
			System.out.println("error in reading practice value from xml");
		}

		
		switch(appLevelPractice.toLowerCase()){
		case "on"	: runPractice = true;
			
			break;
		case "off" : runPractice = false;
		
			break;
			
		default:	switch(taskLevelPractice.toLowerCase()){
						case "on"	: runPractice = true;
							break;
						case "off" : runPractice = false;
							break;
							
						default:	System.err.println("skipping practice");
					}
		}
		
	//	System.out.println("runPractice : "+ runPractice);
		return runPractice;
	}
	
	public void practice_ON(WebDriver driver,String taskApplication) throws Exception{
		  WebDriverWait wait;
		  seleniumActionLib.clickAndWait("learning_aid");
		  seleniumActionLib.clickAndWait("practice");
		  wait = new WebDriverWait(driver, 60);// 1 minute
		  wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("LoadingImage")));
		  try {
			if(MemoryTestRunner.isMemoryUsageLogging){
				TestRunner._CurrentTaskMemory = MemoryUsageTracker.logMemory( TestRunner._CurrentTaskMemory, MemoryTestRunner.getSIMSPID());
				BaseTest.logInstruction("=====================================================================\nLOGGING MEMORY - PRACTICE START \n=====================================================================");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
	  }
	
	public Boolean isPracticeOn(WebDriver driver,String taskApplication){
		
		int count = 0;
		boolean status = false;
		   do{
			count++;
			seleniumActionLib.waitFor(1000);
			
			try{
				WebElement learningAid= driver.findElement(By.xpath("//*[@id='LearningAidDiv']"));
				if (!learningAid.isDisplayed()){
					status = true;
				}
			}catch(Exception e){
				BaseTest.logInfo(taskName ,"Practice Div not present");
			}
		            
		   }
		   while(!status && count<= 10);
			
		return status;
	}
}
