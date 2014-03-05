package honeycache.cache.policy;

import java.sql.ResultSet;

public interface CachePolicy {
	
	public static final String CACHE_TABLE = "table";
	public static final String CACHE_PARTITION = "partition";
	public static final String CACHE_QUERY = "query";
	
	
	public ResultSet processQuery();
	
	public ResultSet get(String key);
	//if the data exists in the cache then return it from the cache and update count
	//if the doesnt exist in the cache, then get it and put it in.
	
	public boolean put(String key, ResultSet data);
	//put data in the cache, inspect expiration, remove something old
	
	public boolean updateCache();
}
