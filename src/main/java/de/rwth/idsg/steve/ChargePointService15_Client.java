
package de.rwth.idsg.steve;
	
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ocpp.cp._2012._06.AuthorisationData;
import ocpp.cp._2012._06.AvailabilityType;
import ocpp.cp._2012._06.CancelReservationRequest;
import ocpp.cp._2012._06.CancelReservationResponse;
import ocpp.cp._2012._06.CancelReservationStatus;
import ocpp.cp._2012._06.ChangeAvailabilityRequest;
import ocpp.cp._2012._06.ChangeAvailabilityResponse;
import ocpp.cp._2012._06.ChangeConfigurationRequest;
import ocpp.cp._2012._06.ChangeConfigurationResponse;
import ocpp.cp._2012._06.ChargePointService;
import ocpp.cp._2012._06.ClearCacheRequest;
import ocpp.cp._2012._06.ClearCacheResponse;
import ocpp.cp._2012._06.DataTransferRequest;
import ocpp.cp._2012._06.DataTransferResponse;
import ocpp.cp._2012._06.GetConfigurationRequest;
import ocpp.cp._2012._06.GetConfigurationResponse;
import ocpp.cp._2012._06.GetDiagnosticsRequest;
import ocpp.cp._2012._06.GetDiagnosticsResponse;
import ocpp.cp._2012._06.GetLocalListVersionRequest;
import ocpp.cp._2012._06.GetLocalListVersionResponse;
import ocpp.cp._2012._06.KeyValue;
import ocpp.cp._2012._06.RemoteStartTransactionRequest;
import ocpp.cp._2012._06.RemoteStartTransactionResponse;
import ocpp.cp._2012._06.RemoteStopTransactionRequest;
import ocpp.cp._2012._06.RemoteStopTransactionResponse;
import ocpp.cp._2012._06.ReservationStatus;
import ocpp.cp._2012._06.ReserveNowRequest;
import ocpp.cp._2012._06.ReserveNowResponse;
import ocpp.cp._2012._06.ResetRequest;
import ocpp.cp._2012._06.ResetResponse;
import ocpp.cp._2012._06.ResetType;
import ocpp.cp._2012._06.SendLocalListRequest;
import ocpp.cp._2012._06.SendLocalListResponse;
import ocpp.cp._2012._06.UnlockConnectorRequest;
import ocpp.cp._2012._06.UnlockConnectorResponse;
import ocpp.cp._2012._06.UpdateFirmwareRequest;
import ocpp.cp._2012._06.UpdateFirmwareResponse;
import ocpp.cp._2012._06.UpdateStatus;
import ocpp.cp._2012._06.UpdateType;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.rwth.idsg.steve.common.ClientDBAccess;
import de.rwth.idsg.steve.common.Utils;

/**
 * Client implementation of OCPP V1.5.
 * 
 * This class has methods to create request payloads, and methods to send these to charge points from dynamically created clients.
 * Since there are multiple charge points and their endpoint addresses vary, the clients need to be created dynamically.
 * 
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * 
 */
public class ChargePointService15_Client {

	private static final Logger LOG = LoggerFactory.getLogger(ChargePointService15_Client.class);
	private static JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();

	static {
		factory.setServiceClass(ChargePointService.class);
	}

	/////// CREATE Request Payloads /////// 

	public ChangeAvailabilityRequest prepareChangeAvailability(int connectorId, String availTypeStr){
		ChangeAvailabilityRequest req = new ChangeAvailabilityRequest();      
		req.setConnectorId(connectorId);
		req.setType( AvailabilityType.fromValue(availTypeStr) );		
		return req;
	}

	public ChangeConfigurationRequest prepareChangeConfiguration(String confKey, String value){   	
		ChangeConfigurationRequest req = new ChangeConfigurationRequest();
		req.setKey(confKey);
		req.setValue(value);		
		return req;
	} 	

	public ClearCacheRequest prepareClearCache(){   	
		ClearCacheRequest req = new ClearCacheRequest();
		return req;
	} 	

	public GetDiagnosticsRequest prepareGetDiagnostics(String location, int retries, int retryInterval, String startTime, String stopTime){  
		GetDiagnosticsRequest req = new GetDiagnosticsRequest();
		req.setLocation(location);
		req.setRetries(retries);
		req.setRetryInterval(retryInterval);
		req.setStartTime( Utils.convertToXMLGregCal(startTime) );
		req.setStopTime( Utils.convertToXMLGregCal(stopTime) );		
		return req;
	} 

	public RemoteStartTransactionRequest prepareRemoteStartTransaction(int connectorId, String idTag){  
		RemoteStartTransactionRequest req = new RemoteStartTransactionRequest();
		req.setConnectorId(connectorId);
		req.setIdTag(idTag);
		return req;
	} 	

