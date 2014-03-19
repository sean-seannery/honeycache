package honeycache.cache.model;

import honeycache.cache.policy.CachePolicy;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

public class HCacheSQLQuery {
	
	private static final String DATA_TABLE_PREFIX = "hc_";
	
	private String query;
	private String uniqueKey;
	
	private String lowerQ = null;
	private int indexOfFrom = -1;
	private int indexOfSelect = -1;
	private int indexOfWhere= -1;
	private int indexOfGroupBy = -1;
	private int indexOfOrderBy = -1;
	private String tableName;

	public HCacheSQLQuery(String newQuery){
		query = newQuery.trim();
		if (query.endsWith(";"))
			query = query.substring( 0, query.length()-1); 
		uniqueKey = null;
		tableName = null;

	}
		
	private void parse(){
		if (lowerQ == null){
			lowerQ = query.toLowerCase();
			indexOfSelect = lowerQ.indexOf("select");
			indexOfFrom = lowerQ.indexOf("from");
			indexOfWhere = lowerQ.indexOf("where");
			indexOfGroupBy = lowerQ.indexOf("group");
			indexOfOrderBy = lowerQ.indexOf("orderby");
		}
	}

	
	public String parseTable() {
	
		if (tableName != null){
			return tableName;
		} else {
			parse();
			String retVal = null;
			
			//everything between FROM and WHERE/GROUPBY/ORDERBY
			if (indexOfWhere != -1) {
				retVal = query.substring(indexOfFrom + 4, indexOfWhere);
				retVal = retVal.trim();
			} else if (indexOfGroupBy != -1) {
				retVal = query.substring(indexOfFrom + 4, indexOfGroupBy);
				retVal = retVal.trim();
			} else if (indexOfOrderBy != -1)
			{
				retVal = query.substring(indexOfFrom + 4, indexOfOrderBy);
				retVal = retVal.trim();
			} else if (lowerQ.indexOf("limit") != -1)
			{
				retVal = query.substring(indexOfFrom + 4, lowerQ.indexOf("limit"));
				retVal = retVal.trim();
			} else {
				retVal = query.substring(indexOfFrom + 4, lowerQ.length());
			}
			tableName = retVal.trim();
			return tableName;
		}

	}
	
	public ArrayList<String> parseColumns() {
		parse();
		ArrayList<String> retVal = new ArrayList<String>();
		
		//everything between SELECT and FROM		
		String colString = query.substring(indexOfSelect + 6, indexOfFrom);
		colString = colString.trim();
		retVal.addAll( Arrays.asList(colString.split(",")) );
		return retVal;
		
	}
	
	
	public String parsePartitions() {
		parse();
		String retVal = "";
		String colString = null;
		if (indexOfWhere != -1) {
			if (indexOfGroupBy != -1) {
				colString = query.substring(indexOfWhere + 5, indexOfGroupBy);
				colString = colString.trim();
			} else if (indexOfOrderBy != -1)
			{
				colString = query.substring(indexOfWhere + 5, indexOfOrderBy);
				colString = colString.trim();
			}  else if (lowerQ.indexOf("limit") != -1)	{
				colString = query.substring(indexOfWhere + 5, lowerQ.indexOf("limit"));
				colString = colString.trim();
			} 
			else {
				colString = query.substring(indexOfWhere + 5, query.length());
				colString = colString.trim();
			}		

			
			if (parseTable().equals("telemetry_hourly_tbl")) {
				for (String token : colString.split("and|or|not")){
					
					/*for (String b : s.split("!=|<>|>=|<=|>|<|=")){
					System.out.println("     " + b.trim());
					}*/
					
					if (token.contains("dt") || token.contains("hour") || token.contains("service")){			 
						retVal += token.trim() + "|";
					}
					
				}
			}
			
		
			if (retVal.endsWith("|"))
				retVal = retVal.substring(0, retVal.length()-1);
			
			
		}
		System.out.println("retVal:" +retVal);
		return retVal;


	}
	
	public boolean isSelect()
	{
		parse();
		return indexOfSelect > -1 && indexOfFrom > -1;
	}
	
	public boolean isUpdate()
	{
		parse();
		return lowerQ.indexOf("update") > -1;
	}
	
	public boolean isDelete()
	{
		parse();
		return lowerQ.indexOf("delete") > -1;
	}
	
	public String getUniqueKey(){
			return uniqueKey;
	}
	
	
	public String getQueryString() {
		return query;
	}

	
	public void generateUniqueKey(String contentType) {
		
		if (contentType.equalsIgnoreCase(CachePolicy.CACHE_QUERY_CONTENT)){ 
			//MD5of Hash
			byte md5Hash[];
			try {
				md5Hash = MessageDigest.getInstance("MD5").digest( query.getBytes("UTF-8") );
				
				StringBuffer sb = new StringBuffer();
				 for(byte b : md5Hash){
		               sb.append(String.format("%02x", b&0xff));
		          }

				uniqueKey = sb.toString();
			} catch (Exception  e) {
				System.out.println("Error getting query key.  Problem with Hashing");
				e.printStackTrace();
				System.exit(1);
			} 
		}
		if (contentType.equalsIgnoreCase(CachePolicy.CACHE_PARTITION_CONTENT)){
			
			uniqueKey = parseTable();
			uniqueKey += parsePartitions();

		}
	}
	
	public String replaceTable(String newTable){
		String queryCopy = query;
		String oldTable = parseTable();
		
		queryCopy = queryCopy.replaceAll("\\b"+oldTable+"\\b", newTable);
		
		return queryCopy;
	}
	
	public String generateTableName( String contentPolicy ){
		if (contentPolicy.equals(CachePolicy.CACHE_QUERY_CONTENT)){
			return DATA_TABLE_PREFIX + getUniqueKey();
		} else { 
			String newPartitions = parsePartitions().replaceAll("\"|\'", "").replaceAll("[^\\dA-Za-z ]", "_");
			String newTable = DATA_TABLE_PREFIX + parseTable();
			if (!newPartitions.isEmpty()){
				newTable += "_" + newPartitions;
			}
			return newTable;
		}
	}
	

	
}

