package actionlib;

import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import junit.framework.Assert;
import locator.SimsWebElementLocatorFromConfig;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import utils.ComponentParser;
import utils.SimsConstants;

import com.compro.core.actionlib.BaseActionLib;
import com.compro.utils.Constants;
import com.compro.utils.FileUtils;

import exceptions.ActionFailedException;

public class SimsActionLibrary extends BaseActionLib{
	WebDriver driver = null;
	public SimsWebElementLocatorFromConfig webLocator = null;
	FileUtils fileUtils = new FileUtils();
	Properties configProperties = null;
	String taskApplication = "";
	
	public SimsActionLibrary(WebDriver d , Properties configProperties , String taskApp) {
		super(d, configProperties , new SimsWebElementLocatorFromConfig(d, configProperties , taskApp));
		this.driver = super.driver;
		this.taskApplication = taskApp;
		this.webLocator = new SimsWebElementLocatorFromConfig(d, configProperties , taskApp);
	}
	

	public void skipToNextItem() throws Exception
	{
		pressKey(Constants.KeyboardKeys.ALT);
		clickAndWait("next_item");
		releaseKey(Constants.KeyboardKeys.ALT);
	}
	
	/* Supporting key press multiple time where counter is noOfTimes key to be pressed.
	 * inputs	"myKey" - keys object
	 * inputs	"counter" - no of times key press to be called.
	 */
	public void pressKeyMultipleTimes(Constants.KeyboardKeys myKey , String counter){
		try{
			int count = Integer.parseInt(counter);
			if(count <= 0){
				System.err.println("Invalid noOfTimes "+ count + " for press key  " + myKey.name());
				Assert.fail("Invalid noOfTimes "+ count + " for press key  " + myKey.name());
			}else{
				for(int i = 0 ; i < count ; i++){ 
					pressKey(myKey);				
				}
				waitForOneSec();
			}
		}catch(NumberFormatException exception){
			System.err.println("Invalid format for noOfTimes "+ counter + " for press key  " + myKey.name());
			Assert.fail("Invalid format for noOfTimes "+ counter + " for press key  " + myKey.name());
		}		
	}
	/*
	 * Overloaded method pressKey
	 * 
	 * Input : KeySequence Supported key sequences (1). Alt,W,PO (2). SHIFT + F4 (3) F10(any function key)
	 * 	
	 */
	public void pressKey(String keySequence) throws Exception{//Overloaded earlier pressKey func to support combination of keys 
		String[] keySeqArr = null;
		String[] plusKeySeqArr =  null;
		if(keySequence.contains(",") && !keySequence.contains("+")){//Handling key combination of keys with Modifier key followed text keys Alt,W,PO
			keySeqArr = keySequence.split(",");
			pressCommaKeySequence(keySeqArr);
		}else if(keySequence.contains("+") && !keySequence.contains(",")){//Handling key combination of keys with Modifier key followed function keys e.g. Shift + F4
			keySeqArr = keySequence.split("\\+");
			pressPlusKeySequence(keySeqArr);
		}else if(keySequence.contains(",") && keySequence.contains("+")){
			keySeqArr = keySequence.split(",");			
			String pressKey = "";
			for(int i = 0; i < keySeqArr.length; i++){			
				pressKey = keySeqArr[i].trim();	
				if(!pressKey.contains("+")){ 
					boolean isfuncKey = false;
					if(pressKey.equalsIgnoreCase("ALT") || pressKey.equalsIgnoreCase("CONTROL") || pressKey.equalsIgnoreCase("CTRL") || pressKey.equalsIgnoreCase("SHIFT")){
						pressNreleaseKey(Constants.getKeyBoardKey(pressKey));
					} else{
						for (Constants.KeyboardKeys functionKeys : Constants.KeyboardKeys.values()) {// check in enum if key is function key
						  if(functionKeys.equals(Constants.getKeyBoardKey(pressKey))){
							  isfuncKey = true;
							  break;
						  }
						}
						if(isfuncKey){
							pressKey(Constants.getKeyBoardKey(pressKey));//if key is function key use pressKey
						}else {
							enter_Text(pressKey.toLowerCase());//if key is alphabet shortcut use enter_text
						}
					}
				}else{
					plusKeySeqArr = pressKey.split("\\+");
					pressPlusKeySequence(plusKeySeqArr);
				}
			}
		}else{// handling simple function keys.Calls overloaded function pressKey(Constants.KeyboardKeys SeleniumKeyboardKeys)
			pressKey(Constants.getKeyBoardKey(keySequence));
		}
		
	}
	
	public void selectFromDropdown(String elementName, String parameter) throws Exception
	{
		String parameterXpath =  "//*[@id='childBlock']//*[text()='" + parameter + "']//ancestor::li[1]";

		// open the dropdown
		WebElement element= webLocator.getWebelement(elementName);
		element.click();
		waitForOneSec();

		// select provided parameter
		ComponentParser parser = new ComponentParser(driver, taskApplication);
		String elexpath = parser.getXpathFromEtext(elementName);
		String dropdownXpath = elexpath + parameterXpath;
		element = driver.findElement(By.xpath(dropdownXpath));
		element.click();
		waitForOneSec();
	}

	public void selectFromColorDropdown(String elementName, String parameter) throws Exception
	{
		String parameterXpath =  "//*[@class='childBlock']//*[@text='" + parameter + "']";

		// open the colorgrid_dropdown
		WebElement element= webLocator.getWebelement(elementName);
		element.click();
		waitForOneSec();

		// select provided parameter
		ComponentParser parser = new ComponentParser(driver, taskApplication);
		String elexpath = parser.getXpathFromEtext(elementName);
		String dropdownXpath = elexpath + parameterXpath;
		element = driver.findElement(By.xpath(dropdownXpath));
		element.click();
		waitForOneSec();
	}

