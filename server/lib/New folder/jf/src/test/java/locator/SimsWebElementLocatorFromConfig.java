package locator;

import java.util.List;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import utils.ComponentParser;

import com.compro.core.locator.WebElementLocator;
import com.compro.core.locator.WebElementLocatorFromConfig;

public class SimsWebElementLocatorFromConfig extends WebElementLocatorFromConfig implements WebElementLocator {
	WebDriver driver = null;
	Properties configProperties = new Properties();
	String taskApp = "";
	
	public SimsWebElementLocatorFromConfig(WebDriver d ,Properties config_properties ,String taskApp) {
		// TODO Auto-generated constructor stub
		super(d, config_properties);
		this.driver = d;
		this.configProperties = config_properties;
		this.taskApp = taskApp;
	}
	
	@Override
	public WebElement getWebelement(String elementName) throws Exception{
		super.currentWebelement = elementName;
		WebElement we = null;

		if(!(elementName.contains("__"))){
			we  = getWebElementByLocator(elementName);
		}
		else {
			ComponentParser ribbonParser = new ComponentParser( driver , taskApp);

			try{
				List<WebElement> weList = getWebElementByXpath(ribbonParser.getXpathFromEtext(elementName));
				we = weList.get(ribbonParser.getElementIndex());
			}catch(Exception e){
                System.out.println("generic Xpath not found, Searching in config..");
				we  = getWebElementByLocator(elementName);
			}


		}

		return we;
	}
	
	public String[] getFixedElementXpath(String etext) throws Exception{
		return (getKeyValue(etext)).split(";");
	}
}
