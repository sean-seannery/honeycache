package honeycache.cache.policy;

import honeycache.cache.endpoint.CacheEndpoint;

import java.sql.ResultSet;

public class LRUPolicy implements CachePolicy{

	private CacheEndpoint cacheConnection;
	
	public LRUPolicy(CacheEndpoint cacheConn){
		cacheConnection = cacheConn;
	}
	
	@Override
	public ResultSet processQuery() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultSet get(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean put(String key, ResultSet data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateCache() {
		// TODO Auto-generated method stub
		return false;
	}

}
