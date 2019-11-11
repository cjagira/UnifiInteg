package ke.co.esuite.unifi.controller;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ke.co.esuite.db.persistence.domain.UnifiPayPackage;
import ke.co.esuite.unifi.service.UnifiService;

@RestController
@RequestMapping("/service")
public class UnifiController {

	@Autowired
	private UnifiService unifiService;
	
	@Autowired
	private Environment env;
	
	@RequestMapping(value = "unifi/generateVoucher", method = RequestMethod.POST)
	public ResponseEntity<String> generateVoucher(@RequestParam String token, @RequestBody String request){	
		ResponseEntity<String> res;
		String resJson;
		try{
			String validToken = env.getRequiredProperty("unifi.access_token");			
			System.out.println("Data: "+request);
			
			if(token.equals(validToken)){
				unifiService.generateAndSendVoucher(request);
						
				resJson = String.format("{\"message\":\"Success\"}");
				
				res = ResponseEntity.status(HttpStatus.OK)
					       .contentType(MediaType.APPLICATION_JSON)
					       .body(resJson);
			}else{
				resJson = String.format("{\"message\":\"Invalid auth token\"}");
				
				res = ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					       .contentType(MediaType.APPLICATION_JSON)
					       .body(resJson);
			}
		}catch(Exception e){
			e.printStackTrace();
			resJson = String.format("{\"message\":\"Error: %s\"}", e.getMessage());
			res = ResponseEntity.status(HttpStatus.BAD_REQUEST)
				       .contentType(MediaType.APPLICATION_JSON)
				       .body(resJson);		}
		
		return res;
	}
	
	@RequestMapping(value = "unifi/update", method = RequestMethod.POST)
	public ResponseEntity<String> updateAction(@RequestBody String request){	
		ResponseEntity<String> res;
		String resJson;
		try{
//			String validToken = env.getRequiredProperty("unifi.access_token");			
			System.out.println("Data: "+request);
						
			resJson = String.format("{\"message\":\"Success\"}");			
			res = ResponseEntity.status(HttpStatus.OK)
				       .contentType(MediaType.APPLICATION_JSON)
				       .body(resJson);
			
		}catch(Exception e){
			e.printStackTrace();
			resJson = String.format("{\"message\":\"Error: %s\"}", e.getMessage());
			res = ResponseEntity.status(HttpStatus.BAD_REQUEST)
				       .contentType(MediaType.APPLICATION_JSON)
				       .body(resJson);		}
		
		return res;
	} 
	
	@RequestMapping(value = "unifi/paypackages", method = RequestMethod.GET)
	public ResponseEntity<String> getPackages(){	
		ResponseEntity<String> res;
		String resJson;
		try{			
				List<UnifiPayPackage> packages = unifiService.getPayPackages();
				
				JSONObject data = new JSONObject();
				data.put("message", "success");
				
				JSONArray jArray = new JSONArray();
				for (UnifiPayPackage pack : packages)
			    {
			         JSONObject offer = new JSONObject();
			         offer.put("id", pack.getId());
			         offer.put("description", pack.getDescription());
			         offer.put("amount", pack.getAmount());			         
			         offer.put("minutes", pack.getMinutes());
			         jArray.put(offer);
			    }
				
				data.put("data", jArray);
				resJson = "unifiPackages("+data.toString()+");";				
				res = ResponseEntity.status(HttpStatus.OK)
					       .contentType(MediaType.APPLICATION_JSON)
					       .body(resJson);
		}catch(Exception e){
			e.printStackTrace();
			resJson = String.format("{\"message\":\"Error: %s\"}", e.getMessage());
			res = ResponseEntity.status(HttpStatus.BAD_REQUEST)
				       .contentType(MediaType.APPLICATION_JSON)
				       .body(resJson);		}
		
		return res;
	}

}
