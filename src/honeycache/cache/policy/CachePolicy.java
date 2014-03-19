package honeycache.cache.policy;

import honeycache.cache.endpoint.Endpoint;
import honeycache.cache.model.HCacheMetadata;
import honeycache.cache.model.HCacheSQLQuery;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public abstract class CachePolicy {
	
	public static final String CACHE_TABLE_CONTENT = "table";
	public static final String CACHE_PARTITION_CONTENT = "partition";
	public static final String CACHE_QUERY_CONTENT = "query";
	public static final String CACHE_NO_CONTENT = "none";
	public static final String EXPIRATION_POLICY_LRU = "lru";
	public static final String EXPIRATION_POLICY_RANDOM = "random";
	
	protected Endpoint cacheEndpoint;
	protected final static Logger LOGGER = Logger.getLogger(CachePolicy.class.getName());
	protected String contentPolicy;
	
	
	//if the data exists in the cache then return it from the cache and update count
	//if the doesnt exist in the cache, then get it and put it in.
	public ResultSet get(HCacheSQLQuery query) throws SQLException{
		
		ResultSet data = null;
		//use key to find data location		
		
		try {
			
			HCacheMetadata cacheMeta = cacheEndpoint.getCacheMetadata(query);	
			if (cacheMeta != null){
				//get the data from the location
				data = cacheEndpoint.getCacheData( query , contentPolicy );
				
				cacheEndpoint.updateMetadata(cacheMeta);
			}
		
		} catch (SQLException e) {
			System.out.println("Error Retrieving Cache Data");
			throw e;
		}
		
	
		return data;
	}

	
	//insert data
	public boolean put(HCacheSQLQuery query, ResultSet data) throws SQLException{

		try {

			cacheEndpoint.putCacheData(query, data, contentPolicy);

		} catch (SQLException e) {
			System.out.println("Error Saving Cache Data");
			throw e;	
		}
		
		return false;
	}


	
	public abstract boolean phaseOutCacheItem() throws SQLException;
}
