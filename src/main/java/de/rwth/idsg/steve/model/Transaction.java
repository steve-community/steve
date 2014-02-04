package de.rwth.idsg.steve.model;

/**
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 *
 */
public class Transaction {
	
	private int transaction_pk, connectorId;	
	private String chargeBoxId, idTag, startTimestamp, stopTimestamp, chargedMeterValue;
	
	public Transaction(int transaction_pk, String chargeBoxId, int connectorId,
			String idTag, String startTimestamp, String stopTimestamp,
			String chargedMeterValue) {
		
		this.transaction_pk = transaction_pk;
		this.chargeBoxId = chargeBoxId;
		this.connectorId = connectorId;
		this.idTag = idTag;
		this.startTimestamp = startTimestamp;
		this.stopTimestamp = stopTimestamp;
		this.chargedMeterValue = chargedMeterValue;
	}

	public int getTransaction_pk() {
		return transaction_pk;
	}

	public int getConnectorId() {
		return connectorId;
	}

	public String getChargedMeterValue() {
		return chargedMeterValue;
	}

	public String getChargeBoxId() {
		return chargeBoxId;
	}

	public String getIdTag() {
		return idTag;
	}

	public String getStartTimestamp() {
		return startTimestamp;
	}

	public String getStopTimestamp() {
		return stopTimestamp;
	}

	public void setTransaction_pk(int transaction_pk) {
		this.transaction_pk = transaction_pk;
	}

	public void setConnectorId(int connectorId) {
		this.connectorId = connectorId;
	}

	public void setChargedMeterValue(String chargedMeterValue) {
		this.chargedMeterValue = chargedMeterValue;
	}

	public void setChargeBoxId(String chargeBoxId) {
		this.chargeBoxId = chargeBoxId;
	}

	public void setIdTag(String idTag) {
		this.idTag = idTag;
	}

	public void setStartTimestamp(String startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	public void setStopTimestamp(String stopTimestamp) {
		this.stopTimestamp = stopTimestamp;
	}
	
	
	
}
