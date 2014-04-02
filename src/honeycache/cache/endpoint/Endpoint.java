package honeycache.cache.endpoint;

import honeycache.cache.model.HCacheMetadata;
import honeycache.cache.model.HCacheSQLQuery;
import honeycache.cache.server.CacheCommander;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

public abstract class Endpoint {
	
	public static final String MYSQL_ENDPOINT = "mysql";
	public static final String HIVE_ENDPOINT = "hive";
	public static final String HBASE_ENDPOINT = "cassandra";
	
	private final static Logger LOGGER = Logger.getLogger(Endpoint.class.getName());

	protected Connection dbConn;
	protected String driverName;
	protected String connectionString;
	protected String user;
	protected String password;
	protected String host;
	protected int port;
	protected String dbName;
	protected Connection metadataConn;

	
	public Endpoint(){
		this(null, -1, null, null, null);
		connectionString = null;
		driverName = null;
		
	}
	
	public Endpoint (String newHost, int newPort, String newUser, String newPassword, String newDB){
		host = newHost;
		port = newPort;
		user = newUser;
		password = newPassword;
		dbName = newDB;
		connectionString = null;
		driverName = null;
		metadataConn = null;
	}
	
	
	//These methods must be implemented in subchildren
	public abstract ResultSet getCacheData(HCacheSQLQuery query, String contentPolicy) throws SQLException;
	public abstract void putCacheData(HCacheSQLQuery query, ResultSet res, String contentPolicy) throws SQLException;
	public abstract void deleteCacheData(HCacheMetadata key) throws SQLException;

	
	public void connect() throws SQLException{	
		
		if (isClosed()){
		
			try {
				Class.forName(driverName);
				Class.forName(MysqlEndpoint.DRIVER_NAME); //for metadata connection
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				System.exit(1);
			}
			dbConn = DriverManager.getConnection(getConnectionString(), user, password); //get conn for main database
			String metaConnString = "jdbc:mysql://" + CacheCommander.HCACHE_PROPS.getMetadataHost() + ":"
									+ CacheCommander.HCACHE_PROPS.getMetadataPort()		+ "/hcache_store";
			metadataConn = DriverManager.getConnection(metaConnString, 
														CacheCommander.HCACHE_PROPS.getMetadataUser(),
														CacheCommander.HCACHE_PROPS.getMetadataPassword()); //get connection for metadata database
		}

	}

