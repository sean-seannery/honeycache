package honeycache.cache.endpoint;

import honeycache.cache.model.HCacheMetadata;
import honeycache.cache.model.HCacheSQLQuery;
import honeycache.cache.policy.CachePolicy;
import honeycache.cache.server.CacheCommander;

import java.sql.Date;
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
	public ResultSet getCacheData(HCacheSQLQuery query, String contentPolicy) throws SQLException {
		
		if (contentPolicy.equals(CachePolicy.CACHE_QUERY_CONTENT)){
			ResultSet results = processQuery("SELECT * from `" + query.generateTableName(contentPolicy) + "`");
			return results;
		} else {
			String newQuery = query.replaceTable(query.generateTableName(contentPolicy));
			System.out.println("SQL:" + newQuery + "tbl:" + query.generateTableName(contentPolicy));
			ResultSet results = processQuery(newQuery);
			return results;

		}
	}

	@Override
	public void putCacheData(HCacheSQLQuery query, ResultSet res, String contentPolicy) throws SQLException {
		String new_data_table_name = query.generateTableName(contentPolicy);
		
		//construct the SQL statements
		String createAndInsertStatement = "";
		if (contentPolicy.equals(CachePolicy.CACHE_QUERY_CONTENT)) {
			createAndInsertStatement = "CREATE TABLE " + new_data_table_name +" AS " + query.getQueryString();
		} else {
			createAndInsertStatement = "CREATE TABLE " + new_data_table_name +" AS SELECT * FROM " + query.parseTable();
			String partitions = query.parsePartitions();
			if (!partitions.isEmpty()){
				createAndInsertStatement += " WHERE " + query.parsePartitions().replace("|", " AND ");
			}
		}
		System.out.println("SQL: " + createAndInsertStatement);
		//create the table
		processUpdate(createAndInsertStatement);
		
		//get the table_size
		int size = 9999;
		//TODO: Get the real table size using hdfs
		
		
		HCacheMetadata meta = new HCacheMetadata(query.getUniqueKey(), new_data_table_name, new java.sql.Date( new java.util.Date().getTime() ), 1, size
												, query.parseTable(), query.parsePartitions());
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



	public void destroyTheCache() throws SQLException{
		metadataConn.connect();
		ResultSet cachedTables = metadataConn.processQuery("SELECT key_id, table_name FROM hcache_key_data");
		while (cachedTables.next()){
			 
			deleteCacheData( new HCacheMetadata(cachedTables.getString(1), cachedTables.getString(2),null,0, 0,null, null) );
			
		}
	}



}
