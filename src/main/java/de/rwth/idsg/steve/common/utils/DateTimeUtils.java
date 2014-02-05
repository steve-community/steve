package de.rwth.idsg.steve.common.utils;

import java.sql.Timestamp;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import de.rwth.idsg.steve.html.ExceptionMessage;
import de.rwth.idsg.steve.html.InputException;

/**
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * 
 */
public class DateTimeUtils {

	private static DatatypeFactory factory;
	private static DateTimeFormatter noMilliFormatter = ISODateTimeFormat.dateTimeNoMillis();
	private static DateTimeFormatter inputFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
	
//	private static DateTimeParser[] parsers = { 
//			DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").getParser(),
//			DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").getParser() };
//	private static DateTimeFormatter inputFormatter = new DateTimeFormatterBuilder().append(null,parsers).toFormatter();
	
	static{
		try {
			// DatatypeFactory.newInstance() is expensive. Generate one DatatypeFactory always to be used.
			factory = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException("Unable to create an XML datatype factory", e);
		}
	}

	/**
	 * Returns the current date/time for dateTime (XML).
	 */
	public static XMLGregorianCalendar getCurrentDateTimeXML(){
		DateTime dt = new DateTime();
		String st = noMilliFormatter.print(dt);
		return factory.newXMLGregorianCalendar(st);
	}

	/**
	 * Returns the current date/time in Timestamp (SQL).
	 */
	public static Timestamp getCurrentDateTimeTS(){
		DateTime dt = new DateTime();
		return new Timestamp(dt.getMillis());
	}

	/**
	 * Sets the date/time for the whitelist of the chargebox to expire.
	 */
	public static XMLGregorianCalendar setExpiryDateTime(int hours){		
		DateTime dt = new DateTime().plusHours(hours);
		String st = noMilliFormatter.print(dt);
		return factory.newXMLGregorianCalendar(st);
	}

	/**
	 * Converts XMLGregorianCalendar in Timestamp (SQL)
	 */
	public static Timestamp convertToTimestamp(XMLGregorianCalendar xmlDateTime) {
		GregorianCalendar gc = xmlDateTime.toGregorianCalendar();
		Timestamp ts = new Timestamp(gc.getTimeInMillis());
		return ts;
	}
	
	/**
	 * Converts String in Timestamp (SQL)
	 */
	public static Timestamp convertToTimestamp(String str) {
		DateTime dt = convertToDateTime(str);
		Timestamp ts = new Timestamp(dt.getMillis());
		return ts;
	}
	
	/**
	 * Converts a DateTime to XMLGregorianCalendar.
	 */
	public static XMLGregorianCalendar convertToXMLGregCal(DateTime dt){
		String st = noMilliFormatter.print(dt);
		return factory.newXMLGregorianCalendar(st);
	}

	/**
	 * Converts a String to XMLGregorianCalendar.
	 */
	public static XMLGregorianCalendar convertToXMLGregCal(String str){
		DateTime dt = convertToDateTime(str);
		String st = noMilliFormatter.print(dt);
		return factory.newXMLGregorianCalendar(st);
	}
	
	/**
	 * Converts a String of pattern "yyyy-MM-dd HH:mm" to DateTime.
	 */
	public static DateTime convertToDateTime(String str){
		DateTime dt = null;
		try {
			dt = inputFormatter.parseDateTime(str);
		} catch (IllegalArgumentException e) {
			throw new InputException(ExceptionMessage.PARSING_DATETIME);
		}
		return dt;
	}
	
	/**
	 * Converts a Timestamp to a String of the pattern "yyyy-MM-dd 'at' HH:mm".
	 */
	public static String convertToString(Timestamp ts){
		if (ts == null) return "";
		
		long timeLong = ts.getTime();
		String st = DateTimeFormat.forPattern("yyyy-MM-dd 'at' HH:mm").print(timeLong);
		return st;
	}
	
	/**
	 * Print the date/time nicer, if it's from today, yesterday or tomorrow.
	 */
	public static String humanize(Timestamp ts){
		if (ts == null) return "";
		
		DateTime input = new DateTime(ts);
		DateTime now = new DateTime();
				
		String result;
		
		// Equalize time fields before comparing date fields
		DateTime inputAtMidnight = input.withTimeAtStartOfDay();
		DateTime todayAtMidnight = now.withTimeAtStartOfDay();
		
		// Is it today?
		if (inputAtMidnight.equals(todayAtMidnight)) {
			result = "Today at " + DateTimeFormat.forPattern("HH:mm").print(input);
			
//			PeriodFormatter pf = new PeriodFormatterBuilder()
//					.printZeroNever()
//					.appendHours().appendSuffix(" hour ", " hours ")
//					.appendMinutes().appendSuffix(" minute ", " minutes ")
//					.toFormatter();
//			
//			String elapsed = pf.print(new Period(input, now));			
//			if (elapsed.length() == 0) elapsed = "Less than a minute ";			
//			result.append(elapsed).append(" ago");				
		
		// Is it yesterday?
		} else if (inputAtMidnight.equals(todayAtMidnight.minusDays(1))) {				
			result = "Yesterday at " + DateTimeFormat.forPattern("HH:mm").print(input);
			
		// Is it tomorrow?
		} else if (inputAtMidnight.equals(todayAtMidnight.plusDays(1))) {				
			result = "Tomorrow at " + DateTimeFormat.forPattern("HH:mm").print(input);
			
		// So long ago OR in the future...
		} else {
			result = convertToString(ts);
		}
		
		return result;
	}
}