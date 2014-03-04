package honeycache.cli;

import honeycache.cache.CacheCommander;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import jline.console.ConsoleReader;

public class HoneyCacheCLI {
	
	
	private static final String PROMPT_STRING = "hcache->";
	private static final String HIVE_DRIVER_NAME = "org.apache.hadoop.hive.jdbc.HiveDriver";
	private static final boolean CACHING_ENABLED = true;
	
	private Connection hiveConnection;
	private ConsoleReader prompt;
	
	
	
	private int port;
	private String host;
	private String user;
	private String password;
	
	
	
	public HoneyCacheCLI() {
		hiveConnection = null;
		prompt = null;
	}
	
	public HoneyCacheCLI(String newHost, int newPort, String newUser, String newPass){
		hiveConnection = null;
		prompt = null;
		host = newHost;
		port = newPort;
		user = newUser;
		password = newPass;
	}
	
	public void connect() throws SQLException{	
		if (hiveConnection != null){
			disconnect();
		}
		
		try {
			Class.forName(HIVE_DRIVER_NAME);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		
		String connString = "jdbc:hive://" + host +":" + port + "/default";
		hiveConnection = DriverManager.getConnection(connString, user, password);

		System.out.println("Connected to " + connString);
		System.out.println("------------------------------------------------");
	}
	
	public void disconnect() {		
		try {
			hiveConnection.close();
		} catch (SQLException e) {
		}
		hiveConnection = null;
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
	}
	
	public ResultSet processQuery( String query) throws SQLException{
		query = query.replace(";", "");

		CacheCommander cache = new CacheCommander(hiveConnection);
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
