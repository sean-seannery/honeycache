package honeycache.cache.endpoint;

import honeycache.cache.model.HCacheMetadata;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface Endpoint {
	
	public static final String MYSQL_ENDPOINT = "mysql";
	public static final String HBASE_ENDPOINT = "hbase";
	public static final String HIVE_ENDPOINT = "hive";
	
	public void connect() throws SQLException;
	public void close();
	public String getDriverName();
	public String getConnectionString();
	public ResultSet processQuery(String query) throws SQLException;
	void processUpdate(String query) throws SQLException;
	
	public HCacheMetadata getCacheMetadata(String key) throws SQLException;
	public HCacheMetadata getOldestCacheEntry() throws SQLException;
	public HCacheMetadata getNewestCacheEntry() throws SQLException;
	public HCacheMetadata getMostFrequentCacheEntry() throws SQLException;
	public HCacheMetadata getLeastFrequentCacheEntry() throws SQLException;
	public HCacheMetadata getRandomCacheEntry() throws SQLException;
	public ResultSet getCacheData(String location) throws SQLException;
	public void putCacheData(String key, ResultSet res) throws SQLException;
	public void deleteCacheData(HCacheMetadata key) throws SQLException;
	public void updateMetadata(HCacheMetadata meta) throws SQLException;
	public int getTotalCacheSize() throws SQLException;
	public int getTotalCacheEntryCount() throws SQLException;



	

}
