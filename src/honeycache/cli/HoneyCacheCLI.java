package honeycache.cli;

import honeycache.cache.endpoint.HiveEndpoint;
import honeycache.cache.policy.CacheCommander;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Scanner;

import org.apache.log4j.Logger;

import jline.console.ConsoleReader;

public class HoneyCacheCLI {
	
	private final static Logger LOGGER = Logger.getLogger(HoneyCacheCLI.class.getName());
	private static final String PROMPT_STRING = "hcache->";
	
	private ConsoleReader prompt;
	private CacheCommander cache;
		
	
	public HoneyCacheCLI(HiveEndpoint newConn){
		cache = new CacheCommander(newConn);
		prompt = null;
	}
	
	public void connect() throws SQLException{	

		cache.connect();

		System.out.println("Connected to " + cache.getHiveConnection().toString() );
		System.out.println("   type !help for a list of commands and options");
		System.out.println("------------------------------------------------");
	}
	
	public void disconnect() {		
		cache.disconnect();
	}
	
	public void startCommandLineInput(){
		String EOL = System.getProperty("line.separator");  
		
		try {
			connect();
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}

		
		try {
			prompt = new ConsoleReader();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		prompt.setPrompt(PROMPT_STRING);
		prompt.setHistoryEnabled(true);
		prompt.setExpandEvents(false);
		
		String line = null;
		String command = "";
		try {
			while ((line = prompt.readLine()) != null && !line.trim().equals("exit;")){
				line = line.trim();
				command += line;
				//if the command ends on this line
				if (command.endsWith(";")){
					command = command.replaceAll(EOL, " ");
					ResultSet results;
					try {
						long startTime = System.currentTimeMillis();
						results = processOneQuery(command);
						printResults(results);
						long endTime = System.currentTimeMillis();
						double elapsedTimeInS = (endTime - startTime) / 1000.00;
						DecimalFormat df = new DecimalFormat("#.##");
						System.out.println("TOTAL EXECUTION TIME:" + df.format(elapsedTimeInS));
					} catch (SQLException e) {
						System.out.println("There was an error with your query.");
						System.out.println(e.getSQLState() +"- " + e.getMessage());
						e.printStackTrace();
						System.out.println("");
					}
					
					prompt.setPrompt(PROMPT_STRING);
					command = "";
				}
				else {
					//this is a multi line command so concatinate them together.
					command += EOL;
					if (!command.trim().isEmpty()) 	{
						prompt.setPrompt("      ->");
					}
				}
				
			}
		} catch (IOException e) {
			System.out.println("Error parsing command line statement");
			e.printStackTrace();
		}

		System.out.println (" goodbye.");
		System.out.println();

		disconnect();

	}
	
	public void processFile( String filename) throws SQLException{
		try {
			cache.connect();
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		
		
		Scanner fileIn;
		try {
			fileIn = new Scanner(new File(filename));
		} catch (FileNotFoundException e) {
			System.out.println("Cannot Find file " + filename);
			cache.disconnect();
			return;
		}
		
		String line = null;
		String command = "";
		String EOL = System.getProperty("line.separator");  
		long startTime = System.currentTimeMillis();
		while (fileIn.hasNextLine()){
			line = fileIn.nextLine().trim();
			command += line;
			//if the command ends on this line
			if (command.endsWith(";")){
				command = command.replaceAll(EOL, " ");
				ResultSet results;
				try {
					results = processOneQuery(command);
					printResults(results);
				} catch (SQLException e) {
					System.out.println("There was an error with your query.");
					System.out.println(e.getSQLState() +"- " + e.getMessage());
					e.printStackTrace();
					System.out.println("");
				}
				command = "";
			}
			else {
				//this is a multi line command so concatinate them together.
				command += EOL;
			}
		}
		
		fileIn.close();
		cache.disconnect();
		
		long endTime = System.currentTimeMillis();
		double elapsedTimeInS = (endTime - startTime) / 1000;
		DecimalFormat df = new DecimalFormat("#.##");
        System.out.print("FILE EXECUTION TIME:" + df.format(elapsedTimeInS));
		LOGGER.info("FILE: " + filename);
		LOGGER.info("FILE EXECUTION TIME: " + df.format(elapsedTimeInS) );

	}
	
	public ResultSet processOneQueryAndDisconnect( String query) throws SQLException{
		cache.connect();
		
		ResultSet res = cache.processQuery(query);
		
		cache.disconnect();

		return res;
	}
	
	private ResultSet processOneQuery( String query) throws SQLException{

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
		
		System.out.println();
	}
	
}
