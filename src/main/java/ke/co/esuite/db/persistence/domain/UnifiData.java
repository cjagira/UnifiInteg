package ke.co.esuite.db.persistence.domain;

import java.io.Serializable;
import java.util.Date;

public class UnifiData implements Serializable{
	private static final long serialVersionUID = 1L;

	private String mpesaTransId;
	private String phoneNumber;
	private double transAmount;
	private int minutes;
	private String voucherNumber;
	private Date createdDate;
	private int smsStatus;
	private String smsMessage;
	private String retMessage;
	
	
	
	public String getMpesaTransId() {
		return mpesaTransId;
	}
	public void setMpesaTransId(String mpesaTransId) {
		this.mpesaTransId = mpesaTransId;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public double getTransAmount() {
		return transAmount;
	}
	public void setTransAmount(double transAmount) {
		this.transAmount = transAmount;
	}
	public int getMinutes() {
		return minutes;
	}
	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}
	public String getVoucherNumber() {
		return voucherNumber;
	}
	public void setVoucherNumber(String voucherNumber) {
		this.voucherNumber = voucherNumber;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public int getSmsStatus() {
		return smsStatus;
	}
	public void setSmsStatus(int smsStatus) {
		this.smsStatus = smsStatus;
	}
	public String getSmsMessage() {
		return smsMessage;
	}
	public void setSmsMessage(String smsMessage) {
		this.smsMessage = smsMessage;
	}
	public String getRetMessage() {
		return retMessage;
	}
	public void setRetMessage(String retMessage) {
		this.retMessage = retMessage;
	}
	
}
