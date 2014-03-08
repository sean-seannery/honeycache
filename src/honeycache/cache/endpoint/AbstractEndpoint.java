package honeycache.cache.endpoint;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class AbstractEndpoint implements Endpoint{

	protected Connection dbConn;
	protected String driverName;
	protected String connectionString;
	protected String user;
	protected String password;
	protected String host;
	protected int port;
	protected String dbName;
	
	public AbstractEndpoint(){
		dbConn = null;
		user = null;
		password = null;
		host = null;
		dbName = null;
		connectionString = null;
		driverName = null;
	}
	
	public AbstractEndpoint (String newHost, int newPort, String newUser, String newPassword, String newDB){
		host = newHost;
		port = newPort;
		user = newUser;
		password = newPassword;
		dbName = newDB;
		connectionString = null;
		driverName = null;
	}
	
	@Override
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

	@Override
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

	@Override
	public ResultSet processQuery( String query ) throws SQLException{
		query = query.trim();
		if (query.endsWith(";"))
			query = query.substring( 0, query.length()-1); 

		Statement stmt = dbConn.createStatement();
		ResultSet res = stmt.executeQuery(query);

		return res;
	}
	
	@Override
	public void processUpdate( String query ) throws SQLException{
		query = query.trim();
		if (query.endsWith(";"))
			query = query.substring( 0, query.length()-1); 

		Statement stmt = dbConn.createStatement();
		stmt.executeUpdate(query);

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


}
