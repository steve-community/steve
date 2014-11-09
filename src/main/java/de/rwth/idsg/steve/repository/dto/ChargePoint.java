package de.rwth.idsg.steve.repository.dto;

import lombok.Getter;
import lombok.experimental.Builder;

/**
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 *
 */
@Getter
@Builder
public final class ChargePoint {
    private final String chargeBoxId, endpointAddress, ocppVersion, chargePointVendor, chargePointModel,
            chargePointSerialNumber, chargeBoxSerialNumber, firewireVersion, firewireUpdateStatus,
            firewireUpdateTimestamp, iccid, imsi, meterType, meterSerialNumber, diagnosticsStatus,
            diagnosticsTimestamp, lastHeartbeatTimestamp;
}