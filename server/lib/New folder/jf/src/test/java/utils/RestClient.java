package utils;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class RestClient {

	private static String REST_API_XPATH = "/api/xpaths/" ;
	private static String RESPONSE_JSON ="application/json";
	public RestClient() {
		// TODO Auto-generated constructor stub
	}
	
	public JSONArray getXPathJson(String appName){
		JSONArray responseJson = new JSONArray();
		try {
			Client client = Client.create();

			WebResource webResource = client
			   .resource(SimsConstants.REST_API_SERVER_URL+ REST_API_XPATH + appName);

			ClientResponse response = webResource.accept(RESPONSE_JSON)
	                   .get(ClientResponse.class);

			if (response.getStatus() != 200) {
			   throw new RuntimeException("Failed : HTTP error code : "
				+ response.getStatus());
			}

			String output = response.getEntity(String.class);
			responseJson = (JSONArray) new JSONParser().parse(output);
		  } catch (Exception e) {
			e.printStackTrace();
		  }
		return responseJson;
	}
}
