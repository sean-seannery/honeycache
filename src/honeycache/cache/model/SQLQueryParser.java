package honeycache.cache.model;

public class SQLQueryParser {
	
	private String query;
	private String lowerQ;
	
	private int indexOfFrom = -1;
	private int indexOfSelect = -1;
	private int indexOfWhere= -1;
	private int indexOfGroup = -1;
	private int indexOfHaving = -1;
	private int indexOfOrderBy = -1;
	
	public SQLQueryParser(String newQuery){
		query = newQuery.trim();
		if (query.endsWith(";"))
			query = query.substring( 0, query.length()-1); 

		lowerQ = query.toLowerCase();
		indexOfSelect = lowerQ.indexOf("select");
		indexOfFrom = lowerQ.indexOf("from");
		indexOfWhere = lowerQ.indexOf("where");
		indexOfGroup = lowerQ.indexOf("group");
		indexOfHaving = lowerQ.indexOf("having");
		indexOfOrderBy = lowerQ.indexOf("orderby");
	}

	
	public String[] parseTables(String query) {
		
		String colString;
		
		if (indexOfWhere != -1) {
			colString = query.substring(indexOfFrom + 4, indexOfWhere);
			colString = colString.trim();
		} else if (indexOfGroup != -1) {
			colString = query.substring(indexOfFrom + 4, indexOfGroup);
			colString = colString.trim();
		} else if (indexOfOrderBy != -1)
		{
			colString = query.substring(indexOfFrom + 4, indexOfOrderBy);
			colString = colString.trim();
		} else {
			colString = query.substring(indexOfFrom + 4, lowerQ.length());
		}
		if (colString.indexOf("JOIN") != -1){
			return colString.split("JOIN");
		} else {
			return colString.split(",");
		}

	}
	
	public String[] parseColumns(String query) {
		//everything between SELECT and FROM		
		String colString = query.substring(indexOfSelect + 6, indexOfFrom);
		colString = colString.trim();
		return colString.split(",");
	}
	
	
	public static String[] parseConditionals(String query) {
		//everything between SELECT and FROM	
		String lowerQuery = query.toLowerCase();
		int index = lowerQuery.indexOf("where");
		if (index > 0) {
			String colString = query.substring(lowerQuery.indexOf("where") + 5, query.length());
			colString = colString.trim();
			return colString.split(" AND ");
		}
		else return null;

	}
	
	

	
}