	public RemoteStopTransactionRequest prepareRemoteStopTransaction(int transactionId){  
		RemoteStopTransactionRequest req = new RemoteStopTransactionRequest();
		req.setTransactionId(transactionId);		
		return req;
	} 

	public ResetRequest prepareReset(String resetTypeStr){   	
		ResetRequest req = new ResetRequest();
		req.setType( ResetType.fromValue(resetTypeStr) );
		return req;
	}	

	public UnlockConnectorRequest prepareUnlockConnector(int connectorId){   	
		UnlockConnectorRequest req = new UnlockConnectorRequest();
		req.setConnectorId(connectorId);
		return req;
	}	

	public UpdateFirmwareRequest prepareUpdateFirmware(String location, int retries, String retrieveDate, int retryInterval){   
		UpdateFirmwareRequest req = new UpdateFirmwareRequest(); 
		req.setLocation(location);
		req.setRetries(retries);
		req.setRetrieveDate( Utils.convertToXMLGregCal(retrieveDate) );
		req.setRetryInterval(retryInterval);
		return req;
	}
	
	
	/** Start: New with OCPP 1.5  **/	
	
	// Dummy implementation. This is new in OCPP 1.5. It must be vendor-specific.
	public DataTransferRequest prepareDataTransfer(String vendorId, String messageId, String data){
		DataTransferRequest req = new DataTransferRequest();
		req.setVendorId(vendorId);
		req.setMessageId(messageId);
		req.setData(data);
		return req;	
	}
	
	public GetConfigurationRequest prepareGetConfiguration(String[] confKeys){
		GetConfigurationRequest req = new GetConfigurationRequest();		
		if (confKeys != null) {
			List<String> confKeysLIST = Arrays.asList(confKeys);
			req.getKey().addAll(confKeysLIST);
		}
		return req;
	}	
	
	public GetLocalListVersionRequest prepareGetLocalListVersion(){
		GetLocalListVersionRequest req = new GetLocalListVersionRequest();
		return req;
	}
	
	/**
	 * Method for FULL update
	 * 
	 */
	public SendLocalListRequest prepareSendLocalList(int listVersion){	
		SendLocalListRequest req = new SendLocalListRequest();
		req.setListVersion(listVersion);
		req.setUpdateType(UpdateType.FULL);
		req.getLocalAuthorisationList().addAll( ClientDBAccess.getIdTags(null) );
		return req;
	}
	
	
	/**
	 * Method for DIFFERENTIAL update
	 * 
	 */
	public SendLocalListRequest prepareSendLocalList(int listVersion, ArrayList<String> addUpdateList, ArrayList<String> deleteList){	
		SendLocalListRequest req = new SendLocalListRequest();
		req.setListVersion(listVersion);
		req.setUpdateType(UpdateType.DIFFERENTIAL);
		
		// Step 1: For the idTags to be deleted, insert only the idTag
		for (String idTag : deleteList) {
			AuthorisationData item = new AuthorisationData();
			item.setIdTag(idTag);
			req.getLocalAuthorisationList().add(item);
		}
		
		// Step 2: For the idTags to be added or updated, insert them with their IdTagInfos
		req.getLocalAuthorisationList().addAll( ClientDBAccess.getIdTags(addUpdateList) );
		
		return req;
	}
	
	/** End: New with OCPP 1.5  **/
	
	/////// SEND Request Payloads /////// 

	public String sendChangeAvailability(String chargeBoxId, String endpoint_address, ChangeAvailabilityRequest req){
		LOG.info("Invoking changeAvailability at {}", chargeBoxId);
		factory.setAddress(endpoint_address);
		ChargePointService client = (ChargePointService) factory.create();
		ChangeAvailabilityResponse response = client.changeAvailability(req, chargeBoxId);
		return "Charge point: " + chargeBoxId + ", Request: ChangeAvailability, Response: " + response.getStatus().value();
	}

	public String sendChangeConfiguration(String chargeBoxId, String endpoint_address, ChangeConfigurationRequest req){   	
		LOG.info("Invoking changeConfiguration at {}", chargeBoxId);
		factory.setAddress(endpoint_address);
		ChargePointService client = (ChargePointService) factory.create();
		ChangeConfigurationResponse response = client.changeConfiguration(req, chargeBoxId);
		return "Charge point: " + chargeBoxId + ", Request: ChangeConfiguration, Response: " + response.getStatus().value();
	} 

	public String sendClearCache(String chargeBoxId, String endpoint_address, ClearCacheRequest req){   	
		LOG.info("Invoking clearCache at {}", chargeBoxId);
		factory.setAddress(endpoint_address);
		ChargePointService client = (ChargePointService) factory.create();
		ClearCacheResponse response = client.clearCache(req, chargeBoxId);
		return "Charge point: " + chargeBoxId + ", Request: ClearCache, Response: " + response.getStatus().value();
	} 

