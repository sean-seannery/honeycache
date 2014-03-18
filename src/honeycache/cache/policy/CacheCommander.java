package honeycache.cache.policy;

import honeycache.cache.endpoint.Endpoint;
import honeycache.cache.endpoint.EndpointFactory;
import honeycache.cache.endpoint.HiveEndpoint;
import honeycache.cache.endpoint.MysqlEndpoint;
import honeycache.cache.model.HCacheProperties;
import honeycache.cache.model.HCacheSQLQuery;

import java.security.MessageDigest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

import org.apache.log4j.Logger;

public class CacheCommander {
	
	public static final HCacheProperties HCACHE_PROPS = new HCacheProperties("config.properties");
	private final static Logger LOGGER = Logger.getLogger(CacheCommander.class.getName());
	
	private HiveEndpoint hiveConnection = null;
	private Endpoint cacheConn;
	private CachePolicy policy;

	public CacheCommander(HiveEndpoint newHiveConn) {

		hiveConnection = newHiveConn;
		
		if (HCACHE_PROPS.getContentPolicy().equalsIgnoreCase(CachePolicy.CACHE_NO_CONTENT)){
			cacheConn = null;
			policy = new NoCachePolicy();		
		} else {
		
			try {
				cacheConn = new EndpointFactory(HCACHE_PROPS.getCacheEndpoint())
									.withHost(HCACHE_PROPS.getCacheHost())
									.withPort(HCACHE_PROPS.getCachePort())
									.withUser(HCACHE_PROPS.getCacheUser())
									.withPassword(HCACHE_PROPS.getCachePassword())
									.build();
				cacheConn.connect();
				LOGGER.info("Connected to Cache Datastore" + cacheConn.getConnectionString());
				
				policy = new CachePolicyFactory(HCACHE_PROPS.getExpirationPolicy())
							.withEndpoint(cacheConn)
							.withContentPolicy(HCACHE_PROPS.getContentPolicy())
							.build();
			} catch (SQLException e) {
				LOGGER.error("Unable to connect to Cache Datastore" + cacheConn.getConnectionString(), e);
				cacheConn.close();
				e.printStackTrace();
				System.exit(1);		
			}catch (Exception e1) {
				e1.printStackTrace();
				System.exit(1);
			}
		
		}

	}
	
	public ResultSet processQuery(String sqlQuery) throws SQLException{
		
		 long startTime = System.currentTimeMillis();
		
		HCacheSQLQuery query = new HCacheSQLQuery(sqlQuery);
		
		ResultSet res = null;
		//if it is a select statement we need to send it to the caching algorithm
		if (query.isSelect()){
			
			query.generateUniqueKey(HCACHE_PROPS.getContentPolicy());
			String key = query.getUniqueKey();
			
			res = policy.get(query);
			
			// if the key doesnt exist in the cache
			if (res == null){
				boolean valid_query = true;
				try{
					res = hiveConnection.processQuery(sqlQuery); //get the data
				} catch (SQLException e){
					valid_query = false;
					throw e;
				}
				
				//if the query successfully executes, put it in the cache
				if (valid_query && !HCACHE_PROPS.getContentPolicy().equals(CachePolicy.CACHE_NO_CONTENT)){
					policy.put(query, res);
					
					//if our cache is full, we need to delete something;
					if ( cacheConn.getTotalCacheSize() > HCACHE_PROPS.getMaxCacheSize()  || cacheConn.getTotalCacheEntryCount() > HCACHE_PROPS.getMaxCacheEntries() ){

						policy.updateCache(); 
						
					}
					
					

					if (HCACHE_PROPS.getCacheEndpoint().endsWith(Endpoint.MYSQL_ENDPOINT))
						//TODO: Think of a better way to do this.  Currently caching the data results in mysql results in traversing to the 
						// end of the resultset and hive doesnt support res.beforeFirst() so I have to run the query again.
					res = cacheConn.getCacheData(query, HCACHE_PROPS.getContentPolicy());
				}

			}		
			
		} else if (sqlQuery.trim().equalsIgnoreCase("!help")){
			//delete the cache entries
			String help_string = 
					"\nHoneycache is a tool that helps store frequent distributed queries in order \n" +
					"to speed up the time to retrieve them. You can configure how things get stored, \n" + 
				    "where things get stored, and other settings in $HCACHE_HOME/conf/config.properties. \n " + 
				    "For the command line options, type 'bin/Hcache.sh -h'. \n \n" +
					"COMMANDS\n" +
				    "!help         -  prints out this display\n" + 
					"!kill_cache   -  resets the cache and destroys all cached data.";
			
			System.out.println(help_string);
			
			
		} else if (sqlQuery.trim().equalsIgnoreCase("!kill_cache")){
			//delete the cache entries
			cacheConn.destroyTheCache();
			
		} else
		{
			//we dont want to process updates, deletes, or metadata queries so just connect as normal
			res = hiveConnection.processQuery(sqlQuery);
		}

		long endTime = System.currentTimeMillis();
		double elapsedTimeInS = (endTime - startTime) / 1000.00;
		DecimalFormat df = new DecimalFormat("#.##");
		System.out.println("QUERY EXECUTION TIME:" + df.format(elapsedTimeInS));
		LOGGER.info("QUERY: " + sqlQuery);
		LOGGER.info("QUERY EXECUTION TIME: " + df.format(elapsedTimeInS) );
		return res;
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
