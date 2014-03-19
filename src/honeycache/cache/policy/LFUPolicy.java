package honeycache.cache.policy;

import honeycache.cache.endpoint.Endpoint;
import honeycache.cache.model.HCacheMetadata;
import honeycache.cache.model.HCacheSQLQuery;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class LFUPolicy extends CachePolicy{

	
	public LFUPolicy(Endpoint cacheConn, String newContentPolicy){
		cacheEndpoint = cacheConn;
		contentPolicy = newContentPolicy;
	}
	

	@Override
	public boolean phaseOutCacheItem() throws SQLException{
		
		try{			
			    //delete oldest query
				HCacheMetadata oldest = cacheEndpoint.getLeastFrequentCacheEntry();
				cacheEndpoint.deleteCacheData(oldest);	
		} catch (SQLException e){
			System.out.println("Error Updating Cache");
			throw e;
		}
				
		//check to see if we should reset counts
		return true;
	}

}
