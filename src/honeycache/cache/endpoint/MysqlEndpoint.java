package honeycache.cache.endpoint;

import honeycache.cache.model.HCacheMetadata;
import honeycache.cache.model.HCacheSQLQuery;
import honeycache.cache.policy.CacheCommander;
import honeycache.cache.policy.CachePolicy;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class MysqlEndpoint extends Endpoint {
	
	public static final String DRIVER_NAME = "com.mysql.jdbc.Driver";


	//default constructor calls argument constructor
	public MysqlEndpoint(){	
		this("localhost", 3306, "hcache", "hcachepw" );
	}
	
	public MysqlEndpoint(String newHost, int newPort, String newUser, String newPassword){
		host = newHost;
		port = newPort;
		user = newUser;
		password = newPassword;
		connectionString = "jdbc:mysql://" + host + ":" + port + "/hcache_store";
		driverName = DRIVER_NAME;
	}

	
	@Override
	public HCacheMetadata getCacheMetadata(HCacheSQLQuery query) throws SQLException{
		HCacheMetadata retVal = null;
		String select_query = "SELECT key_id, table_name, date_accessed, frequency_accessed, size, orig_table, part_data from hcache_key_data WHERE key_id = '" + query.getUniqueKey() + "'";
		ResultSet res = processQuery(select_query);
		if (res.next())
			retVal = new HCacheMetadata(res.getString("key_id"), res.getString("table_name"), res.getDate("date_accessed"), 
										res.getInt("frequency_accessed"), res.getInt("size"), res.getString("orig_table"), res.getString("part_data"));
		return retVal;
	}
	
	public HCacheMetadata getOldestCacheEntry() throws SQLException{
		HCacheMetadata retVal = null;
		String select_query = "SELECT key_id, table_name, date_accessed, frequency_accessed, size, orig_table, part_data from hcache_key_data " + 
								"WHERE date_accessed = (select MIN(date_accessed) from hcache_key_data)";
		ResultSet res = processQuery(select_query);
		if (res.next())
			retVal = new HCacheMetadata(res.getString("key_id"), res.getString("table_name"), res.getDate("date_accessed"), 
										res.getInt("frequency_accessed"), res.getInt("size"), res.getString("orig_table"), res.getString("part_data"));
		return retVal;
	}
	
	public HCacheMetadata getNewestCacheEntry() throws SQLException{
		HCacheMetadata retVal = null;
		String select_query = "SELECT key_id, table_name, date_accessed, frequency_accessed, size, orig_table, part_data from hcache_key_data " + 
								"WHERE date_accessed = (select MAX(date_accessed) from hcache_key_data)";
		ResultSet res = processQuery(select_query);
		if (res.next())
			retVal = new HCacheMetadata(res.getString("key_id"), res.getString("table_name"), res.getDate("date_accessed"), 
										res.getInt("frequency_accessed"), res.getInt("size"), res.getString("orig_table"), res.getString("part_data"));
		return retVal;
		
	};
	public HCacheMetadata getMostFrequentCacheEntry() throws SQLException{
		HCacheMetadata retVal = null;
		String select_query = "SELECT key_id, table_name, date_accessed, frequency_accessed, size, orig_table, part_data from hcache_key_data " + 
								"WHERE frequency_accessed = (select MAX(frequency_accessed) from hcache_key_data)";
		ResultSet res = processQuery(select_query);
		if (res.next())
			retVal = new HCacheMetadata(res.getString("key_id"), res.getString("table_name"), res.getDate("date_accessed"), 
										res.getInt("frequency_accessed"), res.getInt("size"), res.getString("orig_table"), res.getString("part_data"));
		return retVal;
	};
	public HCacheMetadata getLeastFrequentCacheEntry() throws SQLException{
		HCacheMetadata retVal = null;
		String select_query = "SELECT key_id, table_name, date_accessed, frequency_accessed, size, orig_table, part_data from hcache_key_data " + 
								"WHERE frequency_accessed = (select MIN(frequency_accessed) from hcache_key_data)";
		ResultSet res = processQuery(select_query);
		if (res.next())
			retVal = new HCacheMetadata(res.getString("key_id"), res.getString("table_name"), res.getDate("date_accessed"), 
										res.getInt("frequency_accessed"), res.getInt("size"), res.getString("orig_table"), res.getString("part_data"));
		return retVal;
	};
	public HCacheMetadata getRandomCacheEntry() throws SQLException{
		HCacheMetadata retVal = null;
		String select_query = "SELECT key_id, table_name, date_accessed, frequency_accessed, size, orig_table, part_data FROM hcache_key_data " + 
								"ORDER BY RAND() LIMIT 1"; 
		ResultSet res = processQuery(select_query);
		if (res.next())
			retVal = new HCacheMetadata(res.getString("key_id"), res.getString("table_name"), res.getDate("date_accessed"), 
										res.getInt("frequency_accessed"), res.getInt("size"), res.getString("orig_table"), res.getString("part_data"));
		return retVal;
	};
	
	@Override
	public void updateMetadata(HCacheMetadata meta) throws SQLException {
		
		String insertMetadata = "INSERT INTO hcache_key_data (key_id, table_name, date_accessed, frequency_accessed, size, orig_table, part_data) " +
								" VALUES (?, ?, NOW(), ?, ?, ?, ?) " + 
								"ON DUPLICATE KEY UPDATE date_accessed=NOW(), frequency_accessed=frequency_accessed + 1";
		PreparedStatement prepStmt = dbConn.prepareStatement(insertMetadata);			
		prepStmt.setString(1, meta.getKey());
		prepStmt.setString(2, meta.getCacheTableName());
		//prepStmt.setDate(3, meta.getDateAccessed());
		prepStmt.setInt(3, meta.getFrequencyAccessed());
		prepStmt.setInt(4, meta.getSize());
		prepStmt.setString(5, meta.getOriginalTable());
		prepStmt.setString(6, meta.getPartitionData());
		
		prepStmt.executeUpdate();
		
	}
	
	@Override
	public ResultSet getCacheData(HCacheSQLQuery query, String contentPolicy) throws SQLException {
		
		if (contentPolicy.equals(CachePolicy.CACHE_QUERY_CONTENT)){
			ResultSet results = processQuery("SELECT * from `" + query.generateTableName(contentPolicy) + "`");
			return results;
		} else {
			String newQuery = query.replaceTable(query.generateTableName(contentPolicy));
			ResultSet results = processQuery(newQuery);
			return results;

		}
		
	}
	
	@Override
	public void deleteCacheData(HCacheMetadata key) throws SQLException{

		//delete metadata
		processUpdate("DELETE FROM hcache_key_data WHERE key_id = '"+ key.getKey() + "'");
		
		//delete table
		processUpdate("DROP TABLE "+ key.getCacheTableName() + " IF EXISTS");

	}
	

	@Override
	public void putCacheData(HCacheSQLQuery query, ResultSet res, String contentPolicy) throws SQLException {	
		
		String new_data_table_name = query.generateTableName(contentPolicy);
		
		//if the content type is table or partition, then we overwrite res with a select * from table query.
		if (!contentPolicy.equals(CachePolicy.CACHE_QUERY_CONTENT)){
			HiveEndpoint endpoint = new HiveEndpoint();
			endpoint.connect();
			String getTableSQL = "SELECT * FROM " + query.parseTable();
			String partitions = query.parsePartitions();
			if (!partitions.isEmpty()){
				getTableSQL += " WHERE " + query.parsePartitions().replace("|", " AND ");
			}
			res = endpoint.processQuery(getTableSQL);
		}
		
		//construct the SQL statements
		ResultSetMetaData metadata = res.getMetaData();
		String createStatement = "CREATE TABLE `" + new_data_table_name +"` ( "; //adding quotes to make table names friendly for mysql
		String insertStatement = "INSERT INTO `" + new_data_table_name + "` ( "; 
		String insertValues = " VALUES ( ";

		for(int i = 1; i <= metadata.getColumnCount(); i++){
			String colName = "`" + metadata.getColumnLabel(i) + "`";
			String colType = "";
			//TODO: There has to be a better way to do this.
			if (metadata.getColumnTypeName(i).toUpperCase().equals("INT"))
				colType = "INT";
			else
				colType = "VARCHAR(255)";
			
			String comma = ", ";
			if (i == metadata.getColumnCount())
				comma = " ) ";
			
			createStatement += colName + " " + colType + comma;
			insertStatement += colName + comma;
			insertValues += "?" + comma;
		}
		insertStatement += insertValues;
		
		//create the table
		processUpdate(createStatement);
		
		//insert the data from the result set
		PreparedStatement prepStmt = dbConn.prepareStatement(insertStatement);	
		while (res.next()) {

			for(int i = 1; i <= metadata.getColumnCount(); i++){
				//TODO: there has to be a better way to do this
				if (metadata.getColumnTypeName(i).toUpperCase().equals("INT"))
					prepStmt.setInt(i, res.getInt(i));
				else 
					prepStmt.setString(i, res.getString(i));
			}
			
			prepStmt.addBatch();
			
		}
		prepStmt.executeBatch();	
		//TODO: move result set back to first row
		//res.first() is not supported for hive jdbc so we must rerun the query again in the cachecommander
		

		//get the table_size
		int size = 9999;
		String size_query = "SELECT table_name, round(((data_length + index_length) / 1024)) 'size_in_kb' FROM information_schema.TABLES " +
							"WHERE table_schema = '"+dbName+"'  AND table_name = '" + new_data_table_name +"'";
		ResultSet sizeData = processQuery(size_query);
		if (sizeData.next()){
			size = sizeData.getInt("size_in_kb");
		}
		
		HCacheMetadata meta = new HCacheMetadata(query.getUniqueKey(), new_data_table_name, new java.sql.Date( new java.util.Date().getTime() ), 1, size, query.parseTable(), query.parsePartitions());
		updateMetadata(meta);
		

	
	}

	public int getTotalCacheSize() throws SQLException{
		int size = 0;
		ResultSet cacheSize = processQuery("SELECT SUM(size) FROM hcache_key_data");
		if (cacheSize.next()){
			size = cacheSize.getInt(1);
		}
		return size;
		
	};
	
	public int getTotalCacheEntryCount() throws SQLException {
		int size = 0;
		ResultSet cacheSize = processQuery("SELECT COUNT(key_id) FROM hcache_key_data");
		if (cacheSize.next()){
			size = cacheSize.getInt(1);
		}
		return size;
	}

	public void destroyTheCache() throws SQLException{
		connect();
		ResultSet cachedTables = processQuery("SELECT key_id, table_name FROM hcache_key_data");
		while (cachedTables.next()){
			 
			deleteCacheData( new HCacheMetadata(cachedTables.getString(1), cachedTables.getString(2),null,0, 0,null, null) );
			
		}
	}
	
}
