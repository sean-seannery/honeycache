package honeycache.cache.policy;

import honeycache.cache.model.HCacheSQLQuery;

import java.sql.ResultSet;

public class NoCachePolicy extends CachePolicy{


	@Override
	public ResultSet get(HCacheSQLQuery query) {
		return null;
	}

	@Override
	public boolean put(HCacheSQLQuery query, ResultSet data) {
		return true;
	}

	@Override
	public boolean phaseOutCacheItem() {
		return true;
	}

}
