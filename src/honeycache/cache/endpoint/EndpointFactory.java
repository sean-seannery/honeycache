package honeycache.cache.endpoint;


public class EndpointFactory {
	

	private String user;
	private String password;
	private String host;
	private int port;
	private String endType;
	
	public EndpointFactory(String newEndType){
		endType = newEndType;
	}
	
   public Endpoint build() throws Exception{
		
		if (endType.equals(Endpoint.MYSQL_ENDPOINT))
			return new MysqlEndpoint(host, port,  user, password);

		if (endType.equals(Endpoint.HIVE_ENDPOINT))
			return new HiveEndpoint(host, port, user, password);
		
		throw new Exception("Endpoint type doesn't exist. Check your properties.config settings");
		 
   }
   
   public EndpointFactory withHost(String newHost){
	   host = newHost;
	   return this;
   }
   
	public EndpointFactory withPort(int port) {
		this.port = port;
		return this;
	}
   
	public EndpointFactory withUser(String user) {
		this.user = user;
		return this;
	}

	public EndpointFactory withPassword(String password) {
		this.password = password;
		return this;
	}

   
 



}

