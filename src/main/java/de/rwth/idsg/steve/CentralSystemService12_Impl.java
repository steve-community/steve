package de.rwth.idsg.steve;

import java.sql.Timestamp;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import ocpp.cs._2010._08.AuthorizationStatus;
import ocpp.cs._2010._08.AuthorizeRequest;
import ocpp.cs._2010._08.AuthorizeResponse;
import ocpp.cs._2010._08.BootNotificationRequest;
import ocpp.cs._2010._08.BootNotificationResponse;
import ocpp.cs._2010._08.CentralSystemService;
import ocpp.cs._2010._08.DiagnosticsStatusNotificationRequest;
import ocpp.cs._2010._08.DiagnosticsStatusNotificationResponse;
import ocpp.cs._2010._08.FirmwareStatusNotificationRequest;
import ocpp.cs._2010._08.FirmwareStatusNotificationResponse;
import ocpp.cs._2010._08.HeartbeatRequest;
import ocpp.cs._2010._08.HeartbeatResponse;
import ocpp.cs._2010._08.IdTagInfo;
import ocpp.cs._2010._08.MeterValue;
import ocpp.cs._2010._08.MeterValuesRequest;
import ocpp.cs._2010._08.MeterValuesResponse;
import ocpp.cs._2010._08.RegistrationStatus;
import ocpp.cs._2010._08.StartTransactionRequest;
import ocpp.cs._2010._08.StartTransactionResponse;
import ocpp.cs._2010._08.StatusNotificationRequest;
import ocpp.cs._2010._08.StatusNotificationResponse;
import ocpp.cs._2010._08.StopTransactionRequest;
import ocpp.cs._2010._08.StopTransactionResponse;

import org.apache.cxf.ws.addressing.AddressingProperties;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.rwth.idsg.steve.common.Constants;
import de.rwth.idsg.steve.common.ServiceDBAccess;
import de.rwth.idsg.steve.common.utils.DateTimeUtils;
import de.rwth.idsg.steve.model.SQLIdTagData;

/**
 * Service implementation of OCPP V1.2
 * 
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 *  
 */
@javax.jws.WebService(
		serviceName = "CentralSystemService",
		portName = "CentralSystemServiceSoap12",
		targetNamespace = "urn://Ocpp/Cs/2010/08/",
		wsdlLocation = "file:/Users/sgokay/git/steve/src/main/webapp/wsdl/centralsystemservice_0.wsdl",
		endpointInterface = "ocpp.cs._2010._08.CentralSystemService")

public class CentralSystemService12_Impl implements CentralSystemService {
	@Resource
	private WebServiceContext webServiceContext;

	private static final Logger LOG = LoggerFactory.getLogger(CentralSystemService12_Impl.class);

	public BootNotificationResponse bootNotification(BootNotificationRequest parameters,java.lang.String chargeBoxIdentity) { 
		LOG.info("Executing bootNotification for {}", chargeBoxIdentity);

		// Get the Address value from WS-A Header
		MessageContext messageContext = webServiceContext.getMessageContext();
		AddressingProperties addressProp = (AddressingProperties) messageContext.get(org.apache.cxf.ws.addressing.JAXWSAConstants.SERVER_ADDRESSING_PROPERTIES_INBOUND);
		String endpoint_address = addressProp.getFrom().getAddress().getValue();
		
		DateTime now = new DateTime();
		
		boolean isRegistered = ServiceDBAccess.updateChargebox(endpoint_address, 
				"1.2",
				parameters.getChargePointVendor(),
				parameters.getChargePointModel(),
				parameters.getChargePointSerialNumber(),
				parameters.getChargeBoxSerialNumber(),
				parameters.getFirmwareVersion(),
				parameters.getIccid(),
				parameters.getImsi(),
				parameters.getMeterType(),
				parameters.getMeterSerialNumber(),
				chargeBoxIdentity, 
				new Timestamp(now.getMillis()));
				
		BootNotificationResponse _return = new BootNotificationResponse();
		RegistrationStatus _returnStatus = null;
		
		if (isRegistered) {
			_returnStatus = RegistrationStatus.ACCEPTED;
			_return.setCurrentTime(DateTimeUtils.convertToXMLGregCal(now));
			_return.setHeartbeatInterval(Integer.valueOf(Constants.HEARTBEAT_INTERVAL));
		} else {
			_returnStatus = RegistrationStatus.REJECTED;		
		}	
		_return.setStatus(_returnStatus);
		return _return;
	}

