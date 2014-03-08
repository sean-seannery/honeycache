package honeycache.cache.model;

import java.io.InputStream;
import java.util.Properties;


public class HCacheProperties {
	
	public static final String CACHE_CONTENT_POLICY_KEY = "CACHE_CONTENT_POLICY";  //query or partition or table
	public static final String EXPIRATION_POLICY_KEY = "CACHE_EXPIRATION_POLICY"; //lru or nocache
	public static final String CACHE_ENDPOINT_KEY = "CACHE_ENDPOINT"; 
	
	private Properties props;
	
	public HCacheProperties(String filename) {
		try {
			props = new Properties();
			InputStream  input = HCacheProperties.class.getClassLoader().getResourceAsStream(filename);
			props.load(input);
		} catch (Exception e) {
			System.out.println("Cannot load config file: " + filename);
			e.printStackTrace();
			System.exit(1);
		} 
	}
	
	public String getContentPolicy(){
		return props.getProperty(CACHE_CONTENT_POLICY_KEY);
	}
	
	public String getExpirationPolicy(){
		return props.getProperty(EXPIRATION_POLICY_KEY);
	}
	
	public String getCacheEndpoint() {
		return props.getProperty(CACHE_ENDPOINT_KEY);
	}

}
