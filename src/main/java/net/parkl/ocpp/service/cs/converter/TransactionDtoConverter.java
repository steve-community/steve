package net.parkl.ocpp.service.cs.converter;

import de.rwth.idsg.steve.utils.DateTimeUtils;
import net.parkl.ocpp.entities.OcppChargeBox;
import net.parkl.ocpp.entities.OcppTag;
import net.parkl.ocpp.entities.Transaction;
import org.joda.time.DateTime;

public class TransactionDtoConverter {

    public static de.rwth.idsg.steve.repository.dto.Transaction toTransactionDto(Transaction t, OcppChargeBox box, OcppTag tag) {
        return de.rwth.idsg.steve.repository.dto.Transaction.builder()
                .id(t.getTransactionPk())
                .chargeBoxId(t.getConnector().getChargeBoxId())
                .connectorId(t.getConnector().getConnectorId())
                .ocppIdTag(t.getOcppTag())
                .startTimestampDT(t.getStartTimestamp() != null ? new DateTime(t.getStartTimestamp()) : null)
                .startTimestamp(DateTimeUtils.humanize(t.getStartTimestamp() != null ? new DateTime(t.getStartTimestamp()) : null))
                .startValue(t.getStartValue())
                .stopTimestampDT(t.getStopTimestamp() != null ? new DateTime(t.getStopTimestamp()) : null)
                .stopTimestamp(DateTimeUtils.humanize(t.getStopTimestamp() != null ? new DateTime(t.getStopTimestamp()) : null))
                .stopValue(t.getStopValue())
                .chargeBoxPk(box.getChargeBoxPk())
                .ocppTagPk(tag.getOcppTagPk())
                .stopEventActor(t.getStopEventActor())
                .build();
    }
}