	public void close(){	
		if (!isClosed()) {
			try {
				dbConn.close();
				dbConn = null;
				metadataConn.close();
				metadataConn = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
	}

	public ResultSet processQuery( String query ) throws SQLException{
		
		query = query.trim();
		if (query.endsWith(";"))
			query = query.substring( 0, query.length()-1); 
		
		LOGGER.trace("QUERY: " + query);

		Statement stmt = dbConn.createStatement();
		ResultSet res = stmt.executeQuery(query);

		return res;
	}
	
	public void processUpdate( String query ) throws SQLException{
		query = query.trim();
		if (query.endsWith(";"))
			query = query.substring( 0, query.length()-1); 

		LOGGER.trace("QUERY: " + query);
		Statement stmt = dbConn.createStatement();
		stmt.execute(query);

	}
	

	
	public String getDriverName() {
		return driverName;
	}

	public String getConnectionString() {
		return connectionString;
	}

	public void setConnectionString(String newConnString) {
		connectionString = newConnString;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String newUser) {
		user = newUser;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String newPassword) {
		password = newPassword;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String newHost) {
		host = newHost;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int newPort) {
		port = newPort;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String newDbName) {
		dbName = newDbName;
	}
	
	public Connection getDbConn() {
		return dbConn;
	}
	
	public boolean isClosed() {
		try {
			return dbConn == null || dbConn.isClosed();
		} catch (SQLException e) {
			e.printStackTrace();
			return true;
		}
	}
	

	public HCacheMetadata getCacheMetadata(HCacheSQLQuery query) throws SQLException {
		connect();
		HCacheMetadata retVal = null;
		String select_query = "SELECT key_id, table_name, date_accessed, frequency_accessed, size, orig_table, part_data from hcache_key_data WHERE key_id = \"" + query.getUniqueKey() + "\"";
		ResultSet res = processMetadataQuery(select_query);
		if (res.next())
			retVal = new HCacheMetadata(res.getString("key_id"), res.getString("table_name"), res.getDate("date_accessed"), 
										res.getInt("frequency_accessed"), res.getInt("size"), res.getString("orig_table"), res.getString("part_data"));
		return retVal;
	}


	public HCacheMetadata getOldestCacheEntry() throws SQLException {
		connect();
		HCacheMetadata retVal = null;
		String select_query = "SELECT key_id, table_name, date_accessed, frequency_accessed, size, orig_table, part_data from hcache_key_data " + 
								"WHERE date_accessed = (select MIN(date_accessed) from hcache_key_data)";
		ResultSet res = processMetadataQuery(select_query);
		if (res.next())
			retVal = new HCacheMetadata(res.getString("key_id"), res.getString("table_name"), res.getDate("date_accessed"), 
										res.getInt("frequency_accessed"), res.getInt("size"), res.getString("orig_table"), res.getString("part_data"));
		return retVal;
	}


	public HCacheMetadata getNewestCacheEntry() throws SQLException {
		connect();
		HCacheMetadata retVal = null;
		String select_query = "SELECT key_id, table_name, date_accessed, frequency_accessed, size, orig_table, part_data from hcache_key_data " + 
								"WHERE date_accessed = (select MAX(date_accessed) from hcache_key_data)";
		ResultSet res = processMetadataQuery(select_query);
		if (res.next())
			retVal = new HCacheMetadata(res.getString("key_id"), res.getString("table_name"), res.getDate("date_accessed"), 
										res.getInt("frequency_accessed"), res.getInt("size"), res.getString("orig_table"), res.getString("part_data"));
		return retVal;
	}


	public HCacheMetadata getMostFrequentCacheEntry() throws SQLException {
		connect();
		HCacheMetadata retVal = null;
		String select_query = "SELECT key_id, table_name, date_accessed, frequency_accessed, size, orig_table, part_data from hcache_key_data " + 
								"WHERE frequency_accessed = (select MAX(frequency_accessed) from hcache_key_data)";
		ResultSet res = processMetadataQuery(select_query);
		if (res.next())
			retVal = new HCacheMetadata(res.getString("key_id"), res.getString("table_name"), res.getDate("date_accessed"), 
										res.getInt("frequency_accessed"), res.getInt("size"), res.getString("orig_table"), res.getString("part_data"));
		return retVal;
	}


	public HCacheMetadata getLeastFrequentCacheEntry() throws SQLException {
		connect();
		HCacheMetadata retVal = null;
		String select_query = "SELECT key_id, table_name, date_accessed, frequency_accessed, size, orig_table, part_data from hcache_key_data " + 
								"WHERE frequency_accessed = (select MIN(frequency_accessed) from hcache_key_data)";
		ResultSet res = processMetadataQuery(select_query);
		if (res.next())
			retVal = new HCacheMetadata(res.getString("key_id"), res.getString("table_name"), res.getDate("date_accessed"), 
										res.getInt("frequency_accessed"), res.getInt("size"), res.getString("orig_table"), res.getString("part_data"));
		return retVal;
	}


	public HCacheMetadata getRandomCacheEntry() throws SQLException {
		connect();
		HCacheMetadata retVal = null;
		String select_query = "SELECT key_id, table_name, date_accessed, frequency_accessed, size, orig_table, part_data FROM hcache_key_data " + 
								"ORDER BY RAND() LIMIT 1"; 
		ResultSet res = processMetadataQuery(select_query);
		if (res.next())
			retVal = new HCacheMetadata(res.getString("key_id"), res.getString("table_name"), res.getDate("date_accessed"), 
										res.getInt("frequency_accessed"), res.getInt("size"), res.getString("orig_table"), res.getString("part_data"));
		return retVal;
	}
	

	public void updateMetadata(HCacheMetadata meta) throws SQLException {
		connect();
		
		String insertMetadata = "INSERT INTO hcache_key_data (key_id, table_name, date_accessed, frequency_accessed, size, orig_table, part_data) " +
								" VALUES (?, ?, NOW(), ?, ?, ?, ?) " + 
								"ON DUPLICATE KEY UPDATE date_accessed=NOW(), frequency_accessed=frequency_accessed + 1";
		PreparedStatement prepStmt = metadataConn.prepareStatement(insertMetadata);			
		prepStmt.setString(1, meta.getKey());
		prepStmt.setString(2, meta.getCacheTableName());
		//prepStmt.setDate(3, meta.getDateAccessed());
		prepStmt.setInt(3, meta.getFrequencyAccessed());
		prepStmt.setInt(4, meta.getSize());
		prepStmt.setString(5, meta.getOriginalTable());
		prepStmt.setString(6, meta.getPartitionData());
		prepStmt.executeUpdate();
		
	}
	
	public void deleteMetadataTableEntry(HCacheMetadata meta) throws SQLException{
		connect();
		Statement deleteme = metadataConn.createStatement();
		deleteme.execute("DELETE FROM hcache_key_data WHERE key_id = \""+ meta.getKey() + "\"");
	}


	public int getTotalCacheSize() throws SQLException {
		connect();
		int size = 0;
		ResultSet cacheSize = processMetadataQuery("SELECT SUM(size) FROM hcache_key_data");
		if (cacheSize.next()){
			size = cacheSize.getInt(1);
		}
		return size;
	}


	public int getTotalCacheEntryCount() throws SQLException {
		connect();
		int size = 0;
		ResultSet cacheSize = processMetadataQuery("SELECT COUNT(key_id) FROM hcache_key_data");
		if (cacheSize.next()){
			size = cacheSize.getInt(1);
		}
		return size;
	}



	public void destroyTheCache() throws SQLException{
		connect();
		ResultSet cachedTables = processMetadataQuery("SELECT key_id, table_name FROM hcache_key_data");
		while (cachedTables.next()){
			 
			deleteCacheData( new HCacheMetadata(cachedTables.getString(1), cachedTables.getString(2),null,0, 0,null, null) );
			
		}
	}
	
	public void destroyCacheForTable(String table) throws SQLException{
		connect();
		ResultSet cachedTables = processMetadataQuery("SELECT key_id, table_name FROM hcache_key_data WHERE orig_table = '"+table+"'");
		while (cachedTables.next()){
			 
			deleteCacheData( new HCacheMetadata(cachedTables.getString(1), cachedTables.getString(2),null,0, 0,null, null) );
			
		}
	}

	
	private ResultSet processMetadataQuery( String query ) throws SQLException{
		
		query = query.trim();
		if (query.endsWith(";"))
			query = query.substring( 0, query.length()-1); 
		
		LOGGER.trace("QUERY: " + query);

		Statement stmt = metadataConn.createStatement();
		ResultSet res = stmt.executeQuery(query);

		return res;
	}
	
	public String toString() {
		return  getConnectionString();
	}

}
