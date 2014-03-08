package honeycache.cache.endpoint;

import honeycache.cache.model.HCacheMetadata;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HiveEndpoint extends AbstractEndpoint implements Endpoint{
	
	public static final String DRIVER_NAME = "org.apache.hadoop.hive.jdbc.HiveDriver"; 

/*	
	private String host = "localhost";
	private int port = 10000;
	private String user = "";
	private String password = "";
	*/
	public HiveEndpoint(){
		//super();
		host = "localhost";
		port = 10000;
		user = "";
		password =  "";
		connectionString = "jdbc:hive://" + host +":" + port + "/default";
		driverName = DRIVER_NAME;
	}
	
	public HiveEndpoint(String newHost, int newPort, String newUser, String newPassword){
		//super(newHost, newPort, newUser, newPassword, "");
		host = newHost;
		port = newPort;
		user = newUser;
		password = newPassword;
		connectionString = "jdbc:hive://" + host +":" + port + "/default";
		driverName = DRIVER_NAME;
	}
	

	public String toString() {
		return  getConnectionString();
	}

	@Override
	public HCacheMetadata getCacheMetadata(String key) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HCacheMetadata getOldestCacheEntry() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HCacheMetadata getNewestCacheEntry() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HCacheMetadata getMostFrequentCacheEntry() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HCacheMetadata getLeastFrequentCacheEntry() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HCacheMetadata getRandomCacheEntry() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultSet getCacheData(String location) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putCacheData(String key, ResultSet res) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteCacheData(HCacheMetadata key) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateMetadata(HCacheMetadata meta) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getTotalCacheSize() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTotalCacheEntryCount() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}





}
