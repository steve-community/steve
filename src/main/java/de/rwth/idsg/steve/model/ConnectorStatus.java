package de.rwth.idsg.steve.model;

/**
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 *
 */
public class ConnectorStatus {
	String chargeBoxId, timeStamp, status, errorCode;
	int connectorId;
	
	public ConnectorStatus(String chargeBoxId, int connectorId, String timeStamp, String status, String errorCode) {
		this.chargeBoxId = chargeBoxId;
		this.connectorId = connectorId;
		this.timeStamp = timeStamp;
		this.status = status;
		this.errorCode = errorCode;
	}

	public String getChargeBoxId() {
		return chargeBoxId;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public String getStatus() {
		return status;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public int getConnectorId() {
		return connectorId;
	}

	public void setChargeBoxId(String chargeBoxId) {
		this.chargeBoxId = chargeBoxId;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public void setConnectorId(int connectorId) {
		this.connectorId = connectorId;
	}

}