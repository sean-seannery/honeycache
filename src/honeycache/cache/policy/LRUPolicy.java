package honeycache.cache.policy;

import honeycache.cache.endpoint.Endpoint;
import honeycache.cache.model.HCacheMetadata;
import honeycache.cache.model.HCacheSQLQuery;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class LRUPolicy implements CachePolicy{

	private Endpoint cacheEndpoint;
	private final static Logger LOGGER = Logger.getLogger(LRUPolicy.class.getName());
	private String contentPolicy;
	
	public LRUPolicy(Endpoint cacheConn, String newContentPolicy){
		cacheEndpoint = cacheConn;
		contentPolicy = newContentPolicy;
	}
	

	@Override
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

	@Override
	public boolean put(HCacheSQLQuery query, ResultSet data) throws SQLException{

		//insert data
		try {

			cacheEndpoint.putCacheData(query, data, contentPolicy);

		} catch (SQLException e) {
			System.out.println("Error Saving Cache Data");
			throw e;	
		}
		
		return false;
	}

	@Override
	public boolean updateCache() throws SQLException{
		
		try{			
			    //delete oldest query
				HCacheMetadata oldest = cacheEndpoint.getOldestCacheEntry();
				cacheEndpoint.deleteCacheData(oldest);	
		} catch (SQLException e){
			System.out.println("Error Updating Cache");
			throw e;
		}
				
		//check to see if we should reset counts
		return true;
	}

}
