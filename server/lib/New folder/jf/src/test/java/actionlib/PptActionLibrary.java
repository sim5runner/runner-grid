package actionlib;

import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.Properties;

import locator.SimsWebElementLocatorFromConfig;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import utils.SimsConstants;

import com.compro.utils.Constants;

public class PptActionLibrary extends SimsActionLibrary{
	SimsWebElementLocatorFromConfig webLocator = null;
	public PptActionLibrary(WebDriver d , Properties configProperties) {
		super(d, configProperties, "PPT");
		this.taskApplication = "PPT";
		this.webLocator = super.webLocator;
	}

	/**
	 * Performs text selection in document area, when selection text starting and ending points are within same webelement.
	 *
	 * @param elementName - Name of element contaning text
	 * @param Start - Starting character position of text inside element
	 * @param End - Ending character position of text inside element
	 * @param InputDevice - Perform action using (Keyboard or Mouse)
	 */
	public void SelectText(String elementName, int Start, int End, Method Method) throws Exception{
		waitForOneSec();
		if(Method.equals(Method.Keyboard)){
			pressKey(Constants.KeyboardKeys.SHIFT);
			for(int i=0;i<(End-Start);i++){
				super.pressKey(Constants.KeyboardKeys.ARROW_RIGHT);
			}
			releaseKey(Constants.KeyboardKeys.SHIFT);
		}

		if(Method.equals(Method.Mouse)){
			clickAt(elementName, Start);
			pressKey(Constants.KeyboardKeys.SHIFT);
			clickAt(elementName, End);
			releaseKey(Constants.KeyboardKeys.SHIFT);
		}
		
		if(Method.equals(Method.Robot)){
			Runtime.getRuntime().exec(SimsConstants.SIMS_BROWSER_EXE_PATH);
			Robot robot = new Robot();
			int mask = InputEvent.BUTTON1_DOWN_MASK;

			WebElement element =  webLocator.getWebelement(elementName);

			int x = element.getLocation().getX();
			int y = element.getLocation().getY();

			robot.mouseMove(x,y + 60);
			robot.mousePress(mask);

			int w = element.getSize().getWidth();
			int h = element.getSize().getHeight();

			robot.mouseMove(x + w, y + 60 + h);
			robot.mouseRelease(mask);

		}

	}

	/**
	 * Performs text selection in document area, when selection text starting and ending points are in different webelements.
	 *
	 * @param elementNameStart - Name of element contaning starting point of text
	 * @param elementNameEnd - Name of element contaning ending point of text
	 * @param Start - Starting character position of text inside first webelement
	 * @param End - Ending character position of text inside second webelement
	 * @param InputDevice - Perform action using (Keyboard or Mouse)
	 */
	public void SelectText(String elementNameStart, String elementNameEnd, int Start, int End, Method Method) throws Exception{

		if(Method.equals(Method.Keyboard)){
			pressKey(Constants.KeyboardKeys.SHIFT);
			for(int i=0;i<(End-Start);i++){
				super.pressKey(Constants.KeyboardKeys.ARROW_RIGHT);
			}
			releaseKey(Constants.KeyboardKeys.SHIFT);
		}

		if(Method.equals(Method.Mouse)){ // Dependency on clickAt


			clickAt(elementNameStart, Start);
			Actions action = setkeybaordAction();
			action.keyDown(Keys.SHIFT).build().perform();
			clickAt(elementNameEnd, End);
			action.keyUp(Keys.SHIFT).build().perform();
		}
		
		if(Method.equals(Method.Robot)){
			Runtime.getRuntime().exec(SimsConstants.SIMS_BROWSER_EXE_PATH);
			Robot robot = new Robot();
			int mask = InputEvent.BUTTON1_DOWN_MASK;

			WebElement element =  webLocator.getWebelement(elementNameStart);
			int x = element.getLocation().getX();
			int y = element.getLocation().getY();
					    
			robot.mouseMove(x,y + 60);
			robot.mousePress(mask);

			element =  webLocator.getWebelement(elementNameEnd);
			x = element.getLocation().getX();
			y = element.getLocation().getY();

			int w = element.getSize().getWidth();
			int h = element.getSize().getHeight();

			robot.mouseMove(x + w, y + 60 + h);
			robot.mouseRelease(mask); 

		}

	}

	/**
	 * Performs click inside doc area at specific position. (~ Workaround using keyboard except for clicking at 0,0)
	 *
	 * @param elementName - Name of element contaning text
	 * @param pos - position inside doc area to click
	 */
	public void clickAt(String elementName, int pos) throws Exception{
		waitForOneSec();
		moveToElement(elementName, 0, 0); click();
		for(int i=1;i<=pos;i++)
			super.pressKey(Constants.KeyboardKeys.ARROW_RIGHT);
	}

	/**
	 * Performs click inside doc area at specific position using percentage x, y position inside element w.r.t. element.
	 *
	 * @param elementName - Name of element contaning text
	 * @param xposPercent - Percentage x position inside element w.r.t. element width from top left
	 * @param yposPercent - Percentage y position inside element w.r.t. element height from top left
	 */
	public void clickAt(String elementName, int xposPercent, int yposPercent) throws Exception{
		waitForOneSec();
		WebElement element =  webLocator.getWebelement(elementName);

		int w = element.getSize().getWidth();
		int h = element.getSize().getHeight();

		int xOffset = w*xposPercent/100;
		int yOffset = h*yposPercent/100;

		moveToElement(elementName, xOffset, yOffset); click();
	}

	public void selectSlideFromSlidePane(String slideNumber) throws Exception {
		String xpath = slideXpath(slideNumber);
		WebElement element = webLocator.getSingleWebElementByXpath(xpath);
		element.click();
		waitForOneSec();
	}

	public void rightClickSlideInSlidePane(String slideNumber) throws Exception {
		String xpath = slideXpath(slideNumber);
		WebElement element = webLocator.getSingleWebElementByXpath(xpath);
		setkeybaordAction().moveToElement(element).contextClick(element).build().perform();
		waitForOneSec();
	}

	public void dragAndDropSlideInSlidePane(String firstSlide, String secondSlide) throws Exception {
		String firstSlideXpath = slideXpath(firstSlide);
		String secondSlideXpath = slideXpath(secondSlide);
		
		
		waitForOneSec();
		WebElement dragElement= webLocator.getSingleWebElementByXpath(firstSlideXpath);
		WebElement dropElement= webLocator.getSingleWebElementByXpath(secondSlideXpath);
		(new Actions(driver)).dragAndDrop(dragElement, dropElement).perform();
		waitForOneSec();
	}

	public String slideXpath(String slideNumber){
		String xpath = "//*[@class='presentationThumbnailAreaWrapper']//*[@class='slideThumbNumber' and text()=" + slideNumber + "]//ancestor::*[@class='presentationThumbnail SIMSGenericContextUsed']";
		return xpath;

	}

	public void pressKey(Constants.KeyboardKeys MyKeys){
		waitForOneSec();
		super.pressKey(MyKeys);
		waitForOneSec();
	}
}