	public FirmwareStatusNotificationResponse firmwareStatusNotification(FirmwareStatusNotificationRequest parameters, java.lang.String chargeBoxIdentity) {
		LOG.info("Executing firmwareStatusNotificatio for {}", chargeBoxIdentity);
				
		String status = parameters.getStatus().toString();
		ServiceDBAccess.updateChargeboxFirmwareStatus(chargeBoxIdentity, status);
		
		FirmwareStatusNotificationResponse _return = new FirmwareStatusNotificationResponse();
		return _return;
	}

	public StatusNotificationResponse statusNotification(StatusNotificationRequest parameters, java.lang.String chargeBoxIdentity) {
		LOG.info("Executing statusNotification for {}", chargeBoxIdentity);

		int connectorId = parameters.getConnectorId();
		String status = parameters.getStatus().toString();
		String errorCode = parameters.getErrorCode().toString();
		
		ServiceDBAccess.insertConnectorStatus(chargeBoxIdentity, connectorId, status, null, errorCode, null, null, null);
		
		StatusNotificationResponse _return = new StatusNotificationResponse();
		return _return;
	}

	public MeterValuesResponse meterValues(MeterValuesRequest parameters,java.lang.String chargeBoxIdentity) { 
		LOG.info("Executing meterValues for {}", chargeBoxIdentity);

		int connectorId = parameters.getConnectorId();
		List<MeterValue> valuesList = parameters.getValues();
		
		if (valuesList != null){
			ServiceDBAccess.insertMeterValues12(chargeBoxIdentity, connectorId, valuesList);
		}
				
		MeterValuesResponse _return = new MeterValuesResponse();
		return _return;
	}

	public DiagnosticsStatusNotificationResponse diagnosticsStatusNotification(DiagnosticsStatusNotificationRequest parameters,java.lang.String chargeBoxIdentity) { 
		LOG.info("Executing diagnosticsStatusNotification for {}", chargeBoxIdentity);

		String status = parameters.getStatus().toString();
		ServiceDBAccess.updateChargeboxDiagnosticsStatus(chargeBoxIdentity, status);
		
		DiagnosticsStatusNotificationResponse _return = new DiagnosticsStatusNotificationResponse();
		return _return;
	}

	public StartTransactionResponse startTransaction(StartTransactionRequest parameters,java.lang.String chargeBoxIdentity) { 
		LOG.info("Executing startTransaction for {}", chargeBoxIdentity);
		
		// Get the authorization info of the user
		String idTag = parameters.getIdTag();		
		SQLIdTagData sqlData = ServiceDBAccess.getIdTagColumns(idTag);
		IdTagInfo _returnIdTagInfo = createIdTagInfo(sqlData);
		
		int transactionId = -1;
		if (_returnIdTagInfo.getStatus() == AuthorizationStatus.ACCEPTED){
			int connectorId = parameters.getConnectorId();
			Timestamp startTimestamp = DateTimeUtils.convertToTimestamp(parameters.getTimestamp());
			String startMeterValue = Integer.toString(parameters.getMeterStart());
			transactionId = ServiceDBAccess.insertTransaction(chargeBoxIdentity, connectorId, idTag, startTimestamp, startMeterValue, null);
		}
		
		StartTransactionResponse _return = new StartTransactionResponse();
		_return.setIdTagInfo(_returnIdTagInfo);
		if (transactionId != -1) { _return.setTransactionId(transactionId); }
		return _return;
	}

