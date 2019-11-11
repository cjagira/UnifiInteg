package ke.co.esuite.sms.service;

import ke.co.esuite.unifi.utils.ApiResponse;

public interface SMSService {
	public ApiResponse sendSMS(String recipients, String message);
}
