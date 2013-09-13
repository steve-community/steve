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
		
		PrintWriter writer = response.getWriter();
		response.setContentType("text/html");
		writer.println(CPS_Common.printHead(contextPath));
		
		if (command.equals("/ChangeAvailability")){	
			printChangeAvail(writer);

		} else if (command.equals("/ChangeConfiguration")){
			printChangeConf(writer);

		} else if (command.equals("/ClearCache")){
			printClearCache(writer);

		} else if (command.equals("/GetDiagnostics")){		
			printGetDiagnostics(writer);

		} else if (command.equals("/RemoteStartTransaction")){		
			printRemoteStartTrans(writer);

		} else if (command.equals("/RemoteStopTransaction")){			
			printRemoteStopTrans(writer);

		} else if (command.equals("/Reset")){			
			printReset(writer);

		} else if (command.equals("/UnlockConnector")){			
			printUnlockConnector(writer);

		} else if (command.equals("/UpdateFirmware")){		
			printUpdateFirmware(writer);
		}

		writer.println(CPS_Common.printFoot(contextPath));
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

	private void printChangeAvail(PrintWriter writer) {
		// Print navigation div
		writer.println("<div class=\"op-menu\">");
		writer.println("<ul>");
		writer.println("<li><a class=\"highlight\" href=\"" + contextPath + servletPath + "/ChangeAvailability\" >Change Availability</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/ChangeConfiguration\">Change Configuration</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/ClearCache\">Clear Cache</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/GetDiagnostics\">Get Diagnostics</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/RemoteStartTransaction\">Remote Start Transaction</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/RemoteStopTransaction\">Remote Stop Transaction</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/Reset\">Reset</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/UnlockConnector\">Unlock Connector</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/UpdateFirmware\">Update Firmware</a></li>");
		writer.println("</ul>");
		writer.println("</div>");	

		// Print the div on the right
		writer.println("<div class=\"op-content\">");
		writer.println("<form method=\"POST\" action=\""+ contextPath + servletPath + "/ChangeAvailability\">");

		printChargePoints(writer);

		writer.println("<b>Parameters</b><hr>");
		writer.println("<table class=\"params\">");		
		writer.println("<tr><td>");
		writer.println("Connector Id (integer, 0 = charge point as a whole): ");
		writer.println("</td><td>");
		writer.println("<input type=\"number\" min=\"0\" name=\"ConnectorId\">");
		writer.println("</td></tr>");
		writer.println("<tr><td>");
		writer.println("Availability Type: ");
		writer.println("</td><td>");
		writer.println("<select name=\"availType\">");
		writer.println("<option value=\"Inoperative\">Inoperative</option>");
		writer.println("<option value=\"Operative\">Operative</option>");
		writer.println("</select>");
		writer.println("</td></tr>");
		writer.println("<tr><td></td>");
		writer.println("<td id=\"add_space\">");
		writer.println("<input type=\"submit\" value=\"Perform\">");
		writer.println("</td>");   	   	
		writer.println("</table>");			
		writer.println("</form>");
		writer.println("</div>");
	}

	private void printChangeConf(PrintWriter writer) {
		// Print navigation div
		writer.println("<div class=\"op-menu\">");
		writer.println("<ul>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/ChangeAvailability\" >Change Availability</a></li>");
		writer.println("<li><a class=\"highlight\" href=\"" + contextPath + servletPath + "/ChangeConfiguration\">Change Configuration</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/ClearCache\">Clear Cache</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/GetDiagnostics\">Get Diagnostics</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/RemoteStartTransaction\">Remote Start Transaction</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/RemoteStopTransaction\">Remote Stop Transaction</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/Reset\">Reset</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/UnlockConnector\">Unlock Connector</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/UpdateFirmware\">Update Firmware</a></li>");
		writer.println("</ul>");
		writer.println("</div>");

		// Print the div on the right
		writer.println("<div class=\"op-content\">");
		writer.println("<form method=\"POST\" action=\""+ contextPath + servletPath + "/ChangeConfiguration\">");

		printChargePoints(writer);

		writer.println("<b>Parameters</b><hr>");
		writer.println("<table class=\"params\">");		
		writer.println("<tr><td>");
		writer.println("Configuration key:");
		writer.println("</td><td>");
		writer.println("<select name=\"confKey\">");
		writer.println("<option value=\"HeartBeatInterval\">HeartBeatInterval (in seconds)</option>");
		writer.println("<option value=\"ConnectionTimeOut\">ConnectionTimeOut (in seconds)</option>");
		writer.println("<option value=\"ProximityContactRetries\">ProximityContactRetries (in times)</option>");
		writer.println("<option value=\"ProximityLockRetries\">ProximityLockRetries (in times)</option>");
		writer.println("<option value=\"ResetRetries\">ResetRetries (in times)</option>");
		writer.println("<option value=\"BlinkRepeat\">BlinkRepeat (in times)</option>");
		writer.println("<option value=\"LightIntensity\">LightIntensity (in %)</option>");
		writer.println("<option value=\"ChargePointId\">ChargePointId (string)</option>");
		writer.println("<option value=\"MeterValueSampleInterval\">MeterValueSampleInterval (in seconds)</option>");		
		writer.println("</select>");
		writer.println("</td></tr>");
		writer.println("<tr><td>");
		writer.println("Value:");
		writer.println("</td><td>");
		writer.println("<input type=\"text\" name=\"value\">");		
		writer.println("</td></tr>");
		writer.println("<tr><td></td>");
		writer.println("<td id=\"add_space\">");
		writer.println("<input type=\"submit\" value=\"Perform\">");
		writer.println("</td>");   	   	
		writer.println("</table>");			
		writer.println("</form>");
		writer.println("</div>");
	}

	private void printClearCache(PrintWriter writer) {
		// Print navigation div
		writer.println("<div class=\"op-menu\">");
		writer.println("<ul>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/ChangeAvailability\" >Change Availability</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/ChangeConfiguration\">Change Configuration</a></li>");
		writer.println("<li><a class=\"highlight\" href=\"" + contextPath + servletPath + "/ClearCache\">Clear Cache</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/GetDiagnostics\">Get Diagnostics</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/RemoteStartTransaction\">Remote Start Transaction</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/RemoteStopTransaction\">Remote Stop Transaction</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/Reset\">Reset</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/UnlockConnector\">Unlock Connector</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/UpdateFirmware\">Update Firmware</a></li>");
		writer.println("</ul>");
		writer.println("</div>");	

		// Print the div on the right
		writer.println("<div class=\"op-content\">");
		writer.println("<form method=\"POST\" action=\""+ contextPath + servletPath + "/ClearCache\">");	

		printChargePoints(writer);

		writer.println("<b>Parameters</b><hr>");
		writer.println("<center>");
		writer.println("<i>No parameters required.</i>");
		writer.println("<br><br>");
		writer.println("<input type=\"submit\" value=\"Perform\">");
		writer.println("</center>");
		writer.println("</form>");
		writer.println("</div>");
	}

	private void printGetDiagnostics(PrintWriter writer) {
		// Print navigation div
		writer.println("<div class=\"op-menu\">");
		writer.println("<ul>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/ChangeAvailability\" >Change Availability</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/ChangeConfiguration\">Change Configuration</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/ClearCache\">Clear Cache</a></li>");
		writer.println("<li><a class=\"highlight\" href=\"" + contextPath + servletPath + "/GetDiagnostics\">Get Diagnostics</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/RemoteStartTransaction\">Remote Start Transaction</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/RemoteStopTransaction\">Remote Stop Transaction</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/Reset\">Reset</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/UnlockConnector\">Unlock Connector</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/UpdateFirmware\">Update Firmware</a></li>");
		writer.println("</ul>");
		writer.println("</div>");	

		// Print the div on the right
		writer.println("<div class=\"op-content\">");
		writer.println("<form method=\"POST\" action=\""+ contextPath + servletPath + "/GetDiagnostics\">");	

		printChargePoints(writer);

		writer.println("<b>Parameters</b><hr>");
		writer.println("<table class=\"params\">");
		writer.println("<tr><td>");		
		writer.println("Location (directory URI):");
		writer.println("</td><td>");
		writer.println("<input type=\"text\" name=\"location\">");
		writer.println("</td></tr><tr><td>");
		writer.println("Retries (integer):");
		writer.println("</td><td>");
		writer.println("<input type=\"number\" min=\"0\" name=\"retries\">");
		writer.println("</td></tr><tr><td>");
		writer.println("Retry Interval (integer):");
		writer.println("</td><td>");
		writer.println("<input type=\"number\" min=\"0\" name=\"retryInterval\">");
		writer.println("</td></tr><tr><td>");
		writer.println("Start time (ex: 2011-12-21T11:33:23Z):");
		writer.println("</td><td>");
		writer.println("<input type=\"datetime\" name=\"startTime\">");
		writer.println("</td></tr><tr><td>");
		writer.println("Stop time (ex: 2011-12-21T11:33:23Z):");
		writer.println("</td><td>");
		writer.println("<input type=\"datetime\" name=\"stopTime\">");		
		writer.println("</td></tr>");
		writer.println("<tr><td></td>");
		writer.println("<td id=\"add_space\">");
		writer.println("<input type=\"submit\" value=\"Perform\">");
		writer.println("</td>");   	   	
		writer.println("</table>");			
		writer.println("</form>");
		writer.println("</div>");
	}

	private void printRemoteStartTrans(PrintWriter writer) {
		// Print navigation div
		writer.println("<div class=\"op-menu\">");
		writer.println("<ul>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/ChangeAvailability\" >Change Availability</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/ChangeConfiguration\">Change Configuration</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/ClearCache\">Clear Cache</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/GetDiagnostics\">Get Diagnostics</a></li>");
		writer.println("<li><a class=\"highlight\" href=\"" + contextPath + servletPath + "/RemoteStartTransaction\">Remote Start Transaction</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/RemoteStopTransaction\">Remote Stop Transaction</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/Reset\">Reset</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/UnlockConnector\">Unlock Connector</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/UpdateFirmware\">Update Firmware</a></li>");
		writer.println("</ul>");
		writer.println("</div>");	

		// Print the div on the right
		writer.println("<div class=\"op-content\">");
		writer.println("<form method=\"POST\" action=\""+ contextPath + servletPath + "/RemoteStartTransaction\">");

		printChargePoints(writer);

		writer.println("<b>Parameters</b><hr>");
		writer.println("<table class=\"params\">");
		writer.println("<tr><td>");		
		writer.println("Connector Id (integer, not 0): ");
		writer.println("</td><td>");
		writer.println("<input type=\"number\" min=\"1\" name=\"ConnectorId\">");
		writer.println("</td></tr><tr><td>");
		writer.println("idTag (string): ");
		writer.println("</td><td>");
		writer.println("<input type=\"text\" name=\"idTag\">");	
		writer.println("</td></tr>");
		writer.println("<tr><td></td>");
		writer.println("<td id=\"add_space\">");
		writer.println("<input type=\"submit\" value=\"Perform\">");
		writer.println("</td>");   	   	
		writer.println("</table>");			
		writer.println("</form>");
		writer.println("</div>");
	}

	private void printRemoteStopTrans(PrintWriter writer) {
		// Print navigation div
		writer.println("<div class=\"op-menu\">");
		writer.println("<ul>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/ChangeAvailability\" >Change Availability</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/ChangeConfiguration\">Change Configuration</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/ClearCache\">Clear Cache</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/GetDiagnostics\">Get Diagnostics</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/RemoteStartTransaction\">Remote Start Transaction</a></li>");
		writer.println("<li><a class=\"highlight\" href=\"" + contextPath + servletPath + "/RemoteStopTransaction\">Remote Stop Transaction</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/Reset\">Reset</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/UnlockConnector\">Unlock Connector</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/UpdateFirmware\">Update Firmware</a></li>");
		writer.println("</ul>");
		writer.println("</div>");

		// Print the div on the right
		writer.println("<div class=\"op-content\">");
		writer.println("<form method=\"POST\" action=\""+ contextPath + servletPath + "/RemoteStopTransaction\">");	

		printChargePoints(writer);

		writer.println("<b>Parameters</b><hr>");
		writer.println("<table class=\"params\">");
		writer.println("<tr><td>");		
		writer.println("Transaction Id (integer): ");
		writer.println("</td><td>");
		writer.println("<input type=\"number\" name=\"transactionId\">");		
		writer.println("</td></tr>");
		writer.println("<tr><td></td>");
		writer.println("<td id=\"add_space\">");
		writer.println("<input type=\"submit\" value=\"Perform\">");
		writer.println("</td>");   	   	
		writer.println("</table>");			
		writer.println("</form>");
		writer.println("</div>");
	}

	private void printReset(PrintWriter writer) throws IOException {
		// Print navigation div
		writer.println("<div class=\"op-menu\">");
		writer.println("<ul>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/ChangeAvailability\" >Change Availability</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/ChangeConfiguration\">Change Configuration</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/ClearCache\">Clear Cache</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/GetDiagnostics\">Get Diagnostics</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/RemoteStartTransaction\">Remote Start Transaction</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/RemoteStopTransaction\">Remote Stop Transaction</a></li>");
		writer.println("<li><a class=\"highlight\" href=\"" + contextPath + servletPath + "/Reset\">Reset</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/UnlockConnector\">Unlock Connector</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/UpdateFirmware\">Update Firmware</a></li>");
		writer.println("</ul>");
		writer.println("</div>");

		// Print the div on the right
		writer.println("<div class=\"op-content\">");
		writer.println("<form method=\"POST\" action=\""+ contextPath + servletPath + "/Reset\">");	

		printChargePoints(writer);

		writer.println("<b>Parameters</b><hr>");
		writer.println("<table class=\"params\">");
		writer.println("<tr><td>");
		writer.println("Reset type:");
		writer.println("</td><td>");
		writer.println("<select name=\"resetType\">");
		writer.println("<option value=\"Hard\">Hard</option>");
		writer.println("<option value=\"Soft\">Soft</option>");
		writer.println("</select>");
		writer.println("</td></tr>");
		writer.println("<tr><td></td>");
		writer.println("<td id=\"add_space\">");
		writer.println("<input type=\"submit\" value=\"Perform\">");
		writer.println("</td>");   	   	
		writer.println("</table>");			
		writer.println("</form>");
		writer.println("</div>");
	}

	private void printUnlockConnector(PrintWriter writer) {
		// Print navigation div
		writer.println("<div class=\"op-menu\">");
		writer.println("<ul>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/ChangeAvailability\" >Change Availability</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/ChangeConfiguration\">Change Configuration</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/ClearCache\">Clear Cache</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/GetDiagnostics\">Get Diagnostics</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/RemoteStartTransaction\">Remote Start Transaction</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/RemoteStopTransaction\">Remote Stop Transaction</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/Reset\">Reset</a></li>");
		writer.println("<li><a class=\"highlight\" href=\"" + contextPath + servletPath + "/UnlockConnector\">Unlock Connector</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/UpdateFirmware\">Update Firmware</a></li>");
		writer.println("</ul>");
		writer.println("</div>");

		// Print the div on the right
		writer.println("<div class=\"op-content\">");
		writer.println("<form method=\"POST\" action=\""+ contextPath + servletPath + "/UnlockConnector\">");	

		printChargePoints(writer);

		writer.println("<b>Parameters</b><hr>");
		writer.println("<table class=\"params\">");
		writer.println("<tr><td>");		
		writer.println("Connector Id (integer, not 0): ");
		writer.println("</td><td>");
		writer.println("<input type=\"number\" min=\"1\" name=\"ConnectorId\">");		
		writer.println("</td></tr>");
		writer.println("<tr><td></td>");
		writer.println("<td id=\"add_space\">");
		writer.println("<input type=\"submit\" value=\"Perform\">");
		writer.println("</td>");   	   	
		writer.println("</table>");			
		writer.println("</form>");
		writer.println("</div>");
	}

	private void printUpdateFirmware(PrintWriter writer) {
		// Print navigation div
		writer.println("<div class=\"op-menu\">");
		writer.println("<ul>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/ChangeAvailability\" >Change Availability</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/ChangeConfiguration\">Change Configuration</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/ClearCache\">Clear Cache</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/GetDiagnostics\">Get Diagnostics</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/RemoteStartTransaction\">Remote Start Transaction</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/RemoteStopTransaction\">Remote Stop Transaction</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/Reset\">Reset</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/UnlockConnector\">Unlock Connector</a></li>");
		writer.println("<li><a class=\"highlight\" href=\"" + contextPath + servletPath + "/UpdateFirmware\">Update Firmware</a></li>");
		writer.println("</ul>");
		writer.println("</div>");	

		// Print the div on the right
		writer.println("<div class=\"op-content\">");
		writer.println("<form method=\"POST\" action=\""+ contextPath + servletPath + "/UpdateFirmware\">");

		printChargePoints(writer);

		writer.println("<b>Parameters</b><hr>");
		writer.println("<table class=\"params\">");		
		writer.println("<tr><td>");		
		writer.println("Location (URI):");
		writer.println("</td><td>");
		writer.println("<input type=\"text\" name=\"location\">");
		writer.println("</td></tr>");
		writer.println("<tr><td>");	
		writer.println("Retries (integer):");
		writer.println("</td><td>");
		writer.println("<input type=\"number\" min=\"0\" name=\"retries\">");
		writer.println("</td></tr><tr><td>");
		writer.println("Retrieve Date (ex: 2011-12-21T11:33:23Z):");
		writer.println("</td><td>");
		writer.println("<input type=\"datetime\" name=\"retrieveDate\">");
		writer.println("</td></tr><tr><td>");
		writer.println("Retry Interval (integer):");
		writer.println("</td><td>");
		writer.println("<input type=\"number\" min=\"0\" name=\"retryInterval\">");		
		writer.println("</td></tr>");
		writer.println("<tr><td></td>");
		writer.println("<td id=\"add_space\">");
		writer.println("<input type=\"submit\" value=\"Perform\">");
		writer.println("</td>");   	   	
		writer.println("</table>");			
		writer.println("</form>");
		writer.println("</div>");
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

	private void printChargePoints(PrintWriter writer) {		
		writer.println("<b>Charge Points</b><hr>");
		writer.println("<table>");
		writer.println("<tr><td style=\"vertical-align:top\">");
		writer.println("<input type=\"button\" value=\"Select All\" onClick=\"selectAll(document.getElementById('cp_items'))\">");
		writer.println("<input type=\"button\" value=\"Select None\" onClick=\"selectNone(document.getElementById('cp_items'))\">");
		writer.println("</td><td>");
		writer.println("<select name=\"cp_items\" id=\"cp_items\" size=\"5\" multiple>");

		for(String key : chargePointsList.keySet()) {
			String value = chargePointsList.get(key);
			writer.print("<option value=\"" + key + ";" + value + "\"> ");
			writer.print(key);
			writer.print(" &#8212; ");
			writer.print(value);
			writer.print("</option>");
		}

		writer.println("</select>");
		writer.println("</td></tr>");
		writer.println("</table>");
		writer.println("<br>");
	}
}
