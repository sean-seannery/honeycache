package honeycache.cache.endpoint;

import honeycache.cache.model.HCacheMetadata;
import honeycache.cache.model.HCacheSQLQuery;
import honeycache.cache.policy.CachePolicy;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HiveEndpoint extends Endpoint{
	
	public static final String DRIVER_NAME = "org.apache.hadoop.hive.jdbc.HiveDriver"; 
	

	//default constructor calls argument constructor
	public HiveEndpoint(){
		this("localhost",10000, "", "");
	}
	
	public HiveEndpoint(String newHost, int newPort, String newUser, String newPassword){
		//super(newHost, newPort, newUser, newPassword, "");
		host = newHost;
		port = newPort;
		user = newUser;
		password = newPassword;
		connectionString = "jdbc:hive://" + host +":" + port + "/default";
		driverName = DRIVER_NAME;
	}
	
	
	@Override
	public ResultSet getCacheData(HCacheSQLQuery query, String contentPolicy) throws SQLException {
		
		if (contentPolicy.equals(CachePolicy.CACHE_QUERY_CONTENT)){
			ResultSet results = processQuery("SELECT * from `" + query.generateTableName(contentPolicy) + "`");
			return results;
		} else {
			String newQuery = query.replaceTable(query.generateTableName(contentPolicy));
			System.out.println("SQL:" + newQuery + "tbl:" + query.generateTableName(contentPolicy));
			ResultSet results = processQuery(newQuery);
			return results;

		}
	}

	@Override
	public void putCacheData(HCacheSQLQuery query, ResultSet res, String contentPolicy) throws SQLException {
		String new_data_table_name = query.generateTableName(contentPolicy);
		
		//construct the SQL statements
		String createAndInsertStatement = "";
		if (contentPolicy.equals(CachePolicy.CACHE_QUERY_CONTENT)) {
			createAndInsertStatement = "CREATE TABLE " + new_data_table_name +" AS " + query.getQueryString();
		} else {
			createAndInsertStatement = "CREATE TABLE " + new_data_table_name +" AS SELECT * FROM " + query.parseTable();
			String partitions = query.parsePartitions();
			if (!partitions.isEmpty()){
				createAndInsertStatement += " WHERE " + query.parsePartitions().replace("|", " AND ");
			}
		}
		System.out.println("SQL: " + createAndInsertStatement);
		//create the table
		processUpdate(createAndInsertStatement);
		
		//get the table_size
		int size_in_kb = 9999;
		try {
			//TODO: make these resource locations not hardcoded somehow
			Configuration conf = new Configuration();
			conf.addResource(new Path("/home/hadoop/hadoop-1.2.1/conf/core-site.xml"));
			conf.addResource(new Path("/home/hadoop/hadoop-1.2.1/conf/hdfs-site.xml"));
			
			Path inFile = new Path("/user/hive/warehouse/" + new_data_table_name);
			long size_in_bytes = FileSystem.get(conf).getContentSummary(inFile).getLength();
			size_in_kb = (int) (size_in_bytes / 1024);
		} catch (IOException e){
			throw new SQLException("Error getting table size from hdfs command", e);
		}

		
		
		HCacheMetadata meta = new HCacheMetadata(query.getUniqueKey(), new_data_table_name, new java.sql.Date( new java.util.Date().getTime() ), 1, size_in_kb
												, query.parseTable(), query.parsePartitions());
		updateMetadata(meta);
		
		
	}

	@Override
	public void deleteCacheData(HCacheMetadata key) throws SQLException {

		deleteMetadataTableEntry(key);
				
		processUpdate("DROP TABLE "+ key.getCacheTableName());
	}



}
