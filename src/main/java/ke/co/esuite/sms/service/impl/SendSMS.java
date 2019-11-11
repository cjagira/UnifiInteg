package ke.co.esuite.sms.service.impl;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import ke.co.esuite.db.persistence.DbMapper;
import ke.co.esuite.sms.service.SMSService;
import ke.co.esuite.unifi.utils.ApiResponse;

@Service
public class SendSMS implements SMSService {

	@Autowired
	private Environment env;
	@Autowired
	private DbMapper db;
	
	//Login credentials
    private String username;
    private String apiKey;
    private String apiEnv;
    
    
    public SendSMS(){
    	super();   			
    }
    
	/**
	 * // Specify the numbers that you want to send to in a comma-separated list
	 * // Please ensure you include the country code (+254 for Kenya in this case)
	 * @param recipients e.g. "+254711XXXYYY,+254733YYYZZZ";
	 * @param message e.g. "We are lumberjacks. We code all day and sleep all night"
	 */
    @Override
	public ApiResponse sendSMS(String recipients, String message){
		
    	ApiResponse res = new ApiResponse();
    	
		AfricasTalkingGateway gateway;
    	this.username = env.getRequiredProperty("sms.username");
    	this.apiKey = env.getRequiredProperty("sms.api_key");
    	this.apiEnv = env.getRequiredProperty("sms.api_env"); 
    	
    	
    	int status = 0;
    	String retMsg = "";
		
		if(apiEnv.equalsIgnoreCase("sandbox")){
			// Create a new instance of our awesome gateway class
	        gateway  = new AfricasTalkingGateway("sandbox", apiKey, "sandbox");
		}else{
			// Create a new instance of our awesome gateway class
	        gateway  = new AfricasTalkingGateway(username, apiKey);
		}
        
        /*************************************************************************************
            NOTE: If connecting to the sandbox:
            1. Use "sandbox" as the username
            2. Use the apiKey generated from your sandbox application
                https://account.africastalking.com/apps/sandbox/settings/key
            3. Add the "sandbox" flag to the constructor
            AfricasTalkingGateway gateway = new AfricasTalkingGateway(username, apiKey, "sandbox");
        **************************************************************************************/
        // Thats it, hit send and we'll take care of the rest. Any errors will
        // be captured in the Exception class below
        try {
            JSONArray results = gateway.sendMessage(recipients, message);
            for( int i = 0; i < results.length(); ++i ) {
                JSONObject result = results.getJSONObject(i);
                System.out.print(result.getString("status") + ","); // status is either "Success" or "error message"
//                System.out.print(result.getString("statusCode") + ",");
                System.out.print(result.getString("number") + ",");
                System.out.print(result.getString("messageId") + ",");
                System.out.println(result.getString("cost"));
                retMsg = result.getString("status")+" "+result.getString("messageId");
            }
            status = 1;
            
            
            res.setData(results.toString());
        } catch (Exception e) {
            System.out.println("Encountered an error while sending " + e.getMessage());
            status = 9;
            retMsg = "Encountered an error while sending " +e.getMessage();
        }finally{
        	res.setStatus(status);
            res.setMessage(retMsg);
        }        
                
        return res;
    }

}
