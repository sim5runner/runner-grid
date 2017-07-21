package locator;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.testng.Assert;

import com.compro.core.testframework.BaseTest;

import utils.SimsConstants;

public class xpathFromProperties{

	public xpathFromProperties() {
		// TODO Auto-generated constructor stub
	}

	public Properties getProperties(String taskName , String propertyFilename) {
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
				   BaseTest.logError(taskName, "Parsing error found in properties file in:"+ propertyFilename);
				   for(int i = 0 ; i < errorlog.size() ;i++){
					   BaseTest.logError(taskName, errorlog.get(i));
				   }
				   Assert.fail("Parsing error found in properties file in:"+ propertyFilename);
			   }
			   BaseTest.logInfo(taskName, "Poperties file loaded successfully: "+propertyFilename);
			  } catch (Exception e) {
				  BaseTest.logError(taskName, "Error: " + e.getMessage());
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
