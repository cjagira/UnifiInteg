package ke.co.esuite.unifi.utils;

public class ApiResponse {

	private int status;
	private String message;
	private String data;
	
	
	public ApiResponse() {
		super();
	}


	public ApiResponse(int status, String message, String data) {
		super();
		this.status = status;
		this.message = message;
		this.data = data;
	}
	
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}


	@Override
	public String toString() {
		return "ApiResponse [status=" + status + ", message=" + message + ", data=" + data + "]";
	}
	
	
	
}
