package honeycache;

import honeycache.cli.HoneyCacheCLI;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Hcache {

	private static int port = 10000;
	private static String host = "localhost";
	private static String user = "";
	private static String password = "";
	private static String filename = null;
	private static String sqlStatement = null;

	/**
	 * @param args
	 * @throws SQLException
	 */
	public static void main(String[] args) {
		
		processArgs(args);
			
		HoneyCacheCLI hcache = new HoneyCacheCLI(host, port, user, password);	
		
		if (filename != null){
			//process filename;
		}else if (sqlStatement != null){
			//process sql and quit
			try {
				hcache.connect();
				hcache.printResults (hcache.processQuery(sqlStatement) );
				System.exit(0);
			} catch (SQLException e) {
				e.printStackTrace();
				System.exit(1);
			}
		} else {
			//start the command line
			try {
				hcache.startCommandLineInput();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			System.exit(0);
		}
		
		
		

	}

	public static void processArgs (String[] args){
		
		Options options = new Options();
		options.addOption("f", "file", true, "/path/to/hive.hsql file to run on hive.");
		options.addOption("e", "execute", true, "'INLINE SQL STATEMENT' to execute on hive.");
		options.addOption("p", "port", true, "hiveserver port to connect to. DEFAULT: 10000.");
		options.addOption("h", "host", true, "hiveserver host to connect to. DEFAULT: localhost.");
		options.addOption("u", "user", true, "username to connect to hive with.");
		options.addOption("p", "passwd",true, "password of user to connect to hive with.");
		options.addOption("h", "help", false, "prints this help message.");
		CommandLine my_args = null;
		
		try {
			my_args = new BasicParser().parse(options, args);	
		} catch (ParseException e) {
			System.out.println( "Bad Argument Values:" + e.getMessage() );
			System.exit(1);
		}
		
		if (my_args.hasOption("help")){
			new HelpFormatter().printHelp( "hcache", options );
			System.out.println();
			System.exit(0);
		}
		if (my_args.hasOption("port"))
			port = Integer.parseInt(my_args.getOptionValue("port"));
		
		if (my_args.hasOption("host"))
			host = my_args.getOptionValue("host");
			
		if (my_args.hasOption("user"))
			user = my_args.getOptionValue("user");
						
		if (my_args.hasOption("passwd"))
			password = my_args.getOptionValue("passwd");
		
		if (my_args.hasOption("file"))
			filename = my_args.getOptionValue("file");
		
		if (my_args.hasOption("execute"))
			sqlStatement = my_args.getOptionValue("execute").trim();
		
		
	}
	

}