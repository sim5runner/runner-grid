package utils;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.compro.utils.FileUtils;
import com.google.common.collect.ObjectArrays;

public class ComponentParser {

	String[] inputString = null;
	public String path = "";
	public int levelCompleted;
	WebDriver driver = null;
	String refElementXpath = "";
	String simsComponent = "";
	String _currentAppName = "";
	int controlIndex = 0;
	TreeParser<String> tree;
	
	public ComponentParser(WebDriver d, String taskApp){
		driver = d;
		_currentAppName = taskApp;
		
		FileUtils utils = new FileUtils();
		String fileString = utils.readFileAsString(SimsConstants.RIBBON_ELEMENTS_FILEPATH);
		
		try{
			tree = TreeParser.parse((fileString.replaceAll("\n", "")).replaceAll("\t", ""));
		}catch (Exception ex){
			ex.printStackTrace();
			
		}
	}
	
	public String getXpathFromEtext(String elementName) throws Exception {
		
		inputString = elementName.split("__");
		
		String result = getXpathFromEtext1(0, tree);
		
		if(simsComponent.equals("ribbon"))
		{
			// adding curly suffix
			result = result + appendCurlySuffix(inputString);
			return result;
		}
		else if(simsComponent.toLowerCase().equals("dialog"))
		{
			
			if(inputString[inputString.length-1].contains("{") && inputString[inputString.length-1].contains("}"))
			{
				
			// xpath to go to selected tab in dialog
			String selectedTabXpath = "/./ancestor-or-self::li[contains(concat(' ', @class, ' '), ' tab-page ')]";
			
			// append xpath to result
			result = result + selectedTabXpath + appendCurlySuffix(inputString);
			
			// Get list of all controls in Dialog matching element in curly braces
			List<WebElement> controlsListInDialog = driver.findElements(By.xpath(result));
			
			if(controlsListInDialog.size() > 1)
			{
				/**
				 * more than 1 control present in dialog 
				 * then find out the control closest to its reference sims label
				 */
				
				HashMap<Point2D, Integer> pointWithIndex = new HashMap<Point2D, Integer>();
				List<Point2D> pointsList = new LinkedList();
				List<Point2D> sortedPointsList = new LinkedList();
				
				
				LinkedList<Entry> xPosSortedList , yPosSortedList, finalList = null; 
				
				//Get coordinates of Ref Element
				int xPos = driver.findElement(locatorValue("xpath", refElementXpath)).getLocation().getX();
				int yPos = driver.findElement(locatorValue("xpath", refElementXpath)).getLocation().getY();
				
				// Create 2D point for Ref Element
				Point2D refElementPos = new Point2D.Double(xPos, yPos);
				
			      for (int i = 0; i < controlsListInDialog.size(); i++) {
			  		double x = controlsListInDialog.get(i).getLocation().getX();
			  		double y = controlsListInDialog.get(i).getLocation().getY();
			  		pointWithIndex.put(new Point2D.Double(x, y), i);
			  		pointsList.add(new Point2D.Double(x, y));
			        }
			      
			      ClosestPointReferer2D pointRefererObj = new ClosestPointReferer2D();
			      // Sort based on xPos
			      xPosSortedList = pointRefererObj.getClosestPoint("xPos" , pointsList, refElementPos);			      
			      
			      // Sort based on yPos
			      yPosSortedList = pointRefererObj.getClosestPoint("yPos" , pointsList, refElementPos);
			      
			      // Sort based on distance
			      for(int index=0; index<yPosSortedList.size(); index++) {
			    	  sortedPointsList.add((Point2D) yPosSortedList.get(index).getKey());
			    	  if((index<yPosSortedList.size()-1) && (yPosSortedList.get(index).getValue() != yPosSortedList.get(index+1).getValue()))
			    	  {
			    		  int xindex = 0;
			    		  while(xindex<xPosSortedList.size() && ((Double)xPosSortedList.get(xindex).getValue() <= (Double)yPosSortedList.get(xindex).getValue()))
			    			  sortedPointsList.add((Point2D) xPosSortedList.get(xindex++).getKey());
			    		  break ;
			    	  }
			      }
			      
			      // Sort based on distance
			      finalList = pointRefererObj.getClosestPoint("point" , sortedPointsList, refElementPos);
			      controlIndex = pointWithIndex.get((Point2D)finalList.get(0).getKey());
				}
			}
			
		}
		return result;
	}
	
