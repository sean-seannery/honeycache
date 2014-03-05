package honeycache.cache.endpoint;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MysqlEndpoint implements CacheEndpoint {
	
	private static final String DRIVER_NAME = "com.mysql.jdbc.Driver";
	Connection dbConn;
	
	private String host = "localhost";
	private int port = 3306;
	private String dbName = "metastore";
	private String user = "hive";
	private String password = "hive";
	
	public MysqlEndpoint(){

	}
	
	public MysqlEndpoint(String newHost, int newPort, String newDbName, String newUser, String newPassword){
		host = newHost;
		port = newPort;
		dbName = newDbName;
		user = newUser;
		password = newPassword;
	}

	@Override
	public void connect()  {
		try {
            Class.forName(DRIVER_NAME).newInstance();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
		
		try {
			dbConn = DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+dbName+"?user="+user+"&password="+password);
		} catch (SQLException e) {
			System.out.println("Cant connect to hCache endpoint");
			e.printStackTrace();
			System.exit(1);
		}

	}
	
	@Override
	public String getCacheLocation(String key) throws SQLException{
		String retVal = null;
		String query = "SELECT table_name from hcache_tables WHERE key = '" + key + "'";
		PreparedStatement stmt = dbConn.prepareStatement(query);
		ResultSet res = stmt.executeQuery(query);
		if (res.next())
			retVal = res.getString(1);
		return retVal;
	}
	
	@Override
	public ResultSet getCacheData(String key) throws SQLException {
		String location = getCacheLocation( key );
		String query = "SELECT * from '" + location + "'";
		PreparedStatement stmt = dbConn.prepareStatement(query);
		ResultSet results = stmt.executeQuery(query);
		return results;
		
	}
	
	@Override
	public void deleteCacheData(String key) {
		
	}
	
	@Override
	public void putCacheData(ResultSet res) {
		
	}

	@Override
	public String getDriverName() {
		return DRIVER_NAME;
	}
	
	@Override
	public void close()  {

		try {
			dbConn.close();
		} catch (SQLException e) {
			System.out.println("Cant connect to hCache endpoint");
			e.printStackTrace();
			System.exit(1);
		}

	}
	
	
}
