package honeycache.cache.policy;

import honeycache.cache.endpoint.Endpoint;
import honeycache.cache.endpoint.HiveEndpoint;
import honeycache.cache.endpoint.MysqlEndpoint;
import honeycache.cache.model.HCacheProperties;

import java.security.MessageDigest;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;




public class CacheCommander {
	
	//TODO: These should get read in from a properties file

	public static final HCacheProperties HCACHE_PROPS = new HCacheProperties("config.properties");
	private final static Logger LOGGER = Logger.getLogger(CacheCommander.class.getName());
	
	private HiveEndpoint hiveConnection = null;
	private Endpoint cacheConn;
	private CachePolicy policy;

	public CacheCommander(HiveEndpoint newHiveConn) {

		hiveConnection = newHiveConn;
		
		if (HCACHE_PROPS.getContentPolicy().equalsIgnoreCase(CachePolicy.CACHE_NO_CONTENT)){
			policy = new NoCachePolicy();		
		} else {
			//TODO: add more endpoints
			if (HCACHE_PROPS.getCacheEndpoint().equalsIgnoreCase(Endpoint.MYSQL_ENDPOINT))
			{
				cacheConn = new MysqlEndpoint();
				try {
					cacheConn.connect();
					LOGGER.info("Connected to Cache Datastore" + cacheConn.getConnectionString());
				} catch (SQLException e) {
					LOGGER.error("Unable to connect to Cache Datastore" + cacheConn.getConnectionString(), e);
					cacheConn.close();
					e.printStackTrace();
					System.exit(1);
					
				}
			}
					
			if (HCACHE_PROPS.getExpirationPolicy().equalsIgnoreCase(CachePolicy.EXPIRATION_POLICY_LRU))
			{
				policy = new LRUPolicy(cacheConn);
			}

		}

	}
	
	public ResultSet processQuery(String query) throws SQLException{
		
		ResultSet res = null;
		//if it is a select statement we need to send it to the caching algorithm
		if (query.toUpperCase().contains("SELECT") && query.toUpperCase().contains("FROM") ){
			
			String cacheKey = generateKey(query);
			LOGGER.trace("KEY=" + cacheKey + "TYPE=OVERHEAD CACHE=GET TIME=" + System.currentTimeMillis() );
			res = policy.get(cacheKey);
			
					
			// if the key doesnt exist in the cache
			if (res == null){
				LOGGER.trace("KEY=" + cacheKey + "TYPE=OVERHEAD CACHE=MISS TIME=" + System.currentTimeMillis());
				boolean valid_query = true;
				try{
					LOGGER.trace("KEY=" + cacheKey + "TYPE=NOT_OVERHEAD START_QUERY_HIVE TIME=" + System.currentTimeMillis());
					res = hiveConnection.processQuery(query); //get the data
					LOGGER.trace("KEY=" + cacheKey + "TYPE=NOT_OVERHEAD END_QUERY_HIVE TIME=" + System.currentTimeMillis());
				} catch (SQLException e){
					LOGGER.warn("Invalid Query Results.  Not storing in cache" + query, e);
					valid_query = false;
					throw e;
				}
				
				//if the query successfully executes, put it in the cache
				if (valid_query){
					//TODO: need to handle what table vs query here	 
					LOGGER.trace("KEY=" + cacheKey + "TYPE=OVERHEAD CACHE=PUT START_TIME=" + System.currentTimeMillis() );
					policy.put(cacheKey, res);
					LOGGER.trace("KEY=" + cacheKey + "TYPE=OVERHEAD CACHE=PUT END_TIME=" + System.currentTimeMillis() );
					//TODO: Think of a better way to do this.  Currently caching the data results in traversing to the end of the resultset
					// and hive doesnt support res.beforeFirst()
					res = cacheConn.getCacheData(cacheKey);
				}

			}		
			
		} else {
			//we dont want to process updates, deletes, or metadata queries so just connect as normal
			res = hiveConnection.processQuery(query);
		}

		return res;
	}
	

	private String generateKey(String query) {
		String key = null;
		
		if (HCACHE_PROPS.getContentPolicy().equalsIgnoreCase("query")){ 
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
		if (HCACHE_PROPS.getContentPolicy().equalsIgnoreCase("table")){
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
		if (hiveConnection == null || hiveConnection.isClosed()){
			LOGGER.info("Connected to Hive" + hiveConnection.getConnectionString());
			hiveConnection.connect();
		}

	}
	
	public void disconnect() {	
		if (hiveConnection != null && !hiveConnection.isClosed()){
			hiveConnection.close();
			hiveConnection = null;
		}

		
	}
	
	


}
