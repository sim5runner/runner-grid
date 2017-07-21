package locator;

import java.util.Iterator;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import utils.RestClient;


public class xpathFromDB {

	public xpathFromDB() {
		// TODO Auto-generated constructor stub
	}
	public Properties getProperties(String taskName, String appName) {
		RestClient rc = new RestClient();
		JSONArray jsonArray = rc.getXPathJson(appName);
		Properties prop = new Properties();
		Iterator itr = jsonArray.iterator();
		while(itr.hasNext()){
	        JSONObject jsonObj = (JSONObject)itr.next();
	        JSONObject xpath = (JSONObject)jsonObj.get("xpath");
	        String key = (String)xpath.get("key");
	        String value = (String)xpath.get("value");
	        prop.setProperty( key , value);
		}
		return prop;
	}
}
