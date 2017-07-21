package actionlib;

import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.util.Properties;

import locator.SimsWebElementLocatorFromConfig;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import utils.SimsConstants;

import com.compro.utils.Constants;

public class ExcelActionLibrary extends SimsActionLibrary{
	SimsWebElementLocatorFromConfig webLocator = null;
	public ExcelActionLibrary(WebDriver d , Properties configProperties) {
		super(d, configProperties, "EXCEL");
		this.taskApplication = "EXCEL";
		this.webLocator = super.webLocator;
	}

	public void selectCell(String cell) throws Exception {
		waitForOneSec();
		// convert to upper case
		cell = cell.toUpperCase();

		int colNum = ((int)cell.charAt(0)) -1;		// subtracted minus one because the index start from Zero(0).
		int rowNum = (Integer.parseInt(cell.substring(1))) - 1;		// subtracted minus one because the index start from Zero(0).

		// change alphabet to index number
        if(colNum<=89 && colNum>=64) {
        	colNum = colNum-64;
        }

        // get the x & y coordinated of the cell to be clicked.
		int x=getwidth(colNum);
		int y=getHeight(rowNum);

		// click on the cell
		setkeybaordAction()
			.moveToElement(webLocator.getWebelement("body"), 0, 0)
			.moveByOffset(x,y)
			.click()
			.build()
			.perform();
		waitForOneSec();
	}

	public void rightClickOnCell(String cell) throws Exception {
         waitForOneSec();

		// convert to upper case
		cell = cell.toUpperCase();

		int colNum = ((int)cell.charAt(0)) - 1;		// subtracted minus one because the index start from Zero(0).
		int rowNum = (Integer.parseInt(cell.substring(1))) - 1;		// subtracted minus one because the index start from Zero(0).

		// change alphabet to index number
        if(colNum<=89 && colNum>=64) {
        	colNum = colNum-64;
        }

        // get the x & y coordinated of the cell to be clicked.
		int x=getwidth(colNum);
		int y=getHeight(rowNum);

		// right click on the cell
		setkeybaordAction()
			.moveToElement(webLocator.getWebelement("body"), 0, 0)
			.moveByOffset(x,y)
			.contextClick()
			.build()
			.perform();

			waitForOneSec();
	}

	public void selectCellRange(String range, Method Method) throws Exception {
		waitForOneSec();
		// If the selection of cell range has to be done using Mouse
		if(Method.equals(Method.Mouse)) {

			String[] arr = range.split(":");

	        int startColNum = (int)arr[0].toUpperCase().charAt(0);
	        int endColNum = (int)arr[1].toUpperCase().charAt(0);
	        int startRowNum = (Integer.parseInt(arr[0].substring(1))) - 1;
	        int endRowNum =  (Integer.parseInt(arr[1].substring(1))) - 1;

	        // change alphabet to index number
	        if(startColNum<=90 && startColNum>=65 && endColNum<=90 && endColNum>=65 ) {
		        startColNum = startColNum-64-1;
		        endColNum = endColNum-64-1;
	        }

	        Actions action=setkeybaordAction();

	        // calculate the initial X & Y coordinated of selection area
			int initialX=getwidth(startColNum);
			int initialY=getHeight(startRowNum);

			// calculate the final X & Y coordinated of selection area
			int finalX,finalY;
			if(startColNum==endColNum)
				finalX=initialX;
			else
				finalX=getwidth(endColNum);

			if(startRowNum==endRowNum)
				finalY=initialY;
			else
				finalY=getHeight(endRowNum);

			action
				.moveToElement(webLocator.getWebelement("body"), 0, 0)
				.moveByOffset(initialX,initialY)
				.clickAndHold()
				.moveToElement(webLocator.getWebelement("body"), 0, 0)
				.moveByOffset(finalX,finalY)
				.release()
				.build()
				.perform();

/*			selectCell(arr[0]);
			waitForOneSec();
			pressKey(MyKeys.SHIFT);
			selectCell(arr[1]);
			waitForOneSec();
			releaseKey(MyKeys.SHIFT);*/
		}

		// If the selection of cell range has to be done only using keyboard
		if(Method.equals(Method.Keyboard)) {

			String[] arr = range.split(":");
			Actions action = new Actions(driver);

			// Convert to column and row number
			int startColNum = (int)(arr[0].toUpperCase().charAt(0));
	        int endColNum = (int)(arr[1].toUpperCase().charAt(0));
	        int startRowNum = Integer.parseInt(arr[0].substring(1));
	        int endRowNum =  Integer.parseInt(arr[1].substring(1));

	        // change alphabet to index number
	        if(startColNum<=90 && startColNum>=65 && endColNum<=90 && endColNum>=65 ) {
		        startColNum = startColNum-64;
		        endColNum = endColNum-64;
	        }

	        int numOfRightShifts = endColNum - startColNum;
	        int numOfDownShifts = endRowNum - startRowNum;

			selectCell(arr[0]);
			action.keyDown(Keys.SHIFT);
			for(int i=0; i<numOfRightShifts; i++){
				action.sendKeys(Keys.ARROW_RIGHT);
			}
			for(int i=0; i<numOfDownShifts; i++){
				action.sendKeys(Keys.ARROW_DOWN);
			}
			action.keyUp(Keys.SHIFT).build().perform();
		}
		waitForOneSec();
	}

