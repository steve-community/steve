package html;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class CPS_Servlet extends HttpServlet {

	private static final Logger LOG = LoggerFactory.getLogger(CPS_Servlet.class);
	private static final long serialVersionUID = 8576766110806723303L;
	String contextPath, servletPath;
	HashMap<String,String> chargePointsList;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		// Get the request details
		String command = request.getPathInfo();				
		contextPath = request.getContextPath();
		servletPath = request.getServletPath();	

		PrintWriter writer = response.getWriter();		

		if (command == null || command.length() == 0) {
			printHead(response, writer);
			printHomepage(writer);

		} else if (command.equals("/")){
			printHead(response, writer);
			printHomepage(writer);

		} else if (command.equals("/log")){
			response.setContentType("text/plain");			
			printLogFile(writer);
			writer.close();	
			return;

		} else if (command.equals("/reservation")){
			printHead(response, writer);
			printReservationPage(writer);

		} else if (command.equals("/operations")){
			// Only refresh the list of charge points when displaying operations page
			chargePointsList = getChargePoints();
			// Redirect to the page of the first operation
			response.sendRedirect(contextPath + servletPath + "/operations/ChangeAvailability");

		} else if (command.equals("/operations/ChangeAvailability")){	
			printHead(response, writer);
			printChangeAvail(writer);

		} else if (command.equals("/operations/ChangeConfiguration")){
			printHead(response, writer);
			printChangeConf(writer);

		} else if (command.equals("/operations/ClearCache")){
			printHead(response, writer);
			printClearCache(writer);

		} else if (command.equals("/operations/GetDiagnostics")){		
			printHead(response, writer);
			printGetDiagnostics(writer);

		} else if (command.equals("/operations/RemoteStartTransaction")){		
			printHead(response, writer);
			printRemoteStartTrans(writer);

		} else if (command.equals("/operations/RemoteStopTransaction")){			
			printHead(response, writer);
			printRemoteStopTrans(writer);

		} else if (command.equals("/operations/Reset")){			
			printHead(response, writer);
			printReset(writer);

		} else if (command.equals("/operations/UnlockConnector")){			
			printHead(response, writer);
			printUnlockConnector(writer);

		} else if (command.equals("/operations/UpdateFirmware")){		
			printHead(response, writer);
			printUpdateFirmware(writer);
		}

		writer.println("</div>");
		writer.println("</body></html>");
		writer.close();	
	}


	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		PrintWriter writer = response.getWriter();
		String command = request.getPathInfo();	

		//// The command can be booking of a new reservation ... ////

		if (command.equals("/reservation/book")){
			String idTag = request.getParameter("idTag");
			String chargeBoxId = request.getParameter("chargeBoxId");
			String startDatetime = request.getParameter("startDatetime");
			String stopDatetime = request.getParameter("stopDatetime");

			bookReservation(idTag, chargeBoxId, startDatetime, stopDatetime);
			response.sendRedirect(contextPath + servletPath + "/reservation");

		} else if (command.equals("/reservation/delete")){
			int connector_pk = Integer.parseInt(request.getParameter("reservation_pk"));

			deleteReservation(connector_pk);
			response.sendRedirect(contextPath + servletPath + "/reservation");
		}

		//// ... or the command can be a charge point operation. ////

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

		if (command.equals("/operations/ChangeAvailability")){
			String availType = request.getParameter("availType");
			int connectorId = Integer.parseInt(request.getParameter("connectorId"));
			ChangeAvailabilityRequest req = cpsClient.prepareChangeAvailability(connectorId, availType);

			for(String temp: chargePointItems){
				String[] chargePointItem = temp.split(";");
				result = cpsClient.sendChangeAvailability(chargePointItem[0], chargePointItem[1], req);
				writer.println(result);
			}

		} else if (command.equals("/operations/ChangeConfiguration")) {
			String confKey = request.getParameter("confKey");
			String value = request.getParameter("value");
			ChangeConfigurationRequest req = cpsClient.prepareChangeConfiguration(confKey, value);

			for(String temp: chargePointItems){
				String[] chargePointItem = temp.split(";");
				result = cpsClient.sendChangeConfiguration(chargePointItem[0], chargePointItem[1], req);
				writer.println(result);
			}

		} else if (command.equals("/operations/ClearCache")) {
			ClearCacheRequest req = cpsClient.prepareClearCache();

			for(String temp: chargePointItems){
				String[] chargePointItem = temp.split(";");
				result = cpsClient.sendClearCache(chargePointItem[0], chargePointItem[1], req);
				writer.println(result);
			}	

		} else if (command.equals("/operations/GetDiagnostics")) {
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

		} else if (command.equals("/operations/RemoteStartTransaction")) {
			int connectorId = Integer.parseInt(request.getParameter("connectorId"));
			String idTag = request.getParameter("idTag");
			RemoteStartTransactionRequest req = cpsClient.prepareRemoteStartTransaction(connectorId, idTag);

			for(String temp: chargePointItems){
				String[] chargePointItem = temp.split(";");
				result = cpsClient.sendRemoteStartTransaction(chargePointItem[0], chargePointItem[1], req);
				writer.println(result);
			}

		} else if (command.equals("/operations/RemoteStopTransaction")) {
			int transactionId = Integer.parseInt(request.getParameter("transactionId"));
			RemoteStopTransactionRequest req = cpsClient.prepareRemoteStopTransaction(transactionId);

			for(String temp: chargePointItems){
				String[] chargePointItem = temp.split(";");
				result = cpsClient.sendRemoteStopTransaction(chargePointItem[0], chargePointItem[1], req);
				writer.println(result);
			}

		} else if (command.equals("/operations/Reset")){			
			String resetType = request.getParameter("resetType");
			ResetRequest req = cpsClient.prepareReset(resetType);

			for(String temp: chargePointItems){
				String[] chargePointItem = temp.split(";");
				result = cpsClient.sendReset(chargePointItem[0], chargePointItem[1], req);
				writer.println(result);
			}

		} else if (command.equals("/operations/UnlockConnector")) {
			int connectorId = Integer.parseInt(request.getParameter("connectorId"));
			UnlockConnectorRequest req = cpsClient.prepareUnlockConnector(connectorId);

			for(String temp: chargePointItems){
				String[] chargePointItem = temp.split(";");
				result = cpsClient.sendUnlockConnector(chargePointItem[0], chargePointItem[1], req);
				writer.println(result);
			}

		} else if (command.equals("/operations/UpdateFirmware")){
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

	private void printHead(HttpServletResponse response, PrintWriter writer) throws IOException {		
		// Start printing regular HTML content
		response.setContentType("text/html");

		writer.println("<!DOCTYPE html>");
		writer.println("<html>");
		writer.println("<head>");
		writer.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + contextPath + "/style.css\">");
		writer.println("<script src=\"" + contextPath + "/script.js\" type=\"text/javascript\"></script>");
		writer.println("<title>SteVe - Steckdosenverwaltung</title>");
		writer.println("</head>");

		writer.println("<body>");		
		writer.println("<div id=\"wrapper\">");
		writer.println("<table class=\"top-menu\">");
		writer.println("<tr><td>");
		writer.println("<img src=\""+ contextPath + "/logo.png\" height=\"100\">");
		writer.println("</td><td>");
		writer.println("<button onclick=\"window.location.href='" + contextPath + servletPath + "'\">HOME</button>");
		writer.println("<button onclick=\"window.location.href='" + contextPath + servletPath + "/reservation'\">RESERVATION</button>");
		writer.println("<button onclick=\"window.location.href='" + contextPath + servletPath + "/operations'\">OPERATIONS</button>");
		writer.println("<button onclick=\"window.location.href='" + contextPath + servletPath + "/log'\">LOG</button>");
		writer.println("</td></tr>");
		writer.println("</table>");

	}


	private void printHomepage(PrintWriter writer) {
		writer.println("<div id=\"reserv\">");
		writer.println("<b>Welcome!</b><hr>");
		writer.println("<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Qui ita affectus, beatum esse numquam probabis; Nihil opus est exemplis hoc facere longius. Itaque ab his ordiamur. Quid turpius quam sapientis vitam ex insipientium sermone pendere? Nam adhuc, meo fortasse vitio, quid ego quaeram non perspicis. Non igitur bene. Dic in quovis conventu te omnia facere, ne doleas. Duo Reges: constructio interrete. Iam enim adesse poterit.</p>");
		writer.println("<p>Ergo, inquit, tibi Q. Neque solum ea communia, verum etiam paria esse dixerunt. Itaque nostrum est-quod nostrum dico, artis est-ad ea principia, quae accepimus. In qua quid est boni praeter summam voluptatem, et eam sempiternam? Qui enim voluptatem ipsam contemnunt, iis licet dicere se acupenserem maenae non anteponere. Nos quidem Virtutes sic natae sumus, ut tibi serviremus, aliud negotii nihil habemus. Mihi vero, inquit, placet agi subtilius et, ut ipse dixisti, pressius. Esse enim quam vellet iniquus iustus poterat inpune. Ut in geometria, prima si dederis, danda sunt omnia.</p>");
		writer.println("<p>Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat.</p>");
		writer.println("<p>Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi.</p>");
		writer.println("</div>");
	}

	///////////////// Log Stuff /////////////////

	private void printLogFile(PrintWriter writer){
		File logDir = new File(System.getProperty("catalina.base"), "logs");
		File cxfLog = new File(logDir, "cxf.log");
		BufferedReader bufferedReader = null;
		try {
			String sCurrentLine;
			bufferedReader = new BufferedReader(new FileReader(cxfLog));
			while ((sCurrentLine = bufferedReader.readLine()) != null) {
				writer.println(sCurrentLine);
			}
		} catch (Exception ex) {
			LOG.error(ex.getMessage());
		} finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	///////////////// Reservation Stuff /////////////////

	private void bookReservation(String idTag, String chargeBoxId, String startDatetime, String stopDatetime) {

		Connection connect = null;
		PreparedStatement pt = null;
		try { 
			// Prepare Database Access
			connect = Utils.getConnectionFromPool();
			connect.setAutoCommit(false);
			pt = connect.prepareStatement("INSERT INTO reservation (idTag, chargeBoxId, startDatetime, stopDatetime) VALUES (?,?,?,?)");

			// Set the parameter indices  
			pt.setString(1, idTag);
			pt.setString(2, chargeBoxId);
			pt.setString(3, startDatetime);
			pt.setString(4, stopDatetime);

			// Insert the new status
			int count = pt.executeUpdate();
			// Validate the change
			Utils.validateDMLChanges(count);          
			// Now we can commit
			connect.commit();

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		} finally {
			Utils.releaseResources(connect, pt, null);
		}		
	}

	private void deleteReservation(int reservation_pk) {

		Connection connect = null;
		PreparedStatement pt = null;
		try { 
			// Prepare Database Access
			connect = Utils.getConnectionFromPool();
			connect.setAutoCommit(false);
			pt = connect.prepareStatement("DELETE FROM reservation WHERE reservation_pk=?");

			// Set the parameter indices  
			pt.setInt(1, reservation_pk);

			pt.executeUpdate();
			connect.commit();

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		} finally {
			Utils.releaseResources(connect, pt, null);
		}		
	}

	private void printReservationPage(PrintWriter writer) {

		writer.println("<div id=\"reserv\">");
		printExistingReservations(writer);
		writer.println("<br>");
		printBookReservation(writer);
		writer.println("<br>");
		printDeleteReservation(writer);
		writer.println("</div>");			
	}

	private void printExistingReservations(PrintWriter writer) {

		writer.println("<b>Existing Reservations</b><hr>");
		writer.println("<center>");	
		writer.println("<table class=\"res\" >");
		writer.println("<tr><th>reservation_pk</th><th>idTag</th><th>chargeBoxId</th><th>startDatetime</th><th>stopDatetime</th><th>active</th></tr>");

		Connection connect = null;
		PreparedStatement pt = null;
		ResultSet rs = null;
		try {	
			// Prepare Database Access
			connect = Utils.getConnectionFromPool();
			pt = connect.prepareStatement("SELECT reservation_pk, idTag, chargeBoxId, DATE_FORMAT(startDatetime, '%Y-%m-%d %H:%i'), DATE_FORMAT(stopDatetime, '%Y-%m-%d %H:%i'), active FROM reservation");
			rs = pt.executeQuery();

			while( rs.next() ) {
				writer.print("<tr>");
				writer.print("<td>");
				writer.print(rs.getInt(1));
				writer.print("</td>");
				writer.print("<td>");
				writer.print(rs.getString(2));
				writer.print("</td>");
				writer.print("<td>");
				writer.print(rs.getString(3));
				writer.print("</td>");
				writer.print("<td>");
				writer.print(rs.getString(4));
				writer.print("</td>");
				writer.print("<td>");
				writer.print(rs.getString(5));
				writer.print("</td>");
				writer.print("<td>");
				writer.print(rs.getBoolean(6));
				writer.print("</td>");

				writer.println("</tr>");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		} finally {
			Utils.releaseResources(connect, pt, rs);
		}			
		writer.println("</table>");
		writer.println("</center>");
	}

	private void printBookReservation(PrintWriter writer) {

		writer.println("<b>Book A New Reservation</b><hr>");
		writer.println("<center>");	
		writer.println("<form method=\"POST\" action=\""+ contextPath + servletPath + "/reservation/book\">");
		writer.println("<table>");		
		writer.println("<tr><td>");
		writer.println("idTag (of the user): ");
		writer.println("</td><td>");
		writer.println("<input type=\"text\" name=\"idTag\">");
		writer.println("</td></tr>");
		writer.println("<tr><td>");
		writer.println("chargeBoxId (of the charging point): ");
		writer.println("</td><td>");
		writer.println("<input type=\"text\" name=\"chargeBoxId\">");
		writer.println("</td></tr><tr><td>");
		writer.println("Start date and time (ex: 2011-12-21 11:30):");
		writer.println("</td><td>");
		writer.println("<input type=\"text\" name=\"startDatetime\">");
		writer.println("</td></tr><tr><td>");
		writer.println("Stop date and time (ex: 2011-12-21 11:30):");
		writer.println("</td><td>");
		writer.println("<input type=\"text\" name=\"stopDatetime\">");		
		writer.println("</td></tr>");
		writer.println("<tr><td></td>");
		writer.println("<td id=\"add_space\">");
		writer.println("<input type=\"submit\" value=\"Book\">");
		writer.println("</td></tr>");   	   	
		writer.println("</table>");		
		writer.println("</form>");
		writer.println("</center>");
	}

	private void printDeleteReservation(PrintWriter writer) {

		writer.println("<b>Delete An Existing Reservation</b><hr>");
		writer.println("<center>");	
		writer.println("<form method=\"POST\" action=\""+ contextPath + servletPath + "/reservation/delete\">");
		writer.println("<table>");		
		writer.println("<tr><td>");
		writer.println("reservation_pk: ");
		writer.println("</td><td>");
		writer.println("<input type=\"number\" min=\"1\" name=\"reservation_pk\">");
		writer.println("</td></tr>");
		writer.println("<tr><td></td>");
		writer.println("<td id=\"add_space\">");
		writer.println("<input type=\"submit\" value=\"Delete\">");
		writer.println("</td></tr>"); 
		writer.println("</table>");		
		writer.println("</form>");
		writer.println("</center>");
	}


	///////////////// Operations Stuff /////////////////

	private void printChangeAvail(PrintWriter writer) {
		// Print navigation div
		writer.println("<div id=\"menu\">");
		writer.println("<ul>");
		writer.println("<li><a class=\"highlight\" href=\"" + contextPath + servletPath + "/operations/ChangeAvailability\" >Change Availability</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/ChangeConfiguration\">Change Configuration</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/ClearCache\">Clear Cache</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/GetDiagnostics\">Get Diagnostics</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/RemoteStartTransaction\">Remote Start Transaction</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/RemoteStopTransaction\">Remote Stop Transaction</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/Reset\">Reset</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/UnlockConnector\">Unlock Connector</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/UpdateFirmware\">Update Firmware</a></li>");
		writer.println("</ul>");
		writer.println("</div>");	

		// Print the div on the right
		writer.println("<div id=\"content\">");
		writer.println("<form method=\"POST\" action=\""+ contextPath + servletPath + "/operations/ChangeAvailability\">");

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
		writer.println("<div id=\"menu\">");
		writer.println("<ul>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/ChangeAvailability\" >Change Availability</a></li>");
		writer.println("<li><a class=\"highlight\" href=\"" + contextPath + servletPath + "/operations/ChangeConfiguration\">Change Configuration</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/ClearCache\">Clear Cache</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/GetDiagnostics\">Get Diagnostics</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/RemoteStartTransaction\">Remote Start Transaction</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/RemoteStopTransaction\">Remote Stop Transaction</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/Reset\">Reset</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/UnlockConnector\">Unlock Connector</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/UpdateFirmware\">Update Firmware</a></li>");
		writer.println("</ul>");
		writer.println("</div>");

		// Print the div on the right
		writer.println("<div id=\"content\">");
		writer.println("<form method=\"POST\" action=\""+ contextPath + servletPath + "/operations/ChangeConfiguration\">");

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
		writer.println("<div id=\"menu\">");
		writer.println("<ul>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/ChangeAvailability\" >Change Availability</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/ChangeConfiguration\">Change Configuration</a></li>");
		writer.println("<li><a class=\"highlight\" href=\"" + contextPath + servletPath + "/operations/ClearCache\">Clear Cache</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/GetDiagnostics\">Get Diagnostics</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/RemoteStartTransaction\">Remote Start Transaction</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/RemoteStopTransaction\">Remote Stop Transaction</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/Reset\">Reset</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/UnlockConnector\">Unlock Connector</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/UpdateFirmware\">Update Firmware</a></li>");
		writer.println("</ul>");
		writer.println("</div>");	

		// Print the div on the right
		writer.println("<div id=\"content\">");
		writer.println("<form method=\"POST\" action=\""+ contextPath + servletPath + "/operations/ClearCache\">");	

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
		writer.println("<div id=\"menu\">");
		writer.println("<ul>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/ChangeAvailability\" >Change Availability</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/ChangeConfiguration\">Change Configuration</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/ClearCache\">Clear Cache</a></li>");
		writer.println("<li><a class=\"highlight\" href=\"" + contextPath + servletPath + "/operations/GetDiagnostics\">Get Diagnostics</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/RemoteStartTransaction\">Remote Start Transaction</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/RemoteStopTransaction\">Remote Stop Transaction</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/Reset\">Reset</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/UnlockConnector\">Unlock Connector</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/UpdateFirmware\">Update Firmware</a></li>");
		writer.println("</ul>");
		writer.println("</div>");	

		// Print the div on the right
		writer.println("<div id=\"content\">");
		writer.println("<form method=\"POST\" action=\""+ contextPath + servletPath + "/operations/GetDiagnostics\">");	

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
		writer.println("<div id=\"menu\">");
		writer.println("<ul>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/ChangeAvailability\" >Change Availability</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/ChangeConfiguration\">Change Configuration</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/ClearCache\">Clear Cache</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/GetDiagnostics\">Get Diagnostics</a></li>");
		writer.println("<li><a class=\"highlight\" href=\"" + contextPath + servletPath + "/operations/RemoteStartTransaction\">Remote Start Transaction</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/RemoteStopTransaction\">Remote Stop Transaction</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/Reset\">Reset</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/UnlockConnector\">Unlock Connector</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/UpdateFirmware\">Update Firmware</a></li>");
		writer.println("</ul>");
		writer.println("</div>");	

		// Print the div on the right
		writer.println("<div id=\"content\">");
		writer.println("<form method=\"POST\" action=\""+ contextPath + servletPath + "/operations/RemoteStartTransaction\">");

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
		writer.println("<div id=\"menu\">");
		writer.println("<ul>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/ChangeAvailability\" >Change Availability</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/ChangeConfiguration\">Change Configuration</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/ClearCache\">Clear Cache</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/GetDiagnostics\">Get Diagnostics</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/RemoteStartTransaction\">Remote Start Transaction</a></li>");
		writer.println("<li><a class=\"highlight\" href=\"" + contextPath + servletPath + "/operations/RemoteStopTransaction\">Remote Stop Transaction</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/Reset\">Reset</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/UnlockConnector\">Unlock Connector</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/UpdateFirmware\">Update Firmware</a></li>");
		writer.println("</ul>");
		writer.println("</div>");

		// Print the div on the right
		writer.println("<div id=\"content\">");
		writer.println("<form method=\"POST\" action=\""+ contextPath + servletPath + "/operations/RemoteStopTransaction\">");	

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
		writer.println("<div id=\"menu\">");
		writer.println("<ul>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/ChangeAvailability\" >Change Availability</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/ChangeConfiguration\">Change Configuration</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/ClearCache\">Clear Cache</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/GetDiagnostics\">Get Diagnostics</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/RemoteStartTransaction\">Remote Start Transaction</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/RemoteStopTransaction\">Remote Stop Transaction</a></li>");
		writer.println("<li><a class=\"highlight\" href=\"" + contextPath + servletPath + "/operations/Reset\">Reset</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/UnlockConnector\">Unlock Connector</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/UpdateFirmware\">Update Firmware</a></li>");
		writer.println("</ul>");
		writer.println("</div>");

		// Print the div on the right
		writer.println("<div id=\"content\">");
		writer.println("<form method=\"POST\" action=\""+ contextPath + servletPath + "/operations/Reset\">");	

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
		writer.println("<div id=\"menu\">");
		writer.println("<ul>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/ChangeAvailability\" >Change Availability</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/ChangeConfiguration\">Change Configuration</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/ClearCache\">Clear Cache</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/GetDiagnostics\">Get Diagnostics</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/RemoteStartTransaction\">Remote Start Transaction</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/RemoteStopTransaction\">Remote Stop Transaction</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/Reset\">Reset</a></li>");
		writer.println("<li><a class=\"highlight\" href=\"" + contextPath + servletPath + "/operations/UnlockConnector\">Unlock Connector</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/UpdateFirmware\">Update Firmware</a></li>");
		writer.println("</ul>");
		writer.println("</div>");

		// Print the div on the right
		writer.println("<div id=\"content\">");
		writer.println("<form method=\"POST\" action=\""+ contextPath + servletPath + "/operations/UnlockConnector\">");	

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
		writer.println("<div id=\"menu\">");
		writer.println("<ul>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/ChangeAvailability\" >Change Availability</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/ChangeConfiguration\">Change Configuration</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/ClearCache\">Clear Cache</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/GetDiagnostics\">Get Diagnostics</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/RemoteStartTransaction\">Remote Start Transaction</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/RemoteStopTransaction\">Remote Stop Transaction</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/Reset\">Reset</a></li>");
		writer.println("<li><a href=\"" + contextPath + servletPath + "/operations/UnlockConnector\">Unlock Connector</a></li>");
		writer.println("<li><a class=\"highlight\" href=\"" + contextPath + servletPath + "/operations/UpdateFirmware\">Update Firmware</a></li>");
		writer.println("</ul>");
		writer.println("</div>");	

		// Print the div on the right
		writer.println("<div id=\"content\">");
		writer.println("<form method=\"POST\" action=\""+ contextPath + servletPath + "/operations/UpdateFirmware\">");

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
		writer.println("<input type=\"button\" value=\"Select All\" style=\"width:100px\" onClick=\"selectAll(document.getElementById('cp_items'))\">");
		writer.println("<input type=\"button\" value=\"Select None\" style=\"width:100px\" onClick=\"selectNone(document.getElementById('cp_items'))\">");
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
