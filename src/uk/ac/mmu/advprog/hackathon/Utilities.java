package uk.ac.mmu.advprog.hackathon;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Handles necessary utility, in this case a dateTime validator
 * @author Kian Wilson 19069770
 */
public class Utilities
{
	/**
	 * Date Validator, attempts to parse the provided date to determine if it is a valid date.
	 * @param date - String containing a date to be validated
	 * @return true if valid date is provided, otherwise returns false
	 */
	public static boolean dateValidation(String date) {
		try {
			 DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			 LocalDateTime.parse(date, dateTimeFormatter);
			 return true;
		}catch(DateTimeParseException e){
			return false;
		}
	}
}
