package honeycache.cache.policy;

import java.sql.ResultSet;

public class NoCachePolicy implements CachePolicy{


	@Override
	public ResultSet get(String key) {
		return null;
	}

	@Override
	public boolean put(String key, ResultSet data) {
		return true;
	}

	@Override
	public boolean updateCache() {
		return true;
	}

}
