package de.rwth.idsg.steve.html;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ocpp.cp._2010._08.ChangeAvailabilityRequest;
import ocpp.cp._2010._08.ChangeConfigurationRequest;
import ocpp.cp._2010._08.ClearCacheRequest;
import ocpp.cp._2010._08.GetDiagnosticsRequest;
import ocpp.cp._2010._08.RemoteStartTransactionRequest;
import ocpp.cp._2010._08.RemoteStopTransactionRequest;
import ocpp.cp._2010._08.ResetRequest;
import ocpp.cp._2010._08.UnlockConnectorRequest;
import ocpp.cp._2010._08.UpdateFirmwareRequest;
import de.rwth.idsg.steve.ChargePointService12_Client;
import de.rwth.idsg.steve.common.ClientDBAccess;


/**
 * This servlet provides the Web interface to manage charging points with OCPP v1.2
 * 
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 */
public class ServletOperationsV12 extends HttpServlet {

	private static final long serialVersionUID = 8576766110806723303L;
	String contextPath, servletPath;
	HashMap<String,String> chargePointsList;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		// Get the request details
		String command = request.getPathInfo();				
		contextPath = request.getContextPath();
		servletPath = request.getServletPath();			

		if (command == null || command.length() == 0) {
			// Only refresh the list of charge points when displaying operations page
			chargePointsList = ClientDBAccess.getChargePoints("1.2");
			// Redirect to the page of the first operation
			response.sendRedirect(contextPath + servletPath + "/ChangeAvailability");
			return;
		}
				
		StringBuilder mainBuilder = new StringBuilder(Common.printHead(contextPath));
				
		if (command.equals("/ChangeAvailability")){	
			mainBuilder.append(printChangeAvail());

		} else if (command.equals("/ChangeConfiguration")){
			mainBuilder.append(printChangeConf());

		} else if (command.equals("/ClearCache")){
			mainBuilder.append(printClearCache());

		} else if (command.equals("/GetDiagnostics")){		
			mainBuilder.append(printGetDiagnostics());

		} else if (command.equals("/RemoteStartTransaction")){		
			mainBuilder.append(printRemoteStartTrans());

		} else if (command.equals("/RemoteStopTransaction")){			
			mainBuilder.append(printRemoteStopTrans());

		} else if (command.equals("/Reset")){			
			mainBuilder.append(printReset());

		} else if (command.equals("/UnlockConnector")){			
			mainBuilder.append(printUnlockConnector());

		} else if (command.equals("/UpdateFirmware")){		
			mainBuilder.append(printUpdateFirmware());
		}

		mainBuilder.append(Common.printFoot(contextPath));
		
		response.setContentType("text/html");
		PrintWriter writer = response.getWriter();
		writer.write(mainBuilder.toString());
		writer.close();	
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		String command = request.getPathInfo();	

		// Retrieve values from HTML select multiple
		String[] chargePointItems = request.getParameterValues("cp_items");

		if (chargePointItems == null) {
			throw new InputException(Common.EXCEPTION_CHARGEPOINTS_NULL);	
		}
		
		StringBuilder returnBuilder = null;

		if (command.equals("/ChangeAvailability")){
			returnBuilder = processChangeAvail(request, chargePointItems);

		} else if (command.equals("/ChangeConfiguration")) {
			returnBuilder = processChangeConf(request, chargePointItems);

		} else if (command.equals("/ClearCache")) {
			returnBuilder = processClearCache(chargePointItems);

		} else if (command.equals("/GetDiagnostics")) {			
			returnBuilder = processGetDiagnostics(request, chargePointItems);

		} else if (command.equals("/RemoteStartTransaction")) {
			returnBuilder = processRemoteStartTrans(request, chargePointItems);

		} else if (command.equals("/RemoteStopTransaction")) {
			returnBuilder = processRemoteStopTrans(request, chargePointItems);

		} else if (command.equals("/Reset")){			
			returnBuilder = processReset(request, chargePointItems);

		} else if (command.equals("/UnlockConnector")) {
			returnBuilder = processUnlockConnector(request, chargePointItems);

		} else if (command.equals("/UpdateFirmware")){
			returnBuilder = processUpdateFirmware(request, chargePointItems);
		}
		
