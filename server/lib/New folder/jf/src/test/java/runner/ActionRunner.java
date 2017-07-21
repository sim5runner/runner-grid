package runner;

import java.util.Properties;

import junit.framework.Assert;

import org.dom4j.Node;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import actionlib.AccessActionLibrary;
import actionlib.ExcelActionLibrary;
import actionlib.PptActionLibrary;
import actionlib.SimsActionLibrary;
import actionlib.WordActionLibrary;

import com.compro.core.actionlib.BaseActionLib;
import com.compro.core.testframework.BaseTest;
import com.compro.utils.Constants;

import exceptions.ActionFailedException;

public class ActionRunner{
	SimsActionLibrary seleniumActionLib = null;
	PptActionLibrary seleniumPPTActionLib = null;
	ExcelActionLibrary   seleniumExcelActionLib = null;
	WordActionLibrary  seleniumWordActionLib = null;
	AccessActionLibrary seleniumAccessActionLib = null;
	int actionExcecutionCounter = 0;
	
	void executeAction(Node taskAction , int actionSNo, WebDriver driver, String taskApplication , String currentItemNum , Properties config_prop ,String taskName ,SimsActionLibrary simsActionLibrary){
		actionExcecutionCounter = actionExcecutionCounter + 1;
		seleniumActionLib = simsActionLibrary;
		if(taskApplication.equalsIgnoreCase("ppt")){
			seleniumPPTActionLib = new PptActionLibrary(driver, config_prop);
	   }else if(taskApplication.equalsIgnoreCase("excel")){
		   seleniumExcelActionLib = new ExcelActionLibrary(driver, config_prop);;
	   }else if(taskApplication.equalsIgnoreCase("word")){
		   seleniumWordActionLib = new WordActionLibrary(driver, config_prop);
	   }else if(taskApplication.equalsIgnoreCase("access")){
		   seleniumAccessActionLib = new AccessActionLibrary(driver, config_prop);
	   }
		
		String actionType = taskAction.selectSingleNode("actionType/@name").getText().toLowerCase();

		try{

			if (actionType.equalsIgnoreCase("skiptonextitem"))
			{
				BaseTest.logInfo(taskName ,"skipping to next item");
				seleniumActionLib.skipToNextItem();
				BaseTest.logInfo(taskName ,"skipping done");
			}

			else if(actionType.equalsIgnoreCase("click"))
			{
				String elementName = taskAction.selectSingleNode("actionType/elementName").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": " + actionType + " on element " + elementName);
				seleniumActionLib.click(elementName);
			}
			
			else if(actionType.equalsIgnoreCase("click"))
			{
				String elementName = taskAction.selectSingleNode("actionType/elementName").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": " + actionType + " on element " + elementName);
				seleniumActionLib.click(elementName);
			}
			
