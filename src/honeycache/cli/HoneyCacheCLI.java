package honeycache.cli;

import honeycache.cache.endpoint.HiveEndpoint;
import honeycache.cache.policy.CacheCommander;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import jline.console.ConsoleReader;

public class HoneyCacheCLI {
	
	
	private static final String PROMPT_STRING = "hcache->";
	
	private ConsoleReader prompt;
	private CacheCommander cache;
		
	public HoneyCacheCLI() {
		prompt = null;
		cache = null;
	}
	
	public HoneyCacheCLI(HiveEndpoint newConn){
		cache = new CacheCommander(newConn);
		prompt = null;
	}
	
	public void connect() throws SQLException{	

		cache.connect();

		System.out.println("Connected to " + cache.getHiveConnection().toString() );
		System.out.println("------------------------------------------------");
	}
	
	public void disconnect() {		
		cache.disconnect();
	}
	
	public void startCommandLineInput() throws IOException{
		try {
			connect();
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}

		
		prompt = new ConsoleReader();
		prompt.setPrompt(PROMPT_STRING);
		prompt.setHistoryEnabled(true);
		
		String line = null;
		String command = "";
		while ((line = prompt.readLine()) != null && !line.trim().equals("exit;")){
			line = line.trim();
			command += line;
			//if the command ends on this line
			if (command.endsWith(";")){
				ResultSet results;
				try {
					results = processQuery(command);
					printResults(results);
				} catch (SQLException e) {
					System.out.println("There was an error with your query.");
					System.out.println(e.getSQLState() +"- " + e.getMessage());
					System.out.println("");
				}
				
				prompt.setPrompt(PROMPT_STRING);
				command = "";
			}
			else {
				//this is a multi line command so concatinate them together.
				command += " ";
				prompt.setPrompt("      ->");
			}
			
		}

		System.out.println (" goodbye.");
		System.out.println();

		disconnect();

	}
	
	public ResultSet processQuery( String query) throws SQLException{
		query = query.replace(";", "");

		ResultSet res = cache.processQuery(query);

		return res;
	}
	
	public void printResults( ResultSet results) throws SQLException{
		ResultSetMetaData metadata = results.getMetaData();
		for(int i = 1; i <= metadata.getColumnCount(); i++){
			System.out.print(metadata.getColumnName(i) + " | ");
		}
		System.out.println("");
		System.out.println("----------------------------------------");
		
		while (results.next()) {
					
			for(int i = 1; i <= metadata.getColumnCount(); i++){
				System.out.print(results.getString(i) + " | ");
			}
			System.out.println("");
		}
	}
	
}
