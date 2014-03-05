package honeycache.cache.policy;

import honeycache.cache.endpoint.CacheEndpoint;
import honeycache.cache.endpoint.HiveEndpoint;
import honeycache.cache.endpoint.MysqlEndpoint;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CacheCommander {
	
	public static final String CACHE_CONTENT_POLICY = "query";  //query or partition or table
	public static final String CACHE_ENDPOINT = "mysql"; //mysql, hbase, hive
	
    private static final boolean CACHING_ENABLED = true;
	
	private HiveEndpoint hiveConnection = null;
	private CacheEndpoint cacheConn;
	private CachePolicy policy;

	public CacheCommander(HiveEndpoint newHiveConn) {
		hiveConnection = newHiveConn;
		if (CACHE_ENDPOINT.equals(CacheEndpoint.MYSQL_ENDPOINT))
		{
			cacheConn = new MysqlEndpoint();
			cacheConn.connect();
		}
		//TODO: add more endpoints
		if (CACHE_CONTENT_POLICY.equals("query"))
		{
			policy = new LRUPolicy(cacheConn);
		}
	}
	
	public ResultSet processQuery(String query) throws SQLException{
		
		ResultSet res = null;
		//if it is a select statement we need to send it to the caching algorithm
		if (query.contains("SELECT") && query.contains("FROM") ){
			
			String cacheKey = generateKey(query);		
			res = policy.get(cacheKey);
					
			// if the key doesnt exist in the cache
			if (res == null){

				res = hiveConnection.processQuery(query); //get the data
				//TODO: need to handle what table vs query here
				policy.put(cacheKey, res);                //put it in the cache
				policy.updateCache();                     //update the cache metadata / delete old records

			}
			else {
	        //if they key does exist in the cache
				policy.updateCache();                     //update the cache metadata / delete old records
			}
			
		} else {
			//we dont want to process updates, deletes, or metadata queries so just connect as normal
			res = hiveConnection.processQuery(query);
		}

		return res;
	}
	

	private String generateKey(String query) {
		String key = null;
		
		if (CACHE_CONTENT_POLICY.equals("query")){ 
			//MD5of Hash
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
		return key;
	}

	public HiveEndpoint getHiveConnection() {
		return hiveConnection;
	}
	
	public void connect() throws SQLException{	
		if (hiveConnection != null){
			disconnect();
		}
		
		hiveConnection.connect();
	}
	
	public void disconnect() {		
		try {
			hiveConnection.close();
		} catch (SQLException e) {
		}
		hiveConnection = null;
	}
	
	


}