		response.setContentType("text/plain");
		PrintWriter writer = response.getWriter();
		writer.write(returnBuilder.toString());
		writer.close();	
	}
	
	/////// HTTP GET: Print HTML /////// 

	private String printChangeAvail() {
		return				
		// Print the menu div on the left 
		"<div class=\"op-menu\">\n"
		+ "<ul>\n"
		+ "<li><a class=\"highlight\" href=\"" + contextPath + servletPath + "/ChangeAvailability\" >Change Availability</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/ChangeConfiguration\">Change Configuration</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/ClearCache\">Clear Cache</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/GetDiagnostics\">Get Diagnostics</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/RemoteStartTransaction\">Remote Start Transaction</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/RemoteStopTransaction\">Remote Stop Transaction</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/Reset\">Reset</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/UnlockConnector\">Unlock Connector</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/UpdateFirmware\">Update Firmware</a></li>\n"
		+ "</ul>\n"
		+ "</div>\n"

		// Print the div on the right
		+ "<div class=\"op-content\">\n<form method=\"POST\" action=\"" + contextPath + servletPath + "/ChangeAvailability\">\n" 				
		+ Common.printChargePointsMultipleSelect(chargePointsList, "1.2")	
		+ "<h3><span>Parameters</span></h3>\n"
		+ "<table>\n"
		+ "<tr><td>Connector Id (integer):</td><td><input type=\"number\" min=\"0\" name=\"connectorId\" placeholder=\"if empty, 0 = charge point as a whole\"></td></tr>\n"
		+ "<tr><td>Availability Type:</td><td><input type=\"radio\" name=\"availType\" value=\"Inoperative\" checked> Inoperative</td></tr>\n"
		+ "<tr><td></td><td><input type=\"radio\" name=\"availType\" value=\"Operative\"> Operative</td></tr>\n"
		+ "</table>\n"
		+ "<div class=\"submit-button\"><input type=\"submit\" value=\"Perform\"></div>\n"
		+ "</form>\n</div>\n";
	}

	private String printChangeConf() {
		return
		// Print the menu div on the left 
		"<div class=\"op-menu\">\n"
		+ "<ul>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/ChangeAvailability\" >Change Availability</a></li>\n"
		+ "<li><a class=\"highlight\" href=\"" + contextPath + servletPath + "/ChangeConfiguration\">Change Configuration</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/ClearCache\">Clear Cache</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/GetDiagnostics\">Get Diagnostics</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/RemoteStartTransaction\">Remote Start Transaction</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/RemoteStopTransaction\">Remote Stop Transaction</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/Reset\">Reset</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/UnlockConnector\">Unlock Connector</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/UpdateFirmware\">Update Firmware</a></li>\n"
		+ "</ul>\n"
		+ "</div>\n"

		// Print the div on the right
		+ "<div class=\"op-content\">\n<form method=\"POST\" action=\"" + contextPath + servletPath + "/ChangeConfiguration\">\n" 
		+ Common.printChargePointsMultipleSelect(chargePointsList, "1.2")	
		+ "<h3><span>Parameters</span></h3>\n"
		+ "<table>\n"
		+ "<tr><td>Configuration key:</td><td>\n"
		+ "<select name=\"confKey\">\n"
		+ "<option value=\"HeartBeatInterval\">HeartBeatInterval (in seconds)</option>\n"
		+ "<option value=\"ConnectionTimeOut\">ConnectionTimeOut (in seconds)</option>\n"
		+ "<option value=\"ProximityContactRetries\">ProximityContactRetries (in times)</option>\n"
		+ "<option value=\"ProximityLockRetries\">ProximityLockRetries (in times)</option>\n"
		+ "<option value=\"ResetRetries\">ResetRetries (in times)</option>\n"
		+ "<option value=\"BlinkRepeat\">BlinkRepeat (in times)</option>\n"
		+ "<option value=\"LightIntensity\">LightIntensity (in %)</option>\n"
		+ "<option value=\"ChargePointId\">ChargePointId (string)</option>\n"
		+ "<option value=\"MeterValueSampleInterval\">MeterValueSampleInterval (in seconds)</option>\n"
		+ "</select>\n"
		+ "</td></tr>\n"
		+ "<tr><td>Value:</td><td><input type=\"text\" name=\"value\"></td></tr>\n"
		+ "</table>\n"
		+ "<div class=\"submit-button\"><input type=\"submit\" value=\"Perform\"></div>\n"
		+ "</form>\n</div>\n";
	}

	private String printClearCache() {		
		return				
		// Print the menu div on the left 
		"<div class=\"op-menu\">\n"
		+ "<ul>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/ChangeAvailability\" >Change Availability</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/ChangeConfiguration\">Change Configuration</a></li>\n"
		+ "<li><a class=\"highlight\" href=\"" + contextPath + servletPath + "/ClearCache\">Clear Cache</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/GetDiagnostics\">Get Diagnostics</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/RemoteStartTransaction\">Remote Start Transaction</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/RemoteStopTransaction\">Remote Stop Transaction</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/Reset\">Reset</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/UnlockConnector\">Unlock Connector</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/UpdateFirmware\">Update Firmware</a></li>\n"
		+ "</ul>\n"
		+ "</div>\n"

		// Print the div on the right
		+ "<div class=\"op-content\">\n<form method=\"POST\" action=\"" + contextPath + servletPath + "/ClearCache\">\n" 
		+ Common.printChargePointsMultipleSelect(chargePointsList, "1.2")	
		+ "<h3><span>Parameters</span></h3>\n"
		+ "<center><i>No parameters required.</i></center>"
		+ "<div class=\"submit-button\"><input type=\"submit\" value=\"Perform\"></div>\n"
		+ "</form>\n</div>\n";			
	}

	private String printGetDiagnostics() {
		return				
		// Print the menu div on the left 
		"<div class=\"op-menu\">\n"
		+ "<ul>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/ChangeAvailability\" >Change Availability</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/ChangeConfiguration\">Change Configuration</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/ClearCache\">Clear Cache</a></li>\n"
		+ "<li><a class=\"highlight\" href=\"" + contextPath + servletPath + "/GetDiagnostics\">Get Diagnostics</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/RemoteStartTransaction\">Remote Start Transaction</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/RemoteStopTransaction\">Remote Stop Transaction</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/Reset\">Reset</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/UnlockConnector\">Unlock Connector</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/UpdateFirmware\">Update Firmware</a></li>\n"
		+ "</ul>\n"
		+ "</div>\n"

		// Print the div on the right
		+ "<div class=\"op-content\">\n<form method=\"POST\" action=\"" + contextPath + servletPath + "/GetDiagnostics\">\n" 
		+ Common.printChargePointsMultipleSelect(chargePointsList, "1.2")	
		+ "<h3><span>Parameters</span></h3>\n"
		+ "<table>\n"
		+ "<tr><td>Location (directory URI):</td><td><input type=\"text\" name=\"location\"></td></tr>\n"		
		+ "<tr><td>Retries (integer):</td><td><input type=\"number\" min=\"0\" name=\"retries\"></td></tr>\n"
		+ "<tr><td>Retry Interval (integer):</td><td><input type=\"number\" min=\"0\" name=\"retryInterval\"></td></tr>\n"
		+ "<tr><td>Start time (ex: 2011-12-21 11:33):</td><td><input type=\"datetime\" name=\"startTime\"></td></tr>\n"
		+ "<tr><td>Stop time (ex: 2011-12-21 11:33):</td><td><input type=\"datetime\" name=\"stopTime\"></td></tr>\n"
		+ "</table>\n"
		+ "<div class=\"submit-button\"><input type=\"submit\" value=\"Perform\"></div>\n"
		+ "</form>\n</div>\n";
	}

	private String printRemoteStartTrans() {
		return
		// Print the menu div on the left 
		"<div class=\"op-menu\">\n"
		+ "<ul>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/ChangeAvailability\" >Change Availability</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/ChangeConfiguration\">Change Configuration</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/ClearCache\">Clear Cache</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/GetDiagnostics\">Get Diagnostics</a></li>\n"
		+ "<li><a class=\"highlight\" href=\"" + contextPath + servletPath + "/RemoteStartTransaction\">Remote Start Transaction</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/RemoteStopTransaction\">Remote Stop Transaction</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/Reset\">Reset</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/UnlockConnector\">Unlock Connector</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/UpdateFirmware\">Update Firmware</a></li>\n"
		+ "</ul>\n"
		+ "</div>\n"

		// Print the div on the right
		+ "<div class=\"op-content\">\n<form method=\"POST\" action=\"" + contextPath + servletPath + "/RemoteStartTransaction\">\n" 
		+ Common.printChargePointsSingleSelect(chargePointsList, "1.2")	
		+ "<h3><span>Parameters</span></h3>\n"
		+ "<table>\n"
		+ "<tr><td>Connector Id (integer, not 0):</td><td><input type=\"number\" min=\"1\" name=\"connectorId\"></td></tr>\n"
		+ "<tr><td>idTag (string):</td><td><input type=\"text\" name=\"idTag\"></td></tr>\n"
		+ "</table>\n"
		+ "<div class=\"submit-button\"><input type=\"submit\" value=\"Perform\"></div>\n"
		+ "</form>\n</div>\n";
	}

	private String printRemoteStopTrans() {
		return
		// Print the menu div on the left 
		"<div class=\"op-menu\">\n"
		+ "<ul>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/ChangeAvailability\" >Change Availability</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/ChangeConfiguration\">Change Configuration</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/ClearCache\">Clear Cache</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/GetDiagnostics\">Get Diagnostics</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/RemoteStartTransaction\">Remote Start Transaction</a></li>\n"
		+ "<li><a class=\"highlight\" href=\"" + contextPath + servletPath + "/RemoteStopTransaction\">Remote Stop Transaction</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/Reset\">Reset</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/UnlockConnector\">Unlock Connector</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/UpdateFirmware\">Update Firmware</a></li>\n"
		+ "</ul>\n"
		+ "</div>\n"

		// Print the div on the right
		+ "<div class=\"op-content\">\n<form method=\"POST\" action=\"" + contextPath + servletPath + "/RemoteStopTransaction\">\n" 
		+ Common.printChargePointsSingleSelect(chargePointsList, "1.2")	
		+ "<h3><span>Parameters</span></h3>\n"
		+ "<table>\n"
		+ "<tr><td>Transaction Id (integer):</td><td><input type=\"number\" name=\"transactionId\"></td></tr>\n"	
		+ "</table>\n"
		+ "<div class=\"submit-button\"><input type=\"submit\" value=\"Perform\"></div>\n"
		+ "</form>\n</div>\n";
	}

	private String printReset() {
		return				
		// Print the menu div on the left 
		"<div class=\"op-menu\">\n"
		+ "<ul>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/ChangeAvailability\" >Change Availability</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/ChangeConfiguration\">Change Configuration</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/ClearCache\">Clear Cache</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/GetDiagnostics\">Get Diagnostics</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/RemoteStartTransaction\">Remote Start Transaction</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/RemoteStopTransaction\">Remote Stop Transaction</a></li>\n"
		+ "<li><a class=\"highlight\" href=\"" + contextPath + servletPath + "/Reset\">Reset</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/UnlockConnector\">Unlock Connector</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/UpdateFirmware\">Update Firmware</a></li>\n"
		+ "</ul>\n"
		+ "</div>\n"

		// Print the div on the right
		+ "<div class=\"op-content\">\n<form method=\"POST\" action=\"" + contextPath + servletPath + "/Reset\">\n" 
		+ Common.printChargePointsMultipleSelect(chargePointsList, "1.2")	
		+ "<h3><span>Parameters</span></h3>\n"
		+ "<table>\n"
		+ "<tr><td>Reset Type:</td><td><input type=\"radio\" name=\"resetType\" value=\"Hard\" checked> Hard</td></tr>\n"
		+ "<tr><td></td><td><input type=\"radio\" name=\"resetType\" value=\"Soft\"> Soft</td></tr>\n"		
		+ "</table>\n"
		+ "<div class=\"submit-button\"><input type=\"submit\" value=\"Perform\"></div>\n"
		+ "</form>\n</div>\n";
	}

	private String printUnlockConnector() {
		return				
		// Print the menu div on the left 
		"<div class=\"op-menu\">\n"
		+ "<ul>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/ChangeAvailability\" >Change Availability</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/ChangeConfiguration\">Change Configuration</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/ClearCache\">Clear Cache</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/GetDiagnostics\">Get Diagnostics</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/RemoteStartTransaction\">Remote Start Transaction</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/RemoteStopTransaction\">Remote Stop Transaction</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/Reset\">Reset</a></li>\n"
		+ "<li><a class=\"highlight\" href=\"" + contextPath + servletPath + "/UnlockConnector\">Unlock Connector</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/UpdateFirmware\">Update Firmware</a></li>\n"
		+ "</ul>\n"
		+ "</div>\n"

		// Print the div on the right
		+ "<div class=\"op-content\">\n<form method=\"POST\" action=\"" + contextPath + servletPath + "/UnlockConnector\">\n" 
		+ Common.printChargePointsSingleSelect(chargePointsList, "1.2")	
		+ "<h3><span>Parameters</span></h3>\n"
		+ "<table>\n"
		+ "<tr><td>Connector Id (integer, not 0):</td><td><input type=\"number\" min=\"1\" name=\"connectorId\"></td></tr>\n"	
		+ "</table>\n"
		+ "<div class=\"submit-button\"><input type=\"submit\" value=\"Perform\"></div>\n"
		+ "</form>\n</div>\n";
	}

	private String printUpdateFirmware() {
		return				
		// Print the menu div on the left 
		"<div class=\"op-menu\">\n"
		+ "<ul>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/ChangeAvailability\" >Change Availability</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/ChangeConfiguration\">Change Configuration</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/ClearCache\">Clear Cache</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/GetDiagnostics\">Get Diagnostics</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/RemoteStartTransaction\">Remote Start Transaction</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/RemoteStopTransaction\">Remote Stop Transaction</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/Reset\">Reset</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/UnlockConnector\">Unlock Connector</a></li>\n"
		+ "<li><a class=\"highlight\" href=\"" + contextPath + servletPath + "/UpdateFirmware\">Update Firmware</a></li>\n"
		+ "</ul>\n"
		+ "</div>\n"

		// Print the div on the right
		+ "<div class=\"op-content\">\n<form method=\"POST\" action=\"" + contextPath + servletPath + "/UpdateFirmware\">\n" 
		+ Common.printChargePointsMultipleSelect(chargePointsList, "1.2")	
		+ "<h3><span>Parameters</span></h3>\n"
		+ "<table>\n"
		+ "<tr><td>Location (URI):</td><td><input type=\"text\" name=\"location\"></td></tr>\n"
		+ "<tr><td>Retries (integer):</td><td><input type=\"number\" min=\"0\" name=\"retries\"></td></tr>\n"
		+ "<tr><td>Retry Interval (integer):</td><td><input type=\"number\" min=\"0\" name=\"retryInterval\"></td></tr>\n"
		+ "<tr><td>Retrieve Date (ex: 2011-12-21 11:33):</td><td><input type=\"datetime\" name=\"retrieveDate\"></td></tr>\n"
		+ "</table>\n"
		+ "<div class=\"submit-button\"><input type=\"submit\" value=\"Perform\"></div>\n"
		+ "</form>\n</div>\n";
	}
	
	/////// HTTP POST: Process Request /////// 
		
	// chargePointItem[0] : chargebox id
	// chargePointItem[1] : endpoint (IP) address
	
	private StringBuilder processChangeAvail(HttpServletRequest request, String[] chargePointItems) {
		String connectorIdSTR = request.getParameter("connectorId");
		int connectorId;
		if (connectorIdSTR.isEmpty()) {
			connectorId = 0;
		} else {
			try {
				connectorId = Integer.parseInt(request.getParameter("connectorId"));	
			} catch (NumberFormatException e) {
				throw new InputException(Common.EXCEPTION_PARSING_NUMBER);
			}
		}
		String availType = request.getParameter("availType");
		
		ChargePointService12_Client cpsClient = new ChargePointService12_Client();
		ChangeAvailabilityRequest req = cpsClient.prepareChangeAvailability(connectorId, availType);

		StringBuilder builder = new StringBuilder();
		String result;
		for (String temp: chargePointItems) {
			String[] chargePointItem = temp.split(";");
			result = cpsClient.sendChangeAvailability(chargePointItem[0], chargePointItem[1], req);
			builder.append(result + "\n");
		}
		return builder;
	}
	
	private StringBuilder processChangeConf(HttpServletRequest request, String[] chargePointItems) {
		String confKey = request.getParameter("confKey");
		String value = request.getParameter("value");
		if (value.isEmpty()) {
			throw new InputException(Common.EXCEPTION_INPUT_EMPTY);
		}
		
		ChargePointService12_Client cpsClient = new ChargePointService12_Client();
		ChangeConfigurationRequest req = cpsClient.prepareChangeConfiguration(confKey, value);

		StringBuilder builder = new StringBuilder();
		String result;
		for (String temp: chargePointItems) {
			String[] chargePointItem = temp.split(";");
			result = cpsClient.sendChangeConfiguration(chargePointItem[0], chargePointItem[1], req);
			builder.append(result + "\n");
		}
		return builder;
	}
	
	private StringBuilder processClearCache(String[] chargePointItems) {
		ChargePointService12_Client cpsClient = new ChargePointService12_Client();
		ClearCacheRequest req = cpsClient.prepareClearCache();

		StringBuilder builder = new StringBuilder();
		String result;
		for (String temp: chargePointItems) {
			String[] chargePointItem = temp.split(";");
			result = cpsClient.sendClearCache(chargePointItem[0], chargePointItem[1], req);
			builder.append(result + "\n");
		}
		return builder;
	}
	
	private StringBuilder processGetDiagnostics(HttpServletRequest request, String[] chargePointItems) {
		String location = request.getParameter("location");
		String retriesSTR = request.getParameter("retries");
		String retryIntervalSTR = request.getParameter("retryInterval");
		String startTime = request.getParameter("startTime");
		String stopTime = request.getParameter("stopTime");
		
		if (location.isEmpty()
				|| retriesSTR.isEmpty() 
				|| retryIntervalSTR.isEmpty()
				|| startTime.isEmpty()
				|| stopTime.isEmpty()){
			throw new InputException(Common.EXCEPTION_INPUT_EMPTY);
		}			
		
		int retries;
		int retryInterval;			
		try {
			retries = Integer.parseInt(retriesSTR);
			retryInterval = Integer.parseInt(retryIntervalSTR);				
		} catch (NumberFormatException e) {
			throw new InputException(Common.EXCEPTION_PARSING_NUMBER);
		}	
		
		ChargePointService12_Client cpsClient = new ChargePointService12_Client();
		GetDiagnosticsRequest req = cpsClient.prepareGetDiagnostics(location, retries, retryInterval, startTime, stopTime);

		StringBuilder builder = new StringBuilder();
		String result;
		for (String temp: chargePointItems) {
			String[] chargePointItem = temp.split(";");
			result = cpsClient.sendGetDiagnostics(chargePointItem[0], chargePointItem[1], req);
			builder.append(result + "\n");
		}
		return builder;
	}
	
	private StringBuilder processRemoteStartTrans(HttpServletRequest request, String[] chargePointItems) {
		String connectorIdSTR = request.getParameter("connectorId");
		if (connectorIdSTR.isEmpty()) {
			throw new InputException(Common.EXCEPTION_INPUT_EMPTY);
		}
		
		int connectorId;			
		try {
			connectorId = Integer.parseInt(connectorIdSTR);	
		} catch (NumberFormatException e) {
			throw new InputException(Common.EXCEPTION_PARSING_NUMBER);
		}
		
		if (connectorId == 0) {
			throw new InputException(Common.EXCEPTION_INPUT_ZERO);
		}
		
		String idTag = request.getParameter("idTag");
		if (idTag.isEmpty()) {
			throw new InputException(Common.EXCEPTION_INPUT_EMPTY);
		}
		
		ChargePointService12_Client cpsClient = new ChargePointService12_Client();
		RemoteStartTransactionRequest req = cpsClient.prepareRemoteStartTransaction(connectorId, idTag);

		StringBuilder builder = new StringBuilder();
		String result;
		for (String temp: chargePointItems) {
			String[] chargePointItem = temp.split(";");
			result = cpsClient.sendRemoteStartTransaction(chargePointItem[0], chargePointItem[1], req);
			builder.append(result + "\n");
		}
		return builder;
	}
	
	private StringBuilder processRemoteStopTrans(HttpServletRequest request, String[] chargePointItems) {
		String transactionIdSTR = request.getParameter("transactionId");
		if (transactionIdSTR.isEmpty()) {
			throw new InputException(Common.EXCEPTION_INPUT_EMPTY);
		}
		
		int transactionId;			
		try {
			transactionId = Integer.parseInt(transactionIdSTR);
		} catch (NumberFormatException e) {
			throw new InputException(Common.EXCEPTION_PARSING_NUMBER);
		}
		
		ChargePointService12_Client cpsClient = new ChargePointService12_Client();
		RemoteStopTransactionRequest req = cpsClient.prepareRemoteStopTransaction(transactionId);

		StringBuilder builder = new StringBuilder();
		String result;
		for (String temp: chargePointItems) {
			String[] chargePointItem = temp.split(";");
			result = cpsClient.sendRemoteStopTransaction(chargePointItem[0], chargePointItem[1], req);
			builder.append(result + "\n");
		}
		return builder;
	}
	
	private StringBuilder processReset(HttpServletRequest request, String[] chargePointItems) {
		String resetType = request.getParameter("resetType");
		
		ChargePointService12_Client cpsClient = new ChargePointService12_Client();
		ResetRequest req = cpsClient.prepareReset(resetType);

		StringBuilder builder = new StringBuilder();
		String result;
		for (String temp: chargePointItems) {
			String[] chargePointItem = temp.split(";");
			result = cpsClient.sendReset(chargePointItem[0], chargePointItem[1], req);
			builder.append(result + "\n");
		}
		return builder;
	}
	
	private StringBuilder processUnlockConnector(HttpServletRequest request, String[] chargePointItems) {
		String connectorIdSTR = request.getParameter("connectorId");
		if (connectorIdSTR.isEmpty()) {
			throw new InputException(Common.EXCEPTION_INPUT_EMPTY);
		}
		
		int connectorId;			
		try {
			connectorId = Integer.parseInt(connectorIdSTR);	
		} catch (NumberFormatException e) {
			throw new InputException(Common.EXCEPTION_PARSING_NUMBER);
		}
		
		if (connectorId == 0) {
			throw new InputException(Common.EXCEPTION_INPUT_ZERO);
		}
		
		ChargePointService12_Client cpsClient = new ChargePointService12_Client();
		UnlockConnectorRequest req = cpsClient.prepareUnlockConnector(connectorId);

		StringBuilder builder = new StringBuilder();
		String result;
		for (String temp: chargePointItems) {
			String[] chargePointItem = temp.split(";");
			result = cpsClient.sendUnlockConnector(chargePointItem[0], chargePointItem[1], req);
			builder.append(result + "\n");
		}
		return builder;
	}
	
	private StringBuilder processUpdateFirmware(HttpServletRequest request, String[] chargePointItems) {
		String location = request.getParameter("location");
		String retriesSTR = request.getParameter("retries");
		String retryIntervalSTR = request.getParameter("retryInterval");
		String retrieveDate = request.getParameter("retrieveDate");	
		
		if (location.isEmpty()
				|| retriesSTR.isEmpty() 
				|| retryIntervalSTR.isEmpty()
				|| retrieveDate.isEmpty()){
			throw new InputException(Common.EXCEPTION_INPUT_EMPTY);
		}			
		
		int retries;
		int retryInterval;			
		try {
			retries = Integer.parseInt(retriesSTR);
			retryInterval = Integer.parseInt(retryIntervalSTR);				
		} catch (NumberFormatException e) {
			throw new InputException(Common.EXCEPTION_PARSING_NUMBER);
		}
		
		ChargePointService12_Client cpsClient = new ChargePointService12_Client();
		UpdateFirmwareRequest req = cpsClient.prepareUpdateFirmware(location, retries, retrieveDate, retryInterval);

		StringBuilder builder = new StringBuilder();
		String result;
		for (String temp: chargePointItems) {
			String[] chargePointItem = temp.split(";");
			result = cpsClient.sendUpdateFirmware(chargePointItem[0], chargePointItem[1], req);
			builder.append(result + "\n");
		}
		return builder;
	}
}
