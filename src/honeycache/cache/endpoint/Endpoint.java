package honeycache.cache.endpoint;

import honeycache.cache.model.HCacheMetadata;
import honeycache.cache.model.HCacheSQLQuery;
import honeycache.cache.server.CacheCommander;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

public abstract class Endpoint {
	
	public static final String MYSQL_ENDPOINT = "mysql";
	public static final String HBASE_ENDPOINT = "hbase";
	public static final String HIVE_ENDPOINT = "hive";
	private final static Logger LOGGER = Logger.getLogger(Endpoint.class.getName());

	protected Connection dbConn;
	protected String driverName;
	protected String connectionString;
	protected String user;
	protected String password;
	protected String host;
	protected int port;
	protected String dbName;
	
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
	}
	
	public void connect() throws SQLException{	
		
		if (isClosed()){
		
			try {
				Class.forName(driverName);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				System.exit(1);
			}
			dbConn = DriverManager.getConnection(getConnectionString(), user, password);
		}

	}

	public void close(){	
		if (!isClosed()) {
			try {
				dbConn.close();
				dbConn = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
	}

	public ResultSet processQuery( String query ) throws SQLException{
		
		query = query.trim();
		if (query.endsWith(";"))
			query = query.substring( 0, query.length()-1); 
		
		LOGGER.info("QUERY: " + query);

		Statement stmt = dbConn.createStatement();
		ResultSet res = stmt.executeQuery(query);

		return res;
	}
	
	public void processUpdate( String query ) throws SQLException{
		query = query.trim();
		if (query.endsWith(";"))
			query = query.substring( 0, query.length()-1); 

		LOGGER.info("QUERY: " + query);
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
	
	
	public abstract HCacheMetadata getCacheMetadata(HCacheSQLQuery query) throws SQLException;
	public abstract HCacheMetadata getOldestCacheEntry() throws SQLException;
	public abstract HCacheMetadata getNewestCacheEntry() throws SQLException;
	public abstract HCacheMetadata getMostFrequentCacheEntry() throws SQLException;
	public abstract HCacheMetadata getLeastFrequentCacheEntry() throws SQLException;
	public abstract HCacheMetadata getRandomCacheEntry() throws SQLException;
	public abstract ResultSet getCacheData(HCacheSQLQuery query, String contentPolicy) throws SQLException;
	public abstract void putCacheData(HCacheSQLQuery query, ResultSet res, String contentPolicy) throws SQLException;
	public abstract void deleteCacheData(HCacheMetadata key) throws SQLException;
	public abstract void updateMetadata(HCacheMetadata meta) throws SQLException;
	public abstract int getTotalCacheSize() throws SQLException;
	public abstract int getTotalCacheEntryCount() throws SQLException;
	public abstract void destroyTheCache() throws SQLException;


}
