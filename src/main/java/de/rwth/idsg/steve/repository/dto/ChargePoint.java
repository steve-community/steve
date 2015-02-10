package de.rwth.idsg.steve.repository.dto;

import lombok.Builder;
import lombok.Getter;

/**
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 *
 */
@Getter
@Builder
public final class ChargePoint {
    private String chargeBoxId, endpointAddress, ocppVersion, chargePointVendor, chargePointModel,
            chargePointSerialNumber, chargeBoxSerialNumber, firewireVersion, firewireUpdateStatus,
            firewireUpdateTimestamp, iccid, imsi, meterType, meterSerialNumber, diagnosticsStatus,
            diagnosticsTimestamp, lastHeartbeatTimestamp;
}