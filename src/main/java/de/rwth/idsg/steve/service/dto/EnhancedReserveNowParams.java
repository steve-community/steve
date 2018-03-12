package de.rwth.idsg.steve.service.dto;

import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import de.rwth.idsg.steve.web.dto.ocpp.ChargePointSelection;
import de.rwth.idsg.steve.web.dto.ocpp.ReserveNowParams;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author Andreas Heuvels <andreas.heuvels@rwth-aachen.de>
 * @since 09.03.18
 */
@Getter
@RequiredArgsConstructor
public class EnhancedReserveNowParams implements ChargePointSelection {
    private final ReserveNowParams reserveNowParams;
    private final int reservationId;
    private final String parentIdTag;

    @Override
    public List<ChargePointSelect> getChargePointSelectList() {
        return reserveNowParams.getChargePointSelectList();
    }
}
