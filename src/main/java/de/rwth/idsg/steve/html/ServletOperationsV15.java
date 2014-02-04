package de.rwth.idsg.steve.html;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
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
import de.rwth.idsg.steve.common.utils.InputUtils;


/**
 * This servlet provides the Web interface to manage charging points with OCPP v1.5
 * 
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * 
 */
@WebServlet("/manager/operations/v1.5/*")
public class ServletOperationsV15 extends HttpServlet {

	private static final long serialVersionUID = 1L;
	String contextPath, servletPath;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		// Get the request details
		String command = request.getPathInfo();				
		contextPath = request.getContextPath();
		servletPath = contextPath + request.getServletPath();			

		if (command == null || command.length() == 0) {
			// Redirect to the page of the first operation
			response.sendRedirect(servletPath + "/ChangeAvailability");
			return;
			
		} else if (command.equals("/RemoteStartTransaction") || command.equals("/ReserveNow")) {
			request.setAttribute("userList", ClientDBAccess.getUsers() );
		}	
		
		request.setAttribute("contextPath", contextPath );
		request.setAttribute("servletPath", servletPath );
		request.setAttribute("cpList", ClientDBAccess.getChargePoints("1.5") );
				
		// Command is equal to the JSP file name. Forward to JSP.
		String path = "/WEB-INF/jsp/op15" + command + ".jsp";
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
	
	/////// HTTP POST: Process Request /////// 
	
	// chargePointItem[0] : chargebox id
	// chargePointItem[1] : endpoint (IP) address
	
	private StringBuilder processChangeAvail(HttpServletRequest request, String[] chargePointItems) {
		String connectorIdSTR = request.getParameter("connectorId");
		String availType = request.getParameter("availType");
		InputUtils.checkNullOrEmpty(availType);
		
		int connectorId = InputUtils.chooseInt(connectorIdSTR);
				
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
		InputUtils.checkNullOrEmpty(confKey, 	value);
		
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
		
		String startDate = request.getParameter("startDate");
		String startTime = request.getParameter("startTime");
		String stopDate = request.getParameter("stopDate");
		String stopTime = request.getParameter("stopTime");	
		
		InputUtils.checkNullOrEmpty(location);
		
		int retries = -1;
		if ( !InputUtils.isNullOrEmpty(retriesSTR) ) {
			retries = InputUtils.toInt(retriesSTR);
		}
		
		int retryInterval = -1;
		if ( !InputUtils.isNullOrEmpty(retryIntervalSTR) ) {
			retryInterval = InputUtils.toInt(retryIntervalSTR);
		}
		
		String startDateTime = null;
		if ( !InputUtils.isNullOrEmpty(startDate) ) {
			if (InputUtils.isNullOrEmpty(startTime)) startTime = "00:00";
			startDateTime = startDate + " " + startTime;
		}
		
		String stopDateTime = null;
		if ( !InputUtils.isNullOrEmpty(stopDate) ) {
			if (InputUtils.isNullOrEmpty(stopTime)) stopTime = "00:00";
			stopDateTime = stopDate + " " + stopTime;
		}
		
		ChargePointService15_Client cpsClient = new ChargePointService15_Client();
		GetDiagnosticsRequest req = cpsClient.prepareGetDiagnostics(location, retries, retryInterval, startDateTime, stopDateTime);

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
		
		int connectorId = InputUtils.toInt(connectorIdSTR);
		
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
		InputUtils.checkNullOrEmpty(transactionIdSTR);
		
		int transactionId = InputUtils.toInt(transactionIdSTR);
		
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
		InputUtils.checkNullOrEmpty(resetType);
		
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
		InputUtils.checkNullOrEmpty(connectorIdSTR);
		
		int connectorId = InputUtils.toInt(connectorIdSTR);
		
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
		String retrieveTime = request.getParameter("retrieveTime");	
		
		InputUtils.checkNullOrEmpty(location, retrieveDate);
		
		int retries = -1;
		if ( !InputUtils.isNullOrEmpty(retriesSTR) ) {
			retries = InputUtils.toInt(retriesSTR);
		}
		
		int retryInterval = -1;
		if ( !InputUtils.isNullOrEmpty(retryIntervalSTR) ) {
			retryInterval = InputUtils.toInt(retryIntervalSTR);
		}
		
		if (InputUtils.isNullOrEmpty(retrieveTime)) retrieveTime = "00:00";
		String retrieveDateTime = retrieveDate + " " + retrieveTime;
		
		ChargePointService15_Client cpsClient = new ChargePointService15_Client();
		UpdateFirmwareRequest req = cpsClient.prepareUpdateFirmware(location, retries, retrieveDateTime, retryInterval);

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
		String expiryDate = request.getParameter("expiryDate");
		String expiryTime = request.getParameter("expiryTime");
		String idTag = request.getParameter("idTag");
		String parentIdTagSTR = request.getParameter("parentIdTag");
		InputUtils.checkNullOrEmpty(connectorIdSTR, expiryDate, idTag);
		
		int connectorId = InputUtils.toInt(connectorIdSTR);
		
		String parentIdTag = null;
		if ( !InputUtils.isNullOrEmpty(parentIdTagSTR) ) {
			parentIdTag = parentIdTagSTR;
		}
		
		// There's only one item in chargePointItems.
		String[] chargePointItem = chargePointItems[0].split(";");
		
		if (InputUtils.isNullOrEmpty(expiryTime)) expiryTime = "00:00";
		String expiryDateTime = expiryDate + " " + expiryTime;
				
		ChargePointService15_Client cpsClient = new ChargePointService15_Client();		
		String result = cpsClient.reserveNow(chargePointItem[0], chargePointItem[1], connectorId, expiryDateTime, idTag, parentIdTag);
		StringBuilder builder = new StringBuilder(result);
		return builder;
	}
	
	private StringBuilder processCancelReservation(HttpServletRequest request, String[] chargePointItems) {
		String reservSTR = request.getParameter("reservationId");
		InputUtils.checkNullOrEmpty(reservSTR);

		int reservationId = InputUtils.toInt(reservSTR);
		
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
		InputUtils.checkNullOrEmpty(vendorId);
		
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
		String updateType = request.getParameter("updateType");	
		String listVersionSTR = request.getParameter("listVersion");
		InputUtils.checkNullOrEmpty(updateType, listVersionSTR);

		int listVersion = InputUtils.toInt(listVersionSTR);
				
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
				
				// Skip empty input
				String iti = idTagItems[i];
				try {
					InputUtils.checkNullOrEmpty(iti);
				} catch (InputException e){
					continue;
				}
				
				String instruction = idTagDataItems[i];					
				if ( instruction.equals("AddUpdate") ) addUpdateList.add(iti);
				else if ( instruction.equals("Delete") ) deleteList.add(iti);				
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