package de.rwth.idsg.steve.repository.dto;

import de.rwth.idsg.steve.ocpp.OcppProtocol;
import lombok.Builder;
import lombok.Getter;
import org.joda.time.DateTime;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 17.11.2015
 */
@Getter
@Builder
public final class UpdateChargeboxParams {
    private final OcppProtocol ocppProtocol;
    private final DateTime heartbeatTimestamp;
    private final String vendor, model, pointSerial, boxSerial, fwVersion,
            iccid, imsi, meterType, meterSerial, chargeBoxId;
}
