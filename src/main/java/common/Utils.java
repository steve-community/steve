package common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.GregorianCalendar;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {
	
	private static final Logger LOG = LoggerFactory.getLogger(Utils.class);
	private static DataSource dataSource = null;	
	private static DatatypeFactory factory;
	
	// DatatypeFactory.newInstance() is expensive. Generate one DatatypeFactory always to be used.
	static{
		try {
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
        	if (dataSource==null){
    			Context initContext = new InitialContext();
    			dataSource = (DataSource) initContext.lookup("java:comp/env/jdbc/cssdb");
    			LOG.info("DB connection pool is opened.");
        	}
			con = dataSource.getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return con;
    }
	
    /**
     * Returns the current date/time for dateTime (XML).
     */
	public static XMLGregorianCalendar getCurrentDateTimeXML(){
		// Returns a GregorianCalendar with the current date/time
		GregorianCalendar gc = new GregorianCalendar();
		// Converts to XML dateTime
		XMLGregorianCalendar xgc = factory.newXMLGregorianCalendar(gc);
		// Remove the ms information
		xgc.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);

		return xgc;
	}
	
    /**
     * Returns the current date/time in Timestamp (SQL).
     */
	public static Timestamp getCurrentDateTimeTS(){
		// Returns a GregorianCalendar with the current date/time
		GregorianCalendar gc = new GregorianCalendar();
		// Remove the ms information
		gc.set(GregorianCalendar.MILLISECOND, 0);
		// Converts to SQL Timestamp
		return new Timestamp(gc.getTimeInMillis());
	}
	

    /**
     * Sets the date/time for the whitelist of the chargebox to expire.
     */
	public static XMLGregorianCalendar setExpiryDateTime(int hours){
		// Returns a GregorianCalendar with the current date/time
		GregorianCalendar gc = new GregorianCalendar();
		// Add hours
		gc.add(GregorianCalendar.HOUR_OF_DAY, hours);
		// Converts to XML dateTime
		XMLGregorianCalendar xgc = factory.newXMLGregorianCalendar(gc);
		// remove the ms information
		xgc.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);

		return xgc;
	}

    /**
     * Converts XMLGregorianCalendar in Timestamp (SQL)
     */
	public static Timestamp convertToTimestamp(XMLGregorianCalendar xmlDateTime) {
		Timestamp newTimestamp = new Timestamp(xmlDateTime.toGregorianCalendar().getTimeInMillis());
		return newTimestamp;
	}
		
    /**
     * Converts a String to XMLGregorianCalendar.
     */
	public static XMLGregorianCalendar convertToXMLGregCal(String str){
		// Converts to XML dateTime
		return factory.newXMLGregorianCalendar(str);
	}	
	
    /**
     * Validates the execution of Data Manipulation Language (DML) statements, such as INSERT, UPDATE or DELETE.
     */
	public static boolean validateDMLChanges(int updateCount) {
		if (updateCount >= 1) {
			return true;
		}else{
			LOG.error("Execution of the changes failed!");
			return false;
		}
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
		
		for (int i = 1; i < updateCounts.length; i++) {
			if (updateCounts[i] >= 1) {
				updatedAll = true;
			} else if (updateCounts[i] == (PreparedStatement.SUCCESS_NO_INFO | PreparedStatement.EXECUTE_FAILED)) {
				updatedAll = false;
				LOG.error("Execution of the changes failed!");
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
