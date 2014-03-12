package honeycache.cache.model;

import java.io.InputStream;
import java.util.Properties;


public class HCacheProperties {
	
	public static final String CACHE_CONTENT_POLICY_KEY = "CACHE_CONTENT_POLICY";  //query or partition or table
	public static final String EXPIRATION_POLICY_KEY = "CACHE_EXPIRATION_POLICY"; //lru or nocache
	public static final String MAX_CACHE_SIZE_IN_KB_KEY = "MAX_CACHE_SIZE_IN_KB";
	public static final String MAX_CACHE_ENTRIES_KEY = "MAX_CACHE_ENTRIES";
	
	public static final String METADATA_HOSTNAME_KEY = "METADATA_HOSTNAME";
	public static final String METADATA_PORT_KEY = "METADATA_PORT";
	public static final String METADATA_USER_KEY = "METADATA_USER";
	public static final String METADATA_PW_KEY   = "METADATA_PW";

	public static final String CACHE_ENDPOINT_KEY = "CACHE_ENDPOINT"; 	
	public static final String CACHE_ENDPOINT_HOSTNAME_KEY = "CACHE_ENDPOINT.HOSTNAME";
	public static final String CACHE_ENDPOINT_PORT_KEY = "CACHE_ENDPOINT.PORT";
	public static final String CACHE_ENDPOINT_CACHE_USER_KEY = "CACHE_ENDPOINT.CACHE_USER";
	public static final String CACHE_ENDPOINT_CACHE_PW_KEY = "CACHE_ENDPOINT.CACHE_PW";
	
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
		return props.getProperty(CACHE_CONTENT_POLICY_KEY).trim();
	}
	
	public String getExpirationPolicy(){
		return props.getProperty(EXPIRATION_POLICY_KEY).trim();
	}
	
	public String getCacheEndpoint() {
		return props.getProperty(CACHE_ENDPOINT_KEY).trim();
	}

	public int getMaxCacheSize() {
		return Integer.parseInt( props.getProperty(MAX_CACHE_SIZE_IN_KB_KEY).trim() );
	}

	public int getMaxCacheEntries() {
		return Integer.parseInt( props.getProperty(MAX_CACHE_ENTRIES_KEY).trim() );
	}
	
	public String getCacheHost(){
		return props.getProperty(CACHE_ENDPOINT_HOSTNAME_KEY).trim();
	}
	
	public int getCachePort(){
		return Integer.parseInt( props.getProperty(CACHE_ENDPOINT_PORT_KEY).trim() );
	}
	
	public String getCacheUser(){
		return props.getProperty(CACHE_ENDPOINT_CACHE_USER_KEY).trim();
	}
	
	public String getCachePassword(){
		return props.getProperty(CACHE_ENDPOINT_CACHE_PW_KEY).trim();
	}
	
	public String getMetadataHost(){
		return props.getProperty(METADATA_HOSTNAME_KEY).trim();
	}
	
	public int getMetadataPort(){
		return Integer.parseInt( props.getProperty(METADATA_PORT_KEY).trim() );
	}
		
	public String getMetadataUser(){
		return props.getProperty(METADATA_USER_KEY).trim();
	}
	
	public String getMetadataPassword(){
		return props.getProperty(METADATA_PW_KEY).trim();
	}

	

}
