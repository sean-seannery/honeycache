package honeycache.cache.endpoint;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface CacheEndpoint {
	
	public static final String MYSQL_ENDPOINT = "mysql";
	public static final String HBASE_ENDPOINT = "hbase";
	public static final String HIVE_ENDPOINT = "hive";
	
	public void connect() throws SQLException;
	public void close() throws SQLException;
	public String getDriverName();
	public ResultSet processQuery(String query) throws SQLException;
	
	
	public String getCacheLocation(String key) throws SQLException;
	public ResultSet getCacheData(String location) throws SQLException;
	public void putCacheData(ResultSet res);
	public void deleteCacheData(String key);

	

}
