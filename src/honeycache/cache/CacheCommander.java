package honeycache.cache;

import honeycache.cache.endpoint.CacheEndpoint;
import honeycache.cache.endpoint.MysqlEndpoint;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CacheCommander {
	
	public static final String CACHE_CONTENT_POLICY = "query";  //query or partition
	public static final String CACHE_ENDPOINT = "mysql";
    private static final boolean CACHING_ENABLED = true;
	
	private Connection hiveConnection = null;
	private CacheEndpoint cacheConn;

	public CacheCommander(Connection newHiveConn) {
		hiveConnection = newHiveConn;
		if (CACHE_ENDPOINT.equals("mysql"))
		{
			cacheConn = new MysqlEndpoint(null, 3306, null, null, null);
			cacheConn.connect();
		}
	}
	
	public ResultSet processQuery(String query) throws SQLException{
		ResultSet res = null;
		// check to see if query or table exists in cache
		String cacheKey = getCacheKeyFromQueryString(query);
		
		if (cacheKey != null && CACHING_ENABLED){
			
			//if yes, query the cache and return it
			cacheConn.connect();
			res = cacheConn.getCacheData(cacheKey);
			//determine what, if anything gets removed

		}
		else {
			
			//if no, run the query and add it		
			PreparedStatement stmt = hiveConnection.prepareStatement(query);
			res = stmt.executeQuery(query);
			
			//determine what, if anything gets removed
			
			
		}

		
		return res;
	}
	
	private String getCacheKeyFromQueryString( String query){
		//only want to cache select queries
		String key = null;
		
		if (query.contains("SELECT") && query.contains("FROM") ){
			//MD5of Hash
			if (CACHE_CONTENT_POLICY.equals("query")){ 
				byte md5Hash[];
				try {
					md5Hash = MessageDigest.getInstance("MD5").digest( query.getBytes("UTF-8") );
					
					StringBuffer sb = new StringBuffer();
					 for(byte b : md5Hash){
			               sb.append(String.format("%02x", b&0xff));
			          }

					key = sb.toString();
				} catch (Exception  e) {
					System.out.println("Error getting query key.  Problem with Hashing");
					e.printStackTrace();
					System.exit(1);
				} 
			}
			if (CACHE_CONTENT_POLICY.equals("table")){
				int start_tbl_idx = query.indexOf("FROM") + 4;
				while (query.charAt(start_tbl_idx) == ' '){
					start_tbl_idx++;
				}
				key = query.substring(start_tbl_idx, query.indexOf(" ", start_tbl_idx));
			}
		}
	
		return key;
	};
	


}
