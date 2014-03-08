package honeycache.cache.model;

public class SQLQueryParser {

	
	public static String[] parseTables(String query) {
		String lowerQuery = query.toLowerCase();
		String colString = query.substring(lowerQuery.indexOf("from") + 4, lowerQuery.indexOf("where"));
		colString = colString.trim();
		return colString.split(",");
	}
	
	public static String[] parseColumns(String query) {
		//everything between SELECT and FROM		
		String lowerQuery = query.toLowerCase();
		String colString = query.substring(lowerQuery.indexOf("select") + 6, lowerQuery.indexOf("from"));
		colString = colString.trim();
		return colString.split(",");
	}
	
	
	public static String[] parseConditionals(String query) {
		//everything between SELECT and FROM		
		String lowerQuery = query.toLowerCase();
		String colString = query.substring(lowerQuery.indexOf("where") + 5, query.length());
		colString = colString.trim();
		return colString.split(" AND ");
	}
	
	

	
}

