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
import de.rwth.idsg.steve.common.utils.InputUtils;


/**
 * This servlet provides the Web interface to manage charging points with OCPP v1.2
 * 
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * 
 */
public class ServletOperationsV12 extends HttpServlet {

	private static final long serialVersionUID = 1L;
	String contextPath, servletPath;
	HashMap<String,String> chargePointsList;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		// Get the request details
		String command = request.getPathInfo();				
		contextPath = request.getContextPath();
		servletPath = contextPath + request.getServletPath();	

		if (command == null || command.length() == 0) {
			// Only refresh the list of charge points when displaying operations page
			chargePointsList = ClientDBAccess.getChargePoints("1.2");
			// Redirect to the page of the first operation
			response.sendRedirect(servletPath + "/ChangeAvailability");
			return;
		}
		
		request.setAttribute("contextPath", contextPath );
		request.setAttribute("servletPath", servletPath );
		request.setAttribute("cpList", chargePointsList );
		
		// Command is equal to the JSP file name. Forward to JSP.
		String path = "/WEB-INF/jsp/op12" + command + ".jsp";
		request.getRequestDispatcher(path).forward(request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		String command = request.getPathInfo();	

		// Retrieve values from HTML select multiple
		String[] chargePointItems = request.getParameterValues("cp_items");

		if (chargePointItems == null) {
			throw new InputException(ExceptionMessage.CHARGEPOINTS_NULL);	
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
	
	/////// HTTP POST: Process Request /////// 
		
	// chargePointItem[0] : chargebox id
	// chargePointItem[1] : endpoint (IP) address
	
	private StringBuilder processChangeAvail(HttpServletRequest request, String[] chargePointItems) {
		String connectorIdSTR = request.getParameter("connectorId");
		String availType = request.getParameter("availType");
		InputUtils.checkNullOrEmpty(availType);
		
		int connectorId = InputUtils.chooseInt(connectorIdSTR);
		
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
		InputUtils.checkNullOrEmpty(confKey, value);
		
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
		InputUtils.checkNullOrEmpty(location, retriesSTR, retryIntervalSTR, startTime, stopTime);
		
		int retries = InputUtils.toInt(retriesSTR);
		int retryInterval = InputUtils.toInt(retryIntervalSTR);
		
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
		String idTag = request.getParameter("idTag");		
		InputUtils.checkNullOrEmpty(connectorIdSTR, idTag);
		
		int connectorId = InputUtils.toNonZeroInt(connectorIdSTR);
		
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
		InputUtils.checkNullOrEmpty(transactionIdSTR);
		
		int transactionId = InputUtils.toInt(transactionIdSTR);
		
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
		InputUtils.checkNullOrEmpty(resetType);
		
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
		InputUtils.checkNullOrEmpty(connectorIdSTR);
		
		int connectorId = InputUtils.toNonZeroInt(connectorIdSTR);
		
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
		InputUtils.checkNullOrEmpty(location, retriesSTR, retryIntervalSTR, retrieveDate);
		
		int retries = InputUtils.toInt(retriesSTR);
		int retryInterval = InputUtils.toInt(retryIntervalSTR);
		
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