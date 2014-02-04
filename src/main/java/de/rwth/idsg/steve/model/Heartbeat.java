package de.rwth.idsg.steve.model;

/**
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 *
 */
public class Heartbeat {
	
	private String chargeBoxId;
	private String lastHeartbeatTimestamp;
	
	public Heartbeat(String chargeBoxId, String lastHeartbeatTimestamp) {
		this.chargeBoxId = chargeBoxId;
		this.lastHeartbeatTimestamp = lastHeartbeatTimestamp;
	}

	public String getChargeBoxId() {
		return chargeBoxId;
	}

	public String getLastHeartbeatTimestamp() {
		return lastHeartbeatTimestamp;
	}

	public void setChargeBoxId(String chargeBoxId) {
		this.chargeBoxId = chargeBoxId;
	}

	public void setLastHeartbeatTimestamp(String lastHeartbeatTimestamp) {
		this.lastHeartbeatTimestamp = lastHeartbeatTimestamp;
	}


}
