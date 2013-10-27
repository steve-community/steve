package de.rwth.idsg.steve.html;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ocpp.cp._2012._06.ChangeAvailabilityRequest;
import ocpp.cp._2012._06.ChangeConfigurationRequest;
import ocpp.cp._2012._06.ClearCacheRequest;
import ocpp.cp._2012._06.DataTransferRequest;
import ocpp.cp._2012._06.GetConfigurationRequest;
import ocpp.cp._2012._06.GetDiagnosticsRequest;
import ocpp.cp._2012._06.GetLocalListVersionRequest;
import ocpp.cp._2012._06.RemoteStartTransactionRequest;
import ocpp.cp._2012._06.RemoteStopTransactionRequest;
import ocpp.cp._2012._06.ResetRequest;
import ocpp.cp._2012._06.SendLocalListRequest;
import ocpp.cp._2012._06.UnlockConnectorRequest;
import ocpp.cp._2012._06.UpdateFirmwareRequest;
import de.rwth.idsg.steve.ChargePointService15_Client;
import de.rwth.idsg.steve.common.ClientDBAccess;


/**
 * This servlet provides the Web interface to manage charging points with OCPP v1.5
 * 
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * 
 */
public class ServletOperationsV15 extends HttpServlet {

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
			chargePointsList = ClientDBAccess.getChargePoints("1.5");
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
			
		} else if (command.equals("/ReserveNow")){		
			mainBuilder.append(printReserveNow());
			
		} else if (command.equals("/CancelReservation")){		
			mainBuilder.append(printCancelReservation());
			
		} else if (command.equals("/DataTransfer")){		
			mainBuilder.append(printDataTransfer());
			
		} else if (command.equals("/GetConfiguration")){		
			mainBuilder.append(printGetConfiguration());
			
		} else if (command.equals("/GetLocalListVersion")){		
			mainBuilder.append(printGetLocalListVersion());
			
		} else if (command.equals("/SendLocalList")){		
			mainBuilder.append(printSendLocalList());
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
			
		///// New operations with OCPP 1.5 /////			
		} else if (command.equals("/ReserveNow")){				
			returnBuilder = processReserveNow(request, chargePointItems);
			
		} else if (command.equals("/CancelReservation")){
			returnBuilder = processCancelReservation(request, chargePointItems);
			
		} else if (command.equals("/DataTransfer")){
			returnBuilder = processDataTransfer(request, chargePointItems);
			
		} else if (command.equals("/GetConfiguration")){
			returnBuilder = processGetConfiguration(request, chargePointItems);
			
		} else if (command.equals("/GetLocalListVersion")){			
			returnBuilder = processGetLocalListVersion(request, chargePointItems);
	
		} else if (command.equals("/SendLocalList")){
			returnBuilder = processSendLocalList(request, chargePointItems);
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
		+ "<hr>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/ReserveNow\">Reserve Now</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/CancelReservation\">Cancel Reservation</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/DataTransfer\">Data Transfer</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/GetConfiguration\">Get Configuration</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/GetLocalListVersion\">Get Local List Version</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/SendLocalList\">Send Local List</a></li>\n"
		+ "</ul>\n"
		+ "</div>\n"

		// Print the div on the right
		+ "<div class=\"op-content\">\n<form method=\"POST\" action=\"" + contextPath + servletPath + "/ChangeAvailability\">\n" 				
		+ Common.printChargePointsMultipleSelect(chargePointsList, "1.5")		
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
		+ "<hr>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/ReserveNow\">Reserve Now</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/CancelReservation\">Cancel Reservation</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/DataTransfer\">Data Transfer</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/GetConfiguration\">Get Configuration</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/GetLocalListVersion\">Get Local List Version</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/SendLocalList\">Send Local List</a></li>\n"
		+ "</ul>\n"
		+ "</div>\n"

		// Print the div on the right
		+ "<div class=\"op-content\">\n<form method=\"POST\" action=\"" + contextPath + servletPath + "/ChangeConfiguration\">\n" 
		+ Common.printChargePointsMultipleSelect(chargePointsList, "1.5")
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
		+ "<option value=\"ClockAlignedDataInterval\">ClockAlignedDataInterval (in seconds)</option>\n"
		+ "<option value=\"MeterValuesSampledData\">MeterValuesSampledData (comma seperated list)</option>\n"
		+ "<option value=\"MeterValuesAlignedData\">MeterValuesAlignedData (comma seperated list)</option>\n"
		+ "<option value=\"StopTxnSampledData\">StopTxnSampledData (comma seperated list)</option>\n"
		+ "<option value=\"StopTxnAlignedData\">StopTxnAlignedData (comma seperated list)</option>\n"
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
		+ "<hr>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/ReserveNow\">Reserve Now</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/CancelReservation\">Cancel Reservation</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/DataTransfer\">Data Transfer</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/GetConfiguration\">Get Configuration</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/GetLocalListVersion\">Get Local List Version</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/SendLocalList\">Send Local List</a></li>\n"
		+ "</ul>\n"
		+ "</div>\n"

		// Print the div on the right
		+ "<div class=\"op-content\">\n<form method=\"POST\" action=\"" + contextPath + servletPath + "/ClearCache\">\n" 
		+ Common.printChargePointsMultipleSelect(chargePointsList, "1.5")
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
		+ "<hr>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/ReserveNow\">Reserve Now</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/CancelReservation\">Cancel Reservation</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/DataTransfer\">Data Transfer</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/GetConfiguration\">Get Configuration</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/GetLocalListVersion\">Get Local List Version</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/SendLocalList\">Send Local List</a></li>\n"
		+ "</ul>\n"
		+ "</div>\n"

		// Print the div on the right
		+ "<div class=\"op-content\">\n<form method=\"POST\" action=\"" + contextPath + servletPath + "/GetDiagnostics\">\n" 
		+ Common.printChargePointsMultipleSelect(chargePointsList, "1.5")
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
		+ "<hr>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/ReserveNow\">Reserve Now</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/CancelReservation\">Cancel Reservation</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/DataTransfer\">Data Transfer</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/GetConfiguration\">Get Configuration</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/GetLocalListVersion\">Get Local List Version</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/SendLocalList\">Send Local List</a></li>\n"
		+ "</ul>\n"
		+ "</div>\n"

		// Print the div on the right
		+ "<div class=\"op-content\">\n<form method=\"POST\" action=\"" + contextPath + servletPath + "/RemoteStartTransaction\">\n" 
		+ Common.printChargePointsSingleSelect(chargePointsList, "1.5")
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
		+ "<hr>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/ReserveNow\">Reserve Now</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/CancelReservation\">Cancel Reservation</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/DataTransfer\">Data Transfer</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/GetConfiguration\">Get Configuration</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/GetLocalListVersion\">Get Local List Version</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/SendLocalList\">Send Local List</a></li>\n"
		+ "</ul>\n"
		+ "</div>\n"

		// Print the div on the right
		+ "<div class=\"op-content\">\n<form method=\"POST\" action=\"" + contextPath + servletPath + "/RemoteStopTransaction\">\n" 
		+ Common.printChargePointsSingleSelect(chargePointsList, "1.5")
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
		+ "<hr>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/ReserveNow\">Reserve Now</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/CancelReservation\">Cancel Reservation</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/DataTransfer\">Data Transfer</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/GetConfiguration\">Get Configuration</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/GetLocalListVersion\">Get Local List Version</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/SendLocalList\">Send Local List</a></li>\n"
		+ "</ul>\n"
		+ "</div>\n"

		// Print the div on the right
		+ "<div class=\"op-content\">\n<form method=\"POST\" action=\"" + contextPath + servletPath + "/Reset\">\n" 
		+ Common.printChargePointsMultipleSelect(chargePointsList, "1.5")
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
		+ "<hr>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/ReserveNow\">Reserve Now</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/CancelReservation\">Cancel Reservation</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/DataTransfer\">Data Transfer</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/GetConfiguration\">Get Configuration</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/GetLocalListVersion\">Get Local List Version</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/SendLocalList\">Send Local List</a></li>\n"
		+ "</ul>\n"
		+ "</div>\n"

		// Print the div on the right
		+ "<div class=\"op-content\">\n<form method=\"POST\" action=\"" + contextPath + servletPath + "/UnlockConnector\">\n" 
		+ Common.printChargePointsSingleSelect(chargePointsList, "1.5")
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
		+ "<hr>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/ReserveNow\">Reserve Now</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/CancelReservation\">Cancel Reservation</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/DataTransfer\">Data Transfer</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/GetConfiguration\">Get Configuration</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/GetLocalListVersion\">Get Local List Version</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/SendLocalList\">Send Local List</a></li>\n"
		+ "</ul>\n"
		+ "</div>\n"

		// Print the div on the right
		+ "<div class=\"op-content\">\n<form method=\"POST\" action=\"" + contextPath + servletPath + "/UpdateFirmware\">\n" 
		+ Common.printChargePointsMultipleSelect(chargePointsList, "1.5")
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
	
	private String printReserveNow() {
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
		+ "<li><a href=\"" + contextPath + servletPath + "/UpdateFirmware\">Update Firmware</a></li>\n"
		+ "<hr>\n"
		+ "<li><a class=\"highlight\" href=\"" + contextPath + servletPath + "/ReserveNow\">Reserve Now</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/CancelReservation\">Cancel Reservation</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/DataTransfer\">Data Transfer</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/GetConfiguration\">Get Configuration</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/GetLocalListVersion\">Get Local List Version</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/SendLocalList\">Send Local List</a></li>\n"
		+ "</ul>\n"
		+ "</div>\n"
		
		// Print the div on the right
		+ "<div class=\"op-content\">\n<form method=\"POST\" action=\"" + contextPath + servletPath + "/ReserveNow\">\n" 
		+ Common.printChargePointsSingleSelect(chargePointsList, "1.5")
		+ "<h3><span>Parameters</span></h3>\n"
		+ "<table>\n"
		+ "<tr><td>Connector Id (integer):</td><td><input type=\"number\" min=\"0\" name=\"connectorId\" placeholder=\"if empty, 0 = not for a specific connector\"></td></tr>\n"	
		+ "<tr><td>Expiry Date (ex: 2011-12-21 11:33):</td><td><input type=\"datetime\" name=\"expiryDate\"></td></tr>\n"
		+ "<tr><td>idTag (string):</td><td><input type=\"text\" name=\"idTag\"></td></tr>\n"
		+ "<tr><td>parentIdTag (string):</td><td><input type=\"text\" name=\"parentIdTag\" placeholder=\"optional\"></td></tr>\n"
		+ "</table>\n"
		+ "<div class=\"submit-button\"><input type=\"submit\" value=\"Perform\"></div>\n"
		+ "</form>\n</div>\n";	
	}

	private String printCancelReservation() {
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
		+ "<li><a href=\"" + contextPath + servletPath + "/UpdateFirmware\">Update Firmware</a></li>\n"
		+ "<hr>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/ReserveNow\">Reserve Now</a></li>\n"
		+ "<li><a class=\"highlight\" href=\"" + contextPath + servletPath + "/CancelReservation\">Cancel Reservation</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/DataTransfer\">Data Transfer</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/GetConfiguration\">Get Configuration</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/GetLocalListVersion\">Get Local List Version</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/SendLocalList\">Send Local List</a></li>\n"
		+ "</ul>\n"
		+ "</div>\n"
		
		// Print the div on the right
		+ "<div class=\"op-content\">\n<form method=\"POST\" action=\"" + contextPath + servletPath + "/CancelReservation\">\n" 
		+ Common.printChargePointsSingleSelect(chargePointsList, "1.5")
		+ "<h3><span>Parameters</span></h3>\n"
		+ "<table>\n"
		+ "<tr><td>Reservation Id (integer):</td><td><input type=\"number\" min=\"0\" name=\"reservationId\"></td></tr>\n"
		+ "</table>\n"
		+ "<div class=\"submit-button\"><input type=\"submit\" value=\"Perform\"></div>\n"
		+ "</form>\n</div>\n";	
	}
	
	private String printDataTransfer() {
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
		+ "<li><a href=\"" + contextPath + servletPath + "/UpdateFirmware\">Update Firmware</a></li>\n"
		+ "<hr>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/ReserveNow\">Reserve Now</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/CancelReservation\">Cancel Reservation</a></li>\n"
		+ "<li><a class=\"highlight\" href=\"" + contextPath + servletPath + "/DataTransfer\">Data Transfer</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/GetConfiguration\">Get Configuration</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/GetLocalListVersion\">Get Local List Version</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/SendLocalList\">Send Local List</a></li>\n"
		+ "</ul>\n"
		+ "</div>\n"
		
		// Print the div on the right
		+ "<div class=\"op-content\">\n<form method=\"POST\" action=\"" + contextPath + servletPath + "/DataTransfer\">\n" 
		+ Common.printChargePointsMultipleSelect(chargePointsList, "1.5")
		+ "<h3><span>Parameters</span></h3>\n"
		+ "<table>\n"
		+ "<tr><td>Vendor Id (String):</td><td><input type=\"text\" name=\"vendorId\"></td></tr>\n"
		+ "<tr><td>Message Id (String):</td><td><input type=\"text\" name=\"messageId\"></td></tr>\n"
		+ "<tr><td>data (Text):</td><td><input type=\"text\" name=\"data\"></td></tr>\n"
		+ "</table>\n"
		+ "<div class=\"submit-button\"><input type=\"submit\" value=\"Perform\"></div>\n"
		+ "</form>\n</div>\n";	
	}
	

	private String printGetConfiguration() {
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
		+ "<li><a href=\"" + contextPath + servletPath + "/UpdateFirmware\">Update Firmware</a></li>\n"
		+ "<hr>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/ReserveNow\">Reserve Now</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/CancelReservation\">Cancel Reservation</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/DataTransfer\">Data Transfer</a></li>\n"
		+ "<li><a class=\"highlight\" href=\"" + contextPath + servletPath + "/GetConfiguration\">Get Configuration</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/GetLocalListVersion\">Get Local List Version</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/SendLocalList\">Send Local List</a></li>\n"
		+ "</ul>\n"
		+ "</div>\n"
		
		// Print the div on the right
		+ "<div class=\"op-content\">\n<form method=\"POST\" action=\"" + contextPath + servletPath + "/GetConfiguration\">\n" 
		+ Common.printChargePointsMultipleSelect(chargePointsList, "1.5")
		+ "<h3><span>Parameters</span></h3>\n"
		+ "<table>\n"
		+ "<tr><td style=\"vertical-align:top\">"
		+ "<input type=\"button\" value=\"Select All\" onClick=\"selectAll(document.getElementById('confKeys'))\">\n"
		+ "<input type=\"button\" value=\"Select None\" onClick=\"selectNone(document.getElementById('confKeys'))\">\n"
		+ "<br><br><i>Note: If none selected, the charge point returns <br> a list of <b>all</b> configuration settings.</i>\n"
		+ "</td>\n"
		+ "<td>\n"
		+ "<select name=\"confKeys\" id=\"confKeys\" size=\"14\" multiple>\n"
		+ "<option value=\"HeartBeatInterval\">HeartBeatInterval</option>\n"
		+ "<option value=\"ConnectionTimeOut\">ConnectionTimeOut</option>\n"
		+ "<option value=\"ProximityContactRetries\">ProximityContactRetries</option>\n"
		+ "<option value=\"ProximityLockRetries\">ProximityLockRetries</option>\n"
		+ "<option value=\"ResetRetries\">ResetRetries</option>\n"
		+ "<option value=\"BlinkRepeat\">BlinkRepeat</option>\n"
		+ "<option value=\"LightIntensity\">LightIntensity</option>\n"
		+ "<option value=\"ChargePointId\">ChargePointId</option>\n"
		+ "<option value=\"MeterValueSampleInterval\">MeterValueSampleInterval</option>\n"
		+ "<option value=\"ClockAlignedDataInterval\">ClockAlignedDataInterval</option>\n"
		+ "<option value=\"MeterValuesSampledData\">MeterValuesSampledData</option>\n"
		+ "<option value=\"MeterValuesAlignedData\">MeterValuesAlignedData</option>\n"
		+ "<option value=\"StopTxnSampledData\">StopTxnSampledData</option>\n"
		+ "<option value=\"StopTxnAlignedData\">StopTxnAlignedData</option>\n"
		+ "</select>\n"
		+ "</td></tr>\n"
		+ "</table>\n"
		+ "<div class=\"submit-button\"><input type=\"submit\" value=\"Perform\"></div>\n"
		+ "</form>\n</div>\n";	
	}
	
	private String printGetLocalListVersion() {
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
		+ "<li><a href=\"" + contextPath + servletPath + "/UpdateFirmware\">Update Firmware</a></li>\n"
		+ "<hr>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/ReserveNow\">Reserve Now</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/CancelReservation\">Cancel Reservation</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/DataTransfer\">Data Transfer</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/GetConfiguration\">Get Configuration</a></li>\n"
		+ "<li><a class=\"highlight\" href=\"" + contextPath + servletPath + "/GetLocalListVersion\">Get Local List Version</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/SendLocalList\">Send Local List</a></li>\n"
		+ "</ul>\n"
		+ "</div>\n"
		
		// Print the div on the right
		+ "<div class=\"op-content\">\n<form method=\"POST\" action=\"" + contextPath + servletPath + "/GetLocalListVersion\">\n" 
		+ Common.printChargePointsMultipleSelect(chargePointsList, "1.5")
		+ "<h3><span>Parameters</span></h3>\n"
		+ "<center><i>No parameters required.</i></center>"
		+ "<div class=\"submit-button\"><input type=\"submit\" value=\"Perform\"></div>\n"
		+ "</form>\n</div>\n";	
	}
	
	private Object printSendLocalList() {
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
		+ "<li><a href=\"" + contextPath + servletPath + "/UpdateFirmware\">Update Firmware</a></li>\n"
		+ "<hr>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/ReserveNow\">Reserve Now</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/CancelReservation\">Cancel Reservation</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/DataTransfer\">Data Transfer</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/GetConfiguration\">Get Configuration</a></li>\n"
		+ "<li><a href=\"" + contextPath + servletPath + "/GetLocalListVersion\">Get Local List Version</a></li>\n"
		+ "<li><a class=\"highlight\" href=\"" + contextPath + servletPath + "/SendLocalList\">Send Local List</a></li>\n"
		+ "</ul>\n"
		+ "</div>\n"
		
		// Print the div on the right
		+ "<div class=\"op-content\">\n<form method=\"POST\" action=\"" + contextPath + servletPath + "/SendLocalList\">\n" 
		+ Common.printChargePointsMultipleSelect(chargePointsList, "1.5")
		+ "<h3><span>Parameters</span></h3>\n"
		+ "<table class=\"sll\">\n"
		+ "<tr><td>hash (String):</td><td><i>Optional, omitted for now</i></td></tr>\n"
		+ "<tr><td>listVersion (integer):</td><td><input type=\"number\" name=\"listVersion\"></td></tr>\n"
		+ "<tr><td>Update Type:</td><td><input type=\"radio\" name=\"updateType\" value=\"Full\" onclick=\"removeElements()\" checked> Full</td></tr>\n"		
		+ "<tr><td></td><td><input type=\"radio\" name=\"updateType\" value=\"Differential\" onclick=\"showElements()\"> Differential</td></tr>\n"
		+ "<tr><td></td><td id=\"diffElements\"></td></tr>\n"
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
		if (connectorIdSTR == null || connectorIdSTR.isEmpty()) {
			connectorId = 0;
		} else {
			try {
				connectorId = Integer.parseInt(request.getParameter("connectorId"));	
			} catch (NumberFormatException e) {
				throw new InputException(Common.EXCEPTION_PARSING_NUMBER);
			}
		}
		String availType = request.getParameter("availType");
		
		ChargePointService15_Client cpsClient = new ChargePointService15_Client();
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
		if (value == null || value.isEmpty()) {
			throw new InputException(Common.EXCEPTION_INPUT_EMPTY);
		}
		
		ChargePointService15_Client cpsClient = new ChargePointService15_Client();
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
		ChargePointService15_Client cpsClient = new ChargePointService15_Client();
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
		
		if (location == null || location.isEmpty()
				|| retriesSTR == null || retriesSTR.isEmpty() 
				|| retryIntervalSTR == null || retryIntervalSTR.isEmpty()
				|| startTime == null || startTime.isEmpty()
				|| stopTime == null || stopTime.isEmpty()) {
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
		
		ChargePointService15_Client cpsClient = new ChargePointService15_Client();
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
		String idTag = request.getParameter("idTag");
		
		if (connectorIdSTR == null || connectorIdSTR.isEmpty()
				|| idTag == null || idTag.isEmpty()) {
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
		
		ChargePointService15_Client cpsClient = new ChargePointService15_Client();
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
		if (transactionIdSTR == null || transactionIdSTR.isEmpty()) {
			throw new InputException(Common.EXCEPTION_INPUT_EMPTY);
		}
		
		int transactionId;			
		try {
			transactionId = Integer.parseInt(transactionIdSTR);
		} catch (NumberFormatException e) {
			throw new InputException(Common.EXCEPTION_PARSING_NUMBER);
		}
		
		ChargePointService15_Client cpsClient = new ChargePointService15_Client();
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
		
		ChargePointService15_Client cpsClient = new ChargePointService15_Client();
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
		if (connectorIdSTR == null || connectorIdSTR.isEmpty()) {
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
		
		ChargePointService15_Client cpsClient = new ChargePointService15_Client();
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
		
		if (location == null || location.isEmpty()
				|| retriesSTR == null || retriesSTR.isEmpty() 
				|| retryIntervalSTR == null || retryIntervalSTR.isEmpty()
				|| retrieveDate == null || retrieveDate.isEmpty()) {
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
		
		ChargePointService15_Client cpsClient = new ChargePointService15_Client();
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
	
	private StringBuilder processReserveNow(HttpServletRequest request, String[] chargePointItems) {
		String connectorIdSTR = request.getParameter("connectorId");			
		int connectorId;
		if (connectorIdSTR == null || connectorIdSTR.isEmpty()) {
			connectorId = 0;
		} else {
			try {
				connectorId = Integer.parseInt(connectorIdSTR);
			} catch (NumberFormatException e) {
				throw new InputException(Common.EXCEPTION_PARSING_NUMBER);
			}
		}
		String expiryString = request.getParameter("expiryDate");
		String idTag = request.getParameter("idTag");
		String parentIdTag = request.getParameter("parentIdTag");
		
		if (expiryString == null || expiryString.isEmpty() 
				|| idTag == null || idTag.isEmpty()) {
			throw new InputException(Common.EXCEPTION_INPUT_EMPTY);
		}
		
		// There's only one item in chargePointItems.
		String[] chargePointItem = chargePointItems[0].split(";");
				
		ChargePointService15_Client cpsClient = new ChargePointService15_Client();		
		String result = cpsClient.reserveNow(chargePointItem[0], chargePointItem[1], connectorId, expiryString, idTag, parentIdTag);
		StringBuilder builder = new StringBuilder(result);
		return builder;
	}
	
	private StringBuilder processCancelReservation(HttpServletRequest request, String[] chargePointItems) {
		String reservSTR = request.getParameter("reservationId");
		if (reservSTR == null || reservSTR.isEmpty()) {
			throw new InputException(Common.EXCEPTION_INPUT_EMPTY);
		}
		int reservationId;
		try {
			reservationId = Integer.parseInt(reservSTR);
		} catch (NumberFormatException e) {
			throw new InputException(Common.EXCEPTION_PARSING_NUMBER);
		}
		
		// There's only one item in chargePointItems.
		String[] chargePointItem = chargePointItems[0].split(";");
		
		ChargePointService15_Client cpsClient = new ChargePointService15_Client();	
		String result = cpsClient.cancelReservation(chargePointItem[0], chargePointItem[1], reservationId);
		StringBuilder builder = new StringBuilder(result);
		return builder;	
	}
	
	// Dummy implementation. This is new in OCPP 1.5. It must be vendor-specific.
	private StringBuilder processDataTransfer(HttpServletRequest request, String[] chargePointItems) {		
		String vendorId = request.getParameter("vendorId");
		String messageId = request.getParameter("messageId");
		String data = request.getParameter("data");
		
		ChargePointService15_Client cpsClient = new ChargePointService15_Client();	
		DataTransferRequest req = cpsClient.prepareDataTransfer(vendorId, messageId, data);
		
		StringBuilder builder = new StringBuilder();
		String result;
		for (String temp: chargePointItems) {
			String[] chargePointItem = temp.split(";");
			result = cpsClient.sendDataTransfer(chargePointItem[0], chargePointItem[1], req);
			builder.append(result + "\n");
		}
		return builder;
	}
	
	private StringBuilder processGetConfiguration(HttpServletRequest request, String[] chargePointItems) {	
		// No input check. This list is allowed to be empty. 
		// Then, charge point returns a list of all configuration settings.
		String[] confKeys = request.getParameterValues("confKeys");
		
		ChargePointService15_Client cpsClient = new ChargePointService15_Client();	
		GetConfigurationRequest req = cpsClient.prepareGetConfiguration(confKeys);
		
		StringBuilder builder = new StringBuilder();
		String result;
		for (String temp: chargePointItems) {
			String[] chargePointItem = temp.split(";");
			result = cpsClient.sendGetConfiguration(chargePointItem[0], chargePointItem[1], req);
			builder.append(result + "\n");
		}
		return builder;
	}
	
	private StringBuilder processGetLocalListVersion(HttpServletRequest request, String[] chargePointItems) {
		ChargePointService15_Client cpsClient = new ChargePointService15_Client();	
		GetLocalListVersionRequest req = cpsClient.prepareGetLocalListVersion();
		
		StringBuilder builder = new StringBuilder();
		String result;
		for (String temp: chargePointItems) {
			String[] chargePointItem = temp.split(";");
			result = cpsClient.sendGetLocalListVersion(chargePointItem[0], chargePointItem[1], req);
			builder.append(result + "\n");
		}
		return builder;
	}
	
	private StringBuilder processSendLocalList(HttpServletRequest request, String[] chargePointItems) {
		String listVersionSTR = request.getParameter("listVersion");
		if (listVersionSTR == null || listVersionSTR.isEmpty()) {
			throw new InputException(Common.EXCEPTION_INPUT_EMPTY);
		}
		int listVersion;
		try {
			listVersion = Integer.parseInt(listVersionSTR);
		} catch (NumberFormatException e) {
			throw new InputException(Common.EXCEPTION_PARSING_NUMBER);
		}			
		String updateType = request.getParameter("updateType");	
		
		ChargePointService15_Client cpsClient = new ChargePointService15_Client();	
		SendLocalListRequest req;
		
		if (updateType.equals("Differential")){
			String[] idTagItems = request.getParameterValues("idTag");
			String[] idTagDataItems = request.getParameterValues("idTagData");			
			
			// Depending on the selected value (AddUpdate or Delete)
			// insert the idTags into corresponding lists.
			ArrayList<String> addUpdateList = new ArrayList<String>();
			ArrayList<String> deleteList = new ArrayList<String>();
			for (int i = 0; i < idTagItems.length; i++) {
				String instruction = idTagDataItems[i];					
				if ( instruction.equals("AddUpdate") ) addUpdateList.add(idTagItems[i]);
				else if ( instruction.equals("Delete") ) deleteList.add(idTagItems[i]);				
			}
			req = cpsClient.prepareSendLocalList(listVersion, addUpdateList, deleteList);
			
		} else {
			// The update type is Full
			req = cpsClient.prepareSendLocalList(listVersion);
		}
		
		StringBuilder builder = new StringBuilder();
		String result;
		for (String temp: chargePointItems) {
			String[] chargePointItem = temp.split(";");
			result = cpsClient.sendSendLocalList(chargePointItem[0], chargePointItem[1], req);
			builder.append(result + "\n");
		}
		return builder;			
	}
}