	public StopTransactionResponse stopTransaction(StopTransactionRequest parameters,java.lang.String chargeBoxIdentity) { 
		LOG.info("Executing stopTransaction for {}", chargeBoxIdentity);

		int transactionId = parameters.getTransactionId();
		Timestamp stopTimestamp = DateTimeUtils.convertToTimestamp(parameters.getTimestamp());
		String stopMeterValue = Integer.toString(parameters.getMeterStop());
		ServiceDBAccess.updateTransaction(chargeBoxIdentity, transactionId, stopTimestamp, stopMeterValue);
		
		// Get the authorization info of the user
		StopTransactionResponse _return = new StopTransactionResponse();
		String idTag = parameters.getIdTag();
		if (!idTag.isEmpty()) {
			SQLIdTagData sqlData = ServiceDBAccess.getIdTagColumns(idTag);
			_return.setIdTagInfo(createIdTagInfo(sqlData));
		}
		return _return;
	}

	public HeartbeatResponse heartbeat(HeartbeatRequest parameters,java.lang.String chargeBoxIdentity) {	
		LOG.info("Executing heartbeat for {}", chargeBoxIdentity);
		
		DateTime now = new DateTime();
		ServiceDBAccess.updateChargeboxHeartbeat(chargeBoxIdentity, new Timestamp(now.getMillis()));
		
		HeartbeatResponse _return = new HeartbeatResponse();
		_return.setCurrentTime(DateTimeUtils.convertToXMLGregCal(now));
		return _return;
	}
	
	public AuthorizeResponse authorize(AuthorizeRequest parameters,java.lang.String chargeBoxIdentity) { 
		LOG.info("Executing authorize for {}", chargeBoxIdentity);

		// Get the authorization info of the user
		String idTag = parameters.getIdTag();
		SQLIdTagData sqlData = ServiceDBAccess.getIdTagColumns(idTag);

        AuthorizeResponse _return = new AuthorizeResponse();
        IdTagInfo iti = createIdTagInfo(sqlData);
        _return.setIdTagInfo(iti);
		return _return;
	}

	private static IdTagInfo createIdTagInfo(SQLIdTagData sqlData){
		IdTagInfo _returnIdTagInfo = new IdTagInfo();
		AuthorizationStatus _returnIdTagInfoStatus = null;

		if (sqlData == null) {
			// Id is not in DB (unknown id). Not allowed for charging.
			_returnIdTagInfoStatus = AuthorizationStatus.INVALID;
			LOG.info("The idTag of this user is INVALID (not present in DB).");
		} else {	
			if (sqlData.isInTransaction()) {
				_returnIdTagInfoStatus = AuthorizationStatus.CONCURRENT_TX;
				LOG.info("The idTag of this user is ALREADY in another transaction.");
			} else if (sqlData.isBlocked()) {
				_returnIdTagInfoStatus = AuthorizationStatus.BLOCKED;
				LOG.info("The idTag of this user is BLOCKED.");
			} else if (sqlData.getExpiryDate() != null && DateTimeUtils.getCurrentDateTimeTS().after(sqlData.getExpiryDate())) {
				_returnIdTagInfoStatus = AuthorizationStatus.EXPIRED;
				LOG.info("The idTag of this user is EXPIRED.");
			} else {
				_returnIdTagInfoStatus = AuthorizationStatus.ACCEPTED;
				// When accepted, set the additional fields
				_returnIdTagInfo.setExpiryDate(DateTimeUtils.setExpiryDateTime(Constants.HOURS_TO_EXPIRE));
				if ( sqlData.getParentIdTag() != null ) _returnIdTagInfo.setParentIdTag(sqlData.getParentIdTag());
				LOG.info("The idTag of this user is ACCEPTED.");
			}
		}
		_returnIdTagInfo.setStatus(_returnIdTagInfoStatus);
		return _returnIdTagInfo;
	}	
}