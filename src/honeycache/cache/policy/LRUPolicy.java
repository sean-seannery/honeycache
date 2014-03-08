package honeycache.cache.policy;

import honeycache.cache.endpoint.Endpoint;
import honeycache.cache.model.HCacheMetadata;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LRUPolicy implements CachePolicy{

	private Endpoint cacheEndpoint;
	public static final int MAX_CACHE_SIZE_IN_KB = 1024 * 1024 * 2;
	public static final int MAX_CACHE_ENTRIES = 4;
	
	public LRUPolicy(Endpoint cacheConn){
		cacheEndpoint = cacheConn;
	}
	

	@Override
	public ResultSet get(String key) throws SQLException{
		
		ResultSet data = null;
		//use key to find data location		
		
		try {
			HCacheMetadata cacheMeta = cacheEndpoint.getCacheMetadata(key);	
			if (cacheMeta != null){
				//get the data from the location
				data = cacheEndpoint.getCacheData( cacheMeta.getKey() );
				cacheEndpoint.updateMetadata(cacheMeta);
			}
		
		} catch (SQLException e) {
			System.out.println("Error Saving Cache Data");
			throw e;
		}
		
	
		return data;
	}

	@Override
	public boolean put(String key, ResultSet data) throws SQLException{

		//insert data
		try {
			//HCacheMetadata cacheMeta = new HCacheMetadata(key, "t_" + key, new java.sql.Date(new java.util.Date().getTime()), 0, 0);
			cacheEndpoint.putCacheData(key, data);
			//cacheEndpoint.updateMetadata(cacheMeta);
			updateCache(); 
		} catch (SQLException e) {
			System.out.println("Error Saving Cache Data");
			throw e;
			
		}
		
		return false;
	}

	@Override
	public boolean updateCache() throws SQLException{
		
		try{
			//if our cache is full, we need to delete something;
			if ( cacheEndpoint.getTotalCacheSize() > MAX_CACHE_SIZE_IN_KB  || cacheEndpoint.getTotalCacheEntryCount() > MAX_CACHE_ENTRIES ){
				
			//delete oldest query
				HCacheMetadata oldest = cacheEndpoint.getOldestCacheEntry();
				cacheEndpoint.deleteCacheData(oldest);	
				
			}
		} catch (SQLException e){
			System.out.println("Error Updating Cache");
			throw e;
		}
				
		//check to see if we should reset counts
		return true;
	}

}
