package honeycache;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Hcache {
	private static String driverName = "org.apache.hadoop.hive.jdbc.HiveDriver";

	/**
	 * @param args
	 * @throws SQLException
	 */
	public static void main(String[] args) throws SQLException {
		try {
			Class.forName(driverName);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		Connection conn = DriverManager.getConnection("jdbc:hive://localhost:10000/default", "", "");
		System.out.println("Connected to jdbc:hive://localhost:10000/default");
		System.out.println("-----------------------------------------------");
		
		Scanner input = new Scanner(System.in);
		System.out.print(new SimpleDateFormat("[HH:mm:ss]").format(new Date()) + "-hcache>");
		String command = input.nextLine().trim();
		while (!command.toLowerCase().equals("exit;")){
			
			if (command.endsWith(";")){
				//process multi_line_command
				ResultSet results = sendQueryToHive(conn, command.trim());
				ResultSetMetaData metadata = results.getMetaData();
				while (results.next()) {
					for(int i = 1; i <= metadata.getColumnCount(); i++){
						System.out.print(results.getString(i) + " | ");
					}
					System.out.println("");
				}

			}

			System.out.print(new SimpleDateFormat("[HH:mm:ss]").format(new Date()) + "-hcache>");
			command += " " + input.nextLine().trim();

			
		}
		
		input.close();
		System.out.println (" goodbye.");
/*
        Statement stmt = conn.createStatement();
		String tableName = "select * from testHiveDriverTable";
		stmt.executeQuery("drop table " + tableName);
		ResultSet res = stmt.executeQuery("create table " + tableName + " (key int, value string)");
		// show tables
		String sql = "show tables '" + tableName + "'";
		System.out.println("Running: " + sql);
		res = stmt.executeQuery(sql);
		if (res.next()) {
			System.out.println(res.getString(1));
		}
		// describe table
		sql = "describe " + tableName;
		System.out.println("Running: " + sql);
		res = stmt.executeQuery(sql);
		while (res.next()) {
			System.out.println(res.getString(1) + "\t" + res.getString(2));
		}

		// load data into table
		// NOTE: filepath has to be local to the hive server
		// NOTE: /tmp/a.txt is a ctrl-A separated file with two fields per line
		String filepath = "/tmp/a.txt";
		sql = "load data local inpath '" + filepath + "' into table " + tableName;
		System.out.println("Running: " + sql);
		res = stmt.executeQuery(sql);

		// select * query
		sql = "select * from " + tableName;
		System.out.println("Running: " + sql);
		res = stmt.executeQuery(sql);
		while (res.next()) {
			System.out.println(String.valueOf(res.getInt(1)) + "\t" + res.getString(2));
		}

		// regular hive query
		sql = "select count(1) from " + tableName;
		System.out.println("Running: " + sql);
		res = stmt.executeQuery(sql);
		while (res.next()) {
			System.out.println(res.getString(1));
		}*/
	}
	
	public static ResultSet sendQueryToHive(Connection con, String query) throws SQLException{
		query = query.replace(";", "");
		Statement stmt = con.createStatement();
		ResultSet res = stmt.executeQuery(query);
		// show tables
		return res;
	}
}