			else if(actionType.equalsIgnoreCase("clickMultipleTimes"))
			{
				String elementName = taskAction.selectSingleNode("actionType/elementName").getText();
				String numOfTimes = taskAction.selectSingleNode("actionType/numOfTimes").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": " + actionType + " on element " + elementName +" "+numOfTimes+ " no of times.");
				seleniumActionLib.clickMultipleTimes(elementName, numOfTimes);
			}

			else if(actionType.equalsIgnoreCase("clickAtCurrentPos"))
			{
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": " + actionType);
				seleniumActionLib.click();
			}
			
			else if(actionType.equalsIgnoreCase("clickTwiceAtCurrentPos"))
			{
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": " + actionType);
				seleniumActionLib.clickTwiceAtCurrentPos();
			}



			else if(actionType.equalsIgnoreCase("doubleClick"))
			{
				String elementName = taskAction.selectSingleNode("actionType/elementName").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": " + actionType + " on element " + elementName);
				seleniumActionLib.doubleClickAndWait(elementName);
			}
			else if(actionType.equalsIgnoreCase("clickTwice"))
			{
				String elementName = taskAction.selectSingleNode("actionType/elementName").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": " + actionType + " on element " + elementName);
				seleniumActionLib.clickTwice(elementName);
			}
			else if(actionType.equalsIgnoreCase("tripleClick"))
			{
				String elementName = taskAction.selectSingleNode("actionType/elementName").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": " + actionType + " on element " + elementName);
				seleniumActionLib.tripleClick(elementName);
			}
			else if(actionType.equalsIgnoreCase("enterText") || actionType.equalsIgnoreCase("enter_text"))
			{
				String text = taskAction.selectSingleNode("actionType/text").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": Enter Text " + text);
				seleniumActionLib.enter_Text(text);
			}
			else if (actionType.equalsIgnoreCase("pressKeyMultipleTimes")){
				String keyName = taskAction.selectSingleNode("actionType/keyName").getText();
				String repeatNum = taskAction.selectSingleNode("actionType/numOfTimes").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": pressKey " + keyName + " " + repeatNum + " times.");
				seleniumActionLib.pressKeyMultipleTimes(Constants.getKeyBoardKey(keyName) , repeatNum);
			}
			else if(actionType.equalsIgnoreCase("enterTextInElement"))
			{
				String elementName = taskAction.selectSingleNode("actionType/elementName").getText();
				String text = taskAction.selectSingleNode("actionType/text").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": Enter Text " + text);
				seleniumActionLib.enter_Text(elementName, text);
			}

			else if(actionType.equalsIgnoreCase("pressKey"))
			{
				String keyName = taskAction.selectSingleNode("actionType/keyName").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": Press " + keyName);
				seleniumActionLib.pressKey(keyName);
			}
			else if(actionType.equalsIgnoreCase("pressNreleaseKey"))
			{
				String keyName = taskAction.selectSingleNode("actionType/keyName").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": PressNRelease " + keyName);
				seleniumActionLib.pressNreleaseKey(Constants.getKeyBoardKey(keyName));
			}
			else if(actionType.equalsIgnoreCase("waitFor"))
			{
				BaseTest.logInfo(taskName ,"Waiting for 1 sec");
				seleniumActionLib.waitForOneSec();
			}

			else if(actionType.equalsIgnoreCase("releaseKey"))
			{
				String keyName = taskAction.selectSingleNode("actionType/keyName").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": Release " + keyName);
				seleniumActionLib.releaseKey(Constants.getKeyBoardKey(keyName));
			}

			/* Newly added Methods - Author: Sahil  */

			else if(actionType.equalsIgnoreCase("selectText"))
			{
				String elementName = taskAction.selectSingleNode("actionType/elementName").getText();
				int start = Integer.parseInt(taskAction.selectSingleNode("actionType/start").getText());
				int end = Integer.parseInt(taskAction.selectSingleNode("actionType/end").getText());
				String methodType = taskAction.selectSingleNode("actionType/methodType").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": " + " selection from " + start + " to " + end );

				
				if(taskApplication.equalsIgnoreCase("excel"))
				{
					if(methodType.equalsIgnoreCase("keyboard"))
					{
						seleniumExcelActionLib.SelectText(elementName, start, end , BaseActionLib.Method.Keyboard);
					}
					if(methodType.equalsIgnoreCase("mouse"))
					{
						seleniumExcelActionLib.SelectText(elementName, start, end , BaseActionLib.Method.Mouse);
					}
					if(methodType.equalsIgnoreCase("robot"))
					{
						seleniumExcelActionLib.SelectText(elementName, start, end , BaseActionLib.Method.Robot);
					}
				}
				else if(taskApplication.equalsIgnoreCase("access"))
				{
					if(methodType.equalsIgnoreCase("keyboard"))
					{
						seleniumAccessActionLib.SelectText(elementName, start, end , BaseActionLib.Method.Keyboard);
					}
					if(methodType.equalsIgnoreCase("mouse"))
					{
						seleniumAccessActionLib.SelectText(elementName, start, end , BaseActionLib.Method.Mouse);
					}
					if(methodType.equalsIgnoreCase("robot"))
					{
						seleniumAccessActionLib.SelectText(elementName, start, end , BaseActionLib.Method.Robot);
					}
				}
				else if(taskApplication.equalsIgnoreCase("word"))
				{
					if(methodType.equalsIgnoreCase("keyboard"))
					{
						seleniumWordActionLib.SelectText(elementName, start, end , BaseActionLib.Method.Keyboard);
					}
					if(methodType.equalsIgnoreCase("mouse"))
					{
						seleniumWordActionLib.SelectText(elementName, start, end , BaseActionLib.Method.Mouse);
					}
					if(methodType.equalsIgnoreCase("robot"))
					{
						seleniumWordActionLib.SelectText(elementName, start, end , BaseActionLib.Method.Robot);
					}
				}
				else if(taskApplication.equalsIgnoreCase("ppt"))
				{
					if(methodType.equalsIgnoreCase("keyboard"))
					{
						seleniumPPTActionLib.SelectText(elementName, start, end , BaseActionLib.Method.Keyboard);
					}
					if(methodType.equalsIgnoreCase("mouse"))
					{
						seleniumPPTActionLib.SelectText(elementName, start, end , BaseActionLib.Method.Mouse);
					}
					if(methodType.equalsIgnoreCase("robot"))
					{
						seleniumPPTActionLib.SelectText(elementName, start, end , BaseActionLib.Method.Robot);
					}
				}
			}

			else if(actionType.equalsIgnoreCase("release"))
			{
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": Performing" + actionType + "on current element");
				seleniumActionLib.release();
			}

			else if(actionType.equalsIgnoreCase("clickAndHold"))
			{
				String elementName = taskAction.selectSingleNode("actionType/elementName").getText();

				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": Performing " + actionType +  " on " + elementName);
				seleniumActionLib.clickAndHold(elementName);
			}

			else if(actionType.equalsIgnoreCase("moveToElement"))
			{
				String moveToElement = taskAction.selectSingleNode("actionType/moveToElement").getText();
				int xpos = Integer.parseInt(taskAction.selectSingleNode("actionType/xOffset").getText());
				int ypos = Integer.parseInt(taskAction.selectSingleNode("actionType/yOffset").getText());

				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": Moving mouse to" + xpos + "," + ypos + "position of" + moveToElement);
				seleniumActionLib.moveToElement(moveToElement, xpos, ypos);
			}

			else if(actionType.equalsIgnoreCase("doubleClickAndWait"))
			{
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": Performing " + actionType + " at current mouse position");
				seleniumActionLib.doubleClickAndWait();
			}

			else if(actionType.equalsIgnoreCase("clearText"))
			{
				String elementName = taskAction.selectSingleNode("actionType/elementName").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": Clearing text from " + elementName);
				seleniumActionLib.clearText(elementName);
			}

			else if(actionType.equalsIgnoreCase("selectCell"))
			{
				String cellName = taskAction.selectSingleNode("actionType/cellName").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": Clicking cell " + cellName);
				//savitha 16/2/2016 this code has been added to support Access SLE using Excel grid
				if(taskApplication.equalsIgnoreCase("excel"))
				{
					seleniumExcelActionLib.selectCell(cellName);
				}
				else if(taskApplication.equalsIgnoreCase("access"))
				{
					seleniumAccessActionLib.selectCell(cellName);
				}
			}

			else if(actionType.equalsIgnoreCase("clickAndWait"))
			{
				String elementName = taskAction.selectSingleNode("actionType/elementName").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": Performing " + actionType + " on " + elementName);
				seleniumActionLib.clickAndWait(elementName);

			}

			else if(actionType.equalsIgnoreCase("doubleClickAndWaitNoEle"))
			{
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": Performing double click at current mouse position");
				seleniumActionLib.doubleClickAndWait();
			}

			else if(actionType.equalsIgnoreCase("enter_TextCurrentpos"))
			{
				String text = taskAction.selectSingleNode("actionType/text").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": Performing " + actionType + " at current mouse position");
				seleniumActionLib.enter_Text(text);
			}


			else if(actionType.equalsIgnoreCase("selectInputText"))
			{
				String elementName = taskAction.selectSingleNode("actionType/elementName").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": Performing " + actionType + " on " + elementName);
				seleniumActionLib.selectInputText(elementName);
			}

			else if(actionType.equalsIgnoreCase("rightClick"))
			{
				String elementName = taskAction.selectSingleNode("actionType/elementName").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": Performing " + actionType + " on " + elementName );
				seleniumActionLib.rightClick(elementName);
			}

			else if(actionType.equalsIgnoreCase("rightClickCurrentPos"))
			{
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": Performing " + actionType + " at current mouse position");
				seleniumActionLib.rightClick();
			}

			else if(actionType.equalsIgnoreCase("pressControlEND"))
			{
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": Performing CTRL+END");
				seleniumActionLib.pressControlEND();
			}

			else if(actionType.equalsIgnoreCase("pressControlHOME"))
			{
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": Performing CTRL+HOME");
				seleniumActionLib.pressControlHOME();
			}

			else if(actionType.equalsIgnoreCase("pressControlENTER"))
			{
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": Performing CTRL+ENTER");
				seleniumActionLib.pressControlENTER();
			}

			else if(actionType.equalsIgnoreCase("pressControlA"))
			{
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": Performing CTRL+A");
				seleniumActionLib.pressControlA();
			}


			else if(actionType.equalsIgnoreCase("getElementText"))
			{
				String elementName = taskAction.selectSingleNode("actionType/elementName").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": Performing " + actionType + " on " + elementName );
				seleniumActionLib.getElementText(elementName);
			}

			else if(actionType.equalsIgnoreCase("clickAndHoldCurrentPos"))
			{
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": Performing " + actionType + " at current position" );
				seleniumActionLib.clickAndHold();
			}

			else if(actionType.equalsIgnoreCase("moveByOffset"))
			{
				int xpos = Integer.parseInt(taskAction.selectSingleNode("actionType/xOffset").getText());
				int ypos = Integer.parseInt(taskAction.selectSingleNode("actionType/yOffset").getText());
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": " + " Performing " + actionType + " from " + xpos + " to " + ypos);
				seleniumActionLib.moveByOffset(xpos, ypos);
			}

			else if(actionType.equalsIgnoreCase("moveToElementCenter"))
			{
				String elementName = taskAction.selectSingleNode("actionType/elementName").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": Performing " + actionType + " on " + elementName );
				seleniumActionLib.moveToElement(elementName);
			}

			else if(actionType.equalsIgnoreCase("moveToElementPercent"))
			{
				double xOffsetPercent = Double.parseDouble(taskAction.selectSingleNode("actionType/xOffsetPercent").getText());
				double yOffsetPercent = Double.parseDouble(taskAction.selectSingleNode("actionType/yOffsetPercent").getText());
				String elementName = taskAction.selectSingleNode("actionType/elementName").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": Performing " + actionType + " on " + elementName + " from " + xOffsetPercent + " to " + yOffsetPercent );
				seleniumActionLib.moveToElementPercent(elementName, xOffsetPercent, yOffsetPercent);
			}

			else if(actionType.equalsIgnoreCase("releaseElement"))
			{
				String elementName = taskAction.selectSingleNode("actionType/elementName").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": Performing " + actionType);
				seleniumActionLib.release(elementName);
			}

			else if(actionType.equalsIgnoreCase("dragAndDropByPosBased"))
			{
				String draggable = taskAction.selectSingleNode("actionType/draggable").getText();
				int xpos = Integer.parseInt(taskAction.selectSingleNode("actionType/xpos").getText());
				int ypos = Integer.parseInt(taskAction.selectSingleNode("actionType/ypos").getText());
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": " + " Performing " + actionType + " on " + draggable + " from " + xpos + " to " + ypos);
				seleniumActionLib.dragAndDropBy(draggable, xpos, ypos);
			}

			else if(actionType.equalsIgnoreCase("dragAndDropByOffset"))
			{
				String draggable = taskAction.selectSingleNode("actionType/elementName").getText();
				int xpos = Integer.parseInt(taskAction.selectSingleNode("actionType/xOffset").getText());
				int ypos = Integer.parseInt(taskAction.selectSingleNode("actionType/yOffset").getText());
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": " + " Performing " + actionType + " on " + draggable + " from " + xpos + " to " + ypos);
				seleniumActionLib.dragAndDropBy(draggable, xpos, ypos);
			}

			else if(actionType.equalsIgnoreCase("dragAndDropBy"))
			{
				String source = taskAction.selectSingleNode("actionType/source").getText();
				String target = taskAction.selectSingleNode("actionType/target").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": " + " Performing " + actionType + " from " + source + " to " + target);
				seleniumActionLib.dragAndDropBy(source, target);
			}

			else if(actionType.equalsIgnoreCase("scroll"))
			{
				String elementName = taskAction.selectSingleNode("actionType/elementName").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": Performing " + actionType + " to " + elementName );
				seleniumActionLib.scroll(elementName);
			}

			else if(actionType.equalsIgnoreCase("rightClickOnCell"))
			{
				String cellName = taskAction.selectSingleNode("actionType/cellName").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": Performing " + actionType + " on " + cellName );
				if(taskApplication.equalsIgnoreCase("excel")){
					seleniumExcelActionLib.rightClickOnCell(cellName);
				}else if(taskApplication.equalsIgnoreCase("access")){
					seleniumAccessActionLib.rightClickOnCell(cellName);
				}
			}

			else if(actionType.equalsIgnoreCase("selectCellRange"))
			{
				String cellrange = taskAction.selectSingleNode("actionType/cellRange").getText();
				String methodType = taskAction.selectSingleNode("actionType/methodType").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": Performing " + actionType + " on range " + cellrange);
				if(taskApplication.equalsIgnoreCase("excel")){
					if(methodType.equalsIgnoreCase("keyboard")){
						seleniumExcelActionLib.selectCellRange(cellrange, BaseActionLib.Method.Keyboard);
					}
					if(methodType.equalsIgnoreCase("mouse")){ 
						seleniumExcelActionLib.selectCellRange(cellrange, BaseActionLib.Method.Mouse);
					}
				}else if(taskApplication.equalsIgnoreCase("access")){
					if(methodType.equalsIgnoreCase("keyboard")){
						seleniumAccessActionLib.selectCellRange(cellrange, BaseActionLib.Method.Keyboard);
					}
					if(methodType.equalsIgnoreCase("mouse")){ 
						seleniumAccessActionLib.selectCellRange(cellrange, BaseActionLib.Method.Mouse);
					}
				}
			}

			else if(actionType.equalsIgnoreCase("selectRow"))
			{
				int rowNumber = Integer.parseInt(taskAction.selectSingleNode("actionType/rowNumber").getText());
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": Performing " + actionType + " on " + rowNumber);
				if(taskApplication.equalsIgnoreCase("excel")){
					seleniumExcelActionLib.selectRow(rowNumber);
				}else if(taskApplication.equalsIgnoreCase("access")){
					seleniumAccessActionLib.selectRow(rowNumber);
				}
			}

			else if(actionType.equalsIgnoreCase("selectColumn"))
			{
				String columnName = taskAction.selectSingleNode("actionType/columnName").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": Performing " + actionType + " on " + columnName);
				if(taskApplication.equalsIgnoreCase("excel")){
					seleniumExcelActionLib.selectColumn(columnName);
				}else if(taskApplication.equalsIgnoreCase("access")){
					seleniumAccessActionLib.selectColumn(columnName);
				}
				
			}

			else if(actionType.equalsIgnoreCase("selectMultiParaText") || actionType.equalsIgnoreCase("selectTextTwoElements"))
			{
				String elementNameStart = taskAction.selectSingleNode("actionType/elementNameStart").getText();
				String elementNameEnd = taskAction.selectSingleNode("actionType/elementNameEnd").getText();
				int Start = Integer.parseInt(taskAction.selectSingleNode("actionType/Start").getText());
				int End = Integer.parseInt(taskAction.selectSingleNode("actionType/End").getText());
				String methodType = taskAction.selectSingleNode("actionType/methodType").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": Performing " + actionType + " from " + elementNameStart + " to " + elementNameEnd + " postion based " + Start + ", " + End);

				if(taskApplication.equalsIgnoreCase("excel"))
				{
					if(methodType.equalsIgnoreCase("keyboard"))
					{
						seleniumExcelActionLib.SelectText(elementNameStart, elementNameEnd, Start, End, BaseActionLib.Method.Keyboard);
					}
					if(methodType.equalsIgnoreCase("mouse"))
					{
						seleniumExcelActionLib.SelectText(elementNameStart, elementNameEnd, Start, End, BaseActionLib.Method.Mouse);
					}
					if(methodType.equalsIgnoreCase("robot"))
					{
						seleniumExcelActionLib.SelectText(elementNameStart, elementNameEnd, Start, End, BaseActionLib.Method.Robot);
					}
				}
				else if(taskApplication.equalsIgnoreCase("access"))
				{
					if(methodType.equalsIgnoreCase("keyboard"))
					{
						seleniumAccessActionLib.SelectText(elementNameStart, elementNameEnd, Start, End, BaseActionLib.Method.Keyboard);
					}
					if(methodType.equalsIgnoreCase("mouse"))
					{
						seleniumAccessActionLib.SelectText(elementNameStart, elementNameEnd, Start, End, BaseActionLib.Method.Mouse);
					}
					if(methodType.equalsIgnoreCase("robot"))
					{
						seleniumAccessActionLib.SelectText(elementNameStart, elementNameEnd, Start, End, BaseActionLib.Method.Robot);
					}
				}
				else if(taskApplication.equalsIgnoreCase("word"))
				{
					if(methodType.equalsIgnoreCase("keyboard"))
					{
						seleniumWordActionLib.SelectText(elementNameStart, elementNameEnd, Start, End, BaseActionLib.Method.Keyboard);
					}
					if(methodType.equalsIgnoreCase("mouse"))
					{
						seleniumWordActionLib.SelectText(elementNameStart, elementNameEnd, Start, End, BaseActionLib.Method.Mouse);
					}
					if(methodType.equalsIgnoreCase("robot"))
					{
						seleniumWordActionLib.SelectText(elementNameStart, elementNameEnd, Start, End, BaseActionLib.Method.Robot);
					}
				}
				else if(taskApplication.equalsIgnoreCase("ppt"))
				{
					if(methodType.equalsIgnoreCase("keyboard"))
					{
						seleniumPPTActionLib.SelectText(elementNameStart, elementNameEnd, Start, End, BaseActionLib.Method.Keyboard);
					}
					if(methodType.equalsIgnoreCase("mouse"))
					{
						seleniumPPTActionLib.SelectText(elementNameStart, elementNameEnd, Start, End, BaseActionLib.Method.Mouse);
					}
					if(methodType.equalsIgnoreCase("robot"))
					{
						seleniumPPTActionLib.SelectText(elementNameStart, elementNameEnd, Start, End, BaseActionLib.Method.Robot);
					}
				}
			}


			else if(actionType.equalsIgnoreCase("selectTextTwoElements_PPT"))
			{
				String elementNameStart = taskAction.selectSingleNode("actionType/elementNameStart").getText();
				String elementNameEnd = taskAction.selectSingleNode("actionType/elementNameEnd").getText();
				int Start = Integer.parseInt(taskAction.selectSingleNode("actionType/Start").getText());
				int End = Integer.parseInt(taskAction.selectSingleNode("actionType/End").getText());
				String methodType = taskAction.selectSingleNode("actionType/methodType").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": Performing " + actionType + " from " + elementNameStart + " to " + elementNameEnd + " postion based " + Start + ", " + End);

				if(methodType.equalsIgnoreCase("keyboard")){
					seleniumPPTActionLib.SelectText(elementNameStart, elementNameEnd, Start, End, BaseActionLib.Method.Keyboard);
				}
				if(methodType.equalsIgnoreCase("mouse")){
					seleniumPPTActionLib.SelectText(elementNameStart, elementNameEnd, Start, End, BaseActionLib.Method.Mouse);
				}
				if(methodType.equalsIgnoreCase("robot")){
					seleniumPPTActionLib.SelectText(elementNameStart, elementNameEnd, Start, End, BaseActionLib.Method.Robot);
				}
			}

			else if(actionType.equalsIgnoreCase("clickAt"))
			{
				int pos = Integer.parseInt(taskAction.selectSingleNode("actionType/pos").getText());
				String elementName = taskAction.selectSingleNode("actionType/elementName").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": Performing " + actionType + " on " + elementName + " at " + pos);
				//action.clickAt(elementName, pos);

				if(taskApplication.equalsIgnoreCase("excel"))
				{
					seleniumExcelActionLib.clickAt(elementName, pos);
				}
				if(taskApplication.equalsIgnoreCase("word"))
				{
					seleniumWordActionLib.clickAt(elementName, pos);
				}
				if(taskApplication.equalsIgnoreCase("access"))
				{
					seleniumAccessActionLib.clickAt(elementName, pos);
				}
				if(taskApplication.equalsIgnoreCase("ppt"))
				{
					seleniumPPTActionLib.clickAt(elementName, pos);
				}
			}

			else if(actionType.equalsIgnoreCase("clickAtPercent_PPT"))
			{
				int xposPercent = Integer.parseInt(taskAction.selectSingleNode("actionType/xposPercent").getText());
				int yposPercent = Integer.parseInt(taskAction.selectSingleNode("actionType/yposPercent").getText());
				String elementName = taskAction.selectSingleNode("actionType/elementName").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": Performing " + actionType + " on " + elementName + " at " + xposPercent + ", " + yposPercent);
				seleniumPPTActionLib.clickAt(elementName, xposPercent, yposPercent);
			}


			else if(actionType.equalsIgnoreCase("clickAtPercent"))
			{
				int xposPercent = Integer.parseInt(taskAction.selectSingleNode("actionType/xposPercent").getText());
				int yposPercent = Integer.parseInt(taskAction.selectSingleNode("actionType/yposPercent").getText());
				String elementName = taskAction.selectSingleNode("actionType/elementName").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": Performing " + actionType + " on " + elementName + " at " + xposPercent + ", " + yposPercent);
				//action.clickAt(elementName, xposPercent, yposPercent);
				if(taskApplication.equalsIgnoreCase("excel"))
				{
					seleniumExcelActionLib.clickAt(elementName, xposPercent, yposPercent);
				}
				if(taskApplication.equalsIgnoreCase("word"))
				{
					seleniumWordActionLib.clickAt(elementName, xposPercent, yposPercent);
				}
				if(taskApplication.equalsIgnoreCase("access"))
				{
					seleniumAccessActionLib.clickAt(elementName, xposPercent, yposPercent);
				}
				if(taskApplication.equalsIgnoreCase("ppt"))
				{
					seleniumPPTActionLib.clickAt(elementName, xposPercent, yposPercent);
				}
			}

			else if(actionType.equalsIgnoreCase("selectText_Word"))
			{
				String elementName = taskAction.selectSingleNode("actionType/elementName").getText();
				int start = Integer.parseInt(taskAction.selectSingleNode("actionType/start").getText());
				int end = Integer.parseInt(taskAction.selectSingleNode("actionType/end").getText());
				String methodType = taskAction.selectSingleNode("actionType/methodType").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": " + " selection from " + start + " to " + end );

				if(methodType.equalsIgnoreCase("keyboard")){
					seleniumWordActionLib.SelectText(elementName, start, end, BaseActionLib.Method.Mouse);
				}
				if(methodType.equalsIgnoreCase("mouse")){
					seleniumWordActionLib.SelectText(elementName, start, end, BaseActionLib.Method.Keyboard);
				}
				if(methodType.equalsIgnoreCase("robot")){
					seleniumWordActionLib.SelectText(elementName, start, end, BaseActionLib.Method.Robot);
				}
			}



			else if(actionType.equalsIgnoreCase("selectFromDropdown"))
			{
				String elementName = taskAction.selectSingleNode("actionType/elementName").getText();
				String dropdownOption = taskAction.selectSingleNode("actionType/option").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": " + actionType + " - " + elementName + " with option - " + dropdownOption);
				seleniumActionLib.selectFromDropdown(elementName, dropdownOption);
			}
			else if(actionType.equalsIgnoreCase("selectFromColorDropdown"))
			{
				String elementName = taskAction.selectSingleNode("actionType/elementName").getText();
				String dropdownOption = taskAction.selectSingleNode("actionType/option").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": " + actionType + " - " + elementName + " with option - " + dropdownOption);
				seleniumActionLib.selectFromColorDropdown(elementName, dropdownOption);
			}

			else if(actionType.equalsIgnoreCase("selectFromListbox"))
			{
				String elementName = taskAction.selectSingleNode("actionType/elementName").getText();
				String listOption = taskAction.selectSingleNode("actionType/option").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": " + actionType + " - " + elementName + " with option - " + listOption);
				seleniumActionLib.selectFromListbox(elementName, listOption);
			}

			else if(actionType.equalsIgnoreCase("selectSlideFromSlidePane"))
			{
				String slideNumber = taskAction.selectSingleNode("actionType/slideNumber").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": " + "slide number " + slideNumber + " selected in the slide pane");
				seleniumPPTActionLib.selectSlideFromSlidePane(slideNumber);
			}


			else if(actionType.equalsIgnoreCase("dragAndDropSlideInSlidePane"))
			{
				String firstSlideNumber = taskAction.selectSingleNode("actionType/firstSlide").getText();
				String secondSlideNumber = taskAction.selectSingleNode("actionType/secondSlide").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": " + actionType + " dragging slide from " + firstSlideNumber + " to " + secondSlideNumber);
				seleniumPPTActionLib.dragAndDropSlideInSlidePane(firstSlideNumber, secondSlideNumber);
			}

			else if(actionType.equalsIgnoreCase("rightClickSlideinSlidePane"))
			{
				String slideNumber = taskAction.selectSingleNode("actionType/slideNumber").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": " + "slide number " + slideNumber + " right clicked in the slide pane");
				seleniumPPTActionLib.rightClickSlideInSlidePane(slideNumber);
			}
			else if(actionType.equalsIgnoreCase("waitForSec"))
			{
				String sec = taskAction.selectSingleNode("actionType/time").getText();
				BaseTest.logInfo(taskName ,"Waiting for " + sec + " sec");
				seleniumActionLib.waitFor(Integer.parseInt(sec)*1000);
			}
			else if(actionType.equalsIgnoreCase("doubleClickFillHandle"))
			{
				String elementName = taskAction.selectSingleNode("actionType/elementName").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": " + actionType + " on element " + elementName);
				if(taskApplication.equalsIgnoreCase("excel")){
					seleniumExcelActionLib.doubleClickFillHandle(elementName);
				}else if(taskApplication.equalsIgnoreCase("access")){
					seleniumAccessActionLib.doubleClickFillHandle(elementName);
				}
			}
			else if(actionType.equalsIgnoreCase("pressKeyFromVirtualKeyboard"))
			{
				String keyName = taskAction.selectSingleNode("actionType/keyName").getText();
				BaseTest.logInfo(taskName ,"Performing Action " + actionSNo + ": Press " + keyName);
				seleniumActionLib.pressKeyFromVirtualKeyboard(keyName);
			}
			else
				{
				System.err.println("Invalid Action type: " + actionType);
				Assert.fail("Invalid Action type: " + actionType);
				}
		} catch(org.openqa.selenium.UnhandledAlertException ex){
			BaseTest.logInfo(taskName ,"Unexpected Alert open while performing action no." + actionSNo);
			Alert alert = driver.switchTo().alert();  
			  if(alert != null){
				  alert.dismiss();
			  }
			 Assert.fail("Unexpected Alert open while performing action no." + actionSNo);
		}catch(org.openqa.selenium.WebDriverException e){
			if(e.getMessage().contains("Element is not clickable at point") && e.getMessage().contains("Other element would receive the click")){
				if(actionExcecutionCounter == 1){
					BaseTest.logError(taskName ,"Other element receiving the click");
					WebElement instructionsDiv1 = driver.findElement(By.xpath("//*[@id='ribbonmaindiv']//*[contains(@class, 'minimal-button')]"));
					Actions moveAction = new Actions(driver);
					moveAction.moveToElement(instructionsDiv1).build().perform();
					seleniumActionLib.waitForOneSec();	
					BaseTest.logInfo(taskName ,"Re running action no."+ actionSNo);
					this.executeAction(taskAction, actionSNo, driver, taskApplication, currentItemNum, config_prop, taskName, simsActionLibrary);
				}else{
					try {
						throw new ActionFailedException(e + "\n[ERROR] Action " + actionSNo + " Failed. Item no "+currentItemNum +" Failed");
					} catch (ActionFailedException e1) {
						BaseTest.logError(taskName , "Error: " + e1.getMessage());
						BaseTest.logInstruction("=====================================================================\n PATHWAY FAILED  "+ taskName +"\n=====================================================================" );
						Assert.fail("Error: " + e1.getMessage());
						e1.printStackTrace();
					}
				}
			}else {
				try {
					throw new ActionFailedException(e + "\n[ERROR] Action " + actionSNo + " Failed. Item no "+currentItemNum +" Failed");
				} catch (ActionFailedException e1) {
					// TODO Auto-generated catch block
					BaseTest.logError(taskName , "Error: " + e1.getMessage());
					BaseTest.logInstruction("=====================================================================\n PATHWAY FAILED  "+ taskName +"\n=====================================================================" );
					Assert.fail("Error: " + e1.getMessage());
					e1.printStackTrace();
				}
			}
		}catch (Exception e) {
			try {
				throw new ActionFailedException(e + "\n[ERROR] Action " + actionSNo + " Failed. Item no "+currentItemNum +" Failed");
			} catch (ActionFailedException e1) {
				// TODO Auto-generated catch block
				BaseTest.logError(taskName , "Error: " + e1.getMessage());
				BaseTest.logInstruction("=====================================================================\n PATHWAY FAILED  "+ taskName +"\n=====================================================================" );
				Assert.fail("Error: " + e1.getMessage());
				e1.printStackTrace();
			}			
		}
	}
}
