package honeycache.cache.endpoint;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface CacheEndpoint {
	
	public void connect();
	public void close();
	public String getDriverName();
	public String getCacheLocation(String key) throws SQLException;
	public ResultSet getCacheData(String location) throws SQLException;
	public void deleteCacheData(String key);
	public void insertCacheData(ResultSet res);
	

}
