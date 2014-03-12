package honeycache.cache.model;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;

public class HCacheSQLQuery {
	
	private String query;
	private String uniqueKey;
	
	private String lowerQ = null;
	private int indexOfFrom = -1;
	private int indexOfSelect = -1;
	private int indexOfWhere= -1;
	private int indexOfGroupBy = -1;
	private int indexOfOrderBy = -1;
	
	public HCacheSQLQuery(String newQuery){
		query = newQuery.trim();
		if (query.endsWith(";"))
			query = query.substring( 0, query.length()-1); 
		uniqueKey = null;

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

	
	public ArrayList<String> parseTables() {
		parse();
		ArrayList <String> retVal = new ArrayList<String>();
		String colString = null;
		
		//everything between FROM and WHERE/GROUPBY/ORDERBY
		if (indexOfWhere != -1) {
			colString = query.substring(indexOfFrom + 4, indexOfWhere);
			colString = colString.trim();
		} else if (indexOfGroupBy != -1) {
			colString = query.substring(indexOfFrom + 4, indexOfGroupBy);
			colString = colString.trim();
		} else if (indexOfOrderBy != -1)
		{
			colString = query.substring(indexOfFrom + 4, indexOfOrderBy);
			colString = colString.trim();
		} else {
			colString = query.substring(indexOfFrom + 4, lowerQ.length());
		}

		if (colString != null) {
			retVal.addAll( Arrays.asList(colString.split(",| JOIN ")) );
		}
			
			return retVal;
		

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
	
	
	public ArrayList<String> parseConditionals() {
		parse();
		ArrayList<String> retVal = new ArrayList<String>();
		String colString = null;
		if (indexOfWhere != -1) {
			if (indexOfGroupBy != -1) {
				colString = query.substring(indexOfWhere + 5, indexOfGroupBy);
				colString = colString.trim();
			} else if (indexOfOrderBy != -1)
			{
				colString = query.substring(indexOfWhere + 5, indexOfOrderBy);
				colString = colString.trim();
			} else {
				colString = query.substring(indexOfWhere + 5, query.length());
				colString = colString.trim();
			}
		}
		
		if (colString != null){
			retVal.addAll( Arrays.asList(colString.split(" AND | OR ")) );
		}
		
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
		
		if (contentType.equalsIgnoreCase("query")){ 
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
		if (contentType.equalsIgnoreCase("table")){
			
			HCacheSQLQuery queryParser = new HCacheSQLQuery(query);
			for (String tblName : queryParser.parseTables()) {
				uniqueKey += tblName;
			}

		}
	}
	
	

	
}

