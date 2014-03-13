package honeycache.cache.policy;

import honeycache.cache.endpoint.Endpoint;

public class CachePolicyFactory {
	

	private Endpoint cacheEndpoint;
	private String policyType;
	private String contentPolicy;
	
	public CachePolicyFactory(String newPolicyType){
		policyType = newPolicyType;
		contentPolicy = CachePolicy.CACHE_QUERY_CONTENT;
	}
	
   public CachePolicy build() throws Exception{
		
		if (policyType.equals(CachePolicy.CACHE_NO_CONTENT))
			return new NoCachePolicy();

		if (policyType.equals(CachePolicy.EXPIRATION_POLICY_LRU))
			return new LRUPolicy(cacheEndpoint, contentPolicy);
		
		throw new Exception("Policy type "+ policyType +" doesn't exist. Check your properties.config settings");
		 
   }
   
   public CachePolicyFactory withEndpoint(Endpoint newCacheEndpoint){
	   cacheEndpoint = newCacheEndpoint;
	   return this;
   }
   
   public CachePolicyFactory withContentPolicy(String newContentPolicy){
	   contentPolicy = newContentPolicy;
	   return this;
   }
   


}
