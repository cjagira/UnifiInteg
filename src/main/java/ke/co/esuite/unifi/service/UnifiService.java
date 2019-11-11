package ke.co.esuite.unifi.service;

import java.util.List;

import ke.co.esuite.db.persistence.domain.UnifiPayPackage;

public interface UnifiService {
	public void generateAndSendVoucher(String data);
	public List<UnifiPayPackage> getPayPackages();
}
