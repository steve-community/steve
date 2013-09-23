package de.rwth.idsg.steve.common;

public class Constants {	
	
	// Heartbeat interval in seconds
	public static final int HEARTBEAT_INTERVAL = 7200;
	// Determines how many hours the idtag should be stored in the local whitelist of a chargebox
	public static final int HOURS_TO_EXPIRE = 1;
	
	
	// Should SteVe take sensors into account while processing/creating messages?
	public static final boolean SENSORS_ENABLED =  false;
	// The endpoint address of the sensor for SOAP communication
	// Hardcoded now only for testing
	public static final String SENSOR_ENDPOINT_ADDRESS = "http://192.168.1.255";

}
