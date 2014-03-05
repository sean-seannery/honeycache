package honeycache.cache.endpoint;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HiveEndpoint implements CacheEndpoint{
	
	private static final String HIVE_DRIVER_NAME = "org.apache.hadoop.hive.jdbc.HiveDriver";
	Connection hiveConnection;
	
	private String host = "localhost";
	private int port = 10000;
	private String user = "";
	private String password = "";
	
	public HiveEndpoint(){
	}
	
	public HiveEndpoint(String newHost, int newPort, String newUser, String newPassword){
		host = newHost;
		port = newPort;
		user = newUser;
		password = newPassword;
	}
	
	@Override
	public void connect() throws SQLException{	
		if (hiveConnection != null){
			close();
		}
		
		try {
			Class.forName(HIVE_DRIVER_NAME);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		hiveConnection = DriverManager.getConnection(this.toString(), user, password);

	}

	@Override
	public void close() throws SQLException{		
		try {
			hiveConnection.close();
		} catch (SQLException e) {
		}
		hiveConnection = null;
	}

	@Override
	public ResultSet processQuery( String query) throws SQLException{
		query = query.replace(";", "");

		PreparedStatement stmt = hiveConnection.prepareStatement(query);
		ResultSet res = stmt.executeQuery(query);

		return res;
	}
	
	@Override
	public String getDriverName() {
		return HIVE_DRIVER_NAME;
	}

	@Override
	public String getCacheLocation(String key) throws SQLException {
		//TODO
		return null;
	}

	@Override
	public ResultSet getCacheData(String location) throws SQLException {
		//TODO
		return null;
	}

	@Override
	public void deleteCacheData(String key) {
		//TODO
	}

	@Override
	public void putCacheData(ResultSet res) {
		//TODO
	}
	
	public String toString() {
		return  "jdbc:hive://" + host +":" + port + "/default";
	}


}
