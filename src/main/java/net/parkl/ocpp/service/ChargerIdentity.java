package net.parkl.ocpp.service;

public class ChargerIdentity {
	private String chargeBoxId;
	private int connectorId;
	public String getChargeBoxId() {
		return chargeBoxId;
	}
	public void setChargeBoxId(String chargeBoxId) {
		this.chargeBoxId = chargeBoxId;
	}
	public int getConnectorId() {
		return connectorId;
	}
	public void setConnectorId(int connectorId) {
		this.connectorId = connectorId;
	}
}
