package de.rwth.idsg.steve.common;


/**
 * This class has the constant variables that are used by the OCPP service implementation.
 * These default values can be overwritten from the properties file, if one exists.
 * 
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 *  
 */
public class Constants {	
	// Current version of the application
	public static final String STEVE_VERSION = "1.0.0";	
	// Heartbeat interval in seconds
	public static int HEARTBEAT_INTERVAL = 14400;
	// Determines how many hours the idtag should be stored in the local whitelist of a chargebox
	public static int HOURS_TO_EXPIRE = 1;
}
