package ke.co.esuite.db.persistence;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import ke.co.esuite.config.UseDatasourceDb;
import ke.co.esuite.db.persistence.domain.UnifiData;
import ke.co.esuite.db.persistence.domain.UnifiPayPackage;

@UseDatasourceDb
public interface DbMapper {
	public void saveUnifiToken(UnifiData data);
	public void updateUnifiTokenSms(@Param("smsStatus") int smsStatus, 
			@Param("smsMessage") String smsMessage, 
			@Param("retMessage") String retMessage, 
			@Param("voucherNumber") String voucherNumber);	
	public List<UnifiPayPackage> searchPackage(@Param("amount") double amount);	
	public List<UnifiPayPackage> getAllPackage();
	
	
}
