package honeycache.cache.policy;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface CachePolicy {
	
	public static final String CACHE_TABLE_CONTENT = "table";
	public static final String CACHE_PARTITION_CONTENT = "partition";
	public static final String CACHE_QUERY_CONTENT = "query";
	public static final String CACHE_NO_CONTENT = "none";
	public static final String EXPIRATION_POLICY_LRU = "lru";
	public static final String EXPIRATION_POLICY_RANDOM = "random";
	
	
	public ResultSet get(String key) throws SQLException;
	//if the data exists in the cache then return it from the cache and update count
	//if the doesnt exist in the cache, then get it and put it in.
	
	public boolean put(String key, ResultSet data) throws SQLException;
	//put data in the cache, inspect expiration, remove something old
	
	public boolean updateCache() throws SQLException;
}
