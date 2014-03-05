package honeycache.cache.policy;

import java.sql.ResultSet;

public class NoCachePolicy implements CachePolicy{

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