	public String getXpathFromEtext1(int i, TreeParser<String> tree) throws Exception {

		TreeParser<String> currtree = tree;
		String out = "XPATH_NOT_FOUND";
		String[] fixedString = {"text" , "partialText"};
		
		int flag = 0;
		
		if(i == 0) {	
			simsComponent = inputString[0].toLowerCase();
			
			if(simsComponent.equals("ribbon"))
				{
				   currtree = currtree.firstChild().firstChild();
				}
			else if(simsComponent.equals("dialog"))
				{
				currtree = currtree.firstChild().nextSibling().firstChild();
				}
			else if(simsComponent.equals("docarea"))	// Note: doc sequence in tree must be fixed -> ppt, word, excel, access
				{
					switch(_currentAppName.toLowerCase()){	// update required
					        case "ppt":  {
					        	currtree = currtree.firstChild().nextSibling().nextSibling().firstChild();
					        }
					                 break;
					        case "word":  {
					        	currtree = currtree.firstChild().nextSibling().nextSibling().nextSibling().firstChild();
					        }
					                 break;
					        case "excel": {
					        	currtree = currtree.firstChild().nextSibling().nextSibling().nextSibling().nextSibling().firstChild();
				        	}
					        		break;
					        case "access": {
					        	currtree = currtree.firstChild().nextSibling().nextSibling().nextSibling().nextSibling().nextSibling().firstChild();
				        	}
					        		break;
					        default: System.out.println("invalid parameter in switch case. " + _currentAppName.toLowerCase());
					}
				}else if(simsComponent.equals("statusbar")){
					currtree = currtree.firstChild().nextSibling().nextSibling().nextSibling().nextSibling().nextSibling().nextSibling().firstChild();
				} else{
					currtree = currtree.firstChild().nextSibling().nextSibling().nextSibling().nextSibling().nextSibling().nextSibling().nextSibling().firstChild();
				}
				i++;
		}
		
		/**
		 * updating fixed string here from tree current node
		 */
		try{
			if((getCurlyContent(currtree.getValue().trim()) != null) && !(getCurlyContent(currtree.getValue().trim()).equals(""))){
				String[] treeFixedProperty = getCurlyContent(currtree.getValue().trim()).split(";");
				String[] treeConditionalProperty = {"partialTitle"};
				
				try{
					if(Arrays.asList(treeFixedProperty).contains("title")){
						treeFixedProperty = ObjectArrays.concat(treeFixedProperty,treeConditionalProperty, String.class);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				fixedString = ObjectArrays.concat(treeFixedProperty,fixedString, String.class);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		for(int j=0; j< fixedString.length; j++) {
			String currStr = "";
			if(i==1){
				// initialize prefix
				switch(simsComponent.toLowerCase()){
			        case "ribbon":  {
			        	currStr = "//*[contains(concat(' ', @class, ' '), ' ribbon ')][1]/./ancestor-or-self::*[contains(concat(' ', @class, ' '), ' ComponentFrame ')][1]";
			        }
			                 break;
			        case "dialog":  {
			        	currStr = "//*[contains(concat(' ', @class, ' '), ' DialogFrame ')][1]";
			        }
			                 break;
			        case "docarea": {
								switch(_currentAppName.toLowerCase()){
								        case "ppt":  {
								        	currStr = "//*[contains(concat(' ', @class, ' '), ' SlideViewComponent ')][1]/./ancestor-or-self::*[contains(concat(' ', @class, ' '), ' ComponentFrame ')][1]";
								        }
								                 break;
								        case "word":  {
								        	currStr = "//*[contains(concat(' ', @class, ' '), ' SIMS_DocumentArea ')][1]/./ancestor-or-self::*[contains(concat(' ', @class, ' '), ' ComponentFrame ')][1]";
								        }
								                 break;
								        case "excel": {
								        	currStr = "//*[contains(concat(' ', @class, ' '), ' SIMS_ExcelWorkBook ')][1]/./ancestor-or-self::*[contains(concat(' ', @class, ' '), ' ComponentFrame ')][1]";
							        	}
								        		break;
								        default: System.out.println("invalid parameter in switech case");
								}
		        	}
			        		break;
			        case "statusbar":  {
			        	currStr = "";
			        }
			                 break;
			        default: {
			        	currStr = "//*[contains(concat(' ', @class, ' '), '"+ simsComponent +"') or contains(concat(' ', @title, ' '), '"+ simsComponent +"')][1]";
			        }
				}
				
			}
			
			if((inputString[i].toLowerCase()).contains("parastartswith")){
				currStr += "//*[starts-with(.,'"+ getCurlyContent(inputString[i]) +"')][1]";
				j = fixedString.length;	 // replace with break
			} else{
				currStr += createPathFromParams(inputString[i], removeCurly(currtree.getValue()).trim(), fixedString[j]);	// removeCurlyContent from tree node value
			}
			
			if(isXpathPresentOnPage(currStr)) {
				path = (path.concat(currStr));
				levelCompleted = i;
				
				flag = 1;
				break;
			}else{
				continue;
			}
		} // end of for loop
		
		
		if(flag == 0) {
			if(currtree.hasNextSibling()) {
				currtree = currtree.nextSibling();
				out = getXpathFromEtext1(i, currtree);
			} else{
				throw new Exception("Xpath Not found for: \"" + inputString[i] + "\" In Element : " + Arrays.toString(inputString));
			}
		} else{
			// check if input array string provided by user has more elements
			if(++i < inputString.length && flag == 1) {
				out = getXpathFromEtext1(i, currtree.firstChild());
			} else {
				out = path;
			}
		}
		return out;
	}
	
	private boolean isXpathPresentOnPage(String xpath){
		boolean found = false;
		By element = null;
		try{
			element =  locatorValue("xpath", xpath);
			found = (driver.findElements(element).size() > 0);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return found;
	}


	private String createPathFromParams(String etext, String property, String searchBy) throws InterruptedException{
		String returnPathValue = null;
		
		etext = removeCurly(etext);
		
		if(!("ribbon;dialog;docarea;statusbar;".contains(simsComponent.toLowerCase()+";"))){
			returnPathValue = "//*[text() ='"+ etext +"']/./ancestor-or-self::*[contains(@class, '"+ property +"')][1]";
		}else{
				switch (searchBy) {
		        case "text":  {
		        	refElementXpath = "//*[. ='"+ etext +"']";
		        	
		        	switch (property) {
		        	case "tab-header": 
		        		returnPathValue = "//*[. ='"+ etext +"']/./ancestor-or-self::*[contains(concat(' ', @class, ' '), ' "
		        						+ property +" ')][1]";
	
		        		By element =  locatorValue("xpath", returnPathValue);
		        		if(driver.findElements(element).size() > 1){
		        			returnPathValue = "//*[. ='"+ etext +"']/./ancestor-or-self::*[contains(concat(' ', @class, ' '), ' "
			        				+ property +" ')  and contains(@style,'display')][1]";
		        		}
		        		break;
		        		default:
		        			returnPathValue = "//*[. ='"+ etext +"']/./ancestor-or-self::*[contains(concat(' ', @class, ' '), ' "+ property +" ')][1]";
		        	}
		        	break;
		        }
		        case "title":  {
		        	returnPathValue = "//*[@title = '"+ etext +"' and contains(concat(' ', @class, ' '), ' "+ property +" ')]";
		        	refElementXpath = returnPathValue;
		        }
		                 break;
		        case "partialText": {
		        	refElementXpath = "//*[contains(.,'"+ etext +"')]";
		        	switch (property) {
		        	case "tab-header": 
		        		returnPathValue = "//*[contains(.,'"+ etext +"')]/./ancestor-or-self::*[contains(concat(' ', @class, ' '), ' "
		        				+ property +" ')][1]";
		        		
		        		By element =  locatorValue("xpath", returnPathValue);
		        		if(driver.findElements(element).size() > 1){
		        			returnPathValue = "//*[contains(.,'"+ etext +"')]/./ancestor-or-self::*[contains(concat(' ', @class, ' '), ' "
			        				+ property +" ')  and contains(@style,'display')][1]";
		        		}
		        		
		        		break;
		        		default:
		        			returnPathValue = "//*[contains(.,'"+ etext +"')]/./ancestor-or-self::*[contains(concat(' ', @class, ' '), ' "
			        				+ property +" ')][1]";
		        	}
		        	
		        }
		        break;
		        case "partialTitle":  {
		        	returnPathValue = "//*[contains(concat(' ', @title, ' '), ' "+ etext +" ') and contains(concat(' ', @class, ' '), ' "+ property +" ')]";
		        	refElementXpath = returnPathValue;
		        }
		        break;
		        default:  {
		        	returnPathValue = "//*[contains(concat(' ', @"+searchBy+", ' '), ' "+ etext +" ')]/./ancestor-or-self::*[contains(@class, '"+ property +"')][1]";
		        	refElementXpath = returnPathValue;
		        }
			}
		}
		return returnPathValue;
	}

	
	private String removeCurly(String s){
        String reg = "\\s*\\{[^}]+\\}(?=[^{}]*$)";
		return s.replaceAll(reg, "");
	}
	
	private enum Curly {
		section_launcher ("//*[contains(concat(' ', @class, ' '), ' section-launcher ')]"),
		gallery_button_up ("//*[contains(concat(' ', @class, ' '), ' gallery-button-up ')]"),
		gallery_button_down ("//*[contains(concat(' ', @class, ' '), ' gallery-button-down ')]"),
		spinUpBtn ("//*[contains(concat(' ', @class, ' '), ' spinUpBtn ')]"),
		spinDownBtn ("//*[contains(concat(' ', @class, ' '), ' spinDownBtn ')]"),
		dropdown_button ("//*[contains(concat(' ', @class, ' '), ' dropdown-button ')]"),
		textbox ("//*[contains(concat(' ', @class, ' '), ' sims-TextBox ')]"),
		spinInput ("//*[contains(concat(' ', @class, ' '), ' spinInput ')]"),
		scrollUp ("//*[contains(concat(' ', @class, ' '), ' simsLBUpScroll ')]"),
		scrollDown ("//*[contains(concat(' ', @class, ' '), ' simsLBDownScroll ')]"),
		dialog_dropdown_button ("//*[contains(concat(' ', @class, ' '), ' sims-DropDown ')]"),
		dialog_colorgriddropdown_button ("//*[contains(concat(' ', @class, ' '), ' sims-ColorGridDropDown ')]"),
		colorgriddropdown_button ("//*[contains(concat(' ', @class, ' '), ' sims-ColorGridDropDown ')]"),
		listbox ("//*[contains(concat(' ', @class, ' '), ' sims-SimsListBox ')]")
	    ; 

	    private final String value;

	    private Curly(String levelCode) {
	        this.value = levelCode;
	    }
	}
	
	private String getCurlyContent(String s){
		 String insideCurly = "";
	     Matcher m = Pattern.compile("\\{(.*?)\\}").matcher(s);
	     while(m.find()) {
	    	 insideCurly = m.group(1);    
	     }
		return insideCurly;
	}
	
	public String appendCurlySuffix(String[] inputString) throws Exception{
		Curly key ;
		String returnValue = "";
		
		String lastEtextElement = inputString[inputString.length - 1];
		
		if(!(getCurlyContent(lastEtextElement).equals("") || getCurlyContent(lastEtextElement) == null)){
			
			switch (getCurlyContent(lastEtextElement)) {
        	case "dropdown-button"		: 
							        		if(simsComponent.equals("dialog"))
							        			key = Curly.dialog_dropdown_button;
							        		else 
							        			key = Curly.dropdown_button;
							        		break;
							        		
        	case "colorgrid-dropdown-button": 
												if(simsComponent.equals("dialog"))
													key = Curly.dialog_colorgriddropdown_button;
								        		else 
								        			key = Curly.colorgriddropdown_button;
					break;
            case "launcher"				:  key = Curly.section_launcher;
                     break;
            case "button-up"			:  key = Curly.gallery_button_up;
                     break;
            case "button-down"			:  key = Curly.gallery_button_down;
                     break;
            case "spinUp"				:  key = Curly.spinUpBtn;
                     break;
            case "spinDown"				:  key = Curly.spinDownBtn;
                     break;
            case "textbox"				:  key = Curly.textbox;  
            		 break;
			case "spinInput"			:  key = Curly.spinInput;  
            		 break;
            case "listbox"				:  key = Curly.listbox;  
   		 			break;
            default: throw new Exception("Invalid parameters inside curly bracets");
			}			
			returnValue = key.value;			
		}		
		return returnValue;
	}
	
	/*
	 * return instance of by class from locator and value
	 */
	private By locatorValue(String locatorType, String value) throws InterruptedException {

		By by;
		switch (locatorType) {
		case "id":
			by = By.id(value);
			break;
		case "name":
			by = By.name(value);
			break;
		case "xpath":
			by = By.xpath(value);
			break;
		case "css":
			by = By.cssSelector(value);
			break;
		case "linkText":
			by = By.linkText(value);
			break;
		case "partialLinkText":
			by = By.partialLinkText(value);
			break;
		default:
			by = null;
			break;
		}
		return by;
	}
	
	public int getElementIndex () {
		return controlIndex;
	}
	
}
