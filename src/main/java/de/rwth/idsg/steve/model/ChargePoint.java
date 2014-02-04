package de.rwth.idsg.steve.model;

/**
* Charge point information for Web interface
* 
* @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
* 
*/
public class ChargePoint {
	private String chargeBoxId, endpoint_address, ocppVersion, chargePointVendor, chargePointModel,
	chargePointSerialNumber, chargeBoxSerialNumber, firewireVersion, firewireUpdateStatus, firewireUpdateTimestamp,
	iccid, imsi, meterType, meterSerialNumber, diagnosticsStatus, diagnosticsTimestamp, lastHeartbeatTimestamp;

	public ChargePoint(String chargeBoxId, String endpoint_address,
			String ocppVersion, String chargePointVendor,
			String chargePointModel, String chargePointSerialNumber,
			String chargeBoxSerialNumber, String firewireVersion,
			String firewireUpdateStatus, String firewireUpdateTimestamp,
			String iccid, String imsi, String meterType,
			String meterSerialNumber, String diagnosticsStatus,
			String diagnosticsTimestamp, String lastHeartbeatTimestamp) {

		this.chargeBoxId = chargeBoxId;
		this.endpoint_address = endpoint_address;
		this.ocppVersion = ocppVersion;
		this.chargePointVendor = chargePointVendor;
		this.chargePointModel = chargePointModel;
		this.chargePointSerialNumber = chargePointSerialNumber;
		this.chargeBoxSerialNumber = chargeBoxSerialNumber;
		this.firewireVersion = firewireVersion;
		this.firewireUpdateStatus = firewireUpdateStatus;
		this.firewireUpdateTimestamp = firewireUpdateTimestamp;
		this.iccid = iccid;
		this.imsi = imsi;
		this.meterType = meterType;
		this.meterSerialNumber = meterSerialNumber;
		this.diagnosticsStatus = diagnosticsStatus;
		this.diagnosticsTimestamp = diagnosticsTimestamp;
		this.lastHeartbeatTimestamp = lastHeartbeatTimestamp;
	}

	public String getChargeBoxId() {
		return chargeBoxId;
	}

	public String getEndpoint_address() {
		return endpoint_address;
	}

	public String getOcppVersion() {
		return ocppVersion;
	}

	public String getChargePointVendor() {
		return chargePointVendor;
	}

	public String getChargePointModel() {
		return chargePointModel;
	}

	public String getChargePointSerialNumber() {
		return chargePointSerialNumber;
	}

	public String getChargeBoxSerialNumber() {
		return chargeBoxSerialNumber;
	}

	public String getFirewireVersion() {
		return firewireVersion;
	}

	public String getFirewireUpdateStatus() {
		return firewireUpdateStatus;
	}

	public String getFirewireUpdateTimestamp() {
		return firewireUpdateTimestamp;
	}

	public String getIccid() {
		return iccid;
	}

	public String getImsi() {
		return imsi;
	}

	public String getMeterType() {
		return meterType;
	}

	public String getMeterSerialNumber() {
		return meterSerialNumber;
	}

	public String getDiagnosticsStatus() {
		return diagnosticsStatus;
	}

	public String getDiagnosticsTimestamp() {
		return diagnosticsTimestamp;
	}

	public String getLastHeartbeatTimestamp() {
		return lastHeartbeatTimestamp;
	}

}