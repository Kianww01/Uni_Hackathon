package uk.ac.mmu.advprog.hackathon;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Handles database access from within your web service
 * @author Kian Wilson 19069770
 */
public class DB implements AutoCloseable {
	
	//allows us to easily change the database used
	private static final String JDBC_CONNECTION_STRING = "jdbc:sqlite:./data/AMI.db";
	
	//allows us to re-use the connection between queries if desired
	private Connection connection = null;
	
	/**
	 * Creates an instance of the DB object and connects to the database
	 */
	public DB() {
		try {
			connection = DriverManager.getConnection(JDBC_CONNECTION_STRING);
		}
		catch (SQLException sqle) {
			error(sqle);
		}
	}
	
	/**
	 * Returns the number of entries in the database, by counting rows
	 * @return The number of entries in the database, or -1 if empty
	 */
	public int getNumberOfEntries() {
		int result = -1;
		try {
			Statement s = connection.createStatement();
			ResultSet results = s.executeQuery("SELECT COUNT(*) AS count FROM ami_data");
			while(results.next()) { //will only execute once, because SELECT COUNT(*) returns just 1 number
				result = results.getInt(results.findColumn("count"));
			}
		}
		catch (SQLException sqle) {
			error(sqle);
			
		}
		return result;
	}
	
	/**
	 * Returns the last signal value registered on a given signal
	 * @param signal_id A String containing a signal id which is used within an SQL prepared statement for querying a database
	 * @return A string representation of SQL data gotten from running a prepared statement using signal_id as an input
	 */
	public String getLastSignal(String signal_id) {
		String result = "";
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT signal_value FROM ami_data\n"
					+ "WHERE signal_id = ?\n"
					+ "AND NOT signal_value = \"OFF\"\n"
					+ "AND NOT signal_value = \"NR\"\n"
					+ "AND NOT signal_value = \"BLNK\"\n"
					+ "ORDER BY datetime DESC\n"
					+ "LIMIT 1;\n");
			
			ps.setString(1, signal_id);
			ResultSet rs = ps.executeQuery();
			// Still needed even with "LIMIT 1" as an attempt to run rs.next() on an empty result set will throw an exception
			while(rs.next()) {
				result = rs.getString("signal_value");
			}
		}
		catch(SQLException sqle) {
			error(sqle);
		}		
		// Returns empty result string if no data is found for a given signal id
		return result;
	}
	
	/**
	 * Returns a string representation of a JSON Array which stores all of the matching Signal Values and Frequencies gotten from the SQL prepared statement
	 * @param motorway String which contains a motorway name, used within an SQL prepared statement for querying a database
	 * @return A string representation of a JSON Array which is gotten using the JSONMaker.makeJSON method
	 */
	public String getSignalFrequency(String motorway) {
		ArrayList<SignalFrequency> signalFrequency = new ArrayList<>();
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT\n"
					+ "COUNT(signal_value) AS frequency,\n"
					+ "signal_value\n"
					+ "FROM ami_data\n"
					+ "WHERE signal_id LIKE ? \n"
					+ "GROUP BY signal_value\n"
					+ "ORDER BY frequency DESC;");
			
			ps.setString(1,  motorway + "%");
			ResultSet results = ps.executeQuery();
			
			while(results.next()) {
				// Creates a new SignalFrequency POJO using the classes constructor
				// getString(2) = signal value      getString(1) = frequency
				signalFrequency.add(new SignalFrequency(results.getString(2), results.getString(1)));
			}
		}
		catch(SQLException sqle) {
			error(sqle);
		}
		return JSONMaker.makeJSON(signalFrequency);
	}
	
	/**
	 * Returns a string representation of an XMl document which stores all of the different signal groups from the database
	 * Each signal group is stored within a <group> tag, all of which are stored within a parent <groups> tag
	 * @return A string representation of an XML document which is gotten using the XMLMaker.getSignalGroups method
	 */
	public String getSignalGroups() {
		ArrayList<String> results = new ArrayList<String>();
		try {
			Statement s = connection.createStatement();
			// Distinct returns only one of each value (prevents duplicates)
			ResultSet rs = s.executeQuery("SELECT DISTINCT signal_group FROM ami_data");
			
			while(rs.next()) {
				results.add(rs.getString(1));
			}
		}
		catch(SQLException sqle) {
			error(sqle);
		}
		return XMLMaker.getSignalGroups(results);
	}
	
	/**
	 * Returns a string representation of an XML document which stores all of the signal id and values which match a given datetime
	 * @param group String which contains a group for use within a prepared SQL statement
	 * @param time String which contains a datetime for use within a prepared SQL statement
	 * @return A string representation of an XML document which is gotten using the XMLMaker.getSignalsByGroupTime method
	 */
	public String getSignalsAtTime(String group, String time) {
		ArrayList<GroupTime> groupTimes = new ArrayList<>();
		try {
			PreparedStatement s = connection.prepareStatement("SELECT datetime, signal_id, signal_value\n"
					+ "FROM ami_data\n"
					+ "WHERE\n"
					+ "signal_group = ?\n"
					+ "AND datetime < ?\n"
					+ "AND (datetime, signal_id) IN (\n"
					+ "SELECT MAX(datetime) AS datetime, signal_id\n"
					+ "FROM ami_data\n"
					+ "WHERE\n"
					+ "signal_group = ?\n"
					+ "AND datetime < ?\n"
					+ "GROUP BY signal_id\n"
					+ ")\n"
					+ "ORDER BY signal_id;\n");
			
			s.setString(1, group);
			s.setString(2, time);
			s.setString(3, group);
			s.setString(4, time);
			ResultSet rs= s.executeQuery();
			
			while(rs.next()) {
				// Creates a new GroupTime POJO using the classes constructor
				// getString(2) = signal id      getString(1) = datetime     getString(3) = signal value
				groupTimes.add(new GroupTime(rs.getString(2), rs.getString(1), rs.getString(3)));
			}
		}
		catch(SQLException sqle) {
			error(sqle);
		}	
		return XMLMaker.getSignalsByGroupTime(groupTimes);
	}
	
	/**
	 * Closes the connection to the database, required by AutoCloseable interface.
	 */
	@Override
	public void close() {
		try {
			if ( !connection.isClosed() ) {
				connection.close();
			}
		}
		catch(SQLException sqle) {
			error(sqle);
		}
	}

	/**
	 * Prints out the details of the SQL error that has occurred, and exits the programme
	 * @param sqle Exception representing the error that occurred
	 */
	private void error(SQLException sqle) {
		System.err.println("Problem Opening Database! " + sqle.getClass().getName());
		sqle.printStackTrace();
		System.exit(1);
	}
}
