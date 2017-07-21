package locator;

import java.util.Properties;

public class xpathLocator implements SimsXpathPropertiesService {

	public xpathLocator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Properties getProperties(boolean fromDB ,String taskName , String fileName) {
	Properties properties = null;
		if(fromDB){
			xpathFromDB xpathFromDB= new xpathFromDB();
			String appName = fileName.substring(0, fileName.indexOf("_config.properties"));
			properties = xpathFromDB.getProperties(taskName , appName);
		}else{
			xpathFromProperties xpathFromProperties = new xpathFromProperties();
			properties = xpathFromProperties.getProperties(taskName , fileName);
		}
		return properties;
	}

}
