package uk.ac.mmu.advprog.hackathon;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Handles the JSON Array creation for task 2
 * @author Kian Wilson 19069770
 */
public class JSONMaker
{
	/**
	 * Creates a JSON Array which stores multiple JSON Objects, each JSON Object has two values of Value and Frequency
	 * @param signalFrequency An ArrayList of signalFrequency POJOs
	 * @return a String representation of a JSON array which has linked Values and Frequencies stored within it
	 */
	public static String makeJSON(ArrayList<SignalFrequency> signalFrequency) {
		JSONArray json = new JSONArray();
		
		for(SignalFrequency sf : signalFrequency) {
			JSONObject values = new JSONObject();
			values.put("Value", sf.getSignal());
			values.put("Frequency", sf.getFrequency());
			
			json.put(values);
		}
		
		return json.toString();
	}
}