	public void selectFromListbox(String elementName, String parameter) throws Exception
	{
		String parameterXpath =  "//*[text()='" + parameter + "']//ancestor::li[1]";
		// select provided parameter
		WebElement element= webLocator.getWebelement(elementName);
		ComponentParser parser = new ComponentParser(driver, taskApplication);
		String elexpath = parser.getXpathFromEtext(elementName);
		String dropdownXpath = elexpath + parameterXpath;
		element = driver.findElement(By.xpath(dropdownXpath));
		element.click();
		waitForOneSec();
	}
	
	private void pressCommaKeySequence(String[] keySequence) throws Exception {
		String pressKey = "";
		for(int i = 0; i < keySequence.length; i++){
			boolean isfuncKey = false;
			pressKey = keySequence[i].trim();				
			if(pressKey.equalsIgnoreCase("ALT") || pressKey.equalsIgnoreCase("CONTROL") || pressKey.equalsIgnoreCase("CTRL") || pressKey.equalsIgnoreCase("SHIFT")){
				pressNreleaseKey(Constants.getKeyBoardKey(pressKey));
			} else{
				for (Constants.KeyboardKeys functionKeys : Constants.KeyboardKeys.values()) {// check in enum if key is function key
				  if(functionKeys.equals(Constants.getKeyBoardKey(pressKey))){
					  isfuncKey = true;
					  break;
				  }
				}
				if(isfuncKey){
					pressKey(Constants.getKeyBoardKey(pressKey));//if key is function key use pressKey
				}else {
					enter_Text(pressKey.toLowerCase());//if key is alphabet shortcut use enter_text
				}
			}						
		}
	}
	
	private void pressPlusKeySequence(String[] keySequence) throws Exception{
		ArrayList<String> modifierKey =  new ArrayList<String>();
		boolean modifierOn = false;
		for(int i = 0; i < keySequence.length; i++){						
			boolean isfuncKey = false;
			String pressKey = keySequence[i].trim();
			if(pressKey.equalsIgnoreCase("ALT") || pressKey.equalsIgnoreCase("CONTROL") || pressKey.equalsIgnoreCase("CTRL") || pressKey.equalsIgnoreCase("SHIFT")){
				modifierOn = true;
				modifierKey.add(pressKey);//Add the modifier keys to list :for release in reverser order
				pressKey(Constants.getKeyBoardKey(pressKey));
			}else{
				for (Constants.KeyboardKeys functionKeys : Constants.KeyboardKeys.values()) {// check in enum if key is function key
				  if(functionKeys.equals(Constants.getKeyBoardKey(pressKey))){
					  isfuncKey = true;
					  break;
				  }
				}
				if(isfuncKey){
					 pressKey(Constants.getKeyBoardKey(pressKey));//if key is function key use pressKey
				}else {
					enter_Text(pressKey.toLowerCase());//if key is alphabet shortcut use enter_text
				}
			}
		}
		if(modifierOn){
			for(int i = (modifierKey.size() - 1) ; i >= 0 ; i--){//release the modifier keys in reverse order if more than one modifier key is used in combination
				releaseKey(Constants.getKeyBoardKey(modifierKey.get(i)));
			}
		}
	}
	
	public void pressKeyFromVirtualKeyboard(String keySequence) throws ActionFailedException , Exception{
		String[] keySeqArr = null;		
			if(keySequence.contains("+")){
				String virtualKey = "";
				keySeqArr = keySequence.split("\\+");	
				clickAndWait("Virtual_Keyboard");
				for(int i = 0; i < keySeqArr.length; i++){				
					virtualKey = keySeqArr[i].trim();
					String vElement =  "//*[contains(@class, 'VirtualKeyboard')]//button[@value='"+ virtualKey +"']";
					List<WebElement> elementList = webLocator.getWebElementByXpath(vElement);
					if(elementList.size() == 0){
						throw new ActionFailedException("Element with value = "+virtualKey+" could not be found in virtual Keyboard.");
					}else{
						elementList.get(0).click();
					}
				}	
				clickAndWait("Virtual_GO");			
			}else{
				if(!keySequence.equals("")){
					String virtualKey = keySequence.trim();
					clickAndWait("Virtual_Keyboard");
					String vElement = "//*[contains(@class, 'VirtualKeyboard')]//button[@value='"+ virtualKey +"']";	
					List<WebElement> elementList = webLocator.getWebElementByXpath(vElement);
					if(elementList.size() == 0){
						throw new ActionFailedException("Element with value = "+virtualKey+" could not be found in virtual Keyboard.");
					}else{
						elementList.get(0).click();
					}
					clickAndWait("Virtual_GO");	
				}else {
					System.err.println("Incorrect Key Sequence Found");
					throw new Exception("Incorrect Key Sequence Found");
				}
			}				
		}	
	
	public void tripleClick(String elementName) throws Exception {
		Runtime.getRuntime().exec(SimsConstants.SIMS_BROWSER_EXE_PATH);
		waitForOneSec();
		WebElement element =  webLocator.getWebelement(elementName);
		int x = element.getLocation().getX();
		int y = element.getLocation().getY();
		Robot robot = new Robot();
		int w = element.getSize().getWidth();
		int h = element.getSize().getHeight();
		robot.mouseMove(x + w/2, y + h/2 + 60);
		int mask = InputEvent.BUTTON1_DOWN_MASK;
		robot.mousePress(mask);
		robot.mouseRelease(mask);
		robot.mousePress(mask);
		robot.mouseRelease(mask);
		robot.mousePress(mask);
		robot.mouseRelease(mask);
	}


	

}
