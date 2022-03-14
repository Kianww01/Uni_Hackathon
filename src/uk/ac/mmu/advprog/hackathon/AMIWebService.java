package uk.ac.mmu.advprog.hackathon;
import static spark.Spark.get;
import static spark.Spark.notFound;
import static spark.Spark.port;

import org.json.JSONArray;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Handles the setting up and starting of the web service
 * You will be adding additional routes to this class, and it might get quite large
 * Feel free to distribute some of the work to additional child classes, like I did with DB
 * @author Kian Wilson 19069770
 */
public class AMIWebService {

	/**
	 * Main program entry point, starts the web service
	 * @param args not used
	 */
	public static void main(String[] args) {		
		port(8088);
		
		//Simple route so you can check things are working...
		//Accessible via http://localhost:8088/test in your browser
		get("/test", new Route() {
			@Override
			public Object handle(Request request, Response response) throws Exception {
				try (DB db = new DB()) {
					return "Number of Entries: " + db.getNumberOfEntries();
				}
			}			
		});
		
		// Task 1 - Route retrieves the last signal shown based on a provided signal ID
		// Accessible via http://localhost:8088/lastsignal?signal_id=?             ? = provided signal ID
		get("/lastsignal", new Route() {

			@Override
			public Object handle(Request request, Response response) throws Exception
			{
				try(DB db = new DB()){
					String signal_id = request.queryParams("signal_id");
					String lastSignal = db.getLastSignal(signal_id);
					
					if(signal_id == null) {
						return "no results";
					}
					
					// Ternary operator - if lastSignal is an empty string it will output the value on the left of the :, 
					// else it will output the value on the right
					return lastSignal.equals("") ? "no results" : "Last sign signal: " + lastSignal;
				}
			}		
		});
		
		// Task 2 - Route retrieves the frequency of all signal IDs on a provided motorway
		// Accessible via http://localhost:8088/frequency?motorway=?       ? = provided motorway 
		get("/frequency", new Route() {
			@Override
			public Object handle(Request request, Response response) throws Exception
			{
				String motorway = request.queryParams("motorway");
				
				try(DB db = new DB()){
					String JSON = db.getSignalFrequency(motorway);
					// Sets HTTP header to json for formatting purposes
					response.type("application/json");
					
					return motorway.equals("") ? new JSONArray() : JSON;
				}
			}		
		});
		
		// Task 3 Part A - Route retrieves the signal groups
		// Accessible via http://localhost:8088/groups
		get("/groups", new Route() {
			@Override
			public Object handle(Request request, Response response) throws Exception
			{
				try(DB db = new DB()){
					// Sets HTTP header to xml for formatting purposes
					response.type("application/xml");
					return db.getSignalGroups();
				}
			}
			
		});
		
		// Task 3 Part B - Route retrieves the signal id and value based on a provided group and datetime
		// Accessible via http://localhost:8088/signalsattime?group=?(1)&time=?(2)       ?(1) = provided group  ?(2) = provided datetime
		get("/signalsattime", new Route() {

			@Override
			public Object handle(Request request, Response response) throws Exception
			{
				String group = request.queryParams("group");
				String time = request.queryParams("time");
						
				// Sets HTTP header to xml for formatting purposes
				response.type("application/xml");
				if(time != null && group != null && Utilities.dateValidation(time) ) {
					try(DB db = new DB()){
						return db.getSignalsAtTime(group, time);
					}
				}
				else {
					return "<Signal/>";
				}
			}		
		});
		
		notFound("<html><head></head><body><h1>404 Not Found</h1></body></html>");
		System.out.println("Server up! Don't forget to kill the program when done!");
	}

}