	public String sendGetDiagnostics(String chargeBoxId, String endpoint_address, GetDiagnosticsRequest req){  
		LOG.info("Invoking getDiagnostics at {}", chargeBoxId);
		factory.setAddress(endpoint_address);
		ChargePointService client = (ChargePointService) factory.create();
		GetDiagnosticsResponse response = client.getDiagnostics(req, chargeBoxId);
		return "Charge point: " + chargeBoxId + ", Request: GetDiagnostics, Response: " + response.getFileName();
	} 

	public String sendRemoteStartTransaction(String chargeBoxId, String endpoint_address, RemoteStartTransactionRequest req){  
		LOG.info("Invoking remoteStartTransaction at {}", chargeBoxId);
		factory.setAddress(endpoint_address);
		ChargePointService client = (ChargePointService) factory.create();
		RemoteStartTransactionResponse response = client.remoteStartTransaction(req, chargeBoxId);
		return "Charge point: " + chargeBoxId + ", Request: RemoteStartTransaction, Response: " + response.getStatus().value();
	}

	public String sendRemoteStopTransaction(String chargeBoxId, String endpoint_address, RemoteStopTransactionRequest req){  
		LOG.info("Invoking remoteStopTransaction at {}", chargeBoxId);
		factory.setAddress(endpoint_address);
		ChargePointService client = (ChargePointService) factory.create();
		RemoteStopTransactionResponse response = client.remoteStopTransaction(req, chargeBoxId);
		return "Charge point: " + chargeBoxId + ", Request: RemoteStopTransaction, Response: " + response.getStatus().value();
	} 

	public String sendReset(String chargeBoxId, String endpoint_address, ResetRequest req){   	
		LOG.info("Invoking reset at {}", chargeBoxId);
		factory.setAddress(endpoint_address);
		ChargePointService client = (ChargePointService) factory.create();
		ResetResponse response = client.reset(req, chargeBoxId);
		return "Charge point: " + chargeBoxId + ", Request: Reset, Response: " + response.getStatus().value();
	}   

	public String sendUnlockConnector(String chargeBoxId, String endpoint_address, UnlockConnectorRequest req){   	
		LOG.info("Invoking unlockConnector at {}", chargeBoxId);
		factory.setAddress(endpoint_address);
		ChargePointService client = (ChargePointService) factory.create();
		UnlockConnectorResponse response = client.unlockConnector(req, chargeBoxId);
		return "Charge point: " + chargeBoxId + ", Request: UnlockConnector, Response: " + response.getStatus().value();
	}

	public String sendUpdateFirmware(String chargeBoxId, String endpoint_address, UpdateFirmwareRequest req){   
		LOG.info("Invoking updateFirmware at {}", chargeBoxId);
		factory.setAddress(endpoint_address);
		ChargePointService client = (ChargePointService) factory.create();
		UpdateFirmwareResponse response = client.updateFirmware(req, chargeBoxId);
		String str = "...";
		if (response != null) str = "OK";
		return "Charge point: " + chargeBoxId + ", Request: UpdateFirmware, Response: " + str;
	}

	/** Start: New with OCPP 1.5  **/
	
	// Dummy implementation. This is new in OCPP 1.5. It must be vendor-specific.
	public String sendDataTransfer(String chargeBoxId, String endpoint_address, DataTransferRequest req){
		LOG.info("Invoking dataTransfer at {}", chargeBoxId);
		factory.setAddress(endpoint_address);
		ChargePointService client = (ChargePointService) factory.create();
		DataTransferResponse response = client.dataTransfer(req, chargeBoxId);
		
		return "Charge point: " + chargeBoxId + ", Request: DataTransfer, Response: " + response.getStatus().value() 
				+ "\n+ Data: " + response.getData();
	}
	
	public String sendGetConfiguration(String chargeBoxId, String endpoint_address, GetConfigurationRequest req){
		LOG.info("Invoking getConfiguration at {}", chargeBoxId);
		factory.setAddress(endpoint_address);
		ChargePointService client = (ChargePointService) factory.create();
		GetConfigurationResponse response = client.getConfiguration(req, chargeBoxId);
		
		// Print the return values		
		StringBuilder builder = new StringBuilder("Charge point: " + chargeBoxId + ", Request: GetConfiguration, Response:\n");
		List<KeyValue> knownList = response.getConfigurationKey();		
		for (KeyValue temp : knownList){
			String str = "NOT_SET";
			String value = temp.getValue();
			if (value != null) str = value;
			builder.append("+ " + temp.getKey() + " (read-only:" + temp.isReadonly() + ") : " + str + "\n");
		}
		
		List<String> unknownList = response.getUnknownKey();
		builder.append("- Unknown keys: ");
		int counter = unknownList.size();
		for (String temp : unknownList){
			counter--;
			if (counter == 0) {
				builder.append(temp);
			} else {
				builder.append(temp + ", ");
			}
		}	
		return builder.toString();
	}
	
