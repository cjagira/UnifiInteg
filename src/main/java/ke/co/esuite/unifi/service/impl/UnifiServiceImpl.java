package ke.co.esuite.unifi.service.impl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import ke.co.esuite.db.persistence.DbMapper;
import ke.co.esuite.db.persistence.domain.UnifiData;
import ke.co.esuite.db.persistence.domain.UnifiPayPackage;
import ke.co.esuite.sms.service.SMSService;
import ke.co.esuite.unifi.service.UnifiService;
import ke.co.esuite.unifi.utils.ApiResponse;

@Service
public class UnifiServiceImpl implements UnifiService {

	@Autowired
	private Environment env;
	@Autowired
	private SMSService sms;
	@Autowired 
	private DbMapper db;

	// Unifi connection properties. Initialized in the constructor
	private String host;
	private int port;
	private String user;
	private String password;
	private String site;

	// Global properties to be used by all methods
	private SSLContext sc;
	private CookieStore cookieStore;
	private CloseableHttpClient client;

	// variables
	private boolean isloggedIn = false;
	// private long createTime;

	public UnifiServiceImpl() {
		super();		
	}

	@Override
	public void generateAndSendVoucher(String data) {
		/*
		 * {"phoneNumber":"254724008507", "amount" : "1000",
		 * "transId":"MPER99838KF"}
		 */

		ApiResponse smsRes = new ApiResponse();
		this.host = env.getRequiredProperty("unifi.host");
		this.port = Integer.parseInt(env.getRequiredProperty("unifi.port"));
		this.user = env.getRequiredProperty("unifi.user");
		this.password = env.getRequiredProperty("unifi.password");
		this.site = env.getRequiredProperty("unifi.site");
		
		try {
			double ratePerMinute = Double.parseDouble(env.getRequiredProperty("unifi.rate_per_minute"));

			JSONObject jsonObject = new JSONObject(data);
			String phone = (String) jsonObject.get("phoneNumber");
			double amount = jsonObject.getDouble("amount");
			String transId = jsonObject.getString("transId");

			/*Check package in db. If not there use default rate/minute*/
			List<UnifiPayPackage> packages = db.searchPackage(amount);			
			int minutes;			
			if(packages.size() > 0){
				minutes = packages.get(0).getMinutes();
				System.out.println("Package: "+packages.get(0).getDescription());
			}else{
				minutes = (int) Math.round(amount / ratePerMinute);
				System.out.println("Default Rate "+minutes);
			}			
			/*End check package*/
			
			phone = this.validatePhone(phone);
			System.out.println(data);
			System.out.println("minutes = "+minutes);
			
			//Login and generate voucher
			String voucher = ""; 
			String smsMessage = "";
			if(!env.getRequiredProperty("unifi.test").equals("true")){
				this.login();
				long voucherCreateTime = this.createVoucher(minutes);
				voucher = this.printVoucher(voucherCreateTime);
				this.logout(); //TODO find a way of reusing the login session
				
				//Send SMS
				smsMessage = String.format(env.getRequiredProperty("unifi.sms_message"), voucher,minutes);
				smsRes = sms.sendSMS(phone, smsMessage);
			}			
			
			//Save to database
			UnifiData unifi = new UnifiData();
			unifi.setMpesaTransId(transId);
			unifi.setCreatedDate(new Date());
			unifi.setMinutes(minutes);
			unifi.setPhoneNumber(phone);
			unifi.setTransAmount(amount);
			unifi.setVoucherNumber(voucher);
			unifi.setSmsStatus(smsRes.getStatus());
			unifi.setRetMessage(smsRes.getMessage());
			unifi.setSmsMessage(smsMessage);
			db.saveUnifiToken(unifi);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void login() {

		try {
			cookieStore = new BasicCookieStore();
			sc = this.getSSLContexts();

			client = HttpClients.custom().setDefaultCookieStore(cookieStore).setSSLContext(sc)
					.setSSLHostnameVerifier(new NoopHostnameVerifier()).build();

			HttpPost post = new HttpPost(String.format("https://%s:%d/api/login", host, port));
			post.setHeader("Accept", "application/json");
			post.setHeader("Content-type", "application/json");
			post.setEntity(
					new StringEntity(String.format("{\"username\":\"%s\", \"password\":\"%s\"}:", user, password)));

			CloseableHttpResponse response = client.execute(post);

			if (response.getStatusLine().getStatusCode() == 200) {
				isloggedIn = true;
			}

			System.out.println(response.toString());
			response.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void logout() {
		String url = String.format("https://%s:%d/api/logout", host, port);
		this.executeApi(url, "{}");
	}

	public long createVoucher(int minutes) {
		long createTime = 0;
		System.out.println("Cookie:" + cookieStore.getCookies().toString());

		try {
			String url = String.format("https://%s:%d/api/s/%s/cmd/hotspot", host, port, site);
			String data = String.format("{\"cmd\":\"create-voucher\",\"quota\":\"1\",\"expire\":\"%d\",\"n\":\"1\"}",
					minutes);

			ApiResponse res = this.executeApi(url, data);

			if (res.getStatus() == 200) {

				JSONObject jsonObject = new JSONObject(res.getData());
				JSONArray dataJ = (JSONArray) jsonObject.get("data");

				JSONObject jsonObject2 = (JSONObject) dataJ.get(0);
				createTime = (Integer) jsonObject2.get("create_time");
				System.out.println("createTime=" + createTime);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return createTime;
	}

	public String printVoucher(long voucherCreateTime) {
		String voucher = "";

		try {
			String url = String.format("https://%s:%d/api/s/%s/stat/voucher", host, port, site);
			String data = String.format("{\"create_time\":%d}", voucherCreateTime);

			System.out.println(data);
			ApiResponse res = this.executeApi(url, data);

			if (res.getStatus() == 200) {

				JSONObject jsonObject = new JSONObject(res.getData());
				JSONArray dataJ = (JSONArray) jsonObject.get("data");

				JSONObject jsonObject2 = (JSONObject) dataJ.get(0);
				voucher = (String) jsonObject2.get("code");
				System.out.println("voucher=" + voucher);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return voucher;
	}

	private SSLContext getSSLContexts() {
		SSLContext s = null;
		try {
			s = SSLContexts.custom().loadTrustMaterial(new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).build();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return s;
	}

	private ApiResponse executeApi(String url, String data) {
		ApiResponse res = new ApiResponse();

		try {
			HttpPost post = new HttpPost(url);
			post.setHeader("Accept", "application/json");
			post.setHeader("Content-type", "application/json");
			post.setEntity(new StringEntity(data));

			CloseableHttpResponse response = client.execute(post);

			res.setStatus(response.getStatusLine().getStatusCode());
			res.setMessage(response.toString());
			res.setData(EntityUtils.toString(response.getEntity()));

			System.out.println(res.getMessage());
			System.out.println(res.getData());

			response.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	private String validatePhone(String phone){
		String validPhone = "+"+phone;
		
		
		return validPhone;
	}
	public boolean isIsloggedIn() {
		return isloggedIn;
	}

	public void setIsloggedIn(boolean isloggedIn) {
		this.isloggedIn = isloggedIn;
	}

	@Override
	public List<UnifiPayPackage> getPayPackages() {
		return db.getAllPackage();
	}
}
