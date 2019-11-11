package ke.co.esuite.db.persistence.domain;

import java.io.Serializable;

public class UnifiPayPackage  implements Serializable{
	private static final long serialVersionUID = 1L;
	
	
	private long id;
	private String description;
	private double amount;
	private int minutes;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public int getMinutes() {
		return minutes;
	}
	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}
	
	
}
