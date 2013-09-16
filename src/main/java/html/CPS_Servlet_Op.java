package html;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.Utils;
import cps.CPS_Client;
import cps.ChangeAvailabilityRequest;
import cps.ChangeConfigurationRequest;
import cps.ClearCacheRequest;
import cps.GetDiagnosticsRequest;
import cps.RemoteStartTransactionRequest;
import cps.RemoteStopTransactionRequest;
import cps.ResetRequest;
import cps.UnlockConnectorRequest;
import cps.UpdateFirmwareRequest;

public class CPS_Servlet_Op extends HttpServlet {

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
			chargePointsList = getChargePoints();
			// Redirect to the page of the first operation
			response.sendRedirect(contextPath + servletPath + "/ChangeAvailability");
			return;
		}
				
		StringBuilder mainBuilder = new StringBuilder(CPS_Common.printHead(contextPath));
				
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

		mainBuilder.append(CPS_Common.printFoot(contextPath));
		
		response.setContentType("text/html");
		PrintWriter writer = response.getWriter();
		writer.write(mainBuilder.toString());
		writer.close();	
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		PrintWriter writer = response.getWriter();
		String command = request.getPathInfo();	

		// Retrieve values from HTML select multiple
		String[] chargePointItems = request.getParameterValues("cp_items");

		response.setContentType("text/plain");	

		if (chargePointItems==null) {		
			writer.println("You did not select any charge points, did you!?");
			writer.close();	
			return;		
		}

		String result = null;
		CPS_Client cpsClient = new CPS_Client();

		// chargePointItem[0] : chargebox id
		// chargePointItem[1] : endpoint (IP) address

		if (command.equals("/ChangeAvailability")){
			String availType = request.getParameter("availType");
			int connectorId = Integer.parseInt(request.getParameter("connectorId"));
			ChangeAvailabilityRequest req = cpsClient.prepareChangeAvailability(connectorId, availType);

			for(String temp: chargePointItems){
				String[] chargePointItem = temp.split(";");
				result = cpsClient.sendChangeAvailability(chargePointItem[0], chargePointItem[1], req);
				writer.println(result);
			}

		} else if (command.equals("/ChangeConfiguration")) {
			String confKey = request.getParameter("confKey");
			String value = request.getParameter("value");
			ChangeConfigurationRequest req = cpsClient.prepareChangeConfiguration(confKey, value);

			for(String temp: chargePointItems){
				String[] chargePointItem = temp.split(";");
				result = cpsClient.sendChangeConfiguration(chargePointItem[0], chargePointItem[1], req);
				writer.println(result);
			}

		} else if (command.equals("/ClearCache")) {
			ClearCacheRequest req = cpsClient.prepareClearCache();

			for(String temp: chargePointItems){
				String[] chargePointItem = temp.split(";");
				result = cpsClient.sendClearCache(chargePointItem[0], chargePointItem[1], req);
				writer.println(result);
			}	

		} else if (command.equals("/GetDiagnostics")) {
			String location = request.getParameter("location");
			int retries = Integer.parseInt(request.getParameter("retries"));
			int retryInterval = Integer.parseInt(request.getParameter("retryInterval"));
			String startTime = request.getParameter("startTime");
			String stopTime = request.getParameter("stopTime");
			GetDiagnosticsRequest req = cpsClient.prepareGetDiagnostics(location, retries, retryInterval, startTime, stopTime);

			for(String temp: chargePointItems){
				String[] chargePointItem = temp.split(";");
				result = cpsClient.sendGetDiagnostics(chargePointItem[0], chargePointItem[1], req);
				writer.println(result);
			}

		} else if (command.equals("/RemoteStartTransaction")) {
			int connectorId = Integer.parseInt(request.getParameter("connectorId"));
			String idTag = request.getParameter("idTag");
			RemoteStartTransactionRequest req = cpsClient.prepareRemoteStartTransaction(connectorId, idTag);

			for(String temp: chargePointItems){
				String[] chargePointItem = temp.split(";");
				result = cpsClient.sendRemoteStartTransaction(chargePointItem[0], chargePointItem[1], req);
				writer.println(result);
			}

		} else if (command.equals("/RemoteStopTransaction")) {
			int transactionId = Integer.parseInt(request.getParameter("transactionId"));
			RemoteStopTransactionRequest req = cpsClient.prepareRemoteStopTransaction(transactionId);

			for(String temp: chargePointItems){
				String[] chargePointItem = temp.split(";");
				result = cpsClient.sendRemoteStopTransaction(chargePointItem[0], chargePointItem[1], req);
				writer.println(result);
			}

		} else if (command.equals("/Reset")){			
			String resetType = request.getParameter("resetType");
			ResetRequest req = cpsClient.prepareReset(resetType);

			for(String temp: chargePointItems){
				String[] chargePointItem = temp.split(";");
				result = cpsClient.sendReset(chargePointItem[0], chargePointItem[1], req);
				writer.println(result);
			}

		} else if (command.equals("/UnlockConnector")) {
			int connectorId = Integer.parseInt(request.getParameter("connectorId"));
			UnlockConnectorRequest req = cpsClient.prepareUnlockConnector(connectorId);

			for(String temp: chargePointItems){
				String[] chargePointItem = temp.split(";");
				result = cpsClient.sendUnlockConnector(chargePointItem[0], chargePointItem[1], req);
				writer.println(result);
			}

		} else if (command.equals("/UpdateFirmware")){
			String location = request.getParameter("location");
			int retries = Integer.parseInt(request.getParameter("retries"));
			String retrieveDate = request.getParameter("retrieveDate");
			int retryInterval = Integer.parseInt(request.getParameter("retryInterval"));
			UpdateFirmwareRequest req = cpsClient.prepareUpdateFirmware(location, retries, retrieveDate, retryInterval);

			for(String temp: chargePointItems){
				String[] chargePointItem = temp.split(";");
				result = cpsClient.sendUpdateFirmware(chargePointItem[0], chargePointItem[1], req);
				writer.println(result);
			}
		}		
		writer.close();	
	}

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
		+ printChargePoints()				
		+ "<b>Parameters</b><hr>\n"
		+ "<table class=\"params\">\n"
		+ "<tr><td>Connector Id (integer, 0 = charge point as a whole):</td><td><input type=\"number\" min=\"0\" name=\"ConnectorId\"></td></tr>\n"
		+ "<tr><td>Availability Type:</td><td><select name=\"availType\">"
		+ "<option value=\"Inoperative\">Inoperative</option>"
		+ "<option value=\"Operative\">Operative</option>"
		+ "</select></td></tr>\n"
		+ "<tr><td></td><td id=\"add_space\"><input type=\"submit\" value=\"Perform\"></td></tr>\n"	   	
		+ "</table>\n</form>\n</div>";
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
		+ printChargePoints()
		+ "<b>Parameters</b><hr>\n"
		+ "<table class=\"params\">\n"
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
		+ "<tr><td></td><td id=\"add_space\"><input type=\"submit\" value=\"Perform\"></td></tr>\n"
		+ "</table>\n</form>\n</div>";		
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
		+ printChargePoints()
		+ "<b>Parameters</b><hr>\n"
		+ "<center>"
		+ "<i>No parameters required.</i>"
		+ "<br><br>"
		+ "<input type=\"submit\" value=\"Perform\">"
		+ "</center>\n</form>\n</div>";		
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
		+ printChargePoints()
		+ "<b>Parameters</b><hr>\n"
		+ "<table class=\"params\">\n"
		+ "<tr><td>Location (directory URI):</td><td><input type=\"text\" name=\"location\"></td></tr>\n"		
		+ "<tr><td>Retries (integer):</td><td><input type=\"number\" min=\"0\" name=\"retries\"></td></tr>\n"
		+ "<tr><td>Retry Interval (integer):</td><td><input type=\"number\" min=\"0\" name=\"retryInterval\"></td></tr>\n"
		+ "<tr><td>Start time (ex: 2011-12-21T11:33:23Z):</td><td><input type=\"datetime\" name=\"startTime\"></td></tr>\n"
		+ "<tr><td>Stop time (ex: 2011-12-21T11:33:23Z):</td><td><input type=\"datetime\" name=\"stopTime\"></td></tr>\n"
		+ "<tr><td></td><td id=\"add_space\"><input type=\"submit\" value=\"Perform\"></td></tr>\n"  	   	
		+ "</table>\n</form>\n</div>";
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
		+ printChargePoints()
		+ "<b>Parameters</b><hr>\n"
		+ "<table class=\"params\">\n"
		+ "<tr><td>Connector Id (integer, not 0):</td><td><input type=\"number\" min=\"1\" name=\"ConnectorId\"></td></tr>\n"
		+ "<tr><td>idTag (string):</td><td><input type=\"text\" name=\"idTag\"></td></tr>\n"
		+ "<tr><td></td><td id=\"add_space\"><input type=\"submit\" value=\"Perform\"></td></tr>\n"
		+ "</table>\n</form>\n</div>";
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
		+ printChargePoints()
		+ "<b>Parameters</b><hr>\n"
		+ "<table class=\"params\">\n"
		+ "<tr><td>Transaction Id (integer):</td><td><input type=\"number\" name=\"transactionId\"></td></tr>\n"	
		+ "<tr><td></td><td id=\"add_space\"><input type=\"submit\" value=\"Perform\"></td></tr>\n"
		+ "</table>\n</form>\n</div>";
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
		+ printChargePoints()
		+ "<b>Parameters</b><hr>\n"
		+ "<table class=\"params\">\n"
		+ "<tr><td>Reset type:</td><td>\n"
		+ "<select name=\"resetType\">\n"
		+ "<option value=\"Hard\">Hard</option>\n"
		+ "<option value=\"Soft\">Soft</option>\n"
		+ "</select>\n"
		+ "</td></tr>\n"
		+ "<tr><td></td><td id=\"add_space\"><input type=\"submit\" value=\"Perform\"></td></tr>\n"
		+ "</table>\n</form>\n</div>";
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
		+ printChargePoints()
		+ "<b>Parameters</b><hr>\n"
		+ "<table class=\"params\">\n"
		+ "<tr><td>Connector Id (integer, not 0):</td><td><input type=\"number\" min=\"1\" name=\"ConnectorId\"></td></tr>\n"	
		+ "<tr><td></td><td id=\"add_space\"><input type=\"submit\" value=\"Perform\"></td></tr>\n"
		+ "</table>\n</form>\n</div>";
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
		+ printChargePoints()
		+ "<b>Parameters</b><hr>\n"
		+ "<table class=\"params\">\n"
		+ "<tr><td>Location (URI):</td><td><input type=\"text\" name=\"location\"></td></tr>\n"
		+ "<tr><td>Retries (integer):</td><td><input type=\"number\" min=\"0\" name=\"retries\"></td></tr>\n"
		+ "<tr><td>Retrieve Date (ex: 2011-12-21T11:33:23Z):</td><td><input type=\"datetime\" name=\"retrieveDate\"></td></tr>\n"
		+ "<tr><td>Retry Interval (integer):</td><td><input type=\"number\" min=\"0\" name=\"retryInterval\"></td></tr>\n"
		+ "<tr><td></td><td id=\"add_space\"><input type=\"submit\" value=\"Perform\"></td></tr>\n"	
		+ "</table>\n</form>\n</div>";
	}

	private static HashMap<String,String> getChargePoints(){
		Connection connect = null;
		PreparedStatement pt = null;
		ResultSet rs = null;
		try {	
			// Prepare Database Access
			connect = Utils.getConnectionFromPool();
			pt = connect.prepareStatement("SELECT chargeBoxId, endpoint_address FROM chargebox");
			rs = pt.executeQuery();

			HashMap<String,String> results = new HashMap<String,String>();
			while( rs.next() ) { results.put(rs.getString(1), rs.getString(2));	}

			return results;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		} finally {
			Utils.releaseResources(connect, pt, rs);
		}
	}

	private String printChargePoints() {		
		StringBuilder builder = new StringBuilder(
				"<b>Charge Points</b><hr>\n"
				+ "<table>\n"
				+ "<tr><td style=\"vertical-align:top\">\n"
				+ "<input type=\"button\" value=\"Select All\" onClick=\"selectAll(document.getElementById('cp_items'))\">\n"
				+ "<input type=\"button\" value=\"Select None\" onClick=\"selectNone(document.getElementById('cp_items'))\">\n"
				+ "</td><td>\n"
				+ "<select name=\"cp_items\" id=\"cp_items\" size=\"5\" multiple>\n");

		for(String key : chargePointsList.keySet()) {
			String value = chargePointsList.get(key);
			builder.append("<option value=\"" + key + ";" + value + "\">" + key + " &#8212; " + value + "</option>\n");
		}
		
		builder.append("</select>\n</td>\n</tr>\n</table>\n<br>\n");
		return builder.toString();
	}
}
