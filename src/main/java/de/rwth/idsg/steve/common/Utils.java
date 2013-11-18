package de.rwth.idsg.steve.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.GregorianCalendar;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.rwth.idsg.steve.html.ExceptionMessage;
import de.rwth.idsg.steve.html.InputException;

/**
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * 
 */
public class Utils {

	private static final Logger LOG = LoggerFactory.getLogger(Utils.class);
	private static DataSource dataSource = null;	
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
	 * Returns a connection from the JDBC pool.
	 * If the pool is not created yet, it first creates one.
	 */
	public static Connection getConnectionFromPool() {
		Connection con = null;
		try {
			if (dataSource == null) {
				Context initContext = new InitialContext();
				dataSource = (DataSource) initContext.lookup("java:comp/env/jdbc/stevedb");
				LOG.info("DB connection pool is opened.");
			}
			con = dataSource.getConnection();
		} catch (Exception e) {
			LOG.error("SQL exception", e);
		}
		return con;
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
			throw new InputException(ExceptionMessage.EXCEPTION_PARSING_DATETIME);
		}
		return dt;
	}
	
	/**
	 * Converts a Timestamp to a String of the pattern "yyyy-MM-dd HH:mm".
	 */
	public static String convertToString(Timestamp ts){
		long timeLong = ts.getTime();
		String st = inputFormatter.print(timeLong);
		return st;
	}

	/**
	 * Validates the BATCH execution of Data Manipulation Language (DML) statements, such as INSERT, UPDATE or DELETE.
	 * 
	 * If the row value in the updateCounts array is 0 or greater, the update was successfully executed.
	 * A value of SUCCESS_NO_INFO means update was successfully executed, but MySQL server unable to determine the number of rows affected.
	 * A value of EXECUTE_FAILED means that an error has occured.
	 */
	public static boolean validateDMLChanges(int [] updateCounts) {
		boolean updatedAll = false;

		for (int i = 0; i < updateCounts.length; i++) {
			if (updateCounts[i] >= 1) {
				updatedAll = true;
			} else if (updateCounts[i] == (PreparedStatement.SUCCESS_NO_INFO | PreparedStatement.EXECUTE_FAILED)) {
				updatedAll = false;
				break;
			}
		}
		return updatedAll;
	}

	/**
	 * Releases all resources and returns the DB connection back to the pool.
	 */
	public static void releaseResources(Connection con, PreparedStatement pt, ResultSet rs) {
		try {
			if (rs != null) rs.close();
			if (pt != null) pt.close();
			if (con != null) con.close();		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
