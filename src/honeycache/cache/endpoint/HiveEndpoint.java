package honeycache.cache.endpoint;

import honeycache.cache.model.HCacheMetadata;
import honeycache.cache.model.HCacheSQLQuery;
import honeycache.cache.policy.CacheCommander;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HiveEndpoint extends Endpoint{
	
	public static final String DRIVER_NAME = "org.apache.hadoop.hive.jdbc.HiveDriver"; 
	private static final String DATA_TABLE_PREFIX = "t_";
	private MysqlEndpoint metadataConn;

	//default constructor calls argument constructor
	public HiveEndpoint(){
		this("localhost",10000, "", "");
	}
	
	public HiveEndpoint(String newHost, int newPort, String newUser, String newPassword){
		//super(newHost, newPort, newUser, newPassword, "");
		host = newHost;
		port = newPort;
		user = newUser;
		password = newPassword;
		connectionString = "jdbc:hive://" + host +":" + port + "/default";
		driverName = DRIVER_NAME;
		metadataConn = new MysqlEndpoint(CacheCommander.HCACHE_PROPS.getMetadataHost(), 
										 CacheCommander.HCACHE_PROPS.getMetadataPort(), 
										 CacheCommander.HCACHE_PROPS.getMetadataUser(), 
										 CacheCommander.HCACHE_PROPS.getMetadataPassword());
	}
	
	

	public String toString() {
		return  getConnectionString();
	}

	@Override
	public HCacheMetadata getCacheMetadata(HCacheSQLQuery query) throws SQLException {
		metadataConn.connect();
		HCacheMetadata retVal = metadataConn.getCacheMetadata(query);
		return retVal;
	}

	@Override
	public HCacheMetadata getOldestCacheEntry() throws SQLException {
		metadataConn.connect();
		HCacheMetadata retVal = metadataConn.getOldestCacheEntry();
		return retVal;
	}

	@Override
	public HCacheMetadata getNewestCacheEntry() throws SQLException {
		metadataConn.connect();
		HCacheMetadata retVal = metadataConn.getNewestCacheEntry();
		return retVal;
	}

	@Override
	public HCacheMetadata getMostFrequentCacheEntry() throws SQLException {
		metadataConn.connect();
		HCacheMetadata retVal = metadataConn.getMostFrequentCacheEntry();
		return retVal;
	}

	@Override
	public HCacheMetadata getLeastFrequentCacheEntry() throws SQLException {
		metadataConn.connect();
		HCacheMetadata retVal = metadataConn.getLeastFrequentCacheEntry();
		return retVal;
	}

	@Override
	public HCacheMetadata getRandomCacheEntry() throws SQLException {
		metadataConn.connect();
		HCacheMetadata retVal = metadataConn.getRandomCacheEntry();
		return retVal;
	}

	@Override
	public ResultSet getCacheData(String key) throws SQLException {
		ResultSet results = processQuery("SELECT * from `" + DATA_TABLE_PREFIX + key + "`");
		return results;
	}

	@Override
	public void putCacheData(HCacheSQLQuery query, ResultSet res) throws SQLException {
		String new_data_table_name = DATA_TABLE_PREFIX + query.getUniqueKey();
		
		//construct the SQL statements
		String createAndInsertStatement = "CREATE TABLE " + new_data_table_name +" AS " + query.getQueryString();
		
		//create the table
		processUpdate(createAndInsertStatement);
		
		//get the table_size
		int size = 9999;
		//TODO: Get the real table size using hdfs
		
		
		HCacheMetadata meta = new HCacheMetadata(query.getUniqueKey(), new_data_table_name, new java.sql.Date( new java.util.Date().getTime() ), 1, size);
		updateMetadata(meta);
		
		
	}

	@Override
	public void deleteCacheData(HCacheMetadata key) throws SQLException {
		metadataConn.connect();
		metadataConn.processUpdate("DELETE FROM hcache_key_data WHERE key_id = '"+ key.getKey() + "'");
				
		processUpdate("DROP TABLE "+ key.getCacheTableName());
	}

	@Override
	public void updateMetadata(HCacheMetadata meta) throws SQLException {
		metadataConn.connect();
		metadataConn.updateMetadata(meta);
	}

	@Override
	public int getTotalCacheSize() throws SQLException {
		metadataConn.connect();
		return metadataConn.getTotalCacheSize();
	}

	@Override
	public int getTotalCacheEntryCount() throws SQLException {
		metadataConn.connect();
		return metadataConn.getTotalCacheEntryCount();
	}





}