	public String sendGetLocalListVersion(String chargeBoxId, String endpoint_address, GetLocalListVersionRequest req){
		LOG.info("Invoking getLocalListVersion at {}", chargeBoxId);
		factory.setAddress(endpoint_address);
		ChargePointService client = (ChargePointService) factory.create();
		GetLocalListVersionResponse response = client.getLocalListVersion(req, chargeBoxId);
		return "Charge point: " + chargeBoxId + ", Request: GetLocalListVersion, Response: " + response.getListVersion();
	}
	
	public String sendSendLocalList(String chargeBoxId, String endpoint_address, SendLocalListRequest req){
		LOG.info("Invoking sendLocalList at {}", chargeBoxId);
		factory.setAddress(endpoint_address);
		ChargePointService client = (ChargePointService) factory.create();
		SendLocalListResponse response = client.sendLocalList(req, chargeBoxId);
		
		String result;
		UpdateStatus answer = response.getStatus();
		if (answer.equals(UpdateStatus.FAILED) || answer.equals(UpdateStatus.VERSION_MISMATCH)) {
			LOG.error("The charge point {} did NOT accept to update its local list because: {}. "
					+ "Retrying sending the FULL local authorization list now.", chargeBoxId, answer.value());
			
			// Does it even make sense to increment the version number by 1?
			int listVersionRETRY = req.getListVersion() + 1 ;
			SendLocalListRequest reqRETRY = this.prepareSendLocalList(listVersionRETRY);
			SendLocalListResponse responseRETRY = client.sendLocalList(reqRETRY, chargeBoxId);
			
			result = "Charge point: " + chargeBoxId + ", Request: SendLocalList, Response: " + responseRETRY.getStatus().value() 
					+ "\n+ Hash: " + responseRETRY.getHash();
		} else {
			
			result = "Charge point: " + chargeBoxId + ", Request: SendLocalList, Response: " + response.getStatus().value() 
					+ "\n+ Hash: " + response.getHash();
		}
		return result;
	}
		
	/////// The following operations are specific to charge point: PREPARE and SEND Request Payloads ///////
	
	// TODO: It's cumbersome now: First book, then cancel if it is not accepted by the charge point. 
	// Needs a better idea:
	// a) Book only after it is accepted by the charge point?
	// b) Check first if the chargebox has available connectors to be reserved?
	public String reserveNow(
			String chargeBoxId, 
			String endpoint_address, 
			int connectorId, 
			String expiryString, 
			String idTag, 
			String parentIdTag){
		
		LOG.info("Invoking reserveNow at {}", chargeBoxId);
		
		DateTime expiryDateTime = Utils.convertToDateTime(expiryString);
		// Book the reservation and get the id
		int reservationId = ClientDBAccess.bookReservation(idTag, chargeBoxId, null, expiryDateTime);
		
		ReserveNowRequest req = new ReserveNowRequest();
		req.setConnectorId(connectorId);
		req.setExpiryDate( Utils.convertToXMLGregCal(expiryString) );
		req.setIdTag(idTag);
		if (parentIdTag != null && !parentIdTag.isEmpty()) req.setParentIdTag(parentIdTag);
		req.setReservationId(reservationId);
		
		factory.setAddress(endpoint_address);
		ChargePointService client = (ChargePointService) factory.create();
		ReserveNowResponse response = client.reserveNow(req, chargeBoxId);
		
		// Check response, and cancel reservation from DB if it is not accepted by the charge point
		ReservationStatus responseStatus = response.getStatus();
		if ( responseStatus.equals(ReservationStatus.ACCEPTED) ) {
			ClientDBAccess.cancelReservation(reservationId);
		}
		return "Charge point: " + chargeBoxId + ", Request: ReserveNow, Response: " + responseStatus.value();
	}
	
	public String cancelReservation(String chargeBoxId, String endpoint_address, int reservationId){		
		LOG.info("Invoking cancelReservation at {}", chargeBoxId);
		
		CancelReservationRequest req = new CancelReservationRequest();
		req.setReservationId(reservationId);
		
		factory.setAddress(endpoint_address);
		ChargePointService client = (ChargePointService) factory.create();
		CancelReservationResponse response = client.cancelReservation(req, chargeBoxId);
		
		CancelReservationStatus responseStatus = response.getStatus();
		if ( responseStatus.equals(CancelReservationStatus.ACCEPTED) ){
			ClientDBAccess.cancelReservation(reservationId);
		}		
		return "Charge point: " + chargeBoxId + ", Request: CancelReservation, Response: " + responseStatus.value();
	}
}
