
package de.rwth.idsg.steve.cps.v12;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.rwth.idsg.steve.common.Utils;

/**
 * This class has methods to create request payloads, and methods to send these to charge points from dynamically created clients.
 * Since there are multiple charge points and their endpoint addresses vary, the clients need to be created dynamically.
 * 
 */
public class CPS_Client {

	private static final Logger LOG = LoggerFactory.getLogger(CPS_Client.class);
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

	/////// SEND Request Payloads /////// 

	public String sendChangeAvailability(String chargeBoxId, String endpoint_address, ChangeAvailabilityRequest req){
		LOG.info("Invoking changeAvailability...");
		factory.setAddress(endpoint_address);
		ChargePointService client = (ChargePointService) factory.create();
		ChangeAvailabilityResponse response = client.changeAvailability(req, chargeBoxId);
		return "The charge point " + chargeBoxId + " responded to change its availability with: " + response.getStatus().value();
	}

	public String sendChangeConfiguration(String chargeBoxId, String endpoint_address, ChangeConfigurationRequest req){   	
		LOG.info("Invoking changeConfiguration...");
		factory.setAddress(endpoint_address);
		ChargePointService client = (ChargePointService) factory.create();
		ChangeConfigurationResponse response = client.changeConfiguration(req, chargeBoxId);
		return "The charge point " + chargeBoxId + " responded to change its configration with: " + response.getStatus().value();
	} 

	public String sendClearCache(String chargeBoxId, String endpoint_address, ClearCacheRequest req){   	
		LOG.info("Invoking clearCache...");
		factory.setAddress(endpoint_address);
		ChargePointService client = (ChargePointService) factory.create();
		ClearCacheResponse response = client.clearCache(req, chargeBoxId);
		return "The charge point " + chargeBoxId + " responded to clear its cache with: " + response.getStatus().value();
	} 

	public String sendGetDiagnostics(String chargeBoxId, String endpoint_address, GetDiagnosticsRequest req){  
		LOG.info("Invoking getDiagnostics...");
		factory.setAddress(endpoint_address);
		ChargePointService client = (ChargePointService) factory.create();
		GetDiagnosticsResponse response = client.getDiagnostics(req, chargeBoxId);
		return "The charge point " + chargeBoxId + " will upload the diagonostic information to the file: " + response.getFileName();
	} 

	public String sendRemoteStartTransaction(String chargeBoxId, String endpoint_address, RemoteStartTransactionRequest req){  
		LOG.info("Invoking remoteStartTransaction...");
		factory.setAddress(endpoint_address);
		ChargePointService client = (ChargePointService) factory.create();
		RemoteStartTransactionResponse response = client.remoteStartTransaction(req, chargeBoxId);
		return "The charge point " + chargeBoxId + " responded to remotely start the transaction with: " + response.getStatus().value();
	}

	public String sendRemoteStopTransaction(String chargeBoxId, String endpoint_address, RemoteStopTransactionRequest req){  
		LOG.info("Invoking remoteStopTransaction...");	
		factory.setAddress(endpoint_address);
		ChargePointService client = (ChargePointService) factory.create();
		RemoteStopTransactionResponse response = client.remoteStopTransaction(req, chargeBoxId);
		return "The charge point " + chargeBoxId + " responded to remotely stop the transaction with: " + response.getStatus().value();
	} 

	public String sendReset(String chargeBoxId, String endpoint_address, ResetRequest req){   	
		LOG.info("Invoking reset...");
		factory.setAddress(endpoint_address);
		ChargePointService client = (ChargePointService) factory.create();
		ResetResponse response = client.reset(req, chargeBoxId);
		return "The charge point " + chargeBoxId + " responded to reset itself with: " + response.getStatus().value();
	}   

	public String sendUnlockConnector(String chargeBoxId, String endpoint_address, UnlockConnectorRequest req){   	
		LOG.info("Invoking unlockConnector...");
		factory.setAddress(endpoint_address);
		ChargePointService client = (ChargePointService) factory.create();
		UnlockConnectorResponse response = client.unlockConnector(req, chargeBoxId);
		return "The charge point " + chargeBoxId + " responded to unlock the connector with: " + response.getStatus().value();
	}

	public String sendUpdateFirmware(String chargeBoxId, String endpoint_address, UpdateFirmwareRequest req){   
		LOG.info("Invoking updateFirmware...");
		factory.setAddress(endpoint_address);
		ChargePointService client = (ChargePointService) factory.create();
		UpdateFirmwareResponse response = client.updateFirmware(req, chargeBoxId);
		if( response != null ){
			return "The charge point " + chargeBoxId + " has responded to update its firmware.";
		}else{
			return "The charge point " + chargeBoxId + " has NOT responded to update its firmware.";
		}
	}
}