	public void selectRow(int rowNumber) {

		// get Row Xpath
		String rowXpath = getRowXpath(rowNumber);
		driver.findElement(webLocator.locatorValue("xpath",rowXpath)).click();

		waitForOneSec();
	}

	public void selectColumn(String columnName) {

		// convert to upper case
		columnName = columnName.toUpperCase();

		int colNum = ((int)columnName.charAt(0)) -1;		// subtracted one because the index start from Zero(0).

		// change alphabet to index number
        if(colNum<=89 && colNum>=64) {
        	colNum = colNum-64;
        }

        // get Column Xpath
        String colXpath = getColXpath(colNum);
        driver.findElement(webLocator.locatorValue("xpath",colXpath)).click();

        waitForOneSec();
	}

	private int getwidth(int indexCol) throws IOException{

		// get indexColXpath
		String indexColXpath = getColXpath(indexCol);

		int IndexColwidth = driver.findElement(webLocator.locatorValue("xpath",indexColXpath)).getSize().getWidth()/2;
		int xPos = driver.findElement(webLocator.locatorValue("xpath",indexColXpath)).getLocation().getX();

		return xPos + (IndexColwidth/2);
	}

	private int getHeight(int indexRow) throws Exception{

		// get indexRowXpath
		String indexRowXpath = getRowXpath(indexRow);

		int IndexRowheight = driver.findElement(webLocator.locatorValue("xpath",indexRowXpath)).getSize().getHeight()/2;
		int yPos = driver.findElement(webLocator.locatorValue("xpath",indexRowXpath)).getLocation().getY();

		return yPos + (IndexRowheight/2);
	}

	private String getRowXpath(int rowNumber) {
		// xPath of the row as per new Excel tasks implementation using data index attribute
		String indexRowXpath = "//*[@id=\"ROW_HEADERContextMenu\"]//*[@data-index=\"" + rowNumber +"\"]";

		// check if the index column is accessible using data index attribute , if not then access using ID
		Boolean xpathPresence = driver.findElement(webLocator.locatorValue("xpath",indexRowXpath)).isDisplayed();
		if(!xpathPresence) {
			indexRowXpath = "//*[@id=__" + rowNumber + "]";
		}
		return indexRowXpath;
	}

	private String getColXpath(int columnNumber) {
		// xPath of the column as per new Excel tasks implementation using data index attribute
		String indexColXpath = "//*[@id=\"COLUMN_HEADERContextMenu\"]//*[@data-index=\"" + columnNumber +"\"]";

		// check if the index column is accessible using data index attribute , if not then access using ID
		Boolean xpathPresence = driver.findElement(webLocator.locatorValue("xpath",indexColXpath)).isDisplayed();
		if(!xpathPresence) {
			String indexColName = String.valueOf((char)(columnNumber + 65));
			indexColXpath = "//*[@id=__" + indexColName + "]";
		}

		return indexColXpath;
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
//			System.err.println("Not fully supported yet.");
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
		waitForOneSec();

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
		waitForOneSec();
		if(Method.equals(Method.Keyboard)){
			pressKey(Constants.KeyboardKeys.SHIFT);
			for(int i=0;i<(End-Start);i++){
				super.pressKey(Constants.KeyboardKeys.ARROW_RIGHT);
			}
			releaseKey(Constants.KeyboardKeys.SHIFT);
		}

		if(Method.equals(Method.Mouse)){ // Dependency on clickAt
//			System.err.println("Not fully Supported yet.");

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
		waitForOneSec();

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
		 waitForOneSec();
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

		 element.getLocation().getX();
		 element.getLocation().getY();

		 int w = element.getSize().getWidth();
		 int h = element.getSize().getHeight();

		 int xOffset = w*xposPercent/100;
		 int yOffset = h*yposPercent/100;

		 moveToElement(elementName, xOffset, yOffset); click();
	  }

	public void pressKey(Constants.KeyboardKeys MyKeys){
		waitForOneSec();
		super.pressKey(MyKeys);
		waitForOneSec();
	}

	public void doubleClickFillHandle(String elementName) throws Exception {
		waitForOneSec();waitForOneSec();
		WebElement element =  webLocator.getWebelement(elementName);
		element.click();
		System.out.println("element clicked");
		Actions a = setkeybaordAction();
		a.doubleClick(element).perform();
		System.out.println("element doubleclicked");
}
}